package net.sourceforge.subsonic.android.activity;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SectionIndexer;
import net.sourceforge.subsonic.android.domain.Artist;
import net.sourceforge.subsonic.android.service.MusicService;
import net.sourceforge.subsonic.android.service.MusicServiceFactory;
import net.sourceforge.subsonic.android.util.BackgroundTask;
import net.sourceforge.subsonic.android.util.Constants;

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
                return musicService.getArtists(SelectArtistActivity.this, this);
            }

            @Override
            protected void done(List<Artist> result) {
                // TODO: Use xml file
                ListView listView = new ListView(SelectArtistActivity.this);
                listView.setAdapter(new ArtistAdapter(result));
                listView.setTextFilterEnabled(true);
                listView.setOnItemClickListener(SelectArtistActivity.this);
                listView.setFastScrollEnabled(true);
                setContentView(listView);
            }

            @Override
            protected void cancel() {
                MusicServiceFactory.getMusicService().cancel(SelectArtistActivity.this, this);
                finish();
            }
        };
        task.execute();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (position >= 0) {
            Artist artist = (Artist) parent.getItemAtPosition(position);
            Log.d(TAG, artist + " clicked.");
            Intent intent = new Intent(this, SelectAlbumActivity.class);
            intent.putExtra(Constants.NAME_PATH, artist.getPath());
            intent.putExtra(Constants.NAME_NAME, artist.getName());
            startActivity(intent);
        }
    }

    public class ArtistAdapter extends ArrayAdapter<Artist> implements SectionIndexer {

        // Both arrays are indexed by section ID.
        private final Object[] sections;
        private final Integer[] positions;

        public ArtistAdapter(List<Artist> artists) {
            super(SelectArtistActivity.this, android.R.layout.simple_list_item_1, artists);

            Set<String> sectionSet = new LinkedHashSet<String>(30);
            List<Integer> positionList = new ArrayList<Integer>(30);
            for (int i = 0; i < artists.size(); i++) {
                Artist artist = artists.get(i);
                String index = artist.getIndex();
                if (!sectionSet.contains(index)) {
                    sectionSet.add(index);
                    positionList.add(i);
                }
            }
            sections = sectionSet.toArray(new Object[sectionSet.size()]);
            positions = positionList.toArray(new Integer[positionList.size()]);
        }

        @Override
        public Object[] getSections() {
            return sections;
        }

        @Override
        public int getPositionForSection(int section) {
            return positions[section];
        }

        @Override
        public int getSectionForPosition(int pos) {
            for (int i = 0; i < sections.length - 1; i++) {
                if (pos < positions[i + 1]) {
                    return i;
                }
            }
            return sections.length - 1;
        }
    }
}