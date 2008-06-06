package net.sourceforge.subsonic.jmeplayer.service;

import net.sourceforge.subsonic.jmeplayer.domain.ArtistIndex;
import net.sourceforge.subsonic.jmeplayer.domain.MusicDirectory;

/**
 * @author Sindre Mehus
 */
public interface MusicService {
    ArtistIndex[] getArtistIndexes() throws Exception;

    MusicDirectory getMusicDirectory(String path) throws Exception;
}
