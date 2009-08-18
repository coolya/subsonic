package net.sourceforge.subsonic.android.activity;

import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import net.sourceforge.subsonic.android.util.Constants;
import net.sourceforge.subsonic.android.util.ErrorDialog;
import net.sourceforge.subsonic.android.R;

import java.net.URL;

public class SettingsActivity extends PreferenceActivity implements Preference.OnPreferenceChangeListener {

    private static final String TAG = SettingsActivity.class.getSimpleName();

    private EditTextPreference serverUrl;
    private EditTextPreference username;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.settings);
        serverUrl = (EditTextPreference) findPreference(Constants.PREFERENCES_KEY_SERVER_URL);
        username = (EditTextPreference) findPreference(Constants.PREFERENCES_KEY_USERNAME);

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
}