/*
 This file is part of Subsonic.

 Subsonic is free software: you can redistribute it and/or modify
 it under the terms of the GNU General Public License as published by
 the Free Software Foundation, either version 3 of the License, or
 (at your option) any later version.

 Subsonic is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with Subsonic.  If not, see <http://www.gnu.org/licenses/>.

 Copyright 2009 (C) Sindre Mehus
 */

package net.sourceforge.subsonic.androidapp.activity;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SectionIndexer;
import net.sourceforge.subsonic.androidapp.R;
import net.sourceforge.subsonic.androidapp.domain.Artist;
import net.sourceforge.subsonic.androidapp.domain.Indexes;
import net.sourceforge.subsonic.androidapp.service.MusicService;
import net.sourceforge.subsonic.androidapp.service.MusicServiceFactory;
import net.sourceforge.subsonic.androidapp.util.BackgroundTask;
import net.sourceforge.subsonic.androidapp.util.Constants;
import net.sourceforge.subsonic.androidapp.util.TabActivityBackgroundTask;
import net.sourceforge.subsonic.androidapp.util.Util;

public class SelectArtistActivity extends SubsonicTabActivity implements AdapterView.OnItemClickListener {

    private ListView artistList;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.select_artist);

        artistList = (ListView) findViewById(R.id.select_artist_list);
        artistList.setOnItemClickListener(this);

        if (Util.isOffline(this)) {
            setTitle(R.string.select_artist_offline_music);
        } else {
            setTitle(R.string.select_artist_my_music);
        }

        load();
    }

    private void load() {
        BackgroundTask<Indexes> task = new TabActivityBackgroundTask<Indexes>(this) {
            @Override
            protected Indexes doInBackground() throws Throwable {
                MusicService musicService = MusicServiceFactory.getMusicService(SelectArtistActivity.this);
                return musicService.getIndexes(SelectArtistActivity.this, this);
            }

            @Override
            protected void done(Indexes result) {
                List<Artist> artists = new ArrayList<Artist>(result.getShortcuts().size() + result.getArtists().size());
                artists.addAll(result.getShortcuts());
                artists.addAll(result.getArtists());
                artistList.setAdapter(new ArtistAdapter(artists));
            }
        };
        task.execute();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (position >= 0) {
            Artist artist = (Artist) parent.getItemAtPosition(position);
            Intent intent = new Intent(this, SelectAlbumActivity.class);
            intent.putExtra(Constants.INTENT_EXTRA_NAME_PATH, artist.getId());
            intent.putExtra(Constants.INTENT_EXTRA_NAME_NAME, artist.getName());
            Util.startActivityWithoutTransition(this, intent);
        }
    }

    public class ArtistAdapter extends ArrayAdapter<Artist> implements SectionIndexer {

        // Both arrays are indexed by section ID.
        private final Object[] sections;
        private final Integer[] positions;

        public ArtistAdapter(List<Artist> artists) {
            super(SelectArtistActivity.this, R.layout.artist_list_item, artists);

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