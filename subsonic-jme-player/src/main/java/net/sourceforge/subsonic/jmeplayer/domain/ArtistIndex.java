/*
 * (c) Copyright WesternGeco. Unpublished work, created 2008. All rights
 * reserved under copyright laws. This information is confidential and is
 * the trade property of WesternGeco. Do not use, disclose, or reproduce
 * without the prior written permission of the owner.
 */
package net.sourceforge.subsonic.jmeplayer.domain;

/**
 * @author Sindre Mehus
 */
public class ArtistIndex {

    private final String index;
    private final Artist[] artists;

    public ArtistIndex(String index, Artist[] artists) {
        this.index = index;
        this.artists = artists;
    }

    public String getIndex() {
        return index;
    }

    public Artist[] getArtists() {
        return artists;
    }
}
