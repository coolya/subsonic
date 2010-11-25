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

import android.content.Intent;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import net.sourceforge.subsonic.androidapp.R;
import net.sourceforge.subsonic.androidapp.domain.Artist;
import net.sourceforge.subsonic.androidapp.domain.Indexes;
import net.sourceforge.subsonic.androidapp.service.MusicService;
import net.sourceforge.subsonic.androidapp.service.MusicServiceFactory;
import net.sourceforge.subsonic.androidapp.util.ArtistAdapter;
import net.sourceforge.subsonic.androidapp.util.BackgroundTask;
import net.sourceforge.subsonic.androidapp.util.Constants;
import net.sourceforge.subsonic.androidapp.util.TabActivityBackgroundTask;
import net.sourceforge.subsonic.androidapp.util.Util;

import java.util.ArrayList;
import java.util.List;

public class SelectArtistActivity extends SubsonicTabActivity implements AdapterView.OnItemClickListener {

    private static final int MENU_ITEM_PLAY_ALL = 1;
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

        View header = LayoutInflater.from(this).inflate(R.layout.select_artist_header, artistList, false);
        artistList.addHeaderView(header);
        registerForContextMenu(artistList);

        setTitle(Util.isOffline(this) ? R.string.music_library_label_offline : R.string.music_library_label);

        load();
    }

    private void refresh() {
        finish();
        Intent intent = new Intent(this, SelectArtistActivity.class);
        intent.putExtra(Constants.INTENT_EXTRA_NAME_REFRESH, true);
        Util.startActivityWithoutTransition(this, intent);
    }

    private void load() {
        BackgroundTask<Indexes> task = new TabActivityBackgroundTask<Indexes>(this) {
            @Override
            protected Indexes doInBackground() throws Throwable {
                boolean refresh = getIntent().getBooleanExtra(Constants.INTENT_EXTRA_NAME_REFRESH, false);
                MusicService musicService = MusicServiceFactory.getMusicService(SelectArtistActivity.this);
                return musicService.getIndexes(refresh, SelectArtistActivity.this, this);
            }

            @Override
            protected void done(Indexes result) {
                List<Artist> artists = new ArrayList<Artist>(result.getShortcuts().size() + result.getArtists().size());
                artists.addAll(result.getShortcuts());
                artists.addAll(result.getArtists());
                artistList.setAdapter(new ArtistAdapter(SelectArtistActivity.this, artists));
            }
        };
        task.execute();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (position == 0) {
            refresh();
        } else {
            Artist artist = (Artist) parent.getItemAtPosition(position);
            Intent intent = new Intent(this, SelectAlbumActivity.class);
            intent.putExtra(Constants.INTENT_EXTRA_NAME_ID, artist.getId());
            intent.putExtra(Constants.INTENT_EXTRA_NAME_NAME, artist.getName());
            Util.startActivityWithoutTransition(this, intent);
        }
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View view, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, view, menuInfo);
        menu.add(Menu.NONE, MENU_ITEM_PLAY_ALL, MENU_ITEM_PLAY_ALL, R.string.select_album_play_all);
    }

    @Override
    public boolean onContextItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case MENU_ITEM_PLAY_ALL:
                playAll(menuItem);
                break;
            default:
                return super.onContextItemSelected(menuItem);
        }
        return true;
    }

    private void playAll(MenuItem menuItem) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuItem.getMenuInfo();
        Artist artist = (Artist) artistList.getItemAtPosition(info.position);
        playAll(artist.getId(), false, false, true);
    }

}