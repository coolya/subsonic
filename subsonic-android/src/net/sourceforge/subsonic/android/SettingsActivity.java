package net.sourceforge.subsonic.android;

import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;

public class SettingsActivity extends PreferenceActivity implements Preference.OnPreferenceChangeListener {

    private static final String TAG = SettingsActivity.class.getSimpleName();

    private EditTextPreference serverUrl;
    private EditTextPreference username;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.settings);
        serverUrl = (EditTextPreference) findPreference(Constants.SETTINGS_KEY_SERVER_URL);
        username = (EditTextPreference) findPreference(Constants.SETTINGS_KEY_USERNAME);

        serverUrl.setSummary(serverUrl.getText());
        username.setSummary(username.getText());

        serverUrl.setOnPreferenceChangeListener(this);
        username.setOnPreferenceChangeListener(this);
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        String key = preference.getKey();
        if (Constants.SETTINGS_KEY_SERVER_URL.equals(key)) {
            serverUrl.setSummary((CharSequence) newValue);
        } else if (Constants.SETTINGS_KEY_USERNAME.equals(key)) {
            username.setSummary((CharSequence) newValue);
        }
        return true;
    }
}