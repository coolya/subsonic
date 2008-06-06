package net.sourceforge.subsonic.jmeplayer.screens;

import net.sourceforge.subsonic.jmeplayer.domain.Artist;
import net.sourceforge.subsonic.jmeplayer.domain.ArtistIndex;

import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.List;

/**
 * @author Sindre Mehus
 */
public class ArtistScreen extends List {

    private ArtistIndex artistIndex;

    public ArtistScreen() {
        super("Select Artist", IMPLICIT);
        addCommand(new Command("Back", Command.BACK, 1));
        addCommand(new Command("Select", Command.ITEM, 1));
    }

    public void setArtistIndex(ArtistIndex artistIndex) {
        this.artistIndex = artistIndex;
        setTitle("Select Artist: " + artistIndex.getIndex());
        deleteAll();
        for (int i = 0; i < artistIndex.getArtists().length; i++) {
            Artist artist = artistIndex.getArtists()[i];
            append(artist.getName(), null);
        }
    }

    public Artist getSelectedArtist() {
        return artistIndex.getArtists()[getSelectedIndex()];
    }
}