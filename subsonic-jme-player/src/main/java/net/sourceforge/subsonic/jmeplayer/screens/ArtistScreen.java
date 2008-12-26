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

import net.sourceforge.subsonic.jmeplayer.domain.Artist;
import net.sourceforge.subsonic.jmeplayer.domain.Index;

import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.List;

/**
 * Lists all artists for a given index.
 *
 * @author Sindre Mehus
 */
public class ArtistScreen extends List {

    private Index index;
    private MusicDirectoryScreen musicDirectoryScreen;
    private IndexScreen indexScreen;

    public ArtistScreen(final Display display) {
        super("Select Artist", IMPLICIT);

        final Command selectCommand = new Command("Select", Command.ITEM, 1);
        final Command backCommand = new Command("Back", Command.BACK, 2);

        addCommand(selectCommand);
        addCommand(backCommand);
        setSelectCommand(selectCommand);

        setCommandListener(new CommandListener() {
            public void commandAction(Command command, Displayable displayable) {
                if (command == backCommand) {
                    display.setCurrent(indexScreen);
                } else {
                    Artist artist = index.getArtists()[getSelectedIndex()];
                    musicDirectoryScreen.setPath(artist.getPath());
                }
            }
        });
    }

    public void setIndex(Index index) {
        this.index = index;
        setTitle("Select Artist: " + index.getName());
        deleteAll();
        for (int i = 0; i < index.getArtists().length; i++) {
            Artist artist = index.getArtists()[i];
            append(artist.getName(), null);
        }
    }

    public void setMusicDirectoryScreen(MusicDirectoryScreen musicDirectoryScreen) {
        this.musicDirectoryScreen = musicDirectoryScreen;
    }

    public void setIndexScreen(IndexScreen indexScreen) {
        this.indexScreen = indexScreen;
    }
}