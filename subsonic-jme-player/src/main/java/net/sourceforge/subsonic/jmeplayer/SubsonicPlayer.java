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
import net.sourceforge.subsonic.jmeplayer.screens.PlayScreen;
import net.sourceforge.subsonic.jmeplayer.screens.SingleArtistIndex;
import net.sourceforge.subsonic.jmeplayer.service.MockMusicServiceImpl;
import net.sourceforge.subsonic.jmeplayer.service.MusicService;

import javax.microedition.lcdui.Alert;
import javax.microedition.lcdui.AlertType;
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
    private PlayScreen playScreen;

    private MusicService musicService;

    public void startApp() throws MIDletStateChangeException {
        display = Display.getDisplay(this);
        musicService = new MockMusicServiceImpl();

        allArtistIndexes = new AllArtistIndexes(musicService.getArtistIndexes());
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
                    MusicDirectory.Entry entry = musicDirectoryScreen.getSelectedEntry();
                    if (entry.isDirectory()) {
                        musicDirectoryScreen.setMusicDirectory(musicService.getMusicDirectory(entry.getPath()));
                    } else {
                        playScreen.setMusicDirectoryEntry(entry);
                        display.setCurrent(playScreen);
                    }
                }
            }
        });

        playScreen = new PlayScreen();
        playScreen.setCommandListener(new CommandListener() {
            public void commandAction(Command command, Displayable displayable) {
                if (command.getCommandType() == Command.BACK) {
                    stopPlayer();
                    display.setCurrent(musicDirectoryScreen);
                } else {
                    startPlayer();
                }
            }
        });

        showIndex(null);
    }

    private void startPlayer() {
        Runnable runnable = new Runnable() {
            public void run() {
                try {
                    playScreen.start();
                } catch (Exception e) {
                    showException(e);
                }
            }
        };
        new Thread(runnable).start();
    }

    private void stopPlayer() {
        Runnable runnable = new Runnable() {
            public void run() {
                try {
                    playScreen.stop();
                } catch (Exception e) {
                    showException(e);
                }
            }
        };
        new Thread(runnable).start();
    }

    public void pauseApp() {
        // TODO
    }

    public void destroyApp(boolean unconditional) {
        // TODO
        try {
            playScreen.stop();
        } catch (Exception x) {
            x.printStackTrace();
        }
        notifyDestroyed();
    }

    private void showDirectory(String name, String path) {
        System.out.println("showDirectory(" + name + ", " + path + ")");
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

    private void showException(Exception e) {
        e.printStackTrace();
        Alert alert = new Alert("Error");
        alert.setString(e.getMessage());
        alert.setType(AlertType.ERROR);
        alert.setTimeout(Alert.FOREVER);
        display.setCurrent(alert);
    }
}