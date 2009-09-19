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
package net.sourceforge.subsonic.android.service;

import java.util.List;

import net.sourceforge.subsonic.android.domain.MusicDirectory;
import net.sourceforge.subsonic.android.domain.Artist;
import net.sourceforge.subsonic.android.util.ProgressListener;
import net.sourceforge.subsonic.android.util.Util;
import net.sourceforge.subsonic.android.util.TimeLimitedCache;
import android.content.Context;

/**
 * @author Sindre Mehus
 */
public class CachedMusicService implements MusicService {

    private static final int MUSIC_DIR_CACHE_SIZE = 20;
    private static final int COVER_ART_CACHE_SIZE = 10;

    private final MusicService musicService;
    private final LRUCache<String,MusicDirectory> cachedMusicDirectories;
    private final LRUCache<String, byte[]> cachedCoverArts;
    private List<Artist> cachedArtists;
    private String restUrl;
    private TimeLimitedCache<Boolean> cachedLicenseValid = null;

    public CachedMusicService(MusicService musicService) {
        this.musicService = musicService;
        cachedMusicDirectories = new LRUCache<String, MusicDirectory>(MUSIC_DIR_CACHE_SIZE);
        cachedCoverArts = new LRUCache<String, byte[]>(COVER_ART_CACHE_SIZE);
    }

    @Override
    public void ping(Context context, ProgressListener progressListener) throws Exception {
        musicService.ping(context, progressListener);
    }

    @Override
    public boolean isLicenseValid(Context context, ProgressListener progressListener) throws Exception {
        checkSettingsChanged(context);
        if (cachedLicenseValid == null || cachedLicenseValid.get() == null) {
            cachedLicenseValid = new TimeLimitedCache<Boolean>(musicService.isLicenseValid(context, progressListener), 120);
        }
        return cachedLicenseValid.get();
    }

    @Override
    public List<Artist> getArtists(Context context, ProgressListener progressListener) throws Exception {
        checkSettingsChanged(context);
        if (cachedArtists == null) {
            cachedArtists = musicService.getArtists(context, progressListener);
        }
        return cachedArtists;
    }

    @Override
    public MusicDirectory getMusicDirectory(String id, Context context, ProgressListener progressListener) throws Exception {
        checkSettingsChanged(context);
        MusicDirectory dir = cachedMusicDirectories.get(id);
        if (dir == null) {
            dir = musicService.getMusicDirectory(id, context, progressListener);
            cachedMusicDirectories.put(id, dir);
        }
        return dir;
    }

    @Override
    public byte[] getCoverArt(Context context, String id, int size, ProgressListener progressListener) throws Exception {
        checkSettingsChanged(context);
        String key = id + size;
        byte[] bytes = cachedCoverArts.get(key);
        if (bytes == null) {
            bytes = musicService.getCoverArt(context, id, size, progressListener);
            cachedCoverArts.put(key, bytes);
        }
        return bytes;
    }

    @Override
    public void cancel(Context context, ProgressListener progressListener) {
        musicService.cancel(context, progressListener);
    }

    private void checkSettingsChanged(Context context) {
        String newUrl = Util.getRestUrl(context, null);
        if (!Util.equals(newUrl, restUrl)) {
            cachedMusicDirectories.clear();
            cachedCoverArts.clear();
            cachedArtists = null;
            cachedLicenseValid = null;
            restUrl = newUrl;
        }
    }
}