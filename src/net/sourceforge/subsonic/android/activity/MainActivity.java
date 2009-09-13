package net.sourceforge.subsonic.android.activity;

import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import net.sourceforge.subsonic.android.R;
import net.sourceforge.subsonic.android.service.DownloadService;
import net.sourceforge.subsonic.android.service.StreamService;

public class MainActivity extends OptionsMenuActivity {

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        PreferenceManager.setDefaultValues(this, R.xml.settings, false);

        startService(new Intent(this, DownloadService.class));
        startService(new Intent(this, StreamService.class));
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

        Button downloadQueueButton = (Button) findViewById(R.id.main_download_queue);
        downloadQueueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, DownloadQueueActivity.class));
            }
        });

        Button streamQueueButton = (Button) findViewById(R.id.main_stream_queue);
        streamQueueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, StreamQueueActivity.class));
            }
        });

        Button helpButton = (Button) findViewById(R.id.main_help);
        helpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, HelpActivity.class));
            }
        });
    }
}