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

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.ContentValues;
import android.os.Binder;
import android.os.IBinder;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;
import android.provider.MediaStore;
import net.sourceforge.subsonic.android.util.Util;
import net.sourceforge.subsonic.android.domain.MusicDirectory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

/**
 * @author Sindre Mehus
 */
public class DownloadService extends Service {

    private static final String TAG = DownloadService.class.getSimpleName();
    private final IBinder binder = new DownloadBinder();
    private final Handler handler = new Handler();
    private final BlockingQueue<MusicDirectory.Entry> queue = new ArrayBlockingQueue<MusicDirectory.Entry>(10);

    public DownloadService() {
        new DownloadThread().start();
    }

    public void download(MusicDirectory.Entry song) {
        showNotification(queue.size() + 1);
        Toast.makeText(this, "Added " + song.getName() + " to download queue.", Toast.LENGTH_SHORT).show();
        queue.add(song);
    }

    private void showNotification(int queueSize) {
        // In this sample, we'll use the same text for the ticker and the expanded notification
        String text = "Download queue: " + queueSize;

        // Set the icon, scrolling text and timestamp
        Notification notification = new Notification(android.R.drawable.ic_media_play, text,
                                                     System.currentTimeMillis());

        // The PendingIntent to launch our activity if the user selects this notification
        // TODO
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, new Intent(this, DownloadService.class), 0);

        // Set the info for the views that show in the notification panel.
        notification.setLatestEventInfo(this, "Subsonic is downloading", text, contentIntent);

        // Send the notification.
        // We use a layout id because it is a unique number.  We use it later to cancel.
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.notify(Constants.NOTIFICATION_ID_DOWNLOAD_QUEUE, notification);
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

    public class DownloadBinder extends Binder {
        public DownloadService getService() {
            return DownloadService.this;
        }
    }

    private class DownloadThread extends Thread {
        @Override
        public void run() {
            while (true) {
                MusicDirectory.Entry song = null;
                try {
                    song = queue.take();
                    downloadToFile(song);
                } catch (Exception e) {
                    Log.e(TAG, "Failed to download " + song);
                }
            }
        }

        private void downloadToFile(final MusicDirectory.Entry song) throws Exception {
            Log.i(TAG, "Starting to download " + song);
            File file = File.createTempFile("subsonic", null);
            InputStream in = null;
            FileOutputStream out = null;
            try {
                in = new URL(song.getUrl()).openStream();
                out = new FileOutputStream(file);
                long n = Util.copy(in, out);

                out.flush();
                out.close();
                saveInMediaStore(song, file);

                Log.i(TAG, "Downloaded " + n + " bytes to " + file);
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        showNotification(queue.size());
                        Toast.makeText(DownloadService.this, "Finished downloading " + song.getName() + ".", Toast.LENGTH_SHORT).show();
                    }
                });
            } catch (Exception e) {
                Log.e(TAG, "Failed to download stream.", e);
            } finally {
                Util.close(in);
                Util.close(out);
            }
        }

        private void saveInMediaStore(MusicDirectory.Entry song, File file) {
            ContentValues values = new ContentValues();
//                values.put(MediaStore.MediaColumns.DISPLAY_NAME, "foo");
            values.put(MediaStore.MediaColumns.TITLE, song.getName());
//                values.put(MediaStore.Audio.AudioColumns.ARTIST, "John Doe");
//                values.put(MediaStore.Audio.AudioColumns.ALBUM, "Pyromantikk");
            values.put(MediaStore.MediaColumns.DATA, file.getAbsolutePath());
//                values.put(MediaStore.MediaColumns.MIME_TYPE, "audio/mpeg");
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
