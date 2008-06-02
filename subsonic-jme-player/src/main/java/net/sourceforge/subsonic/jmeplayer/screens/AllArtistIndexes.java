/*
 * (c) Copyright WesternGeco. Unpublished work, created 2008. All rights
 * reserved under copyright laws. This information is confidential and is
 * the trade property of WesternGeco. Do not use, disclose, or reproduce
 * without the prior written permission of the owner.
 */
package net.sourceforge.subsonic.jmeplayer.screens;

import net.sourceforge.subsonic.jmeplayer.domain.ArtistIndex;

import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.List;

/**
 * @author Sindre Mehus
 */
public class AllArtistIndexes extends List {

    private ArtistIndex[] indexes;

    public AllArtistIndexes(ArtistIndex[] indexes) {
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
