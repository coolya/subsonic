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
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import net.sourceforge.subsonic.androidapp.R;
import net.sourceforge.subsonic.androidapp.util.Util;

/**
 * @author Sindre Mehus
 */
public class SubsonicTabActivity extends Activity {

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
    }

    @Override
    protected void onPostCreate(Bundle bundle) {
        super.onPostCreate(bundle);

        findViewById(R.id.button_bar_home).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Util.startActivityWithoutTransition(SubsonicTabActivity.this, MainActivity.class);
            }
        });
        findViewById(R.id.button_bar_music).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Util.startActivityWithoutTransition(SubsonicTabActivity.this, SelectArtistActivity.class);
            }
        });
        findViewById(R.id.button_bar_search).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Util.startActivityWithoutTransition(SubsonicTabActivity.this, SearchActivity.class);
            }
        });
        findViewById(R.id.button_bar_playlists).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Util.startActivityWithoutTransition(SubsonicTabActivity.this, SelectPlaylistActivity.class);
            }
        });
        findViewById(R.id.button_bar_now_playing).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Util.startActivityWithoutTransition(SubsonicTabActivity.this, DownloadActivity.class);
            }
        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_SEARCH) {
            Util.startActivityWithoutTransition(SubsonicTabActivity.this, SearchActivity.class);
        }
        return super.onKeyDown(keyCode, event);
    }
}
