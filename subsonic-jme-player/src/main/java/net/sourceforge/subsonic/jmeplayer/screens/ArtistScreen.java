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