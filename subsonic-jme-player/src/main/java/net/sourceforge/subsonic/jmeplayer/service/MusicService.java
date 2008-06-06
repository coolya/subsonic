package net.sourceforge.subsonic.jmeplayer.service;

import net.sourceforge.subsonic.jmeplayer.domain.Index;
import net.sourceforge.subsonic.jmeplayer.domain.MusicDirectory;

/**
 * @author Sindre Mehus
 */
public interface MusicService {
    Index[] getIndexes() throws Exception;

    MusicDirectory getMusicDirectory(String path) throws Exception;

    void interrupt();
}
