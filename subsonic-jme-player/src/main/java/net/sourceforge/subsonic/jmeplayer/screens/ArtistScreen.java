package net.sourceforge.subsonic.jmeplayer.screens;

import net.sourceforge.subsonic.jmeplayer.domain.Artist;
import net.sourceforge.subsonic.jmeplayer.domain.Index;

import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.List;

/**
 * @author Sindre Mehus
 */
public class ArtistScreen extends List {

    private Index index;

    public ArtistScreen() {
        super("Select Artist", IMPLICIT);
        addCommand(new Command("Back", Command.BACK, 1));
        addCommand(new Command("Select", Command.ITEM, 1));
    }

    public void setArtistIndex(Index index) {
        this.index = index;
        setTitle("Select Artist: " + index.getIndex());
        deleteAll();
        for (int i = 0; i < index.getArtists().length; i++) {
            Artist artist = index.getArtists()[i];
            append(artist.getName(), null);
        }
    }

    public Artist getSelectedArtist() {
        return index.getArtists()[getSelectedIndex()];
    }
}