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

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ListView;
import net.sourceforge.subsonic.androidapp.R;
import net.sourceforge.subsonic.androidapp.domain.SearchCritera;
import net.sourceforge.subsonic.androidapp.domain.SearchResult;
import net.sourceforge.subsonic.androidapp.service.MusicServiceFactory;
import net.sourceforge.subsonic.androidapp.util.ArtistAdapter;
import net.sourceforge.subsonic.androidapp.util.BackgroundTask;
import net.sourceforge.subsonic.androidapp.util.Constants;
import net.sourceforge.subsonic.androidapp.util.EntryAdapter;
import net.sourceforge.subsonic.androidapp.util.ImageLoader;
import net.sourceforge.subsonic.androidapp.util.MergeAdapter;
import net.sourceforge.subsonic.androidapp.util.TabActivityBackgroundTask;

/**
 * Performs search and displays the matching artists, albums and songs.
 *
 * @author Sindre Mehus
 */
public class SearchResultActivity extends SubsonicTabActivity {

    private ImageLoader imageLoader;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_result);

        imageLoader = new ImageLoader(this);

        String query = getIntent().getStringExtra(Constants.INTENT_EXTRA_NAME_QUERY);
        if (query != null) {
            search(query);
        }
    }

    private void search(final String query) {
//        setTitle(R.string.select_album_searching);
        BackgroundTask<SearchResult> task = new TabActivityBackgroundTask<SearchResult>(this) {
            @Override
            protected SearchResult doInBackground() throws Throwable {
                SearchCritera criteria = new SearchCritera(query, 10, 10, 30);
                return MusicServiceFactory.getMusicService(SearchResultActivity.this).search(criteria, SearchResultActivity.this, this);
            }

            @Override
            protected void done(SearchResult result) {
//                setTitle(R.string.select_album_0_search_result);
                View buttons = LayoutInflater.from(SearchResultActivity.this).inflate(R.layout.search_result_buttons, null);
                MergeAdapter mergeAdapter = new MergeAdapter();

                if (!result.getArtists().isEmpty()) {
                    mergeAdapter.addView(buttons.findViewById(R.id.search_result_artists));
                    mergeAdapter.addAdapter(new ArtistAdapter(SearchResultActivity.this, result.getArtists()));
                    mergeAdapter.addView(buttons.findViewById(R.id.search_result_more_artists), true);
                }

                if (!result.getAlbums().isEmpty()) {
                    mergeAdapter.addView(buttons.findViewById(R.id.search_result_albums));
                    mergeAdapter.addAdapter(new EntryAdapter(SearchResultActivity.this, imageLoader, result.getAlbums()));
                    mergeAdapter.addView(buttons.findViewById(R.id.search_result_more_albums), true);
                }

                if (!result.getSongs().isEmpty()) {
                    mergeAdapter.addView(buttons.findViewById(R.id.search_result_songs));
                    mergeAdapter.addAdapter(new EntryAdapter(SearchResultActivity.this, imageLoader, result.getSongs()));
                }

                ListView list = (ListView) findViewById(R.id.search_result_list);
                list.setAdapter(mergeAdapter);
//                emptyTextView.setVisibility(result.isEmpty() ? View.VISIBLE : View.GONE);
            }
        };
        task.execute();
    }

}