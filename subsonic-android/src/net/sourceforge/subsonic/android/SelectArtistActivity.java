package net.sourceforge.subsonic.android;

import java.util.List;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import net.sourceforge.subsonic.android.domain.Artist;
import net.sourceforge.subsonic.android.service.MusicService;
import net.sourceforge.subsonic.android.service.MusicServiceFactory;

public class SelectArtistActivity extends ListActivity implements AdapterView.OnItemClickListener {

    private static final String TAG = SelectArtistActivity.class.getSimpleName();

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            MusicService musicService = MusicServiceFactory.getMusicService();
            List<Artist> artists = musicService.getArtists();
            Log.i(TAG, "Found " + artists.size() + " artists.");
            for (Artist artist : artists) {
                Log.i(TAG, artist.getName() + " - " + artist.getPath());
            }

            setListAdapter(new ArrayAdapter<Artist>(this, android.R.layout.simple_list_item_1, artists));
            getListView().setTextFilterEnabled(true);

            getListView().setOnItemClickListener(this);
        } catch (Exception e) {
            Log.e(TAG, "Failed to parse artists.", e);
        }
    }

    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (position >= 0) {
            Artist artist = (Artist) parent.getItemAtPosition(position);
            Log.d(TAG, artist + " clicked.");
            Intent intent = new Intent(this, SelectAlbumActivity.class);
            intent.putExtra(Constants.NAME_ARTIST, artist.getName());
            intent.putExtra(Constants.NAME_PATH, artist.getPath());
            startActivity(intent);
        }
    }
}