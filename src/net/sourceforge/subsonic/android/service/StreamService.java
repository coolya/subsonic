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
package net.sourceforge.subsonic.android.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;
import net.sourceforge.subsonic.android.activity.StreamQueueActivity;
import net.sourceforge.subsonic.android.activity.ErrorActivity;
import net.sourceforge.subsonic.android.domain.MusicDirectory;
import net.sourceforge.subsonic.android.util.Constants;
import net.sourceforge.subsonic.android.util.SimpleServiceBinder;
import net.sourceforge.subsonic.android.util.Util;
import net.sourceforge.subsonic.android.util.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Sindre Mehus
 */
public class StreamService extends Service {

    private static final String TAG = StreamService.class.getSimpleName();
//    private static final MusicDirectory.Entry POISON = new MusicDirectory.Entry();

    private final MediaPlayer player = new MediaPlayer();
    private final IBinder binder = new SimpleServiceBinder<StreamService>(this);
    private final Handler handler = new Handler();

    private final AtomicInteger current = new AtomicInteger(-1);
    private final List<MusicDirectory.Entry> playlist = new CopyOnWriteArrayList<MusicDirectory.Entry>();
    private int duration;
    private final ScheduledExecutorService progressNotifier = Executors.newSingleThreadScheduledExecutor();

    @Override
    public void onCreate() {
        super.onCreate();

        player.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                duration = player.getDuration();
                broadcastChange(false);
                player.setAudioStreamType(AudioManager.STREAM_MUSIC);
                player.start();
                Log.i(TAG, "start() done");
            }
        });
        player.setOnBufferingUpdateListener(new MediaPlayer.OnBufferingUpdateListener() {
            @Override
            public void onBufferingUpdate(MediaPlayer mediaPlayer, int percent) {
                Log.i(TAG, "Buffer: " + percent + " %");
            }
        });
        player.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mediaPlayer, int what, int more) {
                Log.i(TAG, "MediaPlayer error: " + what + " (" + more + ")");
                return false;
            }
        });
        player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                Log.i(TAG, "End of media.");
                play(current.get() + 1);
            }
        });

        Runnable runnable = new Runnable() {
            private int position = 0;
            @Override
            public void run() {
                int newPosition = player.getCurrentPosition();
                if (newPosition != position) {
                    position = newPosition;
                    broadcastChange(false);
                }
            }
        };
        progressNotifier.scheduleWithFixedDelay(runnable, 500L, 500L, TimeUnit.MILLISECONDS);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.cancel(Constants.NOTIFICATION_ID_STREAM_QUEUE);

        player.reset();
        progressNotifier.shutdown();

//        clear();
//        queue.offer(POISON);
    }

    public void add(List<MusicDirectory.Entry> songs, boolean append) {
        boolean shouldStart = playlist.isEmpty() || !append;

        String message = songs.size() == 1 ? "Added \"" + songs.get(0).getTitle() + "\" to playlist." :
                         "Added " + songs.size() + " songs to playlist.";
        updateNotification();
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        if (!append) {
            playlist.clear();
        }

        playlist.addAll(songs);
        broadcastChange(false);

        if (shouldStart) {
            play(0);
        }
    }

    public void play(int index) {
        if (index < 0 || index >= playlist.size()) {
            return;
        }
        duration = 0;
        current.set(index);
        MusicDirectory.Entry song = playlist.get(index);
        String url = Util.getRestUrl(StreamService.this, "stream") + "&id=" + song.getId();

        broadcastChange(true);

        try {
            player.reset();
            Log.i(TAG, "reset() done");
            player.setDataSource(url);
            Log.i(TAG, "setDataSource() done");
            player.prepareAsync();
            Log.i(TAG, "prepareAsync() done");
        } catch (Exception e) {
            Log.e(TAG, "Failed to start MediaPlayer.", e);
            addErrorNotification(song, e);
        }
    }

    public void previous() {
        play(current.get() - 1);
    }

    public void next() {
        play(current.get() + 1);
    }

    public void stop() {
        player.reset();
    }

    public void togglePause() {
        if (player.isPlaying()) {
            player.pause();
        } else {
            player.start();
        }
    }

    public List<MusicDirectory.Entry> getQueue() {
        return new ArrayList<MusicDirectory.Entry>(playlist);
    }

//    public void remove(MusicDirectory.Entry song) {
//        if (current.get() == song) {
//            streamThread.interrupt();
//        } else if (playlist.remove(song)) {
//            broadcastChange(true);
//        }
//    }

//    public void clear() {
//        playlist.clear();
//        streamThread.interrupt();
//        broadcastChange(true);
//    }

    /**
     * The pair of longs contains (number of millis played, number of millis total).
     */
    public Pair<MusicDirectory.Entry, Pair<Long, Long>> getCurrent() {
        MusicDirectory.Entry current = getCurrentSong();
        if (current == null) {
            return null;
        }

        Pair<Long, Long> progress = new Pair<Long, Long>((long) player.getCurrentPosition(), (long) duration);
        return new Pair<MusicDirectory.Entry, Pair<Long, Long>>(current, progress);
    }

    private MusicDirectory.Entry getCurrentSong() {
        try {
            return playlist.get(current.get());
        } catch (IndexOutOfBoundsException e) {
            return null;
        }
    }

    private void broadcastChange(boolean queueChange) {
        if (queueChange) {
            sendBroadcast(new Intent(Constants.INTENT_ACTION_STREAM_QUEUE));
        }

        sendBroadcast(new Intent(Constants.INTENT_ACTION_STREAM_PROGRESS));
    }

    private void updateNotification() {
        final NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        MusicDirectory.Entry song = getCurrentSong();
        if (song == null) {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    notificationManager.cancel(Constants.NOTIFICATION_ID_STREAM_QUEUE);
                }
            });
        } else {

            // Use the same text for the ticker and the expanded notification
            String title = song.getTitle();

            // Set the icon, scrolling text and timestamp
            final Notification notification = new Notification(android.R.drawable.stat_sys_speakerphone, title, System.currentTimeMillis());
            notification.flags |= Notification.FLAG_NO_CLEAR | Notification.FLAG_ONGOING_EVENT;

            // The PendingIntent to launch our activity if the user selects this notification
            PendingIntent contentIntent = PendingIntent.getActivity(this, 0, new Intent(this, StreamQueueActivity.class), 0);

            String text = song.getArtist();
            notification.setLatestEventInfo(this, title, text, contentIntent);

            // Send the notification.
            handler.post(new Runnable() {
                @Override
                public void run() {
                    notificationManager.notify(Constants.NOTIFICATION_ID_STREAM_QUEUE, notification);
                }
            });
        }
    }

    private void addErrorNotification(MusicDirectory.Entry song, Exception error) {
        final NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        // Use the same text for the ticker and the expanded notification
        String title = "Failed to play \"" + song.getTitle() + "\"";

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
}