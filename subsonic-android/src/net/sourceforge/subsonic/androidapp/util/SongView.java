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
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Checkable;
import android.widget.CheckedTextView;
import android.widget.LinearLayout;
import android.widget.TextView;
import net.sourceforge.subsonic.androidapp.R;
import net.sourceforge.subsonic.androidapp.domain.MusicDirectory;
import net.sourceforge.subsonic.androidapp.service.DownloadFile;

import java.io.File;

/**
 * @author Sindre Mehus
 */
public class SongView extends LinearLayout implements Checkable {

    private CheckedTextView checkedTextView;
    private TextView textView1;
    private TextView textView2;
    private TextView textView3;
    private TextView imageView1; // TODO: Remove
    private TextView imageView2;

    public SongView(Context context) {
        super(context);
        LayoutInflater.from(context).inflate(R.layout.song, this, true);

        checkedTextView = (CheckedTextView) findViewById(R.id.song_check);
        textView1 = (TextView) findViewById(R.id.song_text1);
        textView2 = (TextView) findViewById(R.id.song_text2);
        textView3 = (TextView) findViewById(R.id.song_text3);
        imageView1 = (TextView) findViewById(R.id.song_image1);
        imageView2 = (TextView) findViewById(R.id.song_image2);
    }

    private void setSong(MusicDirectory.Entry song, File file) {
        if (file.exists()) {
            imageView2.setCompoundDrawablesWithIntrinsicBounds(R.drawable.downloaded, 0, 0, 0);
        } else {
            imageView2.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
        }

        StringBuilder text2 = new StringBuilder(40);
        text2.append(song.getArtist()).append(" (");
        if (song.getBitRate() != null) {
            text2.append(song.getBitRate()).append(" Kbps ");
        }
        text2.append(song.getSuffix());
        text2.append(")");

        textView1.setText(song.getTitle());
        textView2.setText(text2);
        textView3.setText(Util.formatDuration(song.getDuration()));
    }

    public void setDownloadFile(DownloadFile downloadFile, boolean playing, boolean checkable) {
        setSong(downloadFile.getSong(), downloadFile.getCompleteFile());
        checkedTextView.setVisibility(checkable ? View.VISIBLE : View.GONE);

        File completeFile = downloadFile.getCompleteFile();
        File partialFile = downloadFile.getPartialFile();
        if (partialFile.exists() && !completeFile.exists()) {
            imageView2.setText(Util.formatBytes(partialFile.length()));
        } else {
            imageView2.setText(null);
        }

        if (playing) {
            textView1.setCompoundDrawablesWithIntrinsicBounds(R.drawable.stat_sys_playing, 0, 0, 0);
        } else {
            textView1.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
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
