package net.sourceforge.subsonic.service;

import net.sf.ehcache.*;
import net.sourceforge.subsonic.domain.*;

import java.io.*;
import java.util.*;

/**
 * Provides services for instantiating and caching music files and cover art.
 *
 * @author Sindre Mehus
 */
public class MusicFileService {

    public static final int MUSIC_FILE_CACHE_SIZE = 1000;
    public static final int COVER_ART_CACHE_SIZE  =  200;
    public static final long COVER_ART_CACHE_TTL  =  5L * 60L;  // 5 minutes

    private Cache musicFileCache;
    private Cache coverArtCache;

    private SecurityService securityService;
    private SettingsService settingsService;

    public MusicFileService() {
        CacheManager cacheManager = CacheManager.create();
        musicFileCache = new Cache("MusicFileCache", MUSIC_FILE_CACHE_SIZE, false, true, 0, 0);
        coverArtCache = new Cache("CoverArtCache", COVER_ART_CACHE_SIZE, false, false, COVER_ART_CACHE_TTL, COVER_ART_CACHE_TTL);
        cacheManager.addCache(musicFileCache);
        cacheManager.addCache(coverArtCache);
    }

    /**
     * Returns a music file instance for the given file.  If possible, a cached value is returned.
     *
     * @param file A file on the local file system.
     * @return A music file instance.
     * @throws SecurityException If access is denied to the given file.
     */
    public MusicFile getMusicFile(File file) {
        Element element = musicFileCache.get(file);
        if (element != null) {
            // Check if cache is obsolete.
            if (element.getCreationTime() > file.lastModified()) {
//                LOG.debug("HIT : " + file); // TODO: Remove
                return (MusicFile) element.getObjectValue();
            } else {
//                LOG.debug("EXP : " + file);
            }
        }

        if (!securityService.isReadAllowed(file)) {
            throw new SecurityException("Access denied to file " + file);
        }

        MusicFile musicFile = new MusicFile(file);
        musicFileCache.put(new Element(file, musicFile));
//        LOG.debug("MISS: " + file);

        return musicFile;
    }

    /**
     * Returns a music file instance for the given path name. If possible, a cached value is returned.
     *
     * @param pathName A path name for a file on the local file system.
     * @return A music file instance.
     * @throws SecurityException If access is denied to the given file.
     */
    public MusicFile getMusicFile(String pathName) {
        return getMusicFile(new File(pathName));
    }

    /**
     * Returns a list of appropriate cover art images for the given directory.
     *
     * @param dir The directory.
     * @param limit Maximum number of images to return.
     * @return A list of appropriate cover art images for the directory.
     * @exception IOException If an I/O error occurs.
     */
    public List<File> getCoverArt(MusicFile dir, int limit) throws IOException {

        // Look in cache.
        Element element = coverArtCache.get(dir);
        if (element != null) {
            // Check if cache is obsolete.
            if (element.getCreationTime() > dir.lastModified()) {
//                LOG.debug("HIT : " + dir); // TODO: Remove
                List<File> result = (List<File>) element.getObjectValue();
                return result.subList(0, Math.min(limit, result.size()));
            } else {
//                LOG.debug("EXP : " + dir);
            }
        }

        List<File> result = new ArrayList<File>();
        listCoverArtRecursively(dir, result, limit);

        coverArtCache.put(new Element(dir, result));
//        LOG.debug("MISS: " + dir);
        return result;
    }

    private void listCoverArtRecursively(MusicFile dir, List<File> coverArtFiles, int limit) throws IOException {
        if (coverArtFiles.size() == limit) {
            return;
        }

        File[] files = dir.getFile().listFiles();

        for (File file : files) {
            if (file.isDirectory() && !dir.isExcluded(file)) {
                listCoverArtRecursively(getMusicFile(file), coverArtFiles, limit);
            }
        }

        if (coverArtFiles.size() == limit) {
            return;
        }

        File best = getBestCoverArt(files);
        if (best != null) {
            coverArtFiles.add(best);
        }
    }

    private File getBestCoverArt(File[] candidates) {
        // TODO: Cache
        for (String mask : settingsService.getCoverArtMaskAsArray()) {
            for (File candidate : candidates) {
                if (candidate.getName().toUpperCase().endsWith(mask.toUpperCase())) {
                    return candidate;
                }
            }
        }
        return null;
    }

    /**
     * Register in service locator so that non-Spring objects can access me.
     * This method is invoked automatically by Spring.
     */
    public void init() {
        ServiceLocator.setMusicFileService(this);
    }

    public void setSecurityService(SecurityService securityService) {
        this.securityService = securityService;
    }

    public void setSettingsService(SettingsService settingsService) {
        this.settingsService = settingsService;
    }
}
