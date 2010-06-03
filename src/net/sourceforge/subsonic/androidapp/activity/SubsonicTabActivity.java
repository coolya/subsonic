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

import android.app.Activity;
import android.media.AudioManager;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.graphics.Color;
import android.widget.TextView;
import net.sourceforge.subsonic.androidapp.R;
import net.sourceforge.subsonic.androidapp.util.Util;

/**
 * @author Sindre Mehus
 */
public class SubsonicTabActivity extends Activity {

    private static final int SELECTED_TAB_COLOR = Color.rgb(190, 190, 190);
    private boolean destroyed;

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setVolumeControlStream(AudioManager.STREAM_MUSIC);
    }

    @Override
    protected void onPostCreate(Bundle bundle) {
        super.onPostCreate(bundle);

        View homeButton = findViewById(R.id.button_bar_home);
        homeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Util.startActivityWithoutTransition(SubsonicTabActivity.this, MainActivity.class);
            }
        });

        View musicButton = findViewById(R.id.button_bar_music);
        musicButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Util.startActivityWithoutTransition(SubsonicTabActivity.this, SelectArtistActivity.class);
            }
        });

        View searchButton = findViewById(R.id.button_bar_search);
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Util.startActivityWithoutTransition(SubsonicTabActivity.this, SearchActivity.class);
            }
        });

        View playlistButton = findViewById(R.id.button_bar_playlists);
        playlistButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Util.startActivityWithoutTransition(SubsonicTabActivity.this, SelectPlaylistActivity.class);
            }
        });

        View nowPlayingButton = findViewById(R.id.button_bar_now_playing);
        nowPlayingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Util.startActivityWithoutTransition(SubsonicTabActivity.this, DownloadActivity.class);
            }
        });

        if (this instanceof MainActivity) {
            homeButton.setBackgroundColor(SELECTED_TAB_COLOR);
        } else if (this instanceof SelectAlbumActivity || this instanceof SelectArtistActivity) {
            musicButton.setBackgroundColor(SELECTED_TAB_COLOR);
        } else if (this instanceof SearchActivity) {
            searchButton.setBackgroundColor(SELECTED_TAB_COLOR);
        } else if (this instanceof SelectPlaylistActivity) {
            playlistButton.setBackgroundColor(SELECTED_TAB_COLOR);
        } else if (this instanceof DownloadActivity) {
            nowPlayingButton.setBackgroundColor(SELECTED_TAB_COLOR);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        destroyed = true;
    }

    public boolean isDestroyed() {
        return destroyed;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_SEARCH) {
            Util.startActivityWithoutTransition(SubsonicTabActivity.this, SearchActivity.class);
        }
        return super.onKeyDown(keyCode, event);
    }

    public void setProgressVisible(boolean visible) {
        View view = findViewById(R.id.tab_progress);
        if (view != null) {
            view.setVisibility(visible ? View.VISIBLE : View.GONE);
        }
    }

    public void updateProgress(String message) {
        TextView view = (TextView) findViewById(R.id.tab_progress_message);
        if (view != null) {
            view.setText(message);
        }
    }
}
