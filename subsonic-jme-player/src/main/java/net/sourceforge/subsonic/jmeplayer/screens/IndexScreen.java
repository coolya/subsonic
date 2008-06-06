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
    private MainScreen mainScreen;

    public IndexScreen(MusicService musicService, Display display) {
        super("Select Index", IMPLICIT);
        this.musicService = musicService;
        this.display = display;

        final Command selectCommand = new Command("Select", Command.ITEM, 1);
        final Command backCommand = new Command("Back", Command.BACK, 2);

        addCommand(selectCommand);
        addCommand(backCommand);
        setSelectCommand(selectCommand);

        setCommandListener(new CommandListener() {
            public void commandAction(Command command, Displayable displayable) {
                if (command == selectCommand) {
                    artistScreen.setIndex(indexes[getSelectedIndex()]);
                    IndexScreen.this.display.setCurrent(artistScreen);
                } else {
                    IndexScreen.this.display.setCurrent(mainScreen);
                }
            }
        });
    }

    public void loadIndexes() {
        new Worker(display, IndexScreen.this, "Contacting server...") {
            protected Object doInBackground() throws Throwable {
                return musicService.getIndexes();
            }

            protected void done(Object result) {
                indexes = (Index[]) result;
                deleteAll();
                for (int i = 0; i < indexes.length; i++) {
                    Index index = indexes[i];
                    append(index.getName(), null);
                }
            }

            protected void interrupt() throws Exception {
                musicService.interrupt();
            }
        }.start();
    }

    public void setMainScreen(MainScreen mainScreen) {
        this.mainScreen = mainScreen;
    }

    public void setArtistScreen(ArtistScreen artistScreen) {
        this.artistScreen = artistScreen;
    }
}
