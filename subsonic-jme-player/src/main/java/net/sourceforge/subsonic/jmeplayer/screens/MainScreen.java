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
package net.sourceforge.subsonic.jmeplayer.screens;

import net.sourceforge.subsonic.jmeplayer.Log;
import net.sourceforge.subsonic.jmeplayer.LogFactory;
import net.sourceforge.subsonic.jmeplayer.SubsonicMIDlet;
import net.sourceforge.subsonic.jmeplayer.service.MusicService;

import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.List;
import javax.microedition.media.Player;

/**
 * Main screen.
 *
 * @author Sindre Mehus
 */
public class MainScreen extends List {

    private static final Log LOG = LogFactory.create("MainScreen");

    private final Display display;
    private IndexScreen indexScreen;
    private SettingsScreen settingsScreen;
    private Player player;

    public MainScreen(MusicService musicService, final SubsonicMIDlet midlet, final Display display) {
        super("Subsonic", IMPLICIT);
        this.display = display;

        append("Music", null);
        append("Settings", null);
        append("Exit", null);

        final Command selectCommand = new Command("Select", Command.ITEM, 1);
        final Command exitCommand = new Command("Exit", Command.EXIT, 2);

        addCommand(selectCommand);
        addCommand(exitCommand);
        setSelectCommand(selectCommand);

        setCommandListener(new CommandListener() {
            public void commandAction(Command command, Displayable displayable) {
                if (command == selectCommand) {
                    switch (getSelectedIndex()) {
                        case 0:
                            music();
                            break;
                        case 1:
                            settings();
                            break;
                        case 2:
                            midlet.exit();
                            break;
                        default:
                            break;
                    }
                } else if (command == exitCommand) {
                    midlet.exit();
                }
            }
        });
    }

    private void music() {
        indexScreen.loadIndexes();
    }

    private void settings() {
        display.setCurrent(settingsScreen);
        settingsScreen.load();
    }

    public void setSettingsScreen(SettingsScreen settingsScreen) {
        this.settingsScreen = settingsScreen;
    }

    public void setIndexScreen(IndexScreen indexScreen) {
        this.indexScreen = indexScreen;
    }
}