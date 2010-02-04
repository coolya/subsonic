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
package net.sourceforge.subsonic.androidapp.util;

import android.content.Context;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Checkable;
import android.widget.CheckedTextView;
import android.widget.LinearLayout;
import android.widget.TextView;
import net.sourceforge.subsonic.androidapp.R;
import net.sourceforge.subsonic.androidapp.domain.MusicDirectory;
import net.sourceforge.subsonic.androidapp.service.DownloadFile;
import net.sourceforge.subsonic.androidapp.service.DownloadService;

import java.io.File;
import java.util.WeakHashMap;

/**
 * @author Sindre Mehus
 */
public class SongView extends LinearLayout implements Checkable {

    private static final String TAG = SongView.class.getSimpleName();
    private static final WeakHashMap<SongView, ?> INSTANCES = new WeakHashMap<SongView, Object>();

    private CheckedTextView checkedTextView;
    private TextView textView1;
    private TextView textView2;
    private TextView textView3;
    private TextView imageView1; // TODO: Remove
    private TextView imageView2;
    private DownloadFile downloadFile;
    private DownloadService downloadService;
    private static Handler handler;

    public SongView(Context context) {
        super(context);
        LayoutInflater.from(context).inflate(R.layout.song, this, true);

        checkedTextView = (CheckedTextView) findViewById(R.id.song_check);
        textView1 = (TextView) findViewById(R.id.song_text1);
        textView2 = (TextView) findViewById(R.id.song_text2);
        textView3 = (TextView) findViewById(R.id.song_text3);
        imageView1 = (TextView) findViewById(R.id.song_image1);
        imageView2 = (TextView) findViewById(R.id.song_image2);

        INSTANCES.put(this, null);
        int instanceCount = INSTANCES.size();
        if (instanceCount > 50) {
            Log.w(TAG, instanceCount + " live SongView instances");
        }
        startUpdater();
    }

    public void setDownloadFile(DownloadFile downloadFile, DownloadService downloadService, boolean checkable) {
        this.downloadFile = downloadFile;
        this.downloadService = downloadService;
        MusicDirectory.Entry song = downloadFile.getSong();

        StringBuilder text = new StringBuilder(40);
        text.append(song.getArtist()).append(" (");
        if (song.getBitRate() != null) {
            text.append(song.getBitRate()).append("k ");
        }
        text.append(song.getSuffix());
        if (song.getTranscodedSuffix() != null && !song.getTranscodedSuffix().equals(song.getSuffix())) {
            text.append(" > ").append(song.getTranscodedSuffix());
        }
        text.append(")");

        textView1.setText(song.getTitle());
        textView2.setText(text);
        textView3.setText(Util.formatDuration(song.getDuration()));
        checkedTextView.setVisibility(checkable ? View.VISIBLE : View.GONE);

        update();
    }

    private void update() {
        File completeFile = downloadFile.getCompleteFile();
        File partialFile = downloadFile.getPartialFile();

        int leftImage = 0;
        int rightImage = 0;

        if (completeFile.exists()) {
            leftImage = downloadFile.isSaved() ? R.drawable.saved : R.drawable.downloaded;
        }

        if (downloadFile.isDownloading() && !downloadFile.isDownloadCancelled() && partialFile.exists()) {
            imageView2.setText(Util.formatBytes(partialFile.length()));
            rightImage = R.drawable.downloading;
        } else {
            imageView2.setText(null);
        }
        imageView2.setCompoundDrawablesWithIntrinsicBounds(leftImage, 0, rightImage, 0);

        boolean playing = downloadService.getCurrentPlaying() == downloadFile;
        if (playing) {
            textView1.setCompoundDrawablesWithIntrinsicBounds(R.drawable.stat_sys_playing, 0, 0, 0);
        } else {
            textView1.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
        }
    }

    private static synchronized void startUpdater() {
        if (handler != null) {
            return;
        }

        handler = new Handler();
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                updateAll();
                handler.postDelayed(this, 1000L);
            }
        };
        handler.postDelayed(runnable, 1000L);
    }

    private static void updateAll() {
        try {
            for (SongView view : INSTANCES.keySet()) {
                if (view.isShown()) {
                    view.update();
                }
            }
        } catch (Throwable x) {
            Log.w(TAG, "Error when updating song views.", x);
        }
    }

    @Override
    public void setChecked(boolean b) {
        checkedTextView.setChecked(b);
    }

    @Override
    public boolean isChecked() {
        return checkedTextView.isChecked();
    }

    @Override
    public void toggle() {
        checkedTextView.toggle();
    }
}
