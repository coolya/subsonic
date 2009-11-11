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
package net.sourceforge.subsonic.androidapp.service;

import java.util.List;
import java.util.concurrent.TimeUnit;

import android.content.Context;
import android.graphics.Bitmap;
import net.sourceforge.subsonic.androidapp.domain.Indexes;
import net.sourceforge.subsonic.androidapp.domain.MusicDirectory;
import net.sourceforge.subsonic.androidapp.domain.Version;
import net.sourceforge.subsonic.androidapp.util.Pair;
import net.sourceforge.subsonic.androidapp.util.ProgressListener;
import net.sourceforge.subsonic.androidapp.util.TimeLimitedCache;
import net.sourceforge.subsonic.androidapp.util.Util;

/**
 * @author Sindre Mehus
 */
public class CachedMusicService implements MusicService {

    private static final int MUSIC_DIR_CACHE_SIZE = 20;
    private static final int COVER_ART_CACHE_SIZE = 10;
    private static final int TTL_MUSIC_DIR = 5 * 60; // Five minutes

    private final MusicService musicService;
    private final LRUCache<String, TimeLimitedCache<MusicDirectory>> cachedMusicDirectories;
    private final LRUCache<String, Bitmap> cachedCoverArts;
    private final TimeLimitedCache<Boolean> cachedLicenseValid = new TimeLimitedCache<Boolean>(120, TimeUnit.SECONDS);
    private final TimeLimitedCache<Indexes> cachedIndexes = new TimeLimitedCache<Indexes>(60 * 60, TimeUnit.SECONDS);

    private final TimeLimitedCache<List<Pair<String, String>>> cachedPlaylists = new TimeLimitedCache<List<Pair<String, String>>>(60, TimeUnit.SECONDS);
    private String restUrl;

    public CachedMusicService(MusicService musicService) {
        this.musicService = musicService;
        cachedMusicDirectories = new LRUCache<String, TimeLimitedCache<MusicDirectory>>(MUSIC_DIR_CACHE_SIZE);
        cachedCoverArts = new LRUCache<String, Bitmap>(COVER_ART_CACHE_SIZE);
    }

    @Override
    public void ping(Context context, ProgressListener progressListener) throws Exception {
        checkSettingsChanged(context);
        musicService.ping(context, progressListener);
    }

    @Override
    public boolean isLicenseValid(Context context, ProgressListener progressListener) throws Exception {
        checkSettingsChanged(context);
        if (cachedLicenseValid.get() == null) {
            cachedLicenseValid.set(musicService.isLicenseValid(context, progressListener));
        }
        return cachedLicenseValid.get();
    }

    @Override
    public Indexes getIndexes(Context context, ProgressListener progressListener) throws Exception {
        checkSettingsChanged(context);
        if (cachedIndexes.get() == null) {
            cachedIndexes.set(musicService.getIndexes(context, progressListener));
        }
        return cachedIndexes.get();
    }

    @Override
    public MusicDirectory getMusicDirectory(String id, Context context, ProgressListener progressListener) throws Exception {
        checkSettingsChanged(context);
        TimeLimitedCache<MusicDirectory> cache = cachedMusicDirectories.get(id);
        MusicDirectory dir = cache == null ? null : cache.get();
        if (dir == null) {
            dir = musicService.getMusicDirectory(id, context, progressListener);
            cache = new TimeLimitedCache<MusicDirectory>(TTL_MUSIC_DIR, TimeUnit.SECONDS);
            cache.set(dir);
            cachedMusicDirectories.put(id, cache);
        }
        return dir;
    }

    @Override
    public MusicDirectory search(String query, Context context, ProgressListener progressListener) throws Exception {
        return musicService.search(query, context, progressListener);
    }

    @Override
    public MusicDirectory getPlaylist(String id, Context context, ProgressListener progressListener) throws Exception {
        return musicService.getPlaylist(id, context, progressListener);
    }

    @Override
    public List<Pair<String, String>> getPlaylists(Context context, ProgressListener progressListener) throws Exception {
        checkSettingsChanged(context);
        if (cachedPlaylists.get() == null) {
            cachedPlaylists.set(musicService.getPlaylists(context, progressListener));
        }
        return cachedPlaylists.get();
    }

    @Override
    public Bitmap getCoverArt(Context context, String id, int size, ProgressListener progressListener) throws Exception {
        checkSettingsChanged(context);
        String key = id + size;
        Bitmap bitmap = cachedCoverArts.get(key);
        if (bitmap == null) {
            bitmap = musicService.getCoverArt(context, id, size, progressListener);
            cachedCoverArts.put(key, bitmap);
        }
        return bitmap;
    }

    @Override
    public void cancel(Context context, ProgressListener progressListener) {
        musicService.cancel(context, progressListener);
    }

    @Override
    public Version getLocalVersion(Context context) throws Exception {
        return musicService.getLocalVersion(context);
    }

    @Override
    public Version getLatestVersion(Context context, ProgressListener progressListener) throws Exception {
        return musicService.getLatestVersion(context, progressListener);
    }

    private void checkSettingsChanged(Context context) {
        String newUrl = Util.getRestUrl(context, null);
        if (!Util.equals(newUrl, restUrl)) {
            cachedMusicDirectories.clear();
            cachedCoverArts.clear();
            cachedLicenseValid.clear();
            cachedIndexes.clear();
            cachedPlaylists.clear();
            restUrl = newUrl;
        }
    }
}