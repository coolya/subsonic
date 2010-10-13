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

import android.app.Activity;
import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import net.sourceforge.subsonic.androidapp.util.Constants;
import net.sourceforge.subsonic.androidapp.util.Util;

/**
 * Handles voice queries.
 *
 * http://android-developers.blogspot.com/2010/09/supporting-new-music-voice-action.html
 *
 * @author Sindre Mehus
 */
public class PlayFromSearchActivity extends Activity {

    private static final String TAG = PlayFromSearchActivity.class.getSimpleName();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String query = getIntent().getStringExtra(SearchManager.QUERY);

        if (query != null) {
            Log.i(TAG, "Got query: " + query);
            Intent intent = new Intent(PlayFromSearchActivity.this, SelectAlbumActivity.class);
            intent.putExtra(Constants.INTENT_EXTRA_NAME_QUERY, query);
            intent.putExtra(Constants.INTENT_EXTRA_NAME_PLAY_ALL, true);
            Util.startActivityWithoutTransition(PlayFromSearchActivity.this, intent);
        }
        finish();
        Util.disablePendingTransition(this);
    }
}