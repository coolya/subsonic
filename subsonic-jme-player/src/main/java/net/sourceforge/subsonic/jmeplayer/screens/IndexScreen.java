package net.sourceforge.subsonic.jmeplayer.screens;

import net.sourceforge.subsonic.jmeplayer.domain.ArtistIndex;

import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.List;

/**
 * @author Sindre Mehus
 */
public class IndexScreen extends List {

    private ArtistIndex[] indexes;

    public IndexScreen(ArtistIndex[] indexes) {
        super("Select Index", IMPLICIT);
        this.indexes = indexes;

        for (int i = 0; i < indexes.length; i++) {
            ArtistIndex index = indexes[i];
            append(index.getIndex(), null);
        }
        addCommand(new Command("Select", Command.ITEM, 1));
    }

    public ArtistIndex getSelectedArtistIndex() {
        return indexes[getSelectedIndex()];
    }
}
