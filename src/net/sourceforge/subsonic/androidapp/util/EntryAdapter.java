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

 Copyright 2010 (C) Sindre Mehus
 */
package net.sourceforge.subsonic.androidapp.util;

import net.sourceforge.subsonic.androidapp.domain.MusicDirectory;
import net.sourceforge.subsonic.androidapp.activity.SubsonicTabActivity;
import net.sourceforge.subsonic.androidapp.service.DownloadService;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.view.View;
import android.view.ViewGroup;
import android.view.LayoutInflater;

import java.util.List;

/**
 * @author Sindre Mehus
*/
public class EntryAdapter extends ArrayAdapter<MusicDirectory.Entry> {

    private final SubsonicTabActivity activity;
    private final ImageLoader imageLoader;

    public EntryAdapter(SubsonicTabActivity activity, ImageLoader imageLoader, List<MusicDirectory.Entry> entries) {
        super(activity, android.R.layout.simple_list_item_1, entries);
        this.activity = activity;
        this.imageLoader = imageLoader;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        MusicDirectory.Entry entry = getItem(position);

        if (entry.isDirectory()) {
            TextView view;
            view = (TextView) LayoutInflater.from(activity).inflate(
                    android.R.layout.simple_list_item_1, parent, false);

            view.setCompoundDrawablePadding(10);
            imageLoader.loadImage(view, entry, false);
            view.setText(entry.getTitle());

            return view;

        } else {
            SongView view;
            if (convertView != null && convertView instanceof SongView) {
                view = (SongView) convertView;
            } else {
                view = new SongView(activity);
            }
            DownloadService service = activity.getDownloadService();
            if (service != null) {
                view.setDownloadFile(service.forSong(entry), service, true);
            }
            return view;
        }
    }
}
