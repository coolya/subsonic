package net.sourceforge.subsonic.android;

import java.util.List;

import android.app.ListActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import net.sourceforge.subsonic.android.domain.Artist;
import net.sourceforge.subsonic.android.domain.MusicDirectory;
import net.sourceforge.subsonic.android.service.MusicService;
import net.sourceforge.subsonic.android.service.MusicServiceFactory;

public class SelectAlbumActivity extends ListActivity {

    private static final String TAG = SelectAlbumActivity.class.getSimpleName();

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        try {
            MusicService musicService = MusicServiceFactory.getMusicService();

            String artist = getIntent().getStringExtra(Constants.NAME_ARTIST);
            String path = getIntent().getStringExtra(Constants.NAME_PATH);
            Log.i(TAG, "Getting albums for artist " + artist);


            MusicDirectory dir = musicService.getMusicDirectory(path);
            List<MusicDirectory.Entry> children = dir.getChildren(true);

            Log.i(TAG, "Found " + children.size() + " albums.");

            setListAdapter(new ArrayAdapter<MusicDirectory.Entry>(this, android.R.layout.simple_list_item_1, children));
            getListView().setTextFilterEnabled(true);

        } catch (Exception e) {
            Log.e(TAG, "Failed to parse artists.", e);
        }
    }
}