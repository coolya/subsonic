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
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import net.sourceforge.subsonic.androidapp.R;
import net.sourceforge.subsonic.androidapp.domain.Playlist;
import net.sourceforge.subsonic.androidapp.service.MusicServiceFactory;
import net.sourceforge.subsonic.androidapp.util.BackgroundTask;
import net.sourceforge.subsonic.androidapp.util.Constants;
import net.sourceforge.subsonic.androidapp.util.TabActivityBackgroundTask;
import net.sourceforge.subsonic.androidapp.util.Util;

import java.util.List;

public class SelectPlaylistActivity extends SubsonicTabActivity implements AdapterView.OnItemClickListener {

    private ListView list;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.select_playlist);
        list = (ListView) findViewById(R.id.select_playlist_list);
        list.setOnItemClickListener(this);
        load();
    }

    private void load() {
        BackgroundTask<List<Playlist>> task = new TabActivityBackgroundTask<List<Playlist>>(this) {
            @Override
            protected List<Playlist> doInBackground() throws Throwable {
                return MusicServiceFactory.getMusicService(SelectPlaylistActivity.this).getPlaylists(SelectPlaylistActivity.this, this);
            }

            @Override
            protected void done(List<Playlist> result) {
                if (result.isEmpty()) {
//                    todo
//                    builder.setMessage(R.string.main_no_playlists);
                } else {
                    list.setAdapter(new PlaylistAdapter(result));
                }
            }
        };
        task.execute();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        Playlist playlist = (Playlist) parent.getItemAtPosition(position);

        Intent intent = new Intent(SelectPlaylistActivity.this, SelectAlbumActivity.class);
        intent.putExtra(Constants.INTENT_EXTRA_NAME_PLAYLIST_ID, playlist.getId());
        intent.putExtra(Constants.INTENT_EXTRA_NAME_PLAYLIST_NAME, playlist.getName());
        Util.startActivityWithoutTransition(SelectPlaylistActivity.this, intent);
    }

    private class PlaylistAdapter extends ArrayAdapter<Playlist> {
        public PlaylistAdapter(List<Playlist> playlists) {
            super(SelectPlaylistActivity.this, R.layout.playlist_list_item, playlists);
        }
    }
}