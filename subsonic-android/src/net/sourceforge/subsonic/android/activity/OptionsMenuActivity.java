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
package net.sourceforge.subsonic.android.activity;

import android.app.Activity;
import android.view.Menu;
import android.view.MenuItem;
import android.content.Intent;
import net.sourceforge.subsonic.android.R;

/**
 * @author Sindre Mehus
 */
public class OptionsMenuActivity extends Activity {
    private static final int MENU_HOME = 1;
    private static final int MENU_QUEUE = 2;
    private static final int MENU_SETTINGS = 3;
    private static final int MENU_HELP = 4;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(0, MENU_HOME, 0, "Home").setIcon(R.drawable.menu_home);
        menu.add(0, MENU_QUEUE, 0, "Download queue").setIcon(android.R.drawable.ic_menu_save);
        menu.add(0, MENU_SETTINGS, 0, "Settings").setIcon(android.R.drawable.ic_menu_preferences);
        menu.add(0, MENU_HELP, 0, "Help").setIcon(android.R.drawable.ic_menu_help);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case MENU_HOME:
                // TODO: use FLAG_ACTIVITY_CLEAR_TOP
                startActivity(new Intent(this, MainActivity.class));
                return true;
            case MENU_QUEUE:
                startActivity(new Intent(this, DownloadQueueActivity.class));
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
}
