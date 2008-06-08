package net.sourceforge.subsonic.jmeplayer.service;

import net.sourceforge.subsonic.jmeplayer.domain.Index;
import net.sourceforge.subsonic.jmeplayer.domain.MusicDirectory;

/**
 * TODO: Implement
 *
 * @author Sindre Mehus
 */
public class CachedMusicService implements MusicService {

    private static final int CACHE_SIZE = 20;

    private final MusicService musicService;
    private final LRUCache cachedMusicDirectories;
    private Index[] cachedIndexes;


    public CachedMusicService(MusicService musicService) {
        this.musicService = musicService;
        cachedMusicDirectories = new LRUCache(CACHE_SIZE);
    }

    public Index[] getIndexes() throws Exception {
        if (cachedIndexes == null) {
            cachedIndexes = musicService.getIndexes();
        }
        return cachedIndexes;
    }

    public MusicDirectory getMusicDirectory(String path) throws Exception {
        MusicDirectory dir = (MusicDirectory) cachedMusicDirectories.get(path);
        if (dir == null) {
            dir = musicService.getMusicDirectory(path);
            cachedMusicDirectories.put(path, dir);
        }
        return dir;
    }

    public void interrupt() {
        musicService.interrupt();
    }
}
