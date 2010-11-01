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
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ListAdapter;
import net.sourceforge.subsonic.androidapp.R;
import net.sourceforge.subsonic.androidapp.domain.Artist;
import net.sourceforge.subsonic.androidapp.domain.MusicDirectory;
import net.sourceforge.subsonic.androidapp.domain.SearchCritera;
import net.sourceforge.subsonic.androidapp.domain.SearchResult;
import net.sourceforge.subsonic.androidapp.service.MusicService;
import net.sourceforge.subsonic.androidapp.service.MusicServiceFactory;
import net.sourceforge.subsonic.androidapp.util.ArtistAdapter;
import net.sourceforge.subsonic.androidapp.util.BackgroundTask;
import net.sourceforge.subsonic.androidapp.util.Constants;
import net.sourceforge.subsonic.androidapp.util.EntryAdapter;
import net.sourceforge.subsonic.androidapp.util.ImageLoader;
import net.sourceforge.subsonic.androidapp.util.MergeAdapter;
import net.sourceforge.subsonic.androidapp.util.TabActivityBackgroundTask;

import java.util.ArrayList;
import java.util.List;

/**
 * Performs searches and displays the matching artists, albums and songs.
 *
 * @author Sindre Mehus
 */
public class SearchActivity extends SubsonicTabActivity {

    private ImageLoader imageLoader;

    private static final int DEFAULT_ARTISTS = 3;
    private static final int DEFAULT_ALBUMS = 5;
    private static final int DEFAULT_SONGS = 10;

    private static final int MAX_ARTISTS = 10;
    private static final int MAX_ALBUMS = 20;
    private static final int MAX_SONGS = 25;
    private ListView list;

    private View artistsHeading;
    private View albumsHeading;
    private View songsHeading;
    private View searchButton;
    private View moreArtistsButton;
    private View moreAlbumsButton;
    private View moreSongsButton;
    private SearchResult searchResult;
    private MergeAdapter mergeAdapter;
    private ArtistAdapter artistAdapter;
    private ListAdapter moreArtistsAdapter;
    private EntryAdapter albumAdapter;
    private ListAdapter moreAlbumsAdapter;
    private ListAdapter moreSongsAdapter;
    private EntryAdapter songAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search);

        imageLoader = new ImageLoader(this);

        View buttons = LayoutInflater.from(this).inflate(R.layout.search_buttons, null);

        artistsHeading = buttons.findViewById(R.id.search_artists);
        albumsHeading = buttons.findViewById(R.id.search_albums);
        songsHeading = buttons.findViewById(R.id.search_songs);

        searchButton = buttons.findViewById(R.id.search_search);
        moreArtistsButton = buttons.findViewById(R.id.search_more_artists);
        moreAlbumsButton = buttons.findViewById(R.id.search_more_albums);
        moreSongsButton = buttons.findViewById(R.id.search_more_songs);

        mergeAdapter = new MergeAdapter();
        list = (ListView) findViewById(R.id.search_list);

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (view == moreArtistsButton) {
                    expandArtists();
                } else if (view == moreAlbumsButton) {
                    expandAlbums();
                } else if (view == moreSongsButton) {
                    expandSongs();
                }
            }
        });

        String query = getIntent().getStringExtra(Constants.INTENT_EXTRA_NAME_QUERY);
        if (query != null) {
            search(query);
        } else {
            mergeAdapter.addView(searchButton, true);
            list.setAdapter(mergeAdapter);
            onSearchRequested();
        }
    }

    private void search(final String query) {
        BackgroundTask<SearchResult> task = new TabActivityBackgroundTask<SearchResult>(this) {
            @Override
            protected SearchResult doInBackground() throws Throwable {
                SearchCritera criteria = new SearchCritera(query, MAX_ARTISTS, MAX_ALBUMS, MAX_SONGS);
                MusicService service = MusicServiceFactory.getMusicService(SearchActivity.this);
                // TODO: Call isLicenseValid to ensure the server's REST version is read.
                service.isLicenseValid(SearchActivity.this, this);
                return service.search(criteria, SearchActivity.this, this);
            }

            @Override
            protected void done(SearchResult result) {
                searchResult = result;
                populateList();
            }
        };
        task.execute();
    }

    private void populateList() {
        mergeAdapter.addView(searchButton, true);
        List<Artist> artists = searchResult.getArtists();
        if (!artists.isEmpty()) {
            mergeAdapter.addView(artistsHeading);
            List<Artist> displayedArtists = new ArrayList<Artist>(artists.subList(0, Math.min(DEFAULT_ARTISTS, artists.size())));
            artistAdapter = new ArtistAdapter(this, displayedArtists);
            mergeAdapter.addAdapter(artistAdapter);
            if (artists.size() > DEFAULT_ARTISTS) {
                moreArtistsAdapter = mergeAdapter.addView(moreArtistsButton, true);
            }
        }

        List<MusicDirectory.Entry> albums = searchResult.getAlbums();
        if (!albums.isEmpty()) {
            mergeAdapter.addView(albumsHeading);
            List<MusicDirectory.Entry> displayedAlbums = new ArrayList<MusicDirectory.Entry>(albums.subList(0, Math.min(DEFAULT_ALBUMS, albums.size())));
            albumAdapter = new EntryAdapter(this, imageLoader, displayedAlbums);
            mergeAdapter.addAdapter(albumAdapter);
            if (albums.size() > DEFAULT_ALBUMS) {
                moreAlbumsAdapter = mergeAdapter.addView(moreAlbumsButton, true);
            }
        }

        List<MusicDirectory.Entry> songs = searchResult.getSongs();
        if (!songs.isEmpty()) {
            mergeAdapter.addView(songsHeading);
            List<MusicDirectory.Entry> displayedSongs = new ArrayList<MusicDirectory.Entry>(songs.subList(0, Math.min(DEFAULT_SONGS, songs.size())));
            songAdapter = new EntryAdapter(this, imageLoader, displayedSongs);
            mergeAdapter.addAdapter(songAdapter);
            if (songs.size() > DEFAULT_SONGS) {
                moreSongsAdapter = mergeAdapter.addView(moreSongsButton, true);
            }
        }

        list.setAdapter(mergeAdapter);
    }

    private void expandArtists() {
        artistAdapter.clear();
        for (Artist artist : searchResult.getArtists()) {
            artistAdapter.add(artist);
        }
        artistAdapter.notifyDataSetChanged();
        mergeAdapter.removeAdapter(moreArtistsAdapter);
        mergeAdapter.notifyDataSetChanged();
    }

    private void expandAlbums() {
        albumAdapter.clear();
        for (MusicDirectory.Entry album : searchResult.getAlbums()) {
            albumAdapter.add(album);
        }
        albumAdapter.notifyDataSetChanged();
        mergeAdapter.removeAdapter(moreAlbumsAdapter);
        mergeAdapter.notifyDataSetChanged();
    }

    private void expandSongs() {
        songAdapter.clear();
        for (MusicDirectory.Entry song : searchResult.getSongs()) {
            songAdapter.add(song);
        }
        songAdapter.notifyDataSetChanged();
        mergeAdapter.removeAdapter(moreSongsAdapter);
        mergeAdapter.notifyDataSetChanged();
    }
}