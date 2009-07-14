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
package net.sourceforge.subsonic.android;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ContentValues;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;
import net.sourceforge.subsonic.android.domain.MusicDirectory;
import net.sourceforge.subsonic.android.util.Util;

/**
 * @author Sindre Mehus
 */
public class DownloadService extends Service {

    private static final String TAG = DownloadService.class.getSimpleName();
    private final IBinder binder = new DownloadBinder();
    private final Handler handler = new Handler();
    private final BlockingQueue<MusicDirectory.Entry> queue = new ArrayBlockingQueue<MusicDirectory.Entry>(10);

    private final AtomicInteger pendingDownloadCount = new AtomicInteger();
    private final AtomicReference<MusicDirectory.Entry> currentDownload = new AtomicReference<MusicDirectory.Entry>();

    public DownloadService() {
        new DownloadThread().start();
    }

    public void download(List<MusicDirectory.Entry> songs) {
        String message = songs.size() == 1 ? "Added \"" + songs.get(0).getName() + "\" to download queue." :
                "Added " + songs.size() + " songs to download queue.";
        pendingDownloadCount.addAndGet(songs.size());
        updateNotification();
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        queue.addAll(songs);
    }

    private void updateNotification() {
        final NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        if (pendingDownloadCount.get() == 0) {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    notificationManager.cancel(Constants.NOTIFICATION_ID_DOWNLOAD_QUEUE);
                }
            });
        } else {

            // Use the same text for the ticker and the expanded notification
            String title = "Download queue: " + pendingDownloadCount;

            // Set the icon, scrolling text and timestamp
            // TODO: Change icon.
            final Notification notification = new Notification(android.R.drawable.ic_media_play, title, System.currentTimeMillis());

            // The PendingIntent to launch our activity if the user selects this notification
            // TODO
            PendingIntent contentIntent = PendingIntent.getActivity(this, 0, new Intent(this, DownloadService.class), 0);

            // Set the info for the views that show in the notification panel.
            MusicDirectory.Entry song = currentDownload.get();
            String text = "Downloading";
            if (song != null) {
                text = "Downloading \"" + song.getName() + "\"";
            }
            notification.setLatestEventInfo(this, title, text, contentIntent);

            // Send the notification.
            handler.post(new Runnable() {
                @Override
                public void run() {
                    notificationManager.notify(Constants.NOTIFICATION_ID_DOWNLOAD_QUEUE, notification);
                }
            });
        }
    }

    private void addErrorNotification(MusicDirectory.Entry song, Exception error) {
        final NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        // Use the same text for the ticker and the expanded notification
        String title = "Failed to download \"" + song.getName() + "\"";

        String text = error.getMessage();
        if (text == null) {
            text = error.getClass().getSimpleName();
        }


        // Set the icon, scrolling text and timestamp
        // TODO: Change icon.
        final Notification notification = new Notification(android.R.drawable.ic_media_play, title, System.currentTimeMillis());

        // The PendingIntent to launch our activity if the user selects this notification
        // TODO
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, new Intent(this, DownloadService.class), 0);
        notification.setLatestEventInfo(this, title, text, contentIntent);

        // Send the notification.
        handler.post(new Runnable() {
            @Override
            public void run() {
                notificationManager.notify(Constants.NOTIFICATION_ID_DOWNLOAD_ERROR, notification);
            }
        });
    }

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public void onDestroy() {
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.cancel(Constants.NOTIFICATION_ID_DOWNLOAD_QUEUE);
    }

    private String getDownloadURL(MusicDirectory.Entry song) {
        String url = getSharedPreferences(Constants.PREFERENCES_FILE_NAME, 0).getString(Constants.PREFERENCES_KEY_SERVER_URL, null);
        if (!url.endsWith("/")) {
            url += "/";
        }
        return url + "stream?pathUtf8Hex=" + song.getId();
    }

    public class DownloadBinder extends Binder {
        public DownloadService getService() {
            return DownloadService.this;
        }
    }

    private class DownloadThread extends Thread {
        @Override
        public void run() {
            while (!isInterrupted()) {
                try {
                    downloadToFile(queue.take());
                } catch (InterruptedException x) {
                    Log.i(TAG, "Download thread interrupted. Stopping.");
                    return;
                }
            }
        }

        private void downloadToFile(final MusicDirectory.Entry song) {
            Log.i(TAG, "Starting to download " + song);
            currentDownload.set(song);
            updateNotification();

            InputStream in = null;
            FileOutputStream out = null;
            try {
                File file = File.createTempFile("subsonic", "." + song.getSuffix());
                in = new URL(getDownloadURL(song)).openStream();
                out = new FileOutputStream(file);
                long n = Util.copy(in, out);

                out.flush();
                out.close();
                saveInMediaStore(song, file);

                Log.i(TAG, "Downloaded " + n + " bytes to " + file);
                Util.toast(DownloadService.this, handler, "Finished downloading \"" + song.getName() + "\".");

            } catch (Exception e) {
                Log.e(TAG, "Failed to download stream.", e);
                addErrorNotification(song, e);
                Util.toast(DownloadService.this, handler, "Failed to download \"" + song.getName() + "\".");
                // TODO: Show notification/toast.
            } finally {
                Util.close(in);
                Util.close(out);
                pendingDownloadCount.decrementAndGet();
                updateNotification();
            }
        }

        private void saveInMediaStore(MusicDirectory.Entry song, File file) {
            ContentValues values = new ContentValues();
//                values.put(MediaStore.MediaColumns.DISPLAY_NAME, "foo");
            values.put(MediaStore.MediaColumns.TITLE, song.getName());
//                values.put(MediaStore.Audio.AudioColumns.ARTIST, "John Doe");
//                values.put(MediaStore.Audio.AudioColumns.ALBUM, "Pyromantikk");
            values.put(MediaStore.MediaColumns.DATA, file.getAbsolutePath());
            values.put(MediaStore.MediaColumns.MIME_TYPE, song.getContentType());
//        values.put(MediaStore.Audio.AudioColumns.ARTIST, "Sindre");
//        values.put(MediaStore.Audio.AudioColumns.ALBUM, "Pick");
//        values.put(MediaStore.Audio.AudioColumns.DURATION, 15000L);
//        values.put(MediaStore.Audio.AudioColumns.DATE_ADDED, System.currentTimeMillis() / 1000L);
//        values.put(MediaStore.Audio.AudioColumns.IS_ALARM, 0);
            values.put(MediaStore.Audio.AudioColumns.IS_MUSIC, 1);
//        values.put(MediaStore.Audio.AudioColumns.IS_NOTIFICATION, 0);
//        values.put(MediaStore.Audio.AudioColumns.IS_RINGTONE, 0);

            // Add a new record without the bitmap, but with the values just set.
            // insert() returns the URI of the new record.
            getContentResolver().insert(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, values);
        }
    }
}
