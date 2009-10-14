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
import android.widget.Checkable;
import android.widget.CheckedTextView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import net.sourceforge.subsonic.androidapp.R;
import net.sourceforge.subsonic.androidapp.domain.MusicDirectory;

import java.io.File;

/**
 * @author Sindre Mehus
 */
public class SongView extends LinearLayout implements Checkable {
    private CheckedTextView checkedTextView;
    private TextView textView1;
    private TextView textView2;
    private TextView textView3;
    private ImageView imageView;

    public SongView(Context context) {
        super(context);
        LayoutInflater.from(context).inflate(R.layout.song, this, true);

        checkedTextView = (CheckedTextView) findViewById(R.id.song_check);
        textView1 = (TextView) findViewById(R.id.song_text1);
        textView2 = (TextView) findViewById(R.id.song_text2);
        textView3 = (TextView) findViewById(R.id.song_text3);
        imageView = (ImageView) findViewById(R.id.song_image);
    }

    public void setSong(MusicDirectory.Entry song, File file) {
        if (file.exists()) {
            imageView.setImageResource(R.drawable.downloaded);
        } else {
            imageView.setImageDrawable(null);
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
