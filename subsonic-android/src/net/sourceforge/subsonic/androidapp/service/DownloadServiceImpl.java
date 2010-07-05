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

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import android.app.Service;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.IBinder;
import android.os.PowerManager;
import android.os.Environment;
import android.util.Log;
import net.sourceforge.subsonic.androidapp.R;
import net.sourceforge.subsonic.androidapp.domain.MusicDirectory;
import net.sourceforge.subsonic.androidapp.domain.PlayerState;
import net.sourceforge.subsonic.androidapp.util.CancellableTask;
import net.sourceforge.subsonic.androidapp.util.ShufflePlayBuffer;
import net.sourceforge.subsonic.androidapp.util.SimpleServiceBinder;
import net.sourceforge.subsonic.androidapp.util.Util;

import static net.sourceforge.subsonic.androidapp.domain.PlayerState.*;

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
    private final ShufflePlayBuffer shufflePlayBuffer = new ShufflePlayBuffer(this);

    private final List<DownloadFile> cleanupCandidates = new ArrayList<DownloadFile>();
    private DownloadFile currentPlaying;
    private DownloadFile currentDownloading;
    private CancellableTask bufferTask;
    private PlayerState playerState = IDLE;
    private boolean shufflePlay;
    private long revision;
    private static DownloadService instance;

    @Override
    public void onCreate() {
        super.onCreate();
        lifecycleSupport.onCreate();

        mediaPlayer = new MediaPlayer();
        mediaPlayer.setWakeMode(this, PowerManager.PARTIAL_WAKE_LOCK);

        mediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mediaPlayer, int what, int more) {
                handleError(new Exception("MediaPlayer error: " + what + " (" + more + ")"));
                return false;
            }
        });
        instance = this;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        lifecycleSupport.onDestroy();
        mediaPlayer.release();
        instance = null;
    }

    public static DownloadService getInstance() {
        return instance;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public synchronized void download(List<MusicDirectory.Entry> songs, boolean save, boolean autoplay) {
        shufflePlay = false;

        if (songs.isEmpty()) {
            return;
        }

        for (MusicDirectory.Entry song : songs) {
            DownloadFile downloadFile = new DownloadFile(this, song, save);
            downloadList.add(downloadFile);
        }
        revision++;

        if (autoplay) {
            play(0);
        } else {
            checkDownloads();
        }
        lifecycleSupport.serializeDownloadQueue();
    }

    @Override
    public void setShufflePlayEnabled(boolean enabled) {
        shufflePlay = enabled;
        if (shufflePlay) {
            clear();
            checkDownloads();
        }
    }

    @Override
    public synchronized DownloadFile forSong(MusicDirectory.Entry song) {
        for (DownloadFile downloadFile : downloadList) {
            if (downloadFile.getSong() == song) {
                return downloadFile;
            }
        }
        return new DownloadFile(this, song, false);
    }

    @Override
    public synchronized void clear() {
        reset();
        downloadList.clear();
        revision++;
        if (currentDownloading != null) {
            currentDownloading.cancelDownload();
        }
        currentPlaying = null;

        lifecycleSupport.serializeDownloadQueue();
    }

    @Override
    public synchronized void remove(DownloadFile downloadFile) {
        if (downloadFile == currentDownloading) {
            currentDownloading.cancelDownload();
        }
        if (downloadFile == currentPlaying) {
            reset();
            currentPlaying = null;
        }
        downloadList.remove(downloadFile);
        revision++;
        lifecycleSupport.serializeDownloadQueue();
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
    public synchronized void play(DownloadFile file) {
        play(downloadList.indexOf(file));
    }

    @Override
    public synchronized void play(int index) {
        if (index < 0 || index >= downloadList.size()) {
            return;
        }

        currentPlaying = downloadList.get(index);
        checkDownloads();
        bufferAndPlay();
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
        if (this.playerState == PAUSED && playerState == STARTED) {
            Util.showPlayingNotification(this, handler, currentPlaying.getSong());
        } else if (this.playerState == STARTED && playerState == PAUSED) {
            Util.hidePlayingNotification(this, handler);
        }
        this.playerState = playerState;
    }

    private synchronized void bufferAndPlay() {
        reset();
        Util.showPlayingNotification(this, handler, currentPlaying.getSong());

        fileSizeAtLastResume = 0;
        bufferTask = new BufferTask(currentPlaying, 0);
        bufferTask.start();
    }

    private synchronized void doPlay(final DownloadFile downloadFile, int position) {
        try {
            final File file = downloadFile.isCompleteFileAvailable() ? downloadFile.getCompleteFile() : downloadFile.getPartialFile();
            downloadFile.updateModificationDate();
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

                    // If COMPLETED and not playing partial file, we are *really" finished
                    // with the song and can move on to the next.
                    if (!file.equals(downloadFile.getPartialFile())) {
                        Util.hidePlayingNotification(DownloadServiceImpl.this, handler);
                        next();
                        return;
                    }

                    // If file is not completely downloaded, restart the playback from the current position.
                    int pos = mediaPlayer.getCurrentPosition();
                    synchronized (DownloadServiceImpl.this) {
                        reset();
                        bufferTask = new BufferTask(downloadFile, pos);
                        bufferTask.start();
                    }
                }
            });

            if (position != 0) {
                Log.i(TAG, "Restarting player from position " + position);
                mediaPlayer.seekTo(position);
            }

            fileSizeAtLastResume = downloadFile.getPartialFile().length();
            mediaPlayer.start();
            setPlayerState(STARTED);

        } catch (Exception x) {
            handleError(x);
        }
    }

    private void handleError(Exception x) {
        String msg = getResources().getString(R.string.download_play_error, currentPlaying.getSong().getTitle());
        Util.showErrorNotification(this, handler, msg, x);
        Log.e(TAG, msg, x);
        mediaPlayer.reset();
        setPlayerState(IDLE);
    }

    protected synchronized void checkDownloads() {

        if (!Util.isNetworkConnected(this) || !isExternalStoragePresent()) {
            return;
        }

        if (shufflePlay) {
            checkShufflePlay();
        }

        if (downloadList.isEmpty()) {
            return;
        }

        // Need to download current playing?
        if (currentPlaying != null &&
                currentPlaying != currentDownloading &&
                !currentPlaying.isCompleteFileAvailable()) {

            // Cancel current download, if necessary.
            if (currentDownloading != null) {
                currentDownloading.cancelDownload();
            }

            currentDownloading = currentPlaying;
            currentDownloading.download();
            cleanupCandidates.add(currentDownloading);
        }

        // Find a suitable target for download.
        else if (currentDownloading == null || currentDownloading.isWorkDone() || currentDownloading.isFailed()) {

            int n = downloadList.size();
            if (n == 0) {
                return;
            }

            int preloaded = 0;

            int start = currentPlaying == null ? 0 : downloadList.indexOf(currentPlaying);
            int i = start;
            do {
                DownloadFile downloadFile = downloadList.get(i);
                if (!downloadFile.isWorkDone()) {
                    currentDownloading = downloadFile;
                    currentDownloading.download();
                    cleanupCandidates.add(currentDownloading);
                    break;
                } else if (!downloadFile.isSaved()) {
                    if (currentPlaying != downloadFile) {
                        preloaded++;
                    }
                    if (preloaded >= Util.getPreloadCount(this)) {
                        break;
                    }
                }

                i = (i + 1) % n;
            } while (i != start);
        }

        // Delete obsolete .partial and .complete files.
        cleanup();
    }

    private synchronized void checkShufflePlay() {

        final int listSize = 10;

        boolean wasEmpty = downloadList.isEmpty();
        int currIndex = currentPlaying == null ? 0 : downloadList.indexOf(currentPlaying);
        int size = downloadList.size();
        int remaining = size - currIndex;

        if (remaining < listSize) {
            for (MusicDirectory.Entry song : shufflePlayBuffer.get(Math.max(size,listSize) - remaining)) {
                DownloadFile downloadFile = new DownloadFile(this, song, false);
                downloadList.add(downloadFile);
                revision++;
            }
            while (downloadList.size() > listSize) {
                downloadList.get(0).cancelDownload();
                downloadList.remove(0);
                revision++;
            }
        }

        if (wasEmpty && !downloadList.isEmpty()) {
            play(0);
        }
    }

    public long getDownloadListUpdateRevision() {
        return revision;
    }

    private boolean isExternalStoragePresent() {
        return Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState());
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

    private long fileSizeAtLastResume;

    private class BufferTask extends CancellableTask {

        private static final int BUFFER_LENGTH_SECONDS = 5;

        private final DownloadFile downloadFile;
        private final int position;
        private final long expectedFileSize;
        private File partialFile;

        public BufferTask(DownloadFile downloadFile, int position) {
            this.downloadFile = downloadFile;
            this.position = position;
            partialFile = downloadFile.getPartialFile();

            // Calculate roughly how many bytes BUFFER_LENGTH_SECONDS corresponds to.
            Integer bitRate = downloadFile.getSong().getBitRate();
            if (bitRate == null) {
                bitRate = 160;
            }
            long byteCount = Math.max(100000, bitRate * 1024 / 8 * BUFFER_LENGTH_SECONDS);

            // Find out how large the file should grow before resuming playback.
            expectedFileSize = fileSizeAtLastResume + byteCount;
        }

        @Override
        public void execute() {
            setPlayerState(DOWNLOADING);

            while (!bufferComplete()) {
                Log.i(TAG, "Buffering " + partialFile + " (" + partialFile.length() + ")");
                Util.sleepQuietly(1000L);
                if (isCancelled()) {
                    return;
                }
            }
            doPlay(downloadFile, position);
        }

        private boolean bufferComplete() {
            return downloadFile.isCompleteFileAvailable() || partialFile.length() >= expectedFileSize;
        }
    }
}
