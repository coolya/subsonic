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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;
import net.sourceforge.subsonic.android.activity.DownloadQueueActivity;
import net.sourceforge.subsonic.android.activity.ErrorActivity;
import net.sourceforge.subsonic.android.domain.MusicDirectory;
import net.sourceforge.subsonic.android.util.Constants;
import net.sourceforge.subsonic.android.util.Pair;
import net.sourceforge.subsonic.android.util.SimpleServiceBinder;
import net.sourceforge.subsonic.android.util.Util;

/**
 * @author Sindre Mehus
 */
public class DownloadService extends Service {

    private static final String TAG = DownloadService.class.getSimpleName();
    private static final Uri ALBUM_ART_URI = Uri.parse("content://media/external/audio/albumart");
    private static final MusicDirectory.Entry POISON = new MusicDirectory.Entry();

    private final IBinder binder = new SimpleServiceBinder<DownloadService>(this);
    private final Handler handler = new Handler();

    private final LinkedBlockingQueue<MusicDirectory.Entry> queue = new LinkedBlockingQueue<MusicDirectory.Entry>();
    private final AtomicReference<MusicDirectory.Entry> currentDownload = new AtomicReference<MusicDirectory.Entry>();
    private final AtomicLong currentProgress = new AtomicLong();
    private File musicDir;
    private File stateDir;
    private DownloadService.DownloadThread downloadThread;

    @Override
    public void onCreate() {
        musicDir = createDirectory("music");
        stateDir = createDirectory("state");

        loadQueue();
        downloadThread = new DownloadThread();
        downloadThread.start();
    }

    @Override
    public void onDestroy() {
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.cancel(Constants.NOTIFICATION_ID_DOWNLOAD_QUEUE);

        saveQueue();
        clear();
        queue.offer(POISON);
    }

    private void loadQueue() {
        File file = new File(stateDir, "queue.dat");
        if (!file.exists()) {
            return;
        }

        ObjectInputStream in = null;
        try {
            in = new ObjectInputStream(new FileInputStream(file));
            List<MusicDirectory.Entry> q = (List<MusicDirectory.Entry>) in.readObject();
            queue.addAll(q);
            Log.i(TAG, "Loaded download queue from disk: " + q.size());
        } catch (Exception x) {
            Util.close(in);
            Log.w(TAG, "Failed to load download queue.", x);
        } finally {
            file.delete();
        }

    }

    private void saveQueue() {
        ObjectOutputStream out = null;
        try {
            out = new ObjectOutputStream(new FileOutputStream(new File(stateDir, "queue.dat")));

            List<MusicDirectory.Entry> q = getQueue();
            MusicDirectory.Entry current = currentDownload.get();
            if (current != null && !q.contains(current)) {
                q.add(0, current);
            }

            out.writeObject(q);
            out.flush();
            Log.i(TAG, "Saved download queue to disk: " + q.size());
        } catch (Exception x) {
            Util.close(out);
            Log.w(TAG, "Failed to save download queue.", x);
        }
    }

    private File createDirectory(String name) {
        File subsonicDir = new File(Environment.getExternalStorageDirectory(), "subsonic");
        File dir = new File(subsonicDir, name);
        if (!dir.exists() && !dir.mkdirs()) {
            Log.e(TAG, "Failed to create " + name);
        }
        return dir;
    }

    public void download(List<MusicDirectory.Entry> songs) {
        String message = songs.size() == 1 ? "Added \"" + songs.get(0).getTitle() + "\" to download queue." :
                "Added " + songs.size() + " songs to download queue.";
        updateNotification();
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        queue.addAll(songs);
        broadcastChange(false);
    }

    public List<MusicDirectory.Entry> getQueue() {
        return new ArrayList<MusicDirectory.Entry>(queue);
    }

    public void remove(MusicDirectory.Entry song) {
        if (currentDownload.get() == song) {
            downloadThread.interrupt();
        } else if (queue.remove(song)) {
            broadcastChange(true);
        }
    }

    public void clear() {
        queue.clear();
        downloadThread.interrupt();
        broadcastChange(true);
    }

    /**
     * The pair of longs contains (number of bytes downloaded, number of bytes total).  The latter
     * may be null if unknown.
     */
    public Pair<MusicDirectory.Entry, Pair<Long, Long>> getCurrent() {
        MusicDirectory.Entry current = this.currentDownload.get();
        if (current == null) {
            return null;
        }

        Pair<Long, Long> progress = new Pair<Long, Long>(currentProgress.get(), current.getSize());
        return new Pair<MusicDirectory.Entry, Pair<Long, Long>>(current, progress);
    }

    private void broadcastChange(boolean queueChange) {
        if (queueChange) {
            sendBroadcast(new Intent(Constants.INTENT_ACTION_DOWNLOAD_QUEUE));
        }

        sendBroadcast(new Intent(Constants.INTENT_ACTION_DOWNLOAD_PROGRESS));
    }

    private void updateNotification() {
        final NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        MusicDirectory.Entry song = currentDownload.get();
        if (song == null && queue.isEmpty()) {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    notificationManager.cancel(Constants.NOTIFICATION_ID_DOWNLOAD_QUEUE);
                }
            });
        } else {

            if (song == null) {
                return;
            }

            // Use the same text for the ticker and the expanded notification
            String title = song.getTitle();

            // Set the icon, scrolling text and timestamp
            final Notification notification = new Notification(android.R.drawable.stat_sys_download, title, System.currentTimeMillis());
            notification.flags |= Notification.FLAG_NO_CLEAR | Notification.FLAG_ONGOING_EVENT;

            // The PendingIntent to launch our activity if the user selects this notification
            PendingIntent contentIntent = PendingIntent.getActivity(this, 0, new Intent(this, DownloadQueueActivity.class), 0);

            String text = song.getArtist();
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
        String title = "Failed to download \"" + song.getTitle() + "\"";

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
                notificationManager.cancel(Constants.NOTIFICATION_ID_DOWNLOAD_ERROR);
                notificationManager.notify(Constants.NOTIFICATION_ID_DOWNLOAD_ERROR, notification);
            }
        });
    }

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    private String getDownloadURL(MusicDirectory.Entry song) {
        return Util.getRestUrl(this, "download") + "&id=" + song.getId();
    }

    private String getAlbumArtURL(MusicDirectory.Entry song) {
        return Util.getRestUrl(this, "getCoverArt") + "&id=" + song.getCoverArt() + "&size=200";
    }

    private class DownloadThread extends Thread {

        @Override
        public void run() {
            while (true) {
                try {
                    MusicDirectory.Entry song = queue.take();
                    if (song == POISON) {
                        return;
                    }

                    downloadToFile(song);
                } catch (InterruptedException x) {
                    Log.i(TAG, "Download thread interrupted. Continuing.");
                }
            }
        }

        private void downloadToFile(final MusicDirectory.Entry song) throws InterruptedException {
            Log.i(TAG, "Starting to download " + song);
            currentProgress.set(0L);
            currentDownload.set(song);
            updateNotification();
            broadcastChange(true);

            InputStream in = null;
            FileOutputStream out = null;
            File file = null;
            try {
                file = createSongFile(song);
                in = connect(getDownloadURL(song));
                out = new FileOutputStream(file);
                long n = copy(in, out);
                Log.i(TAG, "Downloaded " + n + " bytes to " + file);

                out.flush();
                out.close();

                saveInMediaStore(song, file);
                Util.toast(DownloadService.this, handler, "Finished downloading \"" + song.getTitle() + "\".");

            } catch (Exception e) {
                Util.close(out);
                Util.delete(file);
                if (e instanceof InterruptedException) {
                    throw (InterruptedException) e;
                }

                Log.e(TAG, "Failed to download stream.", e);
                addErrorNotification(song, e);
                Util.toast(DownloadService.this, handler, "Failed to download \"" + song.getTitle() + "\".");
            } finally {
                Util.close(in);
                Util.close(out);
                currentDownload.set(null);
                updateNotification();
                broadcastChange(true);
            }
        }

        private File createSongFile(MusicDirectory.Entry song) {
            File dir = getAlbumDirectory(song);

            String title = Util.fileSystemSafe(song.getTitle());
            return new File(dir, title + "." + song.getSuffix());
        }

        private File createAlbumArtFile(MusicDirectory.Entry song) {
            File dir = getAlbumDirectory(song);
            return new File(dir, "folder.jpeg");
        }

        private File getAlbumDirectory(MusicDirectory.Entry song) {
            String artist = Util.fileSystemSafe(song.getArtist());
            String album = Util.fileSystemSafe(song.getAlbum());

            File dir = new File(musicDir.getPath() + "/" + artist + "/" + album);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            return dir;
        }

        private InputStream connect(String url) throws Exception {
            URLConnection connection = new URL(url).openConnection();
            connection.setConnectTimeout(Constants.SOCKET_TIMEOUT);
            connection.setReadTimeout(Constants.SOCKET_TIMEOUT);
            connection.connect();
            InputStream in = connection.getInputStream();

            // If content type is XML, an error occured.  Get it.
            String contentType = connection.getContentType();
            if (contentType != null && contentType.startsWith("text/xml")) {
                try {
                    new ErrorParser().parse(new InputStreamReader(in, Constants.UTF_8));
                } finally {
                    Util.close(in);
                }
            }

            return in;
        }

        private File downloadAlbumArt(MusicDirectory.Entry song) {
            if (song.getCoverArt() == null) {
                return null;
            }

            InputStream in = null;
            FileOutputStream out = null;
            File file = null;
            try {
                file = createAlbumArtFile(song);
                in = connect(getAlbumArtURL(song));
                out = new FileOutputStream(file);
                Util.copy(in, out);
            } catch (Exception e) {
                Util.delete(file);
                Log.e(TAG, "Failed to download album art.", e);
            } finally {
                Util.close(in);
                Util.close(out);
            }
            return file;
        }

        private long copy(InputStream in, OutputStream out) throws IOException, InterruptedException {
            byte[] buffer = new byte[1024 * 16];
            long count = 0;
            int n;
            long lastBroadcast = System.currentTimeMillis();

            while ((n = in.read(buffer)) != -1) {
                if (Thread.interrupted()) {
                    throw new InterruptedException("Interrupted while downloading");
                }

                out.write(buffer, 0, n);
                count += n;
                currentProgress.addAndGet(n);

                long now = System.currentTimeMillis();
                if (now - lastBroadcast > 250L) {  // Only every so often.
                    broadcastChange(false);
                    lastBroadcast = now;
                }
            }
            broadcastChange(false);
            return count;
        }

        private void saveInMediaStore(MusicDirectory.Entry song, File songFile) {
            ContentResolver contentResolver = getContentResolver();

            // Delete existing row in case the song has been downloaded before.
            int n = contentResolver.delete(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                    MediaStore.Audio.AudioColumns.TITLE_KEY + "=? AND " +
                            MediaStore.MediaColumns.DATA + "=?",
                    new String[]{MediaStore.Audio.keyFor(song.getTitle()), songFile.getAbsolutePath()});
            if (n > 0) {
                Log.i(TAG, "Overwriting media store row for " + song);
            }

            ContentValues values = new ContentValues();
            values.put(MediaStore.MediaColumns.TITLE, song.getTitle());
            values.put(MediaStore.Audio.AudioColumns.ARTIST, song.getArtist());
            values.put(MediaStore.Audio.AudioColumns.ALBUM, song.getAlbum());
            values.put(MediaStore.Audio.AudioColumns.TRACK, song.getTrack());
            values.put(MediaStore.Audio.AudioColumns.YEAR, song.getYear());
            values.put(MediaStore.MediaColumns.DATA, songFile.getAbsolutePath());
            values.put(MediaStore.MediaColumns.MIME_TYPE, song.getContentType());
            values.put(MediaStore.Audio.AudioColumns.IS_MUSIC, 1);

            Uri uri = contentResolver.insert(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, values);

            // Look up album, and add cover art if found.
            Cursor cursor = contentResolver.query(uri, new String[]{MediaStore.Audio.AudioColumns.ALBUM_ID}, null, null, null);
            if (cursor.moveToFirst()) {
                int albumId = cursor.getInt(0);
                insertAlbumArt(albumId, song);
            }
            cursor.close();
        }

        private void insertAlbumArt(int albumId, MusicDirectory.Entry song) {
            ContentResolver contentResolver = getContentResolver();

            Cursor cursor = contentResolver.query(Uri.withAppendedPath(ALBUM_ART_URI, String.valueOf(albumId)), null, null, null, null);
            if (!cursor.moveToFirst()) {

                // No album art found, add it.
                File albumArtFile = downloadAlbumArt(song);
                if (albumArtFile == null) {
                    return;
                }

                ContentValues values = new ContentValues();
                values.put(MediaStore.Audio.AlbumColumns.ALBUM_ID, albumId);
                values.put(MediaStore.MediaColumns.DATA, albumArtFile.getPath());
                contentResolver.insert(ALBUM_ART_URI, values);
                Log.i(TAG, "Added album art: " + albumArtFile);
            }
            cursor.close();
        }
    }
}
