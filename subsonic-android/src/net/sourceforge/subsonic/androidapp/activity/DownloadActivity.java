package net.sourceforge.subsonic.androidapp.activity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.view.KeyEvent;
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
import static net.sourceforge.subsonic.androidapp.domain.PlayerState.*;
import net.sourceforge.subsonic.androidapp.service.DownloadFile;
import net.sourceforge.subsonic.androidapp.service.DownloadService;
import net.sourceforge.subsonic.androidapp.service.DownloadServiceImpl;
import net.sourceforge.subsonic.androidapp.util.HorizontalSlider;
import net.sourceforge.subsonic.androidapp.util.ImageLoader;
import net.sourceforge.subsonic.androidapp.util.SimpleServiceBinder;
import net.sourceforge.subsonic.androidapp.util.SongView;
import net.sourceforge.subsonic.androidapp.util.Util;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class DownloadActivity extends OptionsMenuActivity {

    private static final String TAG = DownloadActivity.class.getSimpleName();
    private final DownloadServiceConnection downloadServiceConnection = new DownloadServiceConnection();
    private ImageLoader imageLoader;
    private DownloadService downloadService;

    private ViewFlipper flipper;
    private TextView currentTextView;
    private TextView albumArtTextView;
    private ImageView albumArtImageView;
    private ListView playlistView;
    private TextView positionTextView;
    private TextView durationTextView;
    private TextView statusTextView;
    private HorizontalSlider progressBar;
    private ImageView previousButton;
    private ImageView nextButton;
    private ImageView pauseButton;
    private ImageView startButton;
    private ScheduledExecutorService executorService;
    private DownloadFile currentPlaying;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.download);

        flipper = (ViewFlipper) findViewById(R.id.download_flipper);
        currentTextView = (TextView) findViewById(R.id.download_current);
        albumArtTextView = (TextView) findViewById(R.id.download_album_art_text);
        albumArtImageView = (ImageView) findViewById(R.id.download_album_art_image);
        positionTextView = (TextView) findViewById(R.id.download_position);
        durationTextView = (TextView) findViewById(R.id.download_duration);
        statusTextView = (TextView) findViewById(R.id.download_status);
        progressBar = (HorizontalSlider) findViewById(R.id.download_progress_bar);
        playlistView = (ListView) findViewById(R.id.download_list);
        previousButton = (ImageView) findViewById(R.id.download_previous);
        nextButton = (ImageView) findViewById(R.id.download_next);
        pauseButton = (ImageView) findViewById(R.id.download_pause);
        startButton = (ImageView) findViewById(R.id.download_start);

        currentTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showFullscreenAlbumArt(true);
            }
        });

        albumArtImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showFullscreenAlbumArt(false);
            }
        });

        previousButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                downloadService.previous();
            }
        });

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                downloadService.next();
            }
        });

        pauseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                downloadService.pause();
            }
        });

        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                start();
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
                downloadService.play(position);
            }
        });

        bindService(new Intent(this, DownloadServiceImpl.class), downloadServiceConnection, Context.BIND_AUTO_CREATE);
        imageLoader = new ImageLoader();
    }

    @Override
    protected void onResume() {
        super.onResume();
        onDownloadListChanged();

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

    private void update() {
        if (downloadService == null) {
            return;
        }

        if (currentPlaying != downloadService.getCurrentPlaying()) {
            onCurrentChanged();
        }

        onProgressChanged();
    }

    private void showFullscreenAlbumArt(boolean fullscreen) {
        boolean empty = downloadService == null || downloadService.getCurrentPlaying() == null;
        int newDisplayedChild = fullscreen && !empty ? 0 : 1;

        if (flipper.getDisplayedChild() != newDisplayedChild) {

            if (newDisplayedChild == 0) {
                flipper.setInAnimation(AnimationUtils.loadAnimation(this, R.anim.push_down_in));
                flipper.setOutAnimation(AnimationUtils.loadAnimation(this, R.anim.push_down_out));
            } else {
                flipper.setInAnimation(AnimationUtils.loadAnimation(this, R.anim.push_up_in));
                flipper.setOutAnimation(AnimationUtils.loadAnimation(this, R.anim.push_up_out));
            }

            flipper.setDisplayedChild(newDisplayedChild);
        }
    }

    private void start() {
        PlayerState state = downloadService.getPlayerState();
        if (state == PAUSED || state == COMPLETED) {
            downloadService.start();
        } else if (state == STOPPED) {
            downloadService.play(downloadService.getCurrentPlaying());
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE:
                Log.i(TAG, "Got MEDIA_PLAY_PAUSE key event.");
                if (downloadService.getPlayerState() == STARTED) {
                    downloadService.pause();
                } else {
                    start();
                }
                break;
            case KeyEvent.KEYCODE_MEDIA_PREVIOUS:
                Log.i(TAG, "Got MEDIA_PREVIOUS key event.");
                downloadService.previous();
                break;
            case KeyEvent.KEYCODE_MEDIA_NEXT:
                Log.i(TAG, "Got MEDIA_NEXT key event.");
                downloadService.next();
                break;
            default:
        }
        return super.onKeyDown(keyCode, event);
    }

    private void onDownloadListChanged() {
        if (downloadService == null) {
            return;
        }

        List<DownloadFile> list = downloadService.getDownloads();

        playlistView.setAdapter(new SongListAdapter(list));
        if (list.isEmpty()) {
            currentTextView.setText(null);
            currentTextView.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
        }
    }

    private void onCurrentChanged() {
        if (downloadService == null) {
            return;
        }
        currentPlaying = downloadService.getCurrentPlaying();
        if (currentPlaying != null) {
            MusicDirectory.Entry song = currentPlaying.getSong();
            currentTextView.setText(song.getTitle());
            albumArtTextView.setText(song.getTitle() + " - " + song.getArtist());
            imageLoader.loadImage(currentTextView, song, 48);
            imageLoader.loadImage(albumArtImageView, song, 320);
        }
    }

    private void onProgressChanged() {
        if (downloadService == null) {
            return;
        }
        if (currentPlaying != null) {

            int millisPlayed = Math.max(0, downloadService.getPlayerPosition());
            Integer duration = currentPlaying.getSong().getDuration();
            int millisTotal = duration == null ? 0 : duration * 1000;

            positionTextView.setText(Util.formatDuration(millisPlayed / 1000));
            durationTextView.setText(Util.formatDuration(millisTotal / 1000));
            progressBar.setMax(millisTotal == 0 ? 100 : millisTotal); // Work-around for apparent bug.
            progressBar.setProgress(millisPlayed);
            progressBar.setSlidingEnabled(currentPlaying.isComplete());
        } else {
            // TODO
        }

        PlayerState playerState = downloadService.getPlayerState();

        if (playerState == PlayerState.DOWNLOADING) {
            long bytes = currentPlaying.getPartialFile().length();
            statusTextView.setText(playerState + " - " + Util.formatBytes(bytes));
        } else {
            statusTextView.setText(playerState.toString());
        }

        if (playerState == STARTED) {
            pauseButton.setVisibility(View.VISIBLE);
            startButton.setVisibility(View.GONE);
        } else {
            pauseButton.setVisibility(View.GONE);
            startButton.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(downloadServiceConnection);
        imageLoader.cancel();
    }

    private class DownloadServiceConnection implements ServiceConnection {

        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {
            downloadService = ((SimpleServiceBinder<DownloadService>) service).getService();
            Log.i(TAG, "Connected to Download Service");
            onDownloadListChanged();
            onCurrentChanged();
            onProgressChanged();
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            downloadService = null;
            Log.i(TAG, "Disconnected from Download Service");
        }
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