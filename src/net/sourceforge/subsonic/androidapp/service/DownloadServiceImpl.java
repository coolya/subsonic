/*
 * (c) Copyright WesternGeco. Unpublished work, created 2009. All rights
 * reserved under copyright laws. This information is confidential and is
 * the trade property of WesternGeco. Do not use, disclose, or reproduce
 * without the prior written permission of the owner.
 */
package net.sourceforge.subsonic.androidapp.service;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

import android.app.Service;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import net.sourceforge.subsonic.androidapp.domain.MusicDirectory;
import net.sourceforge.subsonic.androidapp.domain.PlayerState;
import static net.sourceforge.subsonic.androidapp.domain.PlayerState.COMPLETED;
import static net.sourceforge.subsonic.androidapp.domain.PlayerState.DOWNLOADING;
import static net.sourceforge.subsonic.androidapp.domain.PlayerState.IDLE;
import static net.sourceforge.subsonic.androidapp.domain.PlayerState.PAUSED;
import static net.sourceforge.subsonic.androidapp.domain.PlayerState.PREPARED;
import static net.sourceforge.subsonic.androidapp.domain.PlayerState.PREPARING;
import static net.sourceforge.subsonic.androidapp.domain.PlayerState.STARTED;
import net.sourceforge.subsonic.androidapp.util.CancellableTask;
import net.sourceforge.subsonic.androidapp.util.SimpleServiceBinder;
import net.sourceforge.subsonic.androidapp.util.Util;

/**
 * @author Sindre Mehus
 * @version $Id$
 */
public class DownloadServiceImpl extends Service implements DownloadService {

    private static final String TAG = DownloadServiceImpl.class.getSimpleName();
    private final IBinder binder = new SimpleServiceBinder<DownloadService>(this);
    private final MediaPlayer mediaPlayer = new MediaPlayer();
    private final List<DownloadFile> downloadList = new CopyOnWriteArrayList<DownloadFile>();
    private final Handler handler = new Handler();
    private final DownloadServiceLifecycleSupport lifecycleSupport = new DownloadServiceLifecycleSupport(this);
    private DownloadFile currentPlaying;
    private DownloadFile currentDownloading;
    private CancellableTask bufferTask;
    private PlayerState playerState = IDLE;

    @Override
    public void onCreate() {
        super.onCreate();
        lifecycleSupport.onCreate();

        mediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mediaPlayer, int what, int more) {
                handleError(new Exception("MediaPlayer error: " + what + " (" + more + ")"));
                return false;
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        lifecycleSupport.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public synchronized void download(List<MusicDirectory.Entry> songs, boolean save, boolean play) {
        if (songs.isEmpty()) {
            return;
        }

        for (MusicDirectory.Entry song : songs) {
            DownloadFile downloadFile = new DownloadFile(this, handler, song, save);
            downloadList.add(downloadFile);
        }

        if (play) {
            play(0);
        }
    }

    @Override
    public synchronized void clear() {
        downloadList.clear();
        if (bufferTask != null) {
            bufferTask.cancel();
        }
        if (currentDownloading != null) {
            currentDownloading.cancelDownload();
        }
        mediaPlayer.reset();
        setPlayerState(IDLE);
    }

    public synchronized void delete(List<DownloadFile> downloadFiles) {
        for (DownloadFile downloadFile : downloadFiles) {
            downloadFile.delete();
        }
    }

    @Override
    public DownloadFile getCurrentPlaying() {
        return currentPlaying;
    }

    @Override
    public DownloadFile getCurrentDownloading() {
        return currentDownloading;
    }

    @Override
    public synchronized List<DownloadFile> getDownloads() {
        return new ArrayList<DownloadFile>(downloadList);
    }

    @Override
    public synchronized DownloadFile getDownloadAt(int index) {
        try {
            return downloadList.get(index);
        } catch (IndexOutOfBoundsException x) {
            return null;
        }
    }

    @Override
    public synchronized void play(DownloadFile file) {
        play(downloadList.indexOf(file));
    }

    @Override
    public synchronized void play(int index) {
        if (index < 0 || index >= downloadList.size()) {
            return;
        }

        DownloadFile downloadFile = downloadList.get(index);
        currentPlaying = downloadFile;
        checkDownloads();
        bufferAndPlay(downloadFile);
    }


    @Override
    public synchronized void seekTo(int position) {
        try {
            mediaPlayer.seekTo(position);
        } catch (Exception x) {
            handleError(x);
        }
    }

    @Override
    public synchronized void previous() {
        play(downloadList.indexOf(currentPlaying) - 1);
    }

    @Override
    public synchronized void next() {
        play(downloadList.indexOf(currentPlaying) + 1);
    }

    @Override
    public synchronized void pause() {
        try {
            mediaPlayer.pause();
            setPlayerState(PAUSED);
        } catch (Exception x) {
            handleError(x);
        }
    }

    @Override
    public synchronized void start() {
        try {
            mediaPlayer.start();
            setPlayerState(STARTED);
        } catch (Exception x) {
            handleError(x);
        }
    }

    @Override
    public synchronized int getPlayerPosition() {
        try {
            return mediaPlayer.getCurrentPosition();
        } catch (Exception x) {
            handleError(x);
            return 0;
        }
    }

    @Override
    public PlayerState getPlayerState() {
        return playerState;
    }

    private synchronized void setPlayerState(PlayerState playerState) {
        Log.i(TAG, this.playerState.name() + " -> " + playerState.name());
        this.playerState = playerState;
        if (playerState == STARTED) {
            Util.showPlayingNotification(this, handler, currentPlaying.getSong());
        } else {
            Util.hidePlayingNotification(this, handler);
        }
    }

    private synchronized void bufferAndPlay(final DownloadFile downloadFile) {
        if (bufferTask != null) {
            bufferTask.cancel();
        }

        mediaPlayer.reset();
        setPlayerState(IDLE);

        // Buffer ten seconds.
        final int bufferSize = Math.max(100000, downloadFile.getSong().getBitRate() * 1024 / 8 * 10);

        bufferTask = new CancellableTask() {
            @Override
            public void execute() {
                setPlayerState(DOWNLOADING);

                while (!isCancelled() && !bufferComplete()) {
                    try {
                        Thread.sleep(250L);
                    } catch (InterruptedException x) {
                        return;
                    }
                }
                doPlay(downloadFile);
            }

            private boolean bufferComplete() {
                File file = downloadFile.getPartialFile();
                return downloadFile.isComplete() || file.exists() && file.length() > bufferSize;
            }
        };
        bufferTask.start();
    }

    private synchronized void doPlay(final DownloadFile downloadFile) {
        try {
            File file = downloadFile.isComplete() ? downloadFile.getCompleteFile() : downloadFile.getPartialFile();
            mediaPlayer.setOnCompletionListener(null);
            mediaPlayer.reset();
            setPlayerState(IDLE);
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayer.setDataSource(file.getPath());
            setPlayerState(PREPARING);
            mediaPlayer.prepare();
            setPlayerState(PREPARED);

            final AtomicBoolean downloadComplete = new AtomicBoolean(false);

            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mediaPlayer) {
                    setPlayerState(COMPLETED);

                    if (downloadComplete.get()) {
                        next();
                        return;
                    }

                    try {
                        downloadComplete.set(downloadFile.isComplete());
                        int pos = mediaPlayer.getCurrentPosition();
                        Log.i(TAG, "Restarting player from position " + pos);

                        mediaPlayer.reset();
                        setPlayerState(IDLE);
                        File file = downloadFile.isComplete() ? downloadFile.getCompleteFile() : downloadFile.getPartialFile();
                        mediaPlayer.setDataSource(file.getPath());
                        setPlayerState(PREPARING);
                        mediaPlayer.prepare();
                        setPlayerState(PREPARED);
                        mediaPlayer.seekTo(pos);
                        mediaPlayer.start();
                        setPlayerState(STARTED);
                    } catch (Exception x) {
                        handleError(x);
                    }
                }
            });

            mediaPlayer.start();
            setPlayerState(STARTED);

        } catch (Exception x) {
            handleError(x);
        }
    }

    private void handleError(Exception x) {
        String msg = "Error playing \"" + currentPlaying.getSong().getTitle() + "\"";
        Util.showErrorNotification(this, handler, msg, x);
        Log.e(TAG, msg, x);
        mediaPlayer.reset();
        setPlayerState(IDLE);
    }

    protected synchronized void checkDownloads() {

        // Need to download current playing?
        if (currentPlaying != null && currentPlaying != currentDownloading && !currentPlaying.isComplete()) {

            // Cancel current download, if necessary.
            if (currentDownloading != null) {
                currentDownloading.cancelDownload();
            }

            currentDownloading = currentPlaying;
            currentPlaying.download();
        }

        // Find a suitable target for download.
        else if (currentDownloading == null || currentDownloading.isComplete()) {

            int n = downloadList.size();
            if (n == 0) {
                return;
            }

            int start = downloadList.indexOf(currentPlaying);
            int i = start;
            do {
                DownloadFile downloadFile = downloadList.get(i);
                if (!downloadFile.isComplete()) {
                    currentDownloading = downloadFile;
                    downloadFile.download();
                    break;
                }
                i = (i + 1) % n;
            } while (i != start);
        }
    }


}
