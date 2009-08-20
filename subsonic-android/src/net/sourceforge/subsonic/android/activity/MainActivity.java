package net.sourceforge.subsonic.android.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.preference.PreferenceManager;
import net.sourceforge.subsonic.android.service.DownloadService;
import net.sourceforge.subsonic.android.R;

public class MainActivity extends OptionsMenuActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        PreferenceManager.setDefaultValues(this, R.xml.settings, false);

        startService(new Intent(this, DownloadService.class));
        setContentView(R.layout.main);

        Button browseButton = (Button) findViewById(R.id.main_browse);
        browseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, SelectArtistActivity.class));
            }
        });

        Button settingsButton = (Button) findViewById(R.id.main_settings);
        settingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, SettingsActivity.class));
            }
        });

        Button queueButton = (Button) findViewById(R.id.main_queue);
        queueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, DownloadQueueActivity.class));
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        // TODO: Stop service or leave it running? Must avoid thread leakage in DownloadService.
        Log.i(TAG, "Stopping service.");
        stopService(new Intent(this, DownloadService.class));
    }
}