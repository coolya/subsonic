package net.sourceforge.subsonic.jmeplayer.screens;

import net.sourceforge.subsonic.jmeplayer.domain.MusicDirectory;
import net.sourceforge.subsonic.jmeplayer.service.MusicService;

import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.List;

/**
 * @author Sindre Mehus
 */
public class MusicDirectoryScreen extends List {

    private MusicDirectory musicDirectory;
    private final MusicService musicService;
    private final Display display;
    private ArtistScreen artistScreen;
    private PlayerScreen playerScreen;

    public MusicDirectoryScreen(final MusicService musicService, final Display display) {
        super("Select", IMPLICIT);
        this.musicService = musicService;
        this.display = display;

        addCommand(new Command("Back", Command.BACK, 1));
        addCommand(new Command("Select", Command.ITEM, 1));

        setCommandListener(new CommandListener() {
            public void commandAction(Command command, Displayable displayable) {
                if (command.getCommandType() == Command.BACK) {
                    display.setCurrent(artistScreen); // TODO: Should rather show parent.
                } else {
                    MusicDirectory.Entry[] entries = getSelectedEntries();
                    if (entries.length == 1 && entries[0].isDirectory()) {
                        try {
                            setMusicDirectory(musicService.getMusicDirectory(entries[0].getPath()));
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
    }

    public void setMusicDirectory(MusicDirectory musicDirectory) {
        this.musicDirectory = musicDirectory;
        setTitle(musicDirectory.getName());
        deleteAll();
        append("[Play all]", null);
        for (int i = 0; i < musicDirectory.getChildren().length; i++) {
            MusicDirectory.Entry entry = musicDirectory.getChildren()[i];
            // TODO: Add icon indicating whether this is a song or album.
            append(entry.getName(), null);
        }
    }

    private MusicDirectory.Entry[] getSelectedEntries() {
        int index = getSelectedIndex();
        if (index == 0) {
            return musicDirectory.getChildren(false);
        }
        return new MusicDirectory.Entry[]{musicDirectory.getChildren()[getSelectedIndex() - 1]};
    }

    public void setArtistScreen(ArtistScreen artistScreen) {
        this.artistScreen = artistScreen;
    }

    public void setPlayerScreen(PlayerScreen playerScreen) {
        this.playerScreen = playerScreen;
    }
}