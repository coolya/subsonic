/*
 This file is part of Subsonic.

 Subsonic is free software: you can redistribute it and/or modify
 it under the terms of the GNU General Public License as published by
 the Free Software Foundation, either version 3 of the License, or
 (at your option) any later version.

 Subsonic is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with Subsonic.  If not, see <http://www.gnu.org/licenses/>.

 Copyright 2009 (C) Sindre Mehus
 */
package net.sourceforge.subsonic.androidapp.activity;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.IBinder;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceScreen;
import android.util.Log;
import net.sourceforge.subsonic.androidapp.R;
import net.sourceforge.subsonic.androidapp.domain.Version;
import net.sourceforge.subsonic.androidapp.service.DownloadFile;
import net.sourceforge.subsonic.androidapp.service.DownloadService;
import net.sourceforge.subsonic.androidapp.service.DownloadServiceImpl;
import net.sourceforge.subsonic.androidapp.service.MusicService;
import net.sourceforge.subsonic.androidapp.service.MusicServiceFactory;
import net.sourceforge.subsonic.androidapp.util.BackgroundTask;
import net.sourceforge.subsonic.androidapp.util.Constants;
import net.sourceforge.subsonic.androidapp.util.ErrorDialog;
import net.sourceforge.subsonic.androidapp.util.FileUtil;
import net.sourceforge.subsonic.androidapp.util.Pair;
import net.sourceforge.subsonic.androidapp.util.SimpleServiceBinder;
import net.sourceforge.subsonic.androidapp.util.Util;

public class SettingsActivity extends PreferenceActivity {

    private static final String TAG = SettingsActivity.class.getSimpleName();
    private final Map<String, ServerSettings> serverSettings = new LinkedHashMap<String, ServerSettings>();
    private ListPreference serverInstance;
    private final DownloadServiceConnection downloadServiceConnection = new DownloadServiceConnection();
    private DownloadService downloadService;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bindService(new Intent(this, DownloadServiceImpl.class), downloadServiceConnection, Context.BIND_AUTO_CREATE);
        addPreferencesFromResource(R.xml.settings);

        serverInstance = (ListPreference) findPreference("serverInstance");

        Preference testConnection = findPreference("testConnection");
        testConnection.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                testConnection();
                return false;
            }
        });

        Preference checkForUpdates = findPreference("checkForUpdates");
        checkForUpdates.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                checkForUpdates();
                return false;
            }
        });

        Preference emptyCache = findPreference("emptyCache");
        emptyCache.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                Runnable task = new Runnable() {
                    @Override
                    public void run() {
                        emptyCache();
                    }
                };
                Util.confirm(SettingsActivity.this, getResources().getString(R.string.settings_empty_cache_confirm), task);
                return false;
            }
        });

        for (int i = 1; i <= 3; i++) {
            String instance = String.valueOf(i);
            serverSettings.put(instance, new ServerSettings(instance));
        }

        SharedPreferences prefs = getSharedPreferences(Constants.PREFERENCES_FILE_NAME, 0);
        prefs.registerOnSharedPreferenceChangeListener(new SharedPreferences.OnSharedPreferenceChangeListener() {
            @Override
            public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
                update();
            }
        });

        update();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(downloadServiceConnection);
    }

    private void update() {
        String instance = serverInstance.getValue();
        ServerSettings serverSetting = serverSettings.get(instance);
        serverInstance.setSummary(serverSetting.serverName.getText());

        List<String> entries = new ArrayList<String>();
        for (ServerSettings ss : serverSettings.values()) {
            ss.update();
            entries.add(ss.serverName.getText());
        }
        serverInstance.setEntries(entries.toArray(new CharSequence[entries.size()]));
    }

    private void testConnection() {
        BackgroundTask<Boolean> task = new BackgroundTask<Boolean>(this) {
            @Override
            protected Boolean doInBackground() throws Throwable {
                updateProgress(R.string.settings_testing_connection);
                MusicService musicService = MusicServiceFactory.getMusicService(SettingsActivity.this);
                musicService.ping(SettingsActivity.this, this);
                return musicService.isLicenseValid(SettingsActivity.this, null);
            }

            @Override
            protected void done(Boolean licenseValid) {
                if (licenseValid) {
                    Util.toast(SettingsActivity.this, getResources().getString(R.string.settings_testing_ok));
                } else {
                    Util.toast(SettingsActivity.this, getResources().getString(R.string.settings_testing_unlicensed));
                }
            }

            @Override
            protected void cancel() {
            }

            @Override
            protected void error(Throwable error) {
                Log.w(TAG, error.toString(), error);
                new ErrorDialog(SettingsActivity.this, getResources().getString(R.string.settings_connection_failure) +
                                                       getErrorMessage(error), false);
            }
        };
        task.execute();
    }

    private void emptyCache() {
        BackgroundTask<?> task = new BackgroundTask<Object>(this) {
            private int deleteCount;
            private Set<File> undeletable;

            @Override
            protected Object doInBackground() throws Throwable {
                updateProgress(R.string.settings_cache_deleting);

                undeletable = new HashSet<File>(5);
                DownloadFile currentDownload = downloadService.getCurrentDownloading();
                if (currentDownload != null) {
                    undeletable.add(currentDownload.getPartialFile());
                    undeletable.add(currentDownload.getCompleteFile());
                }
                DownloadFile currentPlaying = downloadService.getCurrentPlaying();
                if (currentPlaying != null) {
                    undeletable.add(currentPlaying.getPartialFile());
                    undeletable.add(currentPlaying.getCompleteFile());
                }

                File root = FileUtil.getMusicDirectory();
                undeletable.add(root);
                cleanRecursively(root);
                return null;
            }

            private void cleanRecursively(File file) {
                if (file.isFile()) {
                    String name = file.getName();

                    boolean isCacheFile = name.endsWith(".partial") || name.endsWith(".complete");
                    if (isCacheFile && !undeletable.contains(file)) {
                        Util.delete(file);
                        if (!file.exists()) {
                            Log.d(TAG, "Deleted " + file.getPath());
                            deleteCount++;
                            updateProgress(getResources().getString(R.string.settings_cache_deleted, deleteCount));
                        }
                    }
                    return;
                }

                for (File child : FileUtil.listFiles(file)) {
                    cleanRecursively(child);
                }

                // Delete directory if empty.
                if (FileUtil.listFiles(file).isEmpty() && !undeletable.contains(file)) {
                    Util.delete(file);
                    if (!file.exists()) {
                        Log.d(TAG, "Deleted " + file.getPath());
                    }
                }
            }

            @Override
            protected void done(Object result) {
            }

            @Override
            protected void cancel() {
            }
        };
        task.execute();
    }

    private void checkForUpdates() {
        BackgroundTask<Pair<Version, Version>> task = new BackgroundTask<Pair<Version, Version>>(this) {
            @Override
            protected Pair<Version, Version> doInBackground() throws Throwable {
                updateProgress(R.string.settings_version_checking);
                MusicService musicService = MusicServiceFactory.getMusicService(SettingsActivity.this);
                Version localVersion = musicService.getLocalVersion(SettingsActivity.this);
                Version latestVersion = musicService.getLatestVersion(SettingsActivity.this, this);
                return new Pair<Version, Version>(localVersion, latestVersion);
            }

            @Override
            protected void done(Pair<Version, Version> versions) {
                Version localVersion = versions.getFirst();
                Version latestVersion = versions.getSecond();
                if (localVersion == null) {
                    Util.error(SettingsActivity.this, getResources().getString(R.string.settings_version_current_failed));
                } else if (latestVersion == null) {
                    Util.error(SettingsActivity.this, getResources().getString(R.string.settings_version_latest_failed));
                } else if (localVersion.compareTo(latestVersion) < 0) {
                    Util.info(SettingsActivity.this,
                              getResources().getString(R.string.settings_version_update_available_title),
                              getResources().getString(R.string.settings_version_update_available_text));
                } else {
                    Util.toast(SettingsActivity.this, getResources().getString(R.string.settings_version_update_not_available));
                }
            }

            @Override
            protected void cancel() {
            }
        };
        task.execute();
    }

    private class ServerSettings {
        private EditTextPreference serverName;
        private EditTextPreference serverUrl;
        private EditTextPreference username;
        private PreferenceScreen screen;

        private ServerSettings(String instance) {

            screen = (PreferenceScreen) findPreference("server" + instance);
            serverName = (EditTextPreference) findPreference(Constants.PREFERENCES_KEY_SERVER_NAME + instance);
            serverUrl = (EditTextPreference) findPreference(Constants.PREFERENCES_KEY_SERVER_URL + instance);
            username = (EditTextPreference) findPreference(Constants.PREFERENCES_KEY_USERNAME + instance);

            serverUrl.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object value) {
                    try {
                        new URL((String) value);
                    } catch (Exception x) {
                        new ErrorDialog(SettingsActivity.this, getResources().getString(R.string.settings_invalid_url), false);
                        return false;
                    }
                    return true;
                }
            });
        }

        public void update() {
            serverName.setSummary(serverName.getText());
            serverUrl.setSummary(serverUrl.getText());
            username.setSummary(username.getText());

            screen.setTitle(""); // Work-around for missing update of screen summary.
            screen.setSummary(serverUrl.getText());
            screen.setTitle(serverName.getText());
        }
    }

    private class DownloadServiceConnection implements ServiceConnection {

        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {
            downloadService = ((SimpleServiceBinder<DownloadService>) service).getService();
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            downloadService = null;
        }
    }

}