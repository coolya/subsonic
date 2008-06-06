package net.sourceforge.subsonic.jmeplayer;

import net.sourceforge.subsonic.jmeplayer.domain.Artist;
import net.sourceforge.subsonic.jmeplayer.domain.ArtistIndex;
import net.sourceforge.subsonic.jmeplayer.domain.MusicDirectory;
import net.sourceforge.subsonic.jmeplayer.screens.ArtistScreen;
import net.sourceforge.subsonic.jmeplayer.screens.IndexScreen;
import net.sourceforge.subsonic.jmeplayer.screens.MusicDirectoryScreen;
import net.sourceforge.subsonic.jmeplayer.screens.PlayerScreen;
import net.sourceforge.subsonic.jmeplayer.service.MockXMLMusicServiceImpl;
import net.sourceforge.subsonic.jmeplayer.service.MusicService;

import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.midlet.MIDlet;
import javax.microedition.midlet.MIDletStateChangeException;

/**
 * // TODO: Add Exit command to menu
 */
public class SubsonicPlayer extends MIDlet {

    private Display display;
    private IndexScreen indexScreen;
    private ArtistScreen artistScreen;
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

        indexScreen = new IndexScreen(indexes);
        indexScreen.setCommandListener(new CommandListener() {
            public void commandAction(Command command, Displayable displayable) {
                showIndex(indexScreen.getSelectedArtistIndex());
            }
        });

        artistScreen = new ArtistScreen();
        artistScreen.setCommandListener(new CommandListener() {
            public void commandAction(Command command, Displayable displayable) {
                if (command.getCommandType() == Command.BACK) {
                    showIndex(null);
                } else {
                    Artist artist = artistScreen.getSelectedArtist();
                    showDirectory(artist.getPath());
                }
            }
        });

        musicDirectoryScreen = new MusicDirectoryScreen();
        musicDirectoryScreen.setCommandListener(new CommandListener() {
            public void commandAction(Command command, Displayable displayable) {
                if (command.getCommandType() == Command.BACK) {
                    display.setCurrent(artistScreen); // TODO: Should rather show parent.
                } else {
                    MusicDirectory.Entry[] entries = musicDirectoryScreen.getSelectedEntries();
                    if (entries.length == 1 && entries[0].isDirectory()) {
                        try {
                            musicDirectoryScreen.setMusicDirectory(musicService.getMusicDirectory(entries[0].getPath()));
                        } catch (Exception e) {
                            e.printStackTrace();
                            // TODO
                        }
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

    private void showDirectory(String path) {
        try {
            musicDirectoryScreen.setMusicDirectory(musicService.getMusicDirectory(path));
            display.setCurrent(musicDirectoryScreen);
        } catch (Exception e) {
            e.printStackTrace();
            // TODO
        }
    }

    private void showIndex(ArtistIndex index) {
        if (index == null) {
            display.setCurrent(indexScreen);
        } else {
            artistScreen.setArtistIndex(index);
            display.setCurrent(artistScreen);
        }
    }
}