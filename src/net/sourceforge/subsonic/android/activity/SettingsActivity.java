package net.sourceforge.subsonic.android.activity;

import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceScreen;
import android.preference.ListPreference;
import android.content.SharedPreferences;
import android.util.Log;
import net.sourceforge.subsonic.android.R;
import net.sourceforge.subsonic.android.service.MusicService;
import net.sourceforge.subsonic.android.service.MusicServiceFactory;
import net.sourceforge.subsonic.android.util.BackgroundTask;
import net.sourceforge.subsonic.android.util.Constants;
import net.sourceforge.subsonic.android.util.ErrorDialog;
import net.sourceforge.subsonic.android.util.Util;

import java.net.URL;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.LinkedHashMap;

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
        BackgroundTask<Object> task = new BackgroundTask<Object>(this) {
            @Override
            protected Object doInBackground() throws Throwable {
                updateProgress("Testing connection...");
                MusicService musicService = MusicServiceFactory.getMusicService();
                musicService.ping(SettingsActivity.this, this);
                return null;
            }

            @Override
            protected void done(Object result) {
                Util.toast(SettingsActivity.this, "Connection is OK");
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
                }});
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