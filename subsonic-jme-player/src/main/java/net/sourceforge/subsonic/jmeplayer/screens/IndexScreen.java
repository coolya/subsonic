package net.sourceforge.subsonic.jmeplayer.screens;

import net.sourceforge.subsonic.jmeplayer.domain.Index;

import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.List;

/**
 * @author Sindre Mehus
 */
public class IndexScreen extends List {

    private Index[] indexes;

    public IndexScreen(Index[] indexes) {
        super("Select Index", IMPLICIT);
        this.indexes = indexes;

        for (int i = 0; i < indexes.length; i++) {
            Index index = indexes[i];
            append(index.getIndex(), null);
        }
        addCommand(new Command("Select", Command.ITEM, 1));
    }

    public Index getSelectedArtistIndex() {
        return indexes[getSelectedIndex()];
    }
}
