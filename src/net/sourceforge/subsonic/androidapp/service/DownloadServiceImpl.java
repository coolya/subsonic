/*
 * (c) Copyright WesternGeco. Unpublished work, created 2009. All rights
 * reserved under copyright laws. This information is confidential and is
 * the trade property of WesternGeco. Do not use, disclose, or reproduce
 * without the prior written permission of the owner.
 */
package net.sourceforge.subsonic.androidapp.service;

import java.io.File;
import java.util.List;
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
import net.sourceforge.subsonic.androidapp.domain.DownloadFile;
import net.sourceforge.subsonic.androidapp.domain.MusicDirectory;
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
//        queue.offer(POISON);
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
            DownloadFile downloadFile = new DownloadFile(this, song);
            downloadList.add(downloadFile);
        }

        if (play) {
            play(0);
        }
    }

    private synchronized void bufferAndPlay(final DownloadFile downloadFile) {
        if (bufferTask != null) {
            bufferTask.cancel();
        }

        bufferTask = new CancellableTask() {
            @Override
            public void execute() {
                while (!isCancelled() && !bufferComplete()) {
                    try {
                        Thread.sleep(100L);
                    } catch (InterruptedException x) {
                        return;
                    }
                }
                play(downloadFile);
            }

            private boolean bufferComplete() {
                File file = downloadFile.getTempFile();

                Log.d(TAG, "File size: " + file.length());

                // TODO: Do not hardcode buffer size.
                return downloadFile.isComplete() || file.exists() && file.length() > 100000L;
            }
        };
        bufferTask.start();
    }

    private void play(final DownloadFile downloadFile) {
        try {
            final File file = downloadFile.isComplete() ? downloadFile.getFile() : downloadFile.getTempFile();
            mediaPlayer.setOnCompletionListener(null);
            mediaPlayer.reset();
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayer.setDataSource(file.getPath());
            mediaPlayer.prepare();

            final AtomicBoolean downloadComplete = new AtomicBoolean(false);

            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mediaPlayer) {

                    if (downloadComplete.get()) {
                        next();
                        return;
                    }

                    try {
                        downloadComplete.set(downloadFile.isComplete());
                        int pos = mediaPlayer.getCurrentPosition();
                        Log.i(TAG, "Restarting player from position " + pos);

                        mediaPlayer.reset();
                        mediaPlayer.setDataSource(file.getPath());
                        mediaPlayer.prepare();
                        mediaPlayer.seekTo(pos);
                        mediaPlayer.start();
                    } catch (Exception x) {
                        x.printStackTrace();
                        // TODO
                    }
                }
            });

            mediaPlayer.start();
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

    @Override
    public void clear() {
    }

    @Override
    public List<DownloadFile> getDownloads() {
        return null;
    }

    @Override
    public DownloadFile getDownloadAt(int index) {
        return null;
    }

    @Override
    public int getCurrentPlayingIndex() {
        return 0;
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
    }

    @Override
    public void start() {
    }

    @Override
    public StreamService.PlayerState getPlayerState() {
        return null;
    }

}
