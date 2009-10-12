package net.sourceforge.subsonic.androidapp.activity;

import android.content.Intent;
import android.content.DialogInterface;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.Window;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.EditText;
import android.util.Log;
import android.app.Dialog;
import android.app.AlertDialog;
import net.sourceforge.subsonic.androidapp.R;
import net.sourceforge.subsonic.androidapp.util.Util;
import net.sourceforge.subsonic.androidapp.util.Constants;
import net.sourceforge.subsonic.androidapp.service.DownloadService;
import net.sourceforge.subsonic.androidapp.service.StreamService;
import static net.sourceforge.subsonic.androidapp.service.StreamService.PlayerState.STARTED;

public class MainActivity extends OptionsMenuActivity {

    private static final int DIALOG_ID_SEARCH = 1;

    /**
    * Called when the activity is first created.
    */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

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

        Button searchButton = (Button) findViewById(R.id.main_search);
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialog(DIALOG_ID_SEARCH);
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

        ImageView settingsButton = (ImageView) findViewById(R.id.main_settings);
        settingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, SettingsActivity.class));
            }
        });

        ImageView helpButton = (ImageView) findViewById(R.id.main_help);
        helpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, HelpActivity.class));
            }
        });
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        if (id != DIALOG_ID_SEARCH) {
            return null;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final EditText input = new EditText(this);
        input.setWidth(1000);
        input.setImeOptions(EditorInfo.IME_ACTION_SEARCH);
        builder.setView(input);
        builder.setCancelable(true);
        builder.setPositiveButton("Search", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                String query = String.valueOf(input.getText());
                Intent intent = new Intent(MainActivity.this, SelectAlbumActivity.class);
                intent.putExtra(Constants.INTENT_EXTRA_NAME_QUERY, query);
                startActivity(intent);
            }
        });

        return builder.create();
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_SEARCH) {
            showDialog(DIALOG_ID_SEARCH);
        }
        return super.onKeyDown(keyCode, event);
    }

}