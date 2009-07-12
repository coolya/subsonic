package net.sourceforge.subsonic.android;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import net.sourceforge.subsonic.android.domain.Artist;
import net.sourceforge.subsonic.android.service.MusicService;
import net.sourceforge.subsonic.android.service.MusicServiceFactory;
import net.sourceforge.subsonic.android.util.BackgroundTask;

import java.util.List;

public class SelectArtistActivity extends Activity implements AdapterView.OnItemClickListener {

    private static final String TAG = SelectArtistActivity.class.getSimpleName();

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        BackgroundTask<List<Artist>> task = new BackgroundTask<List<Artist>>(this) {
            @Override
            protected List<Artist> doInBackground() throws Throwable {
                MusicService musicService = MusicServiceFactory.getMusicService();
                return musicService.getArtists(this);
            }

            @Override
            protected void done(List<Artist> result) {
                // TODO: Use xml file
                ListView listView = new ListView(SelectArtistActivity.this);
                listView.setAdapter(new ArrayAdapter<Artist>(SelectArtistActivity.this, android.R.layout.simple_list_item_1, result));
                listView.setTextFilterEnabled(true);
                listView.setOnItemClickListener(SelectArtistActivity.this);
                setContentView(listView);
            }

            @Override
            protected void cancel() {
                MusicServiceFactory.getMusicService().cancel(this);
                finish();
            }
        };
        task.execute();
    }

    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (position >= 0) {
            Artist artist = (Artist) parent.getItemAtPosition(position);
            Log.d(TAG, artist + " clicked.");
            Intent intent = new Intent(this, SelectAlbumActivity.class);
            intent.putExtra(Constants.NAME_PATH, artist.getPath());
            startActivity(intent);
        }
    }
}