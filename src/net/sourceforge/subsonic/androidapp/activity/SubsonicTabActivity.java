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
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.IBinder;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;
import net.sourceforge.subsonic.androidapp.R;
import net.sourceforge.subsonic.androidapp.service.DownloadService;
import net.sourceforge.subsonic.androidapp.service.DownloadServiceImpl;
import net.sourceforge.subsonic.androidapp.util.Util;

/**
 * @author Sindre Mehus
 */
public class SubsonicTabActivity extends Activity {

    private boolean destroyed;
    private View homeButton;
    private View musicButton;
    private View searchButton;
    private View playlistButton;
    private View nowPlayingButton;
    private DownloadService downloadService;
    private ServiceConnection downloadServiceConnection;

    @Override
    protected void onCreate(Bundle bundle) {
        applyTheme();
        super.onCreate(bundle);
        downloadServiceConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
                downloadService = DownloadServiceImpl.getInstance();
                onDownloadServiceConnected();
            }

            @Override
            public void onServiceDisconnected(ComponentName componentName) {
            }
        };
        bindService(new Intent(this, DownloadServiceImpl.class), downloadServiceConnection, Context.BIND_AUTO_CREATE);
        downloadService = DownloadServiceImpl.getInstance();
        setVolumeControlStream(AudioManager.STREAM_MUSIC);
    }

    @Override
    protected void onPostCreate(Bundle bundle) {
        super.onPostCreate(bundle);

        homeButton = findViewById(R.id.button_bar_home);
        homeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SubsonicTabActivity.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                Util.startActivityWithoutTransition(SubsonicTabActivity.this, intent);
            }
        });

        musicButton = findViewById(R.id.button_bar_music);
        musicButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SubsonicTabActivity.this, SelectArtistActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                Util.startActivityWithoutTransition(SubsonicTabActivity.this, intent);
            }
        });

        searchButton = findViewById(R.id.button_bar_search);
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SubsonicTabActivity.this, SearchActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                Util.startActivityWithoutTransition(SubsonicTabActivity.this, intent);
            }
        });

        playlistButton = findViewById(R.id.button_bar_playlists);
        playlistButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SubsonicTabActivity.this, SelectPlaylistActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                Util.startActivityWithoutTransition(SubsonicTabActivity.this, intent);
            }
        });

        nowPlayingButton = findViewById(R.id.button_bar_now_playing);
        nowPlayingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Util.startActivityWithoutTransition(SubsonicTabActivity.this, DownloadActivity.class);
            }
        });

        if (this instanceof MainActivity) {
            homeButton.setEnabled(false);
        } else if (this instanceof SelectAlbumActivity || this instanceof SelectArtistActivity) {
            musicButton.setEnabled(false);
        } else if (this instanceof SearchActivity) {
            searchButton.setEnabled(false);
        } else if (this instanceof SelectPlaylistActivity) {
            playlistButton.setEnabled(false);
        } else if (this instanceof DownloadActivity) {
            nowPlayingButton.setEnabled(false);
        }

        updateButtonVisibility();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        destroyed = true;
        unbindService(downloadServiceConnection);
    }

    @Override
    public void finish() {
        super.finish();
        Util.disablePendingTransition(this);
    }

    private void applyTheme() {
        String theme = Util.getTheme(this);
        if ("dark".equals(theme)) {
            setTheme(android.R.style.Theme);
        }
    }

    public boolean isDestroyed() {
        return destroyed;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_SEARCH) {
            Intent intent = new Intent(SubsonicTabActivity.this, SearchActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            Util.startActivityWithoutTransition(SubsonicTabActivity.this, intent);
        }
        return super.onKeyDown(keyCode, event);
    }

    private void updateButtonVisibility() {
        int visibility = Util.isOffline(this) ? View.GONE : View.VISIBLE;
        searchButton.setVisibility(visibility);
        playlistButton.setVisibility(visibility);
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

    /**
     * Overridden by subclasses that are interested in knowing when the
     * DownloadService is available.
     */
    protected void onDownloadServiceConnected() {
    }

    protected DownloadService getDownloadService() {
        return downloadService;
    }

    protected void warnIfNetworkOrStorageUnavailable() {
        if (!Util.isExternalStoragePresent()) {
            Util.toast(this, R.string.select_album_no_sdcard);
        } else if (!Util.isOffline(this) && !Util.isNetworkConnected(this)) {
            Util.toast(this, R.string.select_album_no_network);
        }
    }
}
