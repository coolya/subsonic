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
package net.sourceforge.subsonic.androidapp.service;

import android.app.Service;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import net.sourceforge.subsonic.androidapp.R;
import net.sourceforge.subsonic.androidapp.domain.MusicDirectory;
import net.sourceforge.subsonic.androidapp.domain.PlayerState;
import static net.sourceforge.subsonic.androidapp.domain.PlayerState.*;
import net.sourceforge.subsonic.androidapp.util.CancellableTask;
import net.sourceforge.subsonic.androidapp.util.SimpleServiceBinder;
import net.sourceforge.subsonic.androidapp.util.Util;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author Sindre Mehus
 * @version $Id$
 */
public class DownloadServiceImpl extends Service implements DownloadService {

    private static final String TAG = DownloadServiceImpl.class.getSimpleName();
    private final IBinder binder = new SimpleServiceBinder<DownloadService>(this);
    private MediaPlayer mediaPlayer;
    private final List<DownloadFile> downloadList = new CopyOnWriteArrayList<DownloadFile>();
    private final Handler handler = new Handler();
    private final DownloadServiceLifecycleSupport lifecycleSupport = new DownloadServiceLifecycleSupport(this);
    private final List<DownloadFile> cleanupCandidates = new ArrayList<DownloadFile>();
    private DownloadFile currentPlaying;
    private DownloadFile currentDownloading;
    private CancellableTask bufferTask;
    private PlayerState playerState = IDLE;

    @Override
    public void onCreate() {
        super.onCreate();
        lifecycleSupport.onCreate();

        mediaPlayer = new MediaPlayer();
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
        mediaPlayer.release();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public synchronized void download(List<MusicDirectory.Entry> songs, boolean save, boolean autoplay) {
        if (songs.isEmpty()) {
            return;
        }

        for (MusicDirectory.Entry song : songs) {
            DownloadFile downloadFile = new DownloadFile(this, handler, song, save);
            downloadList.add(downloadFile);
        }

        if (autoplay) {
            play(0);
        } else {
            checkDownloads();
        }
        lifecycleSupport.serializeDownloadQueue();
    }

    @Override
    public synchronized DownloadFile forSong(MusicDirectory.Entry song) {
        for (DownloadFile downloadFile : downloadList) {
            if (downloadFile.getSong() == song) {
                return downloadFile;
            }
        }
        return new DownloadFile(this, handler, song, false);
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
    public synchronized void remove(DownloadFile downloadFile) {
        if (downloadFile == currentDownloading) {
            currentDownloading.cancelDownload();
        }
        if (downloadFile == currentPlaying) {
            reset();
            next();
        }
        downloadList.remove(downloadFile);
    }

    @Override
    public synchronized void delete(List<MusicDirectory.Entry> songs) {
        for (MusicDirectory.Entry song : songs) {
            forSong(song).delete();
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
        int index = downloadList.indexOf(currentPlaying);
        if (index != -1) {
            play(index - 1);
        }

    }

    @Override
    public synchronized void next() {
        int index = downloadList.indexOf(currentPlaying);
        if (index != -1) {
            play(index + 1);
        }
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
    public synchronized void reset() {
        if (bufferTask != null) {
            bufferTask.cancel();
        }
        try {
            mediaPlayer.reset();
            setPlayerState(IDLE);
        } catch (Exception x) {
            handleError(x);
        }
    }

    @Override
    public synchronized int getPlayerPosition() {
        try {
            if (playerState == IDLE || playerState == DOWNLOADING || playerState == PREPARING) {
                return 0;
            }
            return mediaPlayer.getCurrentPosition();
        } catch (Exception x) {
            handleError(x);
            return 0;
        }
    }

    @Override
    public synchronized int getPlayerDuration() {
        if (currentPlaying != null) {
            Integer duration = currentPlaying.getSong().getDuration();
            if (duration != null) {
                return duration * 1000;
            }
        }
        if (playerState != IDLE && playerState != DOWNLOADING && playerState != PlayerState.PREPARING) {
            try {
                return mediaPlayer.getDuration();
            } catch (Exception x) {
                handleError(x);
            }
        }
        return 0;
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
        Integer bitRate = downloadFile.getSong().getBitRate();
        if (bitRate == null) {
            bitRate = 160;
        }
        final int bufferSize = Math.max(100000, bitRate * 1024 / 8 * 10);

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


            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mediaPlayer) {
                    setPlayerState(COMPLETED);

                    if (downloadFile.isComplete()) {
                        next();
                        return;
                    }

                    // If file is not completely downloaded, restart the playback from the current position.
                    try {
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
        String msg = getResources().getString(R.string.download_error, currentPlaying.getSong().getTitle());
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
            currentDownloading.download();
            cleanupCandidates.add(currentDownloading);
        }

        // Find a suitable target for download.
        else if (currentDownloading == null || currentDownloading.isComplete()) {

            int n = downloadList.size();
            if (n == 0) {
                return;
            }

            int start = currentPlaying == null ? 0 : downloadList.indexOf(currentPlaying);
            int i = start;
            do {
                DownloadFile downloadFile = downloadList.get(i);
                if (!downloadFile.isDone()) {
                    currentDownloading = downloadFile;
                    currentDownloading.download();
                    cleanupCandidates.add(currentDownloading);
                    break;
                }
                i = (i + 1) % n;
            } while (i != start);
        }

        // Delete obsolete .partial and .complete files.
        cleanup();
    }

    private synchronized void cleanup() {
        Iterator<DownloadFile> iterator = cleanupCandidates.iterator();
        while (iterator.hasNext()) {
            DownloadFile downloadFile = iterator.next();
            if (downloadFile != currentPlaying && downloadFile != currentDownloading) {
                if (downloadFile.cleanup()) {
                    iterator.remove();
                }
            }
        }
    }

}
