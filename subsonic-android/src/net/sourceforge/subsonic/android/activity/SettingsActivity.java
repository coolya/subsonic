package net.sourceforge.subsonic.android.activity;

import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceScreen;
import net.sourceforge.subsonic.android.R;
import net.sourceforge.subsonic.android.service.MusicService;
import net.sourceforge.subsonic.android.service.MusicServiceFactory;
import net.sourceforge.subsonic.android.util.BackgroundTask;
import net.sourceforge.subsonic.android.util.Constants;
import net.sourceforge.subsonic.android.util.ErrorDialog;
import net.sourceforge.subsonic.android.util.Util;

import java.net.URL;

public class SettingsActivity extends PreferenceActivity implements Preference.OnPreferenceChangeListener {

    private static final String TAG = SettingsActivity.class.getSimpleName();

    private EditTextPreference serverUrl;
    private EditTextPreference username;
    private Preference testConnection;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.settings);
        serverUrl = (EditTextPreference) findPreference(Constants.PREFERENCES_KEY_SERVER_URL);
        username = (EditTextPreference) findPreference(Constants.PREFERENCES_KEY_USERNAME);
        testConnection = findPreference("test_connection");

        serverUrl.setSummary(serverUrl.getText());
        username.setSummary(username.getText());

        serverUrl.setOnPreferenceChangeListener(this);
        username.setOnPreferenceChangeListener(this);
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        String key = preference.getKey();
        String value = (String) newValue;
        if (Constants.PREFERENCES_KEY_SERVER_URL.equals(key)) {
            try {
                new URL(value);
            } catch (Exception x) {
                new ErrorDialog(this, "Please specify a valid URL.", false);
                return false;
            }
            serverUrl.setSummary(value);
        } else if (Constants.PREFERENCES_KEY_USERNAME.equals(key)) {
            username.setSummary(value);
        }
        return true;
    }

    @Override
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
        super.onPreferenceTreeClick(preferenceScreen, preference);
        if (preference == testConnection) {
            testConnection();
        }
        return false;
    }

    private void testConnection() {
        BackgroundTask<Object> task = new BackgroundTask<Object>(this) {
            @Override
            protected Object doInBackground() throws Throwable {
                this.updateProgress("Testing connection...");
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
                new ErrorDialog(SettingsActivity.this, "Connection failure. " + getErrorMessage(error), false);
            }
        };
        task.execute();
    }
}