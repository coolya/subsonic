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
package net.sourceforge.subsonic.android.util;

import android.widget.ArrayAdapter;
import android.widget.TwoLineListItem;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.view.LayoutInflater;

import java.util.List;

/**
 * @author Sindre Mehus
*/
public abstract class TwoLineListAdapter<T> extends ArrayAdapter<T> {
    public TwoLineListAdapter(Context context, List<T> list) {
        super(context, android.R.layout.simple_list_item_2, list);
    }

    @Override
    public View getView(int pos, View convertView, ViewGroup parent) {
        TwoLineListItem twoLine;
        if (convertView == null) {
            twoLine = (TwoLineListItem) LayoutInflater.from(getContext()).inflate(
                    android.R.layout.simple_list_item_2, parent, false);
        } else {
            twoLine = (TwoLineListItem) convertView;
        }
        T item = getItem(pos);
        twoLine.getText1().setText(getFirstLine(item));
        twoLine.getText2().setText(getSecondLine(item));

        return twoLine;
    }

    protected abstract String getFirstLine(T item);
    protected abstract String getSecondLine(T item);
}
