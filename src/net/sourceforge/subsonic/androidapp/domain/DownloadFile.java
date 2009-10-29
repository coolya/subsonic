/*
 * (c) Copyright WesternGeco. Unpublished work, created 2009. All rights
 * reserved under copyright laws. This information is confidential and is
 * the trade property of WesternGeco. Do not use, disclose, or reproduce
 * without the prior written permission of the owner.
 */
package net.sourceforge.subsonic.androidapp.domain;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.IOException;
import java.net.URLConnection;
import java.net.URL;
import java.util.concurrent.atomic.AtomicBoolean;

import android.content.Context;
import android.util.Log;
import net.sourceforge.subsonic.androidapp.util.FileUtil;
import net.sourceforge.subsonic.androidapp.util.Util;
import net.sourceforge.subsonic.androidapp.util.Constants;
import net.sourceforge.subsonic.androidapp.util.CancellableTask;
import net.sourceforge.subsonic.androidapp.service.ErrorParser;

/**
 * @author Sindre Mehus
 * @version $Id$
 */
public class DownloadFile {

    private static final String TAG = DownloadFile.class.getSimpleName();
    private final Context context;
    private final MusicDirectory.Entry song;
    private final File file;
    private final File tempFile;
    private final AtomicBoolean complete = new AtomicBoolean(false);
    private CancellableTask downloadTask;

    public DownloadFile(Context context, MusicDirectory.Entry song) {
        this.context = context;
        this.song = song;
        file = FileUtil.getSongFile(song, true);
        tempFile = new File(file.getPath() + ".tmp");

        if (file.exists()) {
            complete.set(true);
        }
    }

    public synchronized void download() {
        if (complete.get()) {
            return;
        }

        downloadTask = new CancellableTask() {
            @Override
            public void execute() {
                try {
                    doDownload();
                } catch (InterruptedException x) {
                    // Intentionally ignored.
                }
            }
        };
        downloadTask.start();
    }

    public synchronized void cancelDownload() {
        if (downloadTask != null) {
            downloadTask.cancel();
        }
    }

    public File getFile() {
        return file;
    }

    public File getTempFile() {
        return tempFile;
    }

    private void doDownload() throws InterruptedException {


        Log.i(TAG, "Starting to download " + song);
//            currentProgress.set(0L);
//            currentDownload.set(song);
//            updateNotification();
//            broadcastChange(true);

        InputStream in = null;
        FileOutputStream out = null;
        try {
            in = connect(getDownloadURL(song));
            out = new FileOutputStream(tempFile);
            long n = copy(in, out);
            Log.i(TAG, "Downloaded " + n + " bytes to " + tempFile);

            out.flush();
            out.close();

//                if (!tmpFile.renameTo(file)) {
//                    throw new IOException("Failed to rename " + tmpFile + " to " + file);
//                }

//                saveInMediaStore(song, file);
//                Util.toast(context, handler, "Finished downloading \"" + song.getTitle() + "\".");

        } catch (Exception e) {
            Util.close(out);
            Util.delete(file);
            Util.delete(tempFile);
            if (e instanceof InterruptedException) {
                throw (InterruptedException) e;
            }

            Log.e(TAG, "Failed to download stream.", e);
//                addErrorNotification(song, e);
//                Util.toast(DownloadService.this, handler, "Failed to download \"" + song.getTitle() + "\".");
        } finally {
            Util.close(in);
            Util.close(out);
            complete.set(true);
//            Util.delete(tmpFile);
//                currentDownload.set(null);
//                updateNotification();
//                broadcastChange(true);
        }
    }

    private String getDownloadURL(MusicDirectory.Entry song) {
        return Util.getRestUrl(context, "download") + "&id=" + song.getId();
    }

    private InputStream connect(String url) throws Exception {
        URLConnection connection = new URL(url).openConnection();
        connection.setConnectTimeout(Constants.SOCKET_CONNECT_TIMEOUT);
        connection.setReadTimeout(Constants.SOCKET_READ_TIMEOUT);
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
//            currentProgress.addAndGet(n);

            long now = System.currentTimeMillis();
            if (now - lastBroadcast > 2000L) {  // Only every so often.
                Log.i(TAG, "Downloaded " + Util.formatBytes(count));
                lastBroadcast = now;
            }
        }
//        broadcastChange(false);
        return count;
    }

    public boolean isComplete() {
        return complete.get();
    }
}
