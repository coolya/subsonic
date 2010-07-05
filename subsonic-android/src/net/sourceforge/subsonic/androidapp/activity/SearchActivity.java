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

package net.sourceforge.subsonic.androidapp.activity;

import org.apache.commons.lang.StringUtils;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import net.sourceforge.subsonic.androidapp.R;
import net.sourceforge.subsonic.androidapp.util.Constants;
import net.sourceforge.subsonic.androidapp.util.Util;

public class SearchActivity extends SubsonicTabActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search);

        final Button searchViewButton = (Button) findViewById(R.id.search_search);
        final TextView queryTextView = (TextView) findViewById(R.id.search_query);
        searchViewButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String query = StringUtils.trimToNull(String.valueOf(queryTextView.getText()));
                if (query != null) {
                    Intent intent = new Intent(SearchActivity.this, SelectAlbumActivity.class);
                    intent.putExtra(Constants.INTENT_EXTRA_NAME_QUERY, query);
                    Util.startActivityWithoutTransition(SearchActivity.this, intent);
                }
            }
        });
    }
}