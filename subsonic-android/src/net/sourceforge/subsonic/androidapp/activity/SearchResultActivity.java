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
import android.widget.ListAdapter;
import android.widget.AdapterView;
import android.util.Log;
import net.sourceforge.subsonic.androidapp.R;
import net.sourceforge.subsonic.androidapp.domain.SearchCritera;
import net.sourceforge.subsonic.androidapp.domain.SearchResult;
import net.sourceforge.subsonic.androidapp.domain.Artist;
import net.sourceforge.subsonic.androidapp.domain.MusicDirectory;
import net.sourceforge.subsonic.androidapp.service.MusicServiceFactory;
import net.sourceforge.subsonic.androidapp.service.MusicService;
import net.sourceforge.subsonic.androidapp.util.ArtistAdapter;
import net.sourceforge.subsonic.androidapp.util.BackgroundTask;
import net.sourceforge.subsonic.androidapp.util.Constants;
import net.sourceforge.subsonic.androidapp.util.EntryAdapter;
import net.sourceforge.subsonic.androidapp.util.ImageLoader;
import net.sourceforge.subsonic.androidapp.util.MergeAdapter;
import net.sourceforge.subsonic.androidapp.util.TabActivityBackgroundTask;

import java.util.List;
import java.util.ArrayList;

/**
 * Performs search and displays the matching artists, albums and songs.
 *
 * @author Sindre Mehus
 */
public class SearchResultActivity extends SubsonicTabActivity {

    private ImageLoader imageLoader;

    private static final int DEFAULT_ARTISTS = 3;
    private static final int DEFAULT_ALBUMS = 5;
    private static final int DEFAULT_SONGS = 10;

    private static final int MAX_ARTISTS = 10;
    private static final int MAX_ALBUMS = 20;
    private static final int MAX_SONGS = 25;

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

    private void processSearchResult(SearchResult result) {

        View buttons = LayoutInflater.from(this).inflate(R.layout.search_result_buttons, null);
        final View moreArtistsButton = buttons.findViewById(R.id.search_result_more_artists);
        final View moreAlbumsButton = buttons.findViewById(R.id.search_result_more_albums);
        final View moreSongsButton = buttons.findViewById(R.id.search_result_more_songs);

        final MergeAdapter mergeAdapter = new MergeAdapter();

        final List<Artist> artists = result.getArtists();
        if (!artists.isEmpty()) {
            mergeAdapter.addView(buttons.findViewById(R.id.search_result_artists));
            List<Artist> firstArtists = new ArrayList<Artist>(artists.subList(0, Math.min(DEFAULT_ARTISTS, artists.size())));
            final ArtistAdapter artistAdapter = new ArtistAdapter(this, firstArtists);
            mergeAdapter.addAdapter(artistAdapter);
            if (artists.size() > DEFAULT_ARTISTS) {
                final ListAdapter moreAdapter = mergeAdapter.addView(moreArtistsButton, true);
                moreArtistsButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        artistAdapter.clear();
                        for (Artist artist : artists) {
                            artistAdapter.add(artist);
                        }
                        artistAdapter.notifyDataSetChanged();
                        mergeAdapter.removeAdapter(moreAdapter);
                    }
                });
            }
        }

        final List<MusicDirectory.Entry> albums = result.getAlbums();
        if (!albums.isEmpty()) {
            mergeAdapter.addView(buttons.findViewById(R.id.search_result_albums));
            List<MusicDirectory.Entry> firstAlbums = new ArrayList<MusicDirectory.Entry>(albums.subList(0, Math.min(DEFAULT_ALBUMS, albums.size())));
            final EntryAdapter albumAdapter = new EntryAdapter(this, imageLoader, firstAlbums);
            mergeAdapter.addAdapter(albumAdapter);
            if (albums.size() > DEFAULT_ALBUMS) {
                final ListAdapter moreAlbumsAdapter = mergeAdapter.addView(moreAlbumsButton, true);
                moreAlbumsButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        albumAdapter.clear();
                        for (MusicDirectory.Entry album : albums) {
                            albumAdapter.add(album);
                        }
                        albumAdapter.notifyDataSetChanged();
                        mergeAdapter.removeAdapter(moreAlbumsAdapter);
                    }
                });
            }
        }

        final List<MusicDirectory.Entry> songs = result.getSongs();
        if (!songs.isEmpty()) {
            mergeAdapter.addView(buttons.findViewById(R.id.search_result_songs));
            List<MusicDirectory.Entry> firstSongs = new ArrayList<MusicDirectory.Entry>(songs.subList(0, Math.min(DEFAULT_SONGS, songs.size())));
            final EntryAdapter songAdapter = new EntryAdapter(this, imageLoader, firstSongs);
            mergeAdapter.addAdapter(songAdapter);
            if (songs.size() > DEFAULT_SONGS) {
                final ListAdapter moreSongsAdapter = mergeAdapter.addView(moreSongsButton, true);
                moreSongsButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        songAdapter.clear();
                        for (MusicDirectory.Entry song : songs) {
                            songAdapter.add(song);
                        }
                        songAdapter.notifyDataSetChanged();
                        mergeAdapter.removeAdapter(moreSongsAdapter);
                    }
                });
            }
        }

//        if (!result.getAlbums().isEmpty()) {
//            mergeAdapter.addView(buttons.findViewById(R.id.search_result_albums));
//            mergeAdapter.addAdapter(new EntryAdapter(this, imageLoader, result.getAlbums()));
//            mergeAdapter.addView(buttons.findViewById(R.id.search_result_more_albums), true);
//        }
//
//        if (!result.getSongs().isEmpty()) {
//            mergeAdapter.addView(buttons.findViewById(R.id.search_result_songs));
//            mergeAdapter.addAdapter(new EntryAdapter(this, imageLoader, result.getSongs()));
//        }

        ListView list = (ListView) findViewById(R.id.search_result_list);
        list.setAdapter(mergeAdapter);

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (view == moreArtistsButton) {

                }

            }
        });
    }

    private void search(final String query) {
//        setTitle(R.string.select_album_searching);
        BackgroundTask<SearchResult> task = new TabActivityBackgroundTask<SearchResult>(this) {
            @Override
            protected SearchResult doInBackground() throws Throwable {
                SearchCritera criteria = new SearchCritera(query, MAX_ARTISTS, MAX_ALBUMS, MAX_SONGS);
                MusicService service = MusicServiceFactory.getMusicService(SearchResultActivity.this);
                // TODO: Call isLicenseValid to ensure the server's REST version is read.
                service.isLicenseValid(SearchResultActivity.this, this);
                return service.search(criteria, SearchResultActivity.this, this);
            }

            @Override
            protected void done(SearchResult result) {
//                setTitle(R.string.select_album_0_search_result);
                processSearchResult(result);
//                emptyTextView.setVisibility(result.isEmpty() ? View.VISIBLE : View.GONE);
            }
        };
        task.execute();
    }

}