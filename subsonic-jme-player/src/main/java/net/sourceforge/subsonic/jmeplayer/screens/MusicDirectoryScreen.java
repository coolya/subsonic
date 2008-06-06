package net.sourceforge.subsonic.jmeplayer.screens;

import net.sourceforge.subsonic.jmeplayer.domain.MusicDirectory;

import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.List;

/**
 * @author Sindre Mehus
 */
public class MusicDirectoryScreen extends List {

    private MusicDirectory musicDirectory;

    public MusicDirectoryScreen() {
        super("Select", IMPLICIT);
        addCommand(new Command("Back", Command.BACK, 1));
        addCommand(new Command("Select", Command.ITEM, 1));
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

    public MusicDirectory.Entry[] getSelectedEntries() {
        int index = getSelectedIndex();
        if (index == 0) {
            return musicDirectory.getChildren(false);
        }
        return new MusicDirectory.Entry[]{musicDirectory.getChildren()[getSelectedIndex() - 1]};
    }
}