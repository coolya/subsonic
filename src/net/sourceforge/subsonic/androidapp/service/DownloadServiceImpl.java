/*
 * (c) Copyright WesternGeco. Unpublished work, created 2009. All rights
 * reserved under copyright laws. This information is confidential and is
 * the trade property of WesternGeco. Do not use, disclose, or reproduce
 * without the prior written permission of the owner.
 */
package net.sourceforge.subsonic.androidapp.service;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import android.app.NotificationManager;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;
import net.sourceforge.subsonic.androidapp.domain.DownloadFile;
import net.sourceforge.subsonic.androidapp.domain.MusicDirectory;
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

    @Override
    public void onCreate() {
        super.onCreate();

//        downloadThread = new DownloadThread();
//        downloadThread.start();
    }

    @Override
    public void onDestroy() {
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.cancel(Constants.NOTIFICATION_ID_DOWNLOAD_QUEUE);
        clear();
//        queue.offer(POISON);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @Override
    public void download(List<MusicDirectory.Entry> songs, boolean save, boolean play) {

//        DownloadFile downloadFile =

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
    public void play(int index) {
    }

    @Override
    public void seekTo(int position) {
    }

    @Override
    public void previous() {
    }

    @Override
    public void next() {
    }

    @Override
    public void pause() {
    }

    @Override
    public void start() {
    }

    @Override
    public StreamService.PlayerState getPlayerState() {
        return null;
    }

}
