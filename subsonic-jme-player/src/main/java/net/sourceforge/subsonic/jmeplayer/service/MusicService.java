/*
 * (c) Copyright WesternGeco. Unpublished work, created 2008. All rights
 * reserved under copyright laws. This information is confidential and is
 * the trade property of WesternGeco. Do not use, disclose, or reproduce
 * without the prior written permission of the owner.
 */
package net.sourceforge.subsonic.jmeplayer.service;

import net.sourceforge.subsonic.jmeplayer.domain.ArtistIndex;
import net.sourceforge.subsonic.jmeplayer.domain.MusicDirectory;

/**
 * @author Sindre Mehus
 */
public interface MusicService {
    ArtistIndex[] getArtistIndexes();

    MusicDirectory getMusicDirectory(String path);
}
