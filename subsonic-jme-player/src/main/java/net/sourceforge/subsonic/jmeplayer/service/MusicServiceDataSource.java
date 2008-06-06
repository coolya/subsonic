package net.sourceforge.subsonic.jmeplayer.service;

import java.io.Reader;

/**
 * @author Sindre Mehus
 */
public interface MusicServiceDataSource {
    Reader getIndexesReader() throws Exception;

    Reader getMusicDirectoryReader(String path) throws Exception;
}
