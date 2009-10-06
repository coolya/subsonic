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

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.IBinder;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;
import net.sourceforge.subsonic.androidapp.R;
import net.sourceforge.subsonic.androidapp.activity.ErrorActivity;
import net.sourceforge.subsonic.androidapp.activity.StreamQueueActivity;
import net.sourceforge.subsonic.androidapp.domain.MusicDirectory;
import static net.sourceforge.subsonic.androidapp.service.StreamService.PlayerState.*;
import net.sourceforge.subsonic.androidapp.util.Constants;
import net.sourceforge.subsonic.androidapp.util.Pair;
import net.sourceforge.subsonic.androidapp.util.SimpleServiceBinder;
import net.sourceforge.subsonic.androidapp.util.Util;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @author Sindre Mehus
 */
public class StreamService extends ServiceBase {

    private static final String TAG = StreamService.class.getSimpleName();

    private final IBinder binder = new SimpleServiceBinder<StreamService>(this);
    private final Handler handler = new Handler();

    private final AtomicInteger current = new AtomicInteger(-1);
    private final List<PlaylistEntry> playlist = new CopyOnWriteArrayList<PlaylistEntry>();
    private final ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
    private final AtomicReference<Player> player = new AtomicReference<Player>();
    private final List<Player> players = new ArrayList<Player>();
    private BroadcastReceiver headsetEventReceiver;
    private PhoneStateListener phoneStateListener;

    @Override
    public void onCreate() {
        super.onCreate();

        Player playerA = new Player("A");
        Player playerB = new Player("B");
        playerA.setActive(true);

        players.add(playerA);
        players.add(playerB);
        player.set(playerA);

        headsetEventReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Log.i(TAG, "Headset event for: " + intent.getExtras().get("name"));
                if (intent.getExtras().getInt("state") == 0 && getPlayerState() == STARTED) {
                    pause();
                }
            }
        };

        // Pause when headset is unplugged.
        registerReceiver(headsetEventReceiver, new IntentFilter(Intent.ACTION_HEADSET_PLUG));

        // Pause temporarily on incoming phone calls.
        phoneStateListener = new MyPhoneStateListener();
        TelephonyManager telephonyManager = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
        telephonyManager.listen(phoneStateListener, PhoneStateListener.LISTEN_CALL_STATE);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        unregisterReceiver(headsetEventReceiver);

        TelephonyManager telephonyManager = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
        telephonyManager.listen(phoneStateListener, PhoneStateListener.LISTEN_NONE);

        for (Player p : players) {
            p.release();
        }

        executorService.shutdown();
        hideNotification();
    }

    public void add(List<MusicDirectory.Entry> songs, boolean append, boolean download) {
        boolean shouldStart = playlist.isEmpty() || !append;

        String message = songs.size() == 1 ? "Added \"" + songs.get(0).getTitle() + "\" to playlist." :
                         "Added " + songs.size() + " songs to playlist.";
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        if (!append) {
            playlist.clear();
        }

        for (MusicDirectory.Entry song : songs) {
            playlist.add(new PlaylistEntry(song, download));
        }
        notifyPlaylistChanged();

        if (shouldStart) {
            play(0);
        }
    }

    public void play(int index) {
        if (index < 0 || index >= playlist.size()) {
            return;
        }
        current.set(index);
        PlaylistEntry song = playlist.get(index);

        player.get().reset();
        selectPlayerForSong(song);
        player.get().play(song);
    }

    private void selectPlayerForSong(PlaylistEntry song) {
        for (Player p : players) {
            if (song == p.getSong()) {
                player.get().setActive(false);
                p.setActive(true);
                player.set(p);
                return;
            }
        }
    }

    private void prepareNextPlayer(Player player) {
        if (players.size() < 2 || playlist.size() < 2) {
            return;
        }

        int playerIndex = (players.indexOf(player) + 1) % players.size();
        Player nextPlayer = players.get(playerIndex);

        int songIndex = (playlist.indexOf(player.getSong()) + 1) % playlist.size();
        PlaylistEntry nextSong = playlist.get(songIndex);

        nextPlayer.reset();
        nextPlayer.play(nextSong);
    }


    public void previous() {
        play(current.get() - 1);
    }

    public void next() {
        play(current.get() + 1);
    }

    public void pause() {
        player.get().pause();
    }

    public void start() {
        player.get().start();
    }

    public List<MusicDirectory.Entry> getPlaylist() {
        List<MusicDirectory.Entry> result = new ArrayList<MusicDirectory.Entry>(playlist.size());
        for (PlaylistEntry entry : playlist) {
            result.add(entry.getSong());
        }
        return result;
    }

    public int getCurrentIndex() {
        return current.intValue();
    }

    /**
     * The pair of longs contains (number of millis played, number of millis total).
     */
    public Pair<MusicDirectory.Entry, Pair<Long, Long>> getCurrent() {
        MusicDirectory.Entry current = getCurrentSong();
        if (current == null) {
            return null;
        }

        long position = (long) Math.max(0, player.get().getCurrentPosition());
        long duration = (long) player.get().getDuration();
        Pair<Long, Long> progress = new Pair<Long, Long>(position, duration);
        return new Pair<MusicDirectory.Entry, Pair<Long, Long>>(current, progress);
    }

    public MusicDirectory.Entry getCurrentSong() {
        try {
            return playlist.get(current.get()).getSong();
        } catch (IndexOutOfBoundsException e) {
            return null;
        }
    }

    private String getStreamUrl(PlaylistEntry song) {

        // First check if song exists locally (or should be downloaded first).
        File file = getSongFile(song.getSong(), false);
        if (song.isDownload() || file.exists()) {
            return file.getPath();
        }

        try {
            URL url = new URL(Util.getRestUrl(StreamService.this, "stream") + "&id=" + song.getSong().getId());

            // Ensure that port is set, otherwise the MediaPlayer complains.
            if (url.getPort() == -1) {
                int port = -1;
                if ("http".equals(url.getProtocol())) {
                    port = 80;
                } else if ("https".equals(url.getProtocol())) {
                    port = 443;
                }
                url = new URL(url.getProtocol(), url.getHost(), port, url.getFile());
            }
            return url.toExternalForm();
        } catch (MalformedURLException e) {
            Log.e(TAG, e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    private void notifyPlaylistChanged() {
        sendBroadcast(new Intent(Constants.INTENT_ACTION_STREAM_PLAYLIST));
    }

    private void notifyCurrentChanged() {
        sendBroadcast(new Intent(Constants.INTENT_ACTION_STREAM_CURRENT));
    }

    private void notifyProgressChanged() {
        sendBroadcast(new Intent(Constants.INTENT_ACTION_STREAM_PROGRESS));
    }

    private void showNotification() {

        // Use the same text for the ticker and the expanded notification
        MusicDirectory.Entry song = getCurrentSong();
        String title = song.getTitle();

        // Set the icon, scrolling text and timestamp
        final Notification notification = new Notification(R.drawable.stat_sys_playing, title, System.currentTimeMillis());
        notification.flags |= Notification.FLAG_NO_CLEAR | Notification.FLAG_ONGOING_EVENT;

        // The PendingIntent to launch our activity if the user selects this notification
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, new Intent(this, StreamQueueActivity.class), 0);

        String text = song.getArtist();
        notification.setLatestEventInfo(this, title, text, contentIntent);

        // Send the notification.
        handler.post(new Runnable() {
            @Override
            public void run() {
                NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                notificationManager.notify(Constants.NOTIFICATION_ID_STREAM_QUEUE, notification);
            }
        });
    }

    private void hideNotification() {
        handler.post(new Runnable() {
            @Override
            public void run() {
                NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                notificationManager.cancel(Constants.NOTIFICATION_ID_STREAM_QUEUE);
            }
        });
    }

    private void addErrorNotification(PlaylistEntry song, Exception error) {
        final NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        // Use the same text for the ticker and the expanded notification
        String title = "Failed to play \"" + song.getSong().getTitle() + "\"";

        String text = error.getMessage();
        if (text == null) {
            text = error.getClass().getSimpleName();
        }

        // Set the icon, scrolling text and timestamp
        final Notification notification = new Notification(android.R.drawable.stat_sys_warning, title, System.currentTimeMillis());

        // The PendingIntent to launch our activity if the user selects this notification
        Intent intent = new Intent(this, ErrorActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(Constants.INTENT_EXTRA_NAME_ERROR, title + ".\n\n" + text);
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        notification.setLatestEventInfo(this, title, text, contentIntent);

        Log.i(TAG, "Sending error message: " + intent.getStringExtra(Constants.INTENT_EXTRA_NAME_ERROR));

        // Send the notification.
        handler.post(new Runnable() {
            @Override
            public void run() {
                notificationManager.cancel(Constants.NOTIFICATION_ID_STREAM_ERROR);
                notificationManager.notify(Constants.NOTIFICATION_ID_STREAM_ERROR, notification);
            }
        });
    }

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    public PlayerState getPlayerState() {
        return player.get().getPlayerState();
    }

    /**
     * Logic taken from packages/apps/Music.  Will pause when an incoming
     * call rings (volume > 0), or if a call (incoming or outgoing) is connected.
     */
    private class MyPhoneStateListener extends PhoneStateListener {
        private boolean resumeAfterCall;

        @Override
        public void onCallStateChanged(int state, String incomingNumber) {
            switch (state) {
                case TelephonyManager.CALL_STATE_RINGING:
                    AudioManager am = (AudioManager) getSystemService(AUDIO_SERVICE);

                    // Don't pause if the ringer isn't making any noise.
                    int ringvol = am.getStreamVolume(AudioManager.STREAM_RING);
                    if (ringvol <= 0) {
                        break;
                    }

                    // Fall through...
                case TelephonyManager.CALL_STATE_OFFHOOK:
                    if (getPlayerState() == STARTED) {
                        resumeAfterCall = true;
                        pause();
                    }
                    break;
                case TelephonyManager.CALL_STATE_IDLE:
                    if (resumeAfterCall) {
                        resumeAfterCall = false;
                        start();
                    }
                    break;
                default:
                    break;
            }
        }
    }

    public static enum PlayerState {
        IDLE(""),
        INITIALIZED(""),
        DOWNLOADING("Downloading"),
        PREPARING("Buffering"),
        PREPARED(""),
        STARTED("Playing"),
        STOPPED(""),
        PAUSED("Paused"),
        COMPLETED(""),
        ERROR("Error");

        private final String description;

        PlayerState(String description) {
            this.description = description;
        }

        @Override
        public String toString() {
            return description;
        }
    }


    private class Player {

        private boolean active;
        private final MediaPlayer mediaPlayer = new MediaPlayer();
        private PlayerState playerState = IDLE;
        private int duration;
        private final String tag;
        private PlaylistEntry song;
        private ScheduledFuture<?> fileCheckerFuture;

        public Player(String name) {
            tag = TAG + " (Player " + name + ")";

            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mediaPlayer) {
                    setPlayerState(PREPARED);
                    mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                    if (active) {
                        start();

                        // Start preparing the other player.
                        prepareNextPlayer(Player.this);
                    }
                }
            });
            mediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
                @Override
                public boolean onError(MediaPlayer mediaPlayer, int what, int more) {
                    Log.i(tag, "MediaPlayer error: " + what + " (" + more + ")");
                    setPlayerState(ERROR);
                    reset();
                    return false;
                }
            });
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mediaPlayer) {
                    setPlayerState(COMPLETED);
                    Log.i(tag, "End of media.");
                    StreamService.this.play(current.get() + 1);
                }
            });

            Runnable runnable = new Runnable() {
                private int position = 0;

                @Override
                public void run() {
                    int newPosition = mediaPlayer.getCurrentPosition();
                    if (newPosition != position) {
                        position = newPosition;
                        if (active) {
                            notifyProgressChanged();
                        }
                    }
                }
            };
            executorService.scheduleWithFixedDelay(runnable, 500L, 500L, TimeUnit.MILLISECONDS);

        }

        public void play(PlaylistEntry song) {
            this.song = song;
            String url = getStreamUrl(song);
            Log.i(tag, "Streaming URL: " + url);

            if (active) {
                notifyCurrentChanged();
            }

            // If currently preparing, no need to do anything.  Playback will start
            // automatically when entering PREPARED state.
            if (playerState == PREPARING) {
                return;
            }

            // If already prepared, just start playing.
            if (playerState == PlayerState.PREPARED) {
                start();

                // Start preparing the other player.
                prepareNextPlayer(Player.this);

                return;
            }

            // Otherwise, start preparing asynchronously.
            try {
                reset();
                mediaPlayer.setDataSource(url);
                setPlayerState(INITIALIZED);

                prepare(song);

            } catch (Exception e) {
                Log.e(tag, "Failed to start MediaPlayer.", e);
                setPlayerState(ERROR);
                addErrorNotification(song, e);
            }
        }

        private void prepare(PlaylistEntry song) {
            if (song.isDownload()) {
                final File file = getSongFile(song.getSong(), false);
                Runnable runnable = new Runnable() {
                    @Override
                    public void run() {
                        Log.d(tag, "Checking for existence of file " + file);
                        if (file.exists()) {
                            mediaPlayer.prepareAsync();
                            setPlayerState(PREPARING);
                        }
                    }
                };

                fileCheckerFuture = executorService.scheduleWithFixedDelay(runnable, 500L, 1000L, TimeUnit.MILLISECONDS);
                setPlayerState(DOWNLOADING);
            } else {
                mediaPlayer.prepareAsync();
                setPlayerState(PREPARING);
            }
        }

        public void pause() {
            mediaPlayer.pause();
            setPlayerState(PAUSED);
        }

        public void reset() {
            mediaPlayer.reset();
            setPlayerState(IDLE);
        }

        public void start() {
            mediaPlayer.start();
            setPlayerState(STARTED);
        }

        public void release() {
            mediaPlayer.release();
        }

        private void setPlayerState(PlayerState playerState) {
            Log.i(tag, this.playerState.name() + " -> " + playerState.name() + "  [" + song + "]");
            this.playerState = playerState;

            if (playerState != DOWNLOADING && fileCheckerFuture != null) {
                fileCheckerFuture.cancel(false);
            }

            if (playerState == PREPARED) {
                duration = mediaPlayer.getDuration();
            } else if (playerState == IDLE) {
                duration = 0;
            }

            if (active) {
                notifyProgressChanged();
                if (playerState == STARTED) {
                    showNotification();
                } else {
                    hideNotification();
                }
            }
        }

        public PlaylistEntry getSong() {
            return song;
        }

        public int getDuration() {
            return duration;
        }

        public PlayerState getPlayerState() {
            return playerState;
        }

        public int getCurrentPosition() {
            return mediaPlayer.getCurrentPosition();
        }

        public void setActive(boolean active) {
            this.active = active;
        }
    }

    private static class PlaylistEntry {

        private final MusicDirectory.Entry song;
        private final boolean download;

        private PlaylistEntry(MusicDirectory.Entry song, boolean download) {
            this.song = song;
            this.download = download;
        }

        public MusicDirectory.Entry getSong() {
            return song;
        }

        public boolean isDownload() {
            return download;
        }
    }
}
