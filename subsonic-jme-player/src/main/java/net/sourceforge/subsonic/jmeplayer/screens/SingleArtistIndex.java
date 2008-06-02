/*
 * (c) Copyright WesternGeco. Unpublished work, created 2008. All rights
 * reserved under copyright laws. This information is confidential and is
 * the trade property of WesternGeco. Do not use, disclose, or reproduce
 * without the prior written permission of the owner.
 */
package net.sourceforge.subsonic.jmeplayer.screens;

import net.sourceforge.subsonic.jmeplayer.domain.Artist;
import net.sourceforge.subsonic.jmeplayer.domain.ArtistIndex;

import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.List;

/**
 * @author Sindre Mehus
 */
public class SingleArtistIndex extends List {

    private ArtistIndex artistIndex;

    public SingleArtistIndex() {
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