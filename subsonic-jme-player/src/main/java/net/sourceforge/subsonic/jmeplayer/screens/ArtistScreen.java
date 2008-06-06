package net.sourceforge.subsonic.jmeplayer.screens;

import net.sourceforge.subsonic.jmeplayer.domain.Artist;
import net.sourceforge.subsonic.jmeplayer.domain.Index;
import net.sourceforge.subsonic.jmeplayer.service.MusicService;

import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.List;

/**
 * // TODO: Handle "default" button.
 * <p/>
 * Lists all artists for a given index.
 *
 * @author Sindre Mehus
 */
public class ArtistScreen extends List {

    private Index index;
    private final MusicService musicService;
    private final Display display;
    private MusicDirectoryScreen musicDirectoryScreen;
    private IndexScreen indexScreen;

    public ArtistScreen(MusicService musicService, final Display display) {
        super("Select Artist", IMPLICIT);
        this.musicService = musicService;
        this.display = display;

        addCommand(new Command("Back", Command.BACK, 1));
        addCommand(new Command("Select", Command.ITEM, 1));

        setCommandListener(new CommandListener() {
            public void commandAction(Command command, Displayable displayable) {
                if (command.getCommandType() == Command.BACK) {
                    display.setCurrent(indexScreen);
                } else {
                    Artist artist = index.getArtists()[getSelectedIndex()];
                    showDirectory(artist.getPath());
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

    private void showDirectory(String path) {
        try {
            musicDirectoryScreen.setMusicDirectory(musicService.getMusicDirectory(path));
            display.setCurrent(musicDirectoryScreen);
        } catch (Exception e) {
            e.printStackTrace();
            // TODO
        }
    }

    public void setMusicDirectoryScreen(MusicDirectoryScreen musicDirectoryScreen) {
        this.musicDirectoryScreen = musicDirectoryScreen;
    }

    public void setIndexScreen(IndexScreen indexScreen) {
        this.indexScreen = indexScreen;
    }
}