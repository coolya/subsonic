/*
 * (c) Copyright WesternGeco. Unpublished work, created 2008. All rights
 * reserved under copyright laws. This information is confidential and is
 * the trade property of WesternGeco. Do not use, disclose, or reproduce
 * without the prior written permission of the owner.
 */
package net.sourceforge.subsonic.jmeplayer;

import net.sourceforge.subsonic.jmeplayer.domain.Artist;
import net.sourceforge.subsonic.jmeplayer.domain.ArtistIndex;
import net.sourceforge.subsonic.jmeplayer.screens.AllArtistIndexes;
import net.sourceforge.subsonic.jmeplayer.screens.SingleArtistIndex;
import net.sourceforge.subsonic.jmeplayer.service.MockMusicServiceImpl;
import net.sourceforge.subsonic.jmeplayer.service.MusicService;

import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.midlet.MIDlet;
import javax.microedition.midlet.MIDletStateChangeException;

/**
 *
 */
public class SubsonicPlayer extends MIDlet {

    private Display display;
    private AllArtistIndexes allArtistIndexes;
    private SingleArtistIndex singleArtistIndex;

    public void startApp() throws MIDletStateChangeException {
        display = Display.getDisplay(this);
        MusicService service = new MockMusicServiceImpl();

        allArtistIndexes = new AllArtistIndexes(service.getArtistIndexes());
        allArtistIndexes.setCommandListener(new CommandListener() {
            public void commandAction(Command command, Displayable displayable) {
                showIndex(allArtistIndexes.getSelectedArtistIndex());
            }
        });

        singleArtistIndex = new SingleArtistIndex();
        singleArtistIndex.setCommandListener(new CommandListener() {
            public void commandAction(Command command, Displayable displayable) {
                if (command.getCommandType() == Command.BACK) {
                    showIndex(null);
                } else {
                    Artist artist = singleArtistIndex.getSelectedArtist();
                    showDirectory(artist.getName(), artist.getPath());
                }
            }
        });

        showIndex(null);
    }

    public void pauseApp() {
        // TODO
    }

    public void destroyApp(boolean unconditional) {
        // TODO
        notifyDestroyed();
    }

    private void showDirectory(String name, String path) {
        System.out.println("showDirectory(" + name + ", " + path + ")");
    }

    void showIndex(ArtistIndex index) {
        if (index == null) {
            display.setCurrent(allArtistIndexes);
        } else {
            singleArtistIndex.setArtistIndex(index);
            display.setCurrent(singleArtistIndex);
        }
    }
}