/*
 This file is part of Subsonic.

 Subsonic is free software: you can redistribute it and/or modify
 it under the terms of the GNU General Public License as published by
 the Free Software Foundation, either version 3 of the License, or
 (at your option) any later version.

 Subsonic is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with Subsonic.  If not, see <http://www.gnu.org/licenses/>.

 Copyright 2009 (C) Sindre Mehus
 */
package net.sourceforge.subsonic.jmeplayer.service;

import net.sourceforge.subsonic.jmeplayer.domain.Index;
import net.sourceforge.subsonic.jmeplayer.domain.MusicDirectory;

/**
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
