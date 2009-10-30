/*
 * (c) Copyright WesternGeco. Unpublished work, created 2009. All rights
 * reserved under copyright laws. This information is confidential and is
 * the trade property of WesternGeco. Do not use, disclose, or reproduce
 * without the prior written permission of the owner.
 */
package net.sourceforge.subsonic.androidapp.service;

import java.io.File;
import java.util.List;
import java.util.ArrayList;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import android.app.NotificationManager;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.util.Log;
import net.sourceforge.subsonic.androidapp.service.DownloadFile;
import net.sourceforge.subsonic.androidapp.domain.MusicDirectory;
import net.sourceforge.subsonic.androidapp.domain.PlayerState;
import static net.sourceforge.subsonic.androidapp.domain.PlayerState.*;
import net.sourceforge.subsonic.androidapp.util.CancellableTask;
import net.sourceforge.subsonic.androidapp.util.Constants;
import net.sourceforge.subsonic.androidapp.util.SimpleServiceBinder;

/**
 * @author Sindre Mehus
 * @version $Id$
 */
public class DownloadServiceImpl extends ServiceBase implements DownloadService2 {

    private static final String TAG = DownloadServiceImpl.class.getSimpleName();
    private final IBinder binder = new SimpleServiceBinder<DownloadService2>(this);
    private final MediaPlayer mediaPlayer = new MediaPlayer();
    private final List<DownloadFile> downloadList = new CopyOnWriteArrayList<DownloadFile>();
    private DownloadFile currentPlaying;
    private DownloadFile currentDownloading;
    private CancellableTask bufferTask;
    private ScheduledExecutorService executorService;
    private PlayerState playerState = IDLE;


    // TODO: synchronization

    @Override
    public void onCreate() {
        super.onCreate();
        executorService = Executors.newSingleThreadScheduledExecutor();
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                checkDownloads();
            }
        };
        executorService.scheduleWithFixedDelay(runnable, 5000L, 5000L, TimeUnit.MILLISECONDS);
    }

    @Override
    public void onDestroy() {
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.cancel(Constants.NOTIFICATION_ID_DOWNLOAD_QUEUE);
        clear();
        executorService.shutdown();
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
            DownloadFile downloadFile = new DownloadFile(this, song, save);
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
    public void seekTo(int position) {
        // TODO: Catch exception on all mediaplayer methods.
        mediaPlayer.seekTo(position);
    }

    @Override
    public void previous() {
        play(downloadList.indexOf(currentPlaying) - 1);
    }

    @Override
    public void next() {
        play(downloadList.indexOf(currentPlaying) + 1);
    }

    @Override
    public void pause() {
        mediaPlayer.pause();
        setPlayerState(PAUSED);
    }

    @Override
    public void start() {
        mediaPlayer.start();
        setPlayerState(STARTED);
    }

    @Override
    public PlayerState getPlayerState() {
        return playerState;
    }

    @Override
    public int getPlayerPosition() {
        return mediaPlayer.getCurrentPosition();
    }

    private void setPlayerState(PlayerState playerState) {
        this.playerState = playerState;
    }

    private synchronized void bufferAndPlay(final DownloadFile downloadFile) {
        if (bufferTask != null) {
            bufferTask.cancel();
        }

        // Buffer ten seconds.
        final int bufferSize = Math.max(100000, downloadFile.getSong().getBitRate() * 1024 / 8 * 10);

        bufferTask = new CancellableTask() {
            @Override
            public void execute() {
                setPlayerState(DOWNLOADING);

                while (!isCancelled() && !bufferComplete()) {
                    try {
                        Thread.sleep(100L);
                    } catch (InterruptedException x) {
                        return;
                    }
                }
                doPlay(downloadFile);
            }

            private boolean bufferComplete() {
                File file = downloadFile.getPartialFile();

                Log.d(TAG, "File size: " + file.length());

                return downloadFile.isComplete() || file.exists() && file.length() > bufferSize;
            }
        };
        bufferTask.start();
    }

    private void doPlay(final DownloadFile downloadFile) {
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
                        x.printStackTrace();
                        // TODO
                    }
                }
            });

            mediaPlayer.start();
            setPlayerState(STARTED);

        } catch (Exception x) {
            x.printStackTrace();
            // TODO
        }
    }

    private synchronized void checkDownloads() {

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
