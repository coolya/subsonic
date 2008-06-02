/*
 * (c) Copyright WesternGeco. Unpublished work, created 2008. All rights
 * reserved under copyright laws. This information is confidential and is
 * the trade property of WesternGeco. Do not use, disclose, or reproduce
 * without the prior written permission of the owner.
 */
package net.sourceforge.subsonic.jmeplayer.service;

import net.sourceforge.subsonic.jmeplayer.domain.Artist;
import net.sourceforge.subsonic.jmeplayer.domain.ArtistIndex;

/**
 * @author Sindre Mehus
 */
public class MockMusicServiceImpl implements MusicService {

    public ArtistIndex[] getArtistIndexes() {
        return new ArtistIndex[]{
                new ArtistIndex("A", new Artist[]{new Artist("A-Ha", "c:/music/aha"),
                                                  new Artist("Accept", "c:/music/accept"),
                                                  new Artist("Anja Garbarek", "c:/music/anja")}),
                new ArtistIndex("B", new Artist[]{new Artist("Bad Liver", "c:/music/badliver"),
                                                  new Artist("Bauhaus", "c:/music/bauhaus"),
                                                  new Artist("The Beatles", "c:/music/beatles")}),
        };
    }
}