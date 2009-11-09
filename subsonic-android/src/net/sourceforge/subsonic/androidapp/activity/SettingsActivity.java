package net.sourceforge.subsonic.androidapp.activity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceScreen;
import android.util.Log;
import net.sourceforge.subsonic.androidapp.R;
import net.sourceforge.subsonic.androidapp.domain.Version;
import net.sourceforge.subsonic.androidapp.service.MusicService;
import net.sourceforge.subsonic.androidapp.service.MusicServiceFactory;
import net.sourceforge.subsonic.androidapp.util.BackgroundTask;
import net.sourceforge.subsonic.androidapp.util.Constants;
import net.sourceforge.subsonic.androidapp.util.ErrorDialog;
import net.sourceforge.subsonic.androidapp.util.Pair;
import net.sourceforge.subsonic.androidapp.util.Util;

import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class SettingsActivity extends PreferenceActivity implements SharedPreferences.OnSharedPreferenceChangeListener {

    private static final String TAG = SettingsActivity.class.getSimpleName();
    private final Map<String, ServerSettings> serverSettings = new LinkedHashMap<String, ServerSettings>();
    private ListPreference serverInstance;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
                emptyCache();
                return false;
            }
        });

        for (int i = 1; i <= 3; i++) {
            String instance = String.valueOf(i);
            serverSettings.put(instance, new ServerSettings(instance));
        }

        SharedPreferences prefs = getSharedPreferences(Constants.PREFERENCES_FILE_NAME, 0);
        prefs.registerOnSharedPreferenceChangeListener(this);

        update();
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        update();
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
                updateProgress("Testing connection...");
                MusicService musicService = MusicServiceFactory.getMusicService();
                musicService.ping(SettingsActivity.this, this);
                return musicService.isLicenseValid(SettingsActivity.this, null);
            }

            @Override
            protected void done(Boolean licenseValid) {
                if (licenseValid) {
                    Util.toast(SettingsActivity.this, "Connection is OK");
                } else {
                    Util.toast(SettingsActivity.this, "Connection is OK. Server unlicensed.");
                }
            }

            @Override
            protected void cancel() {
            }

            @Override
            protected void error(Throwable error) {
                Log.w(TAG, error.toString(), error);
                new ErrorDialog(SettingsActivity.this, "Connection failure. " + getErrorMessage(error), false);
            }
        };
        task.execute();
    }

    private void emptyCache() {
//        TODO
    }

    private void checkForUpdates() {
        BackgroundTask<Pair<Version, Version>> task = new BackgroundTask<Pair<Version, Version>>(this) {
            @Override
            protected Pair<Version, Version> doInBackground() throws Throwable {
                updateProgress("Checking for updates...");
                MusicService musicService = MusicServiceFactory.getMusicService();
                Version localVersion = musicService.getLocalVersion(SettingsActivity.this);
                Version latestVersion = musicService.getLatestVersion(SettingsActivity.this, this);
                return new Pair<Version, Version>(localVersion, latestVersion);
            }

            @Override
            protected void done(Pair<Version, Version> versions) {
                Version localVersion = versions.getFirst();
                Version latestVersion = versions.getSecond();
                if (localVersion == null) {
                    Util.error(SettingsActivity.this, "Failed to resolve current version.");
                } else if (latestVersion == null) {
                    Util.error(SettingsActivity.this, "Failed to resolve latest version.");
                } else if (localVersion.compareTo(latestVersion) < 0) {
                    Util.info(SettingsActivity.this, "Update available", "A newer version is available on Android Market.");
                } else {
                    Util.toast(SettingsActivity.this, "You are running the latest version.");
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
                        new ErrorDialog(SettingsActivity.this, "Please specify a valid URL.", false);
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
}