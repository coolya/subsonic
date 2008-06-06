/*
 * (c) Copyright WesternGeco. Unpublished work, created 2008. All rights
 * reserved under copyright laws. This information is confidential and is
 * the trade property of WesternGeco. Do not use, disclose, or reproduce
 * without the prior written permission of the owner.
 */
package net.sourceforge.subsonic.jmeplayer;

import net.sourceforge.subsonic.jmeplayer.domain.Artist;
import net.sourceforge.subsonic.jmeplayer.domain.ArtistIndex;
import net.sourceforge.subsonic.jmeplayer.domain.MusicDirectory;
import net.sourceforge.subsonic.jmeplayer.screens.AllArtistIndexes;
import net.sourceforge.subsonic.jmeplayer.screens.MusicDirectoryScreen;
import net.sourceforge.subsonic.jmeplayer.screens.PlayerScreen;
import net.sourceforge.subsonic.jmeplayer.screens.SingleArtistIndex;
import net.sourceforge.subsonic.jmeplayer.service.MockXMLMusicServiceImpl;
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
    private MusicDirectoryScreen musicDirectoryScreen;
    private PlayerScreen playerScreen;
    private MusicService musicService;

    public void startApp() throws MIDletStateChangeException {
        display = Display.getDisplay(this);
        musicService = new MockXMLMusicServiceImpl();

        ArtistIndex[] indexes = new ArtistIndex[0];
        try {
            indexes = musicService.getArtistIndexes();
        } catch (Exception x) {
            // TODO
            x.printStackTrace();
        }

        allArtistIndexes = new AllArtistIndexes(indexes);
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

        musicDirectoryScreen = new MusicDirectoryScreen();
        musicDirectoryScreen.setCommandListener(new CommandListener() {
            public void commandAction(Command command, Displayable displayable) {
                if (command.getCommandType() == Command.BACK) {
                    display.setCurrent(singleArtistIndex); // TODO: Should rather show parent.
                } else {
                    MusicDirectory.Entry[] entries = musicDirectoryScreen.getSelectedEntries();
                    if (entries.length == 1 && entries[0].isDirectory()) {
                        musicDirectoryScreen.setMusicDirectory(musicService.getMusicDirectory(entries[0].getPath()));
                    } else {
                        playerScreen.setMusicDirectoryEntries(entries);
                        display.setCurrent(playerScreen);
                    }
                }
            }
        });

        playerScreen = new PlayerScreen(display);
        playerScreen.setMusicDirectoryScreen(musicDirectoryScreen);

        showIndex(null);
    }

    public void pauseApp() {
        // TODO
    }

    public void destroyApp(boolean unconditional) {
        // TODO
//        try {
//            playerScreen.stop();
//        } catch (Exception x) {
//            x.printStackTrace();
//        }
        notifyDestroyed();
    }

    private void showDirectory(String name, String path) {
        musicDirectoryScreen.setMusicDirectory(musicService.getMusicDirectory(path));
        display.setCurrent(musicDirectoryScreen);
    }

    private void showIndex(ArtistIndex index) {
        if (index == null) {
            display.setCurrent(allArtistIndexes);
        } else {
            singleArtistIndex.setArtistIndex(index);
            display.setCurrent(singleArtistIndex);
        }
    }
}