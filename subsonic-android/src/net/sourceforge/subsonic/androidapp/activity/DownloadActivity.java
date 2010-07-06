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

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import android.os.Bundle;
import android.os.Handler;
import android.view.ContextMenu;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.ViewFlipper;
import net.sourceforge.subsonic.androidapp.R;
import net.sourceforge.subsonic.androidapp.domain.MusicDirectory;
import net.sourceforge.subsonic.androidapp.domain.PlayerState;
import net.sourceforge.subsonic.androidapp.service.DownloadFile;
import net.sourceforge.subsonic.androidapp.service.DownloadService;
import net.sourceforge.subsonic.androidapp.service.DownloadServiceImpl;
import net.sourceforge.subsonic.androidapp.util.Constants;
import net.sourceforge.subsonic.androidapp.util.HorizontalSlider;
import net.sourceforge.subsonic.androidapp.util.ImageLoader;
import net.sourceforge.subsonic.androidapp.util.SongView;
import net.sourceforge.subsonic.androidapp.util.Util;

import static net.sourceforge.subsonic.androidapp.domain.PlayerState.*;

public class DownloadActivity extends SubsonicTabActivity {

    private static final int MENU_ITEM_REMOVE = 100;
    private static final int MENU_ITEM_REMOVE_ALL = 101;

    private ImageLoader imageLoader;
    private DownloadService downloadService;

    private ViewFlipper flipper;
    private TextView emptyTextView;
    private TextView albumArtTextView;
    private ImageView albumArtImageView;
    private ListView playlistView;
    private TextView positionTextView;
    private TextView durationTextView;
    private TextView statusTextView;
    private HorizontalSlider progressBar;
    private View previousButton;
    private View nextButton;
    private View pauseButton;
    private View stopButton;
    private View startButton;
    private View toggleListButton;
    private ScheduledExecutorService executorService;
    private DownloadFile currentPlaying;
    private long currentRevision;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.download);

        flipper = (ViewFlipper) findViewById(R.id.download_flipper);
        emptyTextView = (TextView) findViewById(R.id.download_empty);
        albumArtTextView = (TextView) findViewById(R.id.download_album_art_text);
        albumArtImageView = (ImageView) findViewById(R.id.download_album_art_image);
        positionTextView = (TextView) findViewById(R.id.download_position);
        durationTextView = (TextView) findViewById(R.id.download_duration);
        statusTextView = (TextView) findViewById(R.id.download_status);
        progressBar = (HorizontalSlider) findViewById(R.id.download_progress_bar);
        playlistView = (ListView) findViewById(R.id.download_list);
        previousButton = findViewById(R.id.download_previous);
        nextButton = findViewById(R.id.download_next);
        pauseButton = findViewById(R.id.download_pause);
        stopButton = findViewById(R.id.download_stop);
        startButton = findViewById(R.id.download_start);
        toggleListButton = findViewById(R.id.download_toggle_list);

        albumArtImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggleFullscreenAlbumArt();
            }
        });

        previousButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                warnIfNetworkOrStorageUnavailable();
                downloadService.previous();
            }
        });

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                warnIfNetworkOrStorageUnavailable();
                downloadService.next();
            }
        });

        pauseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                downloadService.pause();
            }
        });

        stopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                downloadService.reset();
            }
        });

        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                warnIfNetworkOrStorageUnavailable();
                start();
            }
        });

        toggleListButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggleFullscreenAlbumArt();
            }
        });

        progressBar.setOnSliderChangeListener(new HorizontalSlider.OnSliderChangeListener() {
            @Override
            public void onSliderChanged(View view, int position) {
                downloadService.seekTo(position);
            }
        });
        playlistView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                warnIfNetworkOrStorageUnavailable();
                downloadService.play(position);
            }
        });

        registerForContextMenu(playlistView);
        imageLoader = new ImageLoader(this);
        downloadService = DownloadServiceImpl.getInstance();

        if (getIntent().getBooleanExtra(Constants.INTENT_EXTRA_NAME_SHUFFLE, false)) {
            warnIfNetworkOrStorageUnavailable();
            downloadService.setShufflePlayEnabled(true);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        onDownloadListChanged();
        onCurrentChanged();
        onProgressChanged();

        final Handler handler = new Handler();
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        update();
                    }
                });
            }
        };

        executorService = Executors.newSingleThreadScheduledExecutor();
        executorService.scheduleWithFixedDelay(runnable, 0L, 1000L, TimeUnit.MILLISECONDS);
    }

    @Override
    protected void onPause() {
        super.onPause();
        executorService.shutdown();
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View view, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, view, menuInfo);
        if (view == playlistView) {
            menu.add(Menu.NONE, MENU_ITEM_REMOVE, MENU_ITEM_REMOVE, R.string.download_remove);
            menu.add(Menu.NONE, MENU_ITEM_REMOVE_ALL, MENU_ITEM_REMOVE_ALL, R.string.download_remove_all);
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case MENU_ITEM_REMOVE:
                AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuItem.getMenuInfo();
                DownloadFile downloadFile = (DownloadFile) playlistView.getItemAtPosition(info.position);
                downloadService.remove(downloadFile);
                onDownloadListChanged();
                break;
            case MENU_ITEM_REMOVE_ALL:
                downloadService.setShufflePlayEnabled(false);
                downloadService.clear();
                onDownloadListChanged();
                break;
            default:
                return super.onContextItemSelected(menuItem);
        }
        return true;
    }

    private void update() {

        if (currentRevision != downloadService.getDownloadListUpdateRevision()) {
            onDownloadListChanged();
        }

        if (currentPlaying != downloadService.getCurrentPlaying()) {
            onCurrentChanged();
        }

        onProgressChanged();
    }

    private void toggleFullscreenAlbumArt() {
        if (flipper.getDisplayedChild() == 1) {
            flipper.setInAnimation(AnimationUtils.loadAnimation(this, R.anim.push_down_in));
            flipper.setOutAnimation(AnimationUtils.loadAnimation(this, R.anim.push_down_out));
            flipper.setDisplayedChild(0);
        } else {
            flipper.setInAnimation(AnimationUtils.loadAnimation(this, R.anim.push_up_in));
            flipper.setOutAnimation(AnimationUtils.loadAnimation(this, R.anim.push_up_out));
            flipper.setDisplayedChild(1);
        }
    }

    private void start() {
        PlayerState state = downloadService.getPlayerState();
        if (state == PAUSED || state == COMPLETED) {
            downloadService.start();
        } else if (state == STOPPED || state == IDLE) {
            warnIfNetworkOrStorageUnavailable();
            downloadService.play(downloadService.getCurrentPlaying());
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE:
                if (downloadService.getPlayerState() == STARTED) {
                    downloadService.pause();
                } else {
                    start();
                }
                break;
            case KeyEvent.KEYCODE_MEDIA_PREVIOUS:
                downloadService.previous();
                break;
            case KeyEvent.KEYCODE_MEDIA_NEXT:
                downloadService.next();
                break;
            default:
        }
        return super.onKeyDown(keyCode, event);
    }

    private void onDownloadListChanged() {
        List<DownloadFile> list = downloadService.getDownloads();

        playlistView.setAdapter(new SongListAdapter(list));
        emptyTextView.setVisibility(list.isEmpty() ? View.VISIBLE : View.GONE);
        currentRevision = downloadService.getDownloadListUpdateRevision();
    }

    private void onCurrentChanged() {
        currentPlaying = downloadService.getCurrentPlaying();
        if (currentPlaying != null) {
            MusicDirectory.Entry song = currentPlaying.getSong();
            albumArtTextView.setText(song.getTitle() + " - " + song.getArtist());
            imageLoader.loadImage(albumArtImageView, song, true);
        } else {
            albumArtTextView.setText(null);
            imageLoader.loadImage(albumArtImageView, (String) null, true);
        } 
    }

    private void onProgressChanged() {
        if (currentPlaying != null) {

            int millisPlayed = Math.max(0, downloadService.getPlayerPosition());
            Integer duration = downloadService.getPlayerDuration();
            int millisTotal = duration == null ? 0 : duration;

            positionTextView.setText(Util.formatDuration(millisPlayed / 1000));
            durationTextView.setText(Util.formatDuration(millisTotal / 1000));
            progressBar.setMax(millisTotal == 0 ? 100 : millisTotal); // Work-around for apparent bug.
            progressBar.setProgress(millisPlayed);
            progressBar.setSlidingEnabled(currentPlaying.isCompleteFileAvailable());
        } else {
            positionTextView.setText("0:00");
            durationTextView.setText("-:--");
            progressBar.setProgress(0);
            progressBar.setSlidingEnabled(false);
        } 

        PlayerState playerState = downloadService.getPlayerState();

        switch (playerState) {
            case DOWNLOADING:
                long bytes = currentPlaying.getPartialFile().length();
                statusTextView.setText(getResources().getString(R.string.download_playerstate_downloading, Util.formatBytes(bytes)));
                break;
            case PREPARING:
                statusTextView.setText(R.string.download_playerstate_buffering);
                break;
            case STARTED:
                statusTextView.setText(downloadService.isShufflePlayEnabled() ? R.string.download_playerstate_playing_shuffle : R.string.download_playerstate_playing);
                break;
            case PAUSED:
                statusTextView.setText(R.string.download_playerstate_paused);
                break;
            default:
                statusTextView.setText(null);
                break;
        }

        switch (playerState) {
            case STARTED:
                pauseButton.setVisibility(View.VISIBLE);
                stopButton.setVisibility(View.GONE);
                startButton.setVisibility(View.GONE);
                break;
            case DOWNLOADING:
            case PREPARING:
                pauseButton.setVisibility(View.GONE);
                stopButton.setVisibility(View.VISIBLE);
                startButton.setVisibility(View.GONE);
                break;
            default:
                pauseButton.setVisibility(View.GONE);
                stopButton.setVisibility(View.GONE);
                startButton.setVisibility(View.VISIBLE);
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        imageLoader.cancel();
    }

    private class SongListAdapter extends ArrayAdapter<DownloadFile> {
        public SongListAdapter(List<DownloadFile> entries) {
            super(DownloadActivity.this, android.R.layout.simple_list_item_1, entries);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            SongView view;
            if (convertView != null && convertView instanceof SongView) {
                view = (SongView) convertView;
            } else {
                view = new SongView(DownloadActivity.this);
            }
            DownloadFile downloadFile = getItem(position);
            view.setDownloadFile(downloadFile, downloadService, false);
            return view;
        }
    }
}