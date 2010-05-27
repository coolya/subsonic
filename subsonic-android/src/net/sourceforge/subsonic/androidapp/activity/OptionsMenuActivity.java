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
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import net.sourceforge.subsonic.androidapp.R;
import net.sourceforge.subsonic.androidapp.util.Constants;
import net.sourceforge.subsonic.androidapp.util.Util;

/**
 * @author Sindre Mehus
 */
public class OptionsMenuActivity extends Activity {

    private static final int MENU_HOME = 1;
    private static final int MENU_PLAYING = 2;
    private static final int MENU_SETTINGS = 3;
    private static final int MENU_HELP = 4;

    private Dialog searchDialog;

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);

        LayoutInflater factory = LayoutInflater.from(this);
        View searchView = factory.inflate(R.layout.search, null);

        final Button searchViewButton = (Button) searchView.findViewById(R.id.search_search);
        final TextView queryTextView = (TextView) searchView.findViewById(R.id.search_query);
        searchViewButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchDialog.dismiss();
                Intent intent = new Intent(OptionsMenuActivity.this, SelectAlbumActivity.class);
                intent.putExtra(Constants.INTENT_EXTRA_NAME_QUERY, String.valueOf(queryTextView.getText()));
                Util.startActivityWithoutTransition(OptionsMenuActivity.this, intent);
            }
        });

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(searchView);
        builder.setCancelable(true);
        searchDialog = builder.create();
    }

    @Override
    protected void onPostCreate(Bundle bundle) {
        super.onPostCreate(bundle);

        findViewById(R.id.button_bar_home).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Util.startActivityWithoutTransition(OptionsMenuActivity.this, MainActivity.class);
            }
        });
        findViewById(R.id.button_bar_music).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Util.startActivityWithoutTransition(OptionsMenuActivity.this, SelectArtistActivity.class);
            }
        });
        findViewById(R.id.button_bar_now_playing).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Util.startActivityWithoutTransition(OptionsMenuActivity.this, DownloadActivity.class);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(0, MENU_HOME, 0, R.string.options_home).setIcon(R.drawable.menu_home);
        menu.add(0, MENU_PLAYING, 0, R.string.options_playing).setIcon(R.drawable.now_playing);
        menu.add(0, MENU_SETTINGS, 0, R.string.options_settings).setIcon(R.drawable.settings);
        menu.add(0, MENU_HELP, 0, R.string.options_help).setIcon(R.drawable.help);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case MENU_HOME:
                // TODO: use FLAG_ACTIVITY_CLEAR_TOP
                startActivity(new Intent(this, MainActivity.class));
                return true;
            case MENU_PLAYING:
                startActivity(new Intent(this, DownloadActivity.class));
                return true;
            case MENU_SETTINGS:
                startActivity(new Intent(this, SettingsActivity.class));
                return true;
            case MENU_HELP:
                startActivity(new Intent(this, HelpActivity.class));
                return true;
        }
        return false;
    }

    protected void showSearchDialog() {
        searchDialog.show();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_SEARCH) {
            showSearchDialog();
        }
        return super.onKeyDown(keyCode, event);
    }
}
