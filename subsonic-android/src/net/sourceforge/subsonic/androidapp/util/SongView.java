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
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.Checkable;
import android.widget.CheckedTextView;
import android.widget.LinearLayout;
import net.sourceforge.subsonic.androidapp.R;
import net.sourceforge.subsonic.androidapp.domain.MusicDirectory;

import java.io.File;

/**
 * @author Sindre Mehus
 */
public class SongView extends LinearLayout implements Checkable {
    private static final String TAG = SongView.class.getSimpleName();
    private CheckedTextView checkedTextView;

    public SongView(Context context) {
        super(context);
        LayoutInflater.from(context).inflate(R.layout.song, this, true);

        checkedTextView = (CheckedTextView) findViewById(R.id.song_text);
        Log.i(TAG, String.valueOf(checkedTextView));
    }

    public void setSong(MusicDirectory.Entry song, File file) {
        if (file.exists()) {
            checkedTextView.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.downloaded, 0);
        } else {
            checkedTextView.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
        }
        checkedTextView.setText(song.getTitle());
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
