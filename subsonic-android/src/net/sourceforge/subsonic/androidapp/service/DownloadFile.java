/*
 * (c) Copyright WesternGeco. Unpublished work, created 2009. All rights
 * reserved under copyright laws. This information is confidential and is
 * the trade property of WesternGeco. Do not use, disclose, or reproduce
 * without the prior written permission of the owner.
 */
package net.sourceforge.subsonic.androidapp.service;

import android.content.Context;
import android.util.Log;
import net.sourceforge.subsonic.androidapp.domain.MusicDirectory;
import net.sourceforge.subsonic.androidapp.util.CancellableTask;
import net.sourceforge.subsonic.androidapp.util.Constants;
import net.sourceforge.subsonic.androidapp.util.FileUtil;
import net.sourceforge.subsonic.androidapp.util.Util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

/**
 * @author Sindre Mehus
 * @version $Id$
 */
public class DownloadFile {

    private static final String TAG = DownloadFile.class.getSimpleName();
    private final Context context;
    private final MusicDirectory.Entry song;
    private final File partialFile;
    private final File completeFile;
    private final File saveFile;
    private final MediaStoreService mediaStoreService;
    private CancellableTask downloadTask;
    private boolean save;

    public DownloadFile(Context context, MusicDirectory.Entry song, boolean save) {
        this.context = context;
        this.song = song;
        this.save = save;
        saveFile = FileUtil.getSongFile(song, true);
        partialFile = new File(saveFile.getPath() + ".partial");
        completeFile = new File(saveFile.getPath() + ".complete");
        mediaStoreService = new MediaStoreService(context);
    }

    public MusicDirectory.Entry getSong() {
        return song;
    }

    public synchronized void download() {
        if (isComplete()) {
            return;
        }

        downloadTask = new DownloadTask();
        downloadTask.start();
    }

    public synchronized void cancelDownload() {
        if (downloadTask != null) {
            downloadTask.cancel();
        }
    }

    public File getCompleteFile() {
        if (saveFile.exists()) {
            return saveFile;
        }

        if (completeFile.exists()) {
            return completeFile;
        }

        Log.w(TAG, "No complete file exists for " + saveFile);
        return null;
    }

    public File getPartialFile() {
        return partialFile;
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

    public boolean isComplete() {
        return saveFile.exists() || completeFile.exists();
    }

    private class DownloadTask extends CancellableTask {

        @Override
        public void execute() {

            Log.i(TAG, "Starting to download " + song);
//            updateNotification();

            InputStream in = null;
            FileOutputStream out = null;
            try {
                in = connect(getDownloadURL(song));
                out = new FileOutputStream(partialFile);
                long n = copy(in, out);
                Log.i(TAG, "Downloaded " + n + " bytes to " + partialFile);
                out.flush();
                out.close();

                Util.atomicCopy(partialFile, completeFile);
                if (save) {
                    Util.atomicCopy(partialFile, saveFile);
                    mediaStoreService.saveInMediaStore(DownloadFile.this);
                }
                if (isCancelled()) {
                    throw new Exception("Download of " + song + " was cancelled");
                }

            } catch (Exception e) {
                Util.close(out);
                Util.delete(partialFile);
                Util.delete(completeFile);
                Util.delete(saveFile);
                if (!isCancelled()) {
                    Log.e(TAG, "Failed to download stream.", e);
//                addErrorNotification(song, e);
                }

            } finally {
                Util.close(in);
                Util.close(out);
//                updateNotification();
            }
        }

        private long copy(InputStream in, OutputStream out) throws IOException, InterruptedException {
            byte[] buffer = new byte[1024 * 16];
            long count = 0;
            int n;
            long lastBroadcast = System.currentTimeMillis();

            while (!isCancelled() && (n = in.read(buffer)) != -1) {
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
    }
}
