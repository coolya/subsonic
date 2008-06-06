package net.sourceforge.subsonic.jmeplayer.screens;

import net.sourceforge.subsonic.jmeplayer.domain.Index;
import net.sourceforge.subsonic.jmeplayer.service.MusicService;

import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.List;

/**
 * Lists all indexes.
 *
 * @author Sindre Mehus
 */
public class IndexScreen extends List {

    private Index[] indexes = {};
    private final MusicService musicService;
    private final Display display;
    private ArtistScreen artistScreen;

    public IndexScreen(MusicService musicService, Display display) {
        super("Select Index", IMPLICIT);
        this.musicService = musicService;
        this.display = display;

        loadIndexes();

        addCommand(new Command("Select", Command.ITEM, 1));
        setCommandListener(new CommandListener() {
            public void commandAction(Command command, Displayable displayable) {
                showArtistScreen();
            }
        });
    }

    private void loadIndexes() {
        try {
            indexes = musicService.getIndexes();
        } catch (Exception x) {
            // TODO
            x.printStackTrace();
        }

        deleteAll();
        for (int i = 0; i < indexes.length; i++) {
            Index index = indexes[i];
            append(index.getName(), null);
        }
    }

    private void showArtistScreen() {
        artistScreen.setIndex(indexes[getSelectedIndex()]);
        display.setCurrent(artistScreen);
    }

    public void setArtistScreen(ArtistScreen artistScreen) {
        this.artistScreen = artistScreen;
    }
}
