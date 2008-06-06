package net.sourceforge.subsonic.jmeplayer.service;

import net.sourceforge.subsonic.jmeplayer.domain.Index;
import net.sourceforge.subsonic.jmeplayer.domain.MusicDirectory;

/**
 * TODO: Implement
 *
 * @author Sindre Mehus
 */
public class CachedMusicService implements MusicService {

    private final MusicService musicService;
    private Index[] cachedIndexes;

    public CachedMusicService(MusicService musicService) {
        this.musicService = musicService;
    }

    public Index[] getIndexes() throws Exception {
        if (cachedIndexes == null) {
            cachedIndexes = musicService.getIndexes();
        }
        return cachedIndexes;
    }

    public MusicDirectory getMusicDirectory(String path) throws Exception {
        return musicService.getMusicDirectory(path);
    }

    public void interrupt() {
        musicService.interrupt();
    }
}
