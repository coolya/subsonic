package net.sourceforge.subsonic.androidapp.activity;

import java.util.List;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.Window;
import net.sourceforge.subsonic.androidapp.R;
import net.sourceforge.subsonic.androidapp.service.DownloadServiceImpl;
import net.sourceforge.subsonic.androidapp.service.MusicServiceFactory;
import net.sourceforge.subsonic.androidapp.util.BackgroundTask;
import net.sourceforge.subsonic.androidapp.util.Constants;
import net.sourceforge.subsonic.androidapp.util.Pair;

public class MainActivity extends OptionsMenuActivity {

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        PreferenceManager.setDefaultValues(this, R.xml.settings, false);

        startService(new Intent(this, DownloadServiceImpl.class));
        setContentView(R.layout.main);

        View browseButton = findViewById(R.id.main_browse);
        browseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, SelectArtistActivity.class));
            }
        });

        final View searchButton = findViewById(R.id.main_search);
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showSearchDialog();
            }
        });

        final View loadPlaylistButton = findViewById(R.id.main_load_playlist);
        loadPlaylistButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPlaylistDialog();
            }
        });

        View nowPlayingButton = findViewById(R.id.main_now_playing);
        nowPlayingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, DownloadActivity.class));
            }
        });

        View settingsButton = findViewById(R.id.main_settings);
        settingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, SettingsActivity.class));
            }
        });

        View helpButton = findViewById(R.id.main_help);
        helpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, HelpActivity.class));
            }
        });
    }

    private void showPlaylistDialog() {
        new BackgroundTask<List<Pair<String, String>>>(this) {
            @Override
            protected List<Pair<String, String>> doInBackground() throws Throwable {
                return MusicServiceFactory.getMusicService().getPlaylists(MainActivity.this, this);
            }

            @Override
            protected void cancel() {
                // Do nothing.
            }

            @Override
            protected void done(final List<Pair<String, String>> result) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("Select playlist");
                builder.setCancelable(true);

                if (result.isEmpty()) {
                    builder.setMessage("No saved playlists on server.");
                } else {
                    final CharSequence[] items = new CharSequence[result.size()];
                    for (int i = 0; i < items.length; i++) {
                        items[i] = result.get(i).getSecond();
                    }
                    builder.setItems(items, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int button) {
                            dialog.dismiss();
                            Intent intent = new Intent(MainActivity.this, SelectAlbumActivity.class);
                            intent.putExtra(Constants.INTENT_EXTRA_NAME_PLAYLIST_ID, result.get(button).getFirst());
                            intent.putExtra(Constants.INTENT_EXTRA_NAME_PLAYLIST_NAME, result.get(button).getSecond());
                            startActivity(intent);
                        }
                    });
                }
                builder.show();
            }
        }.execute();
    }
}