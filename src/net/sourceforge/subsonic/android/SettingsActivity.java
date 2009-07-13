package net.sourceforge.subsonic.android;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.preference.CheckBoxPreference;
import android.preference.PreferenceActivity;
import android.preference.ListPreference;
import android.preference.EditTextPreference;
import net.sourceforge.subsonic.android.domain.Artist;
import net.sourceforge.subsonic.android.service.MusicService;
import net.sourceforge.subsonic.android.service.MusicServiceFactory;
import net.sourceforge.subsonic.android.util.BackgroundTask;

import java.util.List;

public class SettingsActivity extends PreferenceActivity {

    private static final String TAG = SettingsActivity.class.getSimpleName();

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.settings);
        SharedPreferences.Editor editor = getPreferenceScreen().getSharedPreferences().edit();
        editor.putString("serverUrl", "http://gosubsonic.com");
        editor.commit();

        EditTextPreference serverUrl = (EditTextPreference) findPreference("serverUrl");
        serverUrl.setSummary(serverUrl.getText());

//        setContentView(R.layout.settings);
//
//        ListView settingsList = (ListView) findViewById(R.id.settings);
//        settingsList.
//        settingsList.setAdapter(new ArrayAdapter<Artist>(SelectArtistActivity.this, android.R.layout.simple_list_item_1, result));
    }
}