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

import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView;
import net.sourceforge.subsonic.androidapp.R;
import net.sourceforge.subsonic.androidapp.service.DownloadServiceImpl;
import net.sourceforge.subsonic.androidapp.util.SackOfViewsAdapter;
import net.sourceforge.subsonic.androidapp.util.Util;
import net.sourceforge.subsonic.androidapp.util.Constants;

import java.util.Arrays;

public class MainActivity extends SubsonicTabActivity {

    private static final int MENU_GROUP_SERVER = 10;
    private static final int MENU_ITEM_SERVER_1 = 101;
    private static final int MENU_ITEM_SERVER_2 = 102;
    private static final int MENU_ITEM_SERVER_3 = 103;
    private static final int MENU_ITEM_OFFLINE = 104;
    private TextView serverTextView;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        PreferenceManager.setDefaultValues(this, R.xml.settings, false);

        startService(new Intent(this, DownloadServiceImpl.class));
        setContentView(R.layout.main);

        View buttons = LayoutInflater.from(this).inflate(R.layout.main_buttons, null);

        final View serverButton = buttons.findViewById(R.id.main_select_server);
        final View settingsButton = buttons.findViewById(R.id.main_settings);
        final View helpButton = buttons.findViewById(R.id.main_help);
        final View albumsTitle = buttons.findViewById(R.id.main_albums);
        final View albumsNewestButton = buttons.findViewById(R.id.main_albums_newest);
        final View albumsRandomButton = buttons.findViewById(R.id.main_albums_random);
        final View albumsHighestButton = buttons.findViewById(R.id.main_albums_highest);
        final View albumsRecentButton = buttons.findViewById(R.id.main_albums_recent);
        final View albumsFrequentButton = buttons.findViewById(R.id.main_albums_frequent);
        serverTextView = (TextView) serverButton.findViewById(R.id.main_select_server_2);

        ListView list = (ListView) findViewById(R.id.main_list);
        SackOfViewsAdapter adapter = new SackOfViewsAdapter(Arrays.asList(serverButton, settingsButton, helpButton,
                                                                          albumsTitle, albumsNewestButton, albumsRandomButton,
                                                                          albumsHighestButton, albumsRecentButton, albumsFrequentButton)) {
            @Override
            public boolean isEnabled(int position) {
                return position != 3;
            }
        };
        list.setAdapter(adapter);
        registerForContextMenu(list);

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (view == serverButton) {
                    serverButton.showContextMenu();
                } else if (view == settingsButton) {
                    startActivity(new Intent(MainActivity.this, SettingsActivity.class));
                } else if (view == helpButton) {
                    startActivity(new Intent(MainActivity.this, HelpActivity.class));
                } else if (view == albumsNewestButton) {
                    showAlbumList("newest");
                } else if (view == albumsRandomButton) {
                    showAlbumList("random");
                } else if (view == albumsHighestButton) {
                    showAlbumList("highest");
                } else if (view == albumsRecentButton) {
                    showAlbumList("recent");
                } else if (view == albumsFrequentButton) {
                    showAlbumList("frequent");
                }
            }
        });
    }

    private void showAlbumList(String type) {
        Intent intent = new Intent(this, SelectAlbumActivity.class);
        intent.putExtra(Constants.INTENT_EXTRA_NAME_ALBUM_LIST_TYPE, type);
        Util.startActivityWithoutTransition(this, intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateActiveServer();
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View view, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, view, menuInfo);

        MenuItem menuItem1 = menu.add(MENU_GROUP_SERVER, MENU_ITEM_SERVER_1, MENU_ITEM_SERVER_1, Util.getServerName(this, 1));
        MenuItem menuItem2 = menu.add(MENU_GROUP_SERVER, MENU_ITEM_SERVER_2, MENU_ITEM_SERVER_2, Util.getServerName(this, 2));
        MenuItem menuItem3 = menu.add(MENU_GROUP_SERVER, MENU_ITEM_SERVER_3, MENU_ITEM_SERVER_3, Util.getServerName(this, 3));
        MenuItem menuItem4 = menu.add(MENU_GROUP_SERVER, MENU_ITEM_OFFLINE, MENU_ITEM_OFFLINE, Util.getServerName(this, 0));
        menu.setGroupCheckable(MENU_GROUP_SERVER, true, true);

        switch (Util.getActiveServer(this)) {
            case 0:
                menuItem4.setChecked(true);
                break;
            case 1:
                menuItem1.setChecked(true);
                break;
            case 2:
                menuItem2.setChecked(true);
                break;
            case 3:
                menuItem3.setChecked(true);
                break;
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case MENU_ITEM_SERVER_1:
                Util.setActiveServer(this, 1);
                break;
            case MENU_ITEM_SERVER_2:
                Util.setActiveServer(this, 2);
                break;
            case MENU_ITEM_SERVER_3:
                Util.setActiveServer(this, 3);
                break;
            case MENU_ITEM_OFFLINE:
                Util.setActiveServer(this, 0);
                break;
            default:
                return super.onContextItemSelected(menuItem);
        }
        updateActiveServer();
        return true;
    }

    private void updateActiveServer() {
        int instance = Util.getActiveServer(this);
        String name = Util.getServerName(this, instance);
        serverTextView.setText(name);

        updateButtonVisibility();
    }

}