package net.sourceforge.subsonic.service;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;
import net.sourceforge.subsonic.domain.Mp3Parser;
import net.sourceforge.subsonic.domain.MusicFile;
import org.apache.commons.io.filefilter.DirectoryFileFilter;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Provides services for instantiating and caching music files and cover art.
 *
 * @author Sindre Mehus
 */
public class MusicFileService {

    public static final int MUSIC_FILE_CACHE_SIZE = 1000;
    public static final int CHILD_DIR_CACHE_SIZE = 100;
    public static final int COVER_ART_CACHE_SIZE = 200;
    public static final long COVER_ART_CACHE_TTL = 5L * 60L;  // 5 minutes

    private Cache musicFileCache;
    private Cache childDirCache;
    private Cache coverArtCache;

    private SecurityService securityService;
    private SettingsService settingsService;

    public MusicFileService() {
        CacheManager cacheManager = CacheManager.create();
        musicFileCache = new Cache("MusicFileCache", MUSIC_FILE_CACHE_SIZE, false, true, 0, 0);
        childDirCache = new Cache("ChildDirCache", CHILD_DIR_CACHE_SIZE, false, true, 0, 0);
        coverArtCache = new Cache("CoverArtCache", COVER_ART_CACHE_SIZE, false, false, COVER_ART_CACHE_TTL, COVER_ART_CACHE_TTL);
        cacheManager.addCache(musicFileCache);
        cacheManager.addCache(childDirCache);
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
                return (MusicFile) element.getObjectValue();
            }
        }

        if (!securityService.isReadAllowed(file)) {
            throw new SecurityException("Access denied to file " + file);
        }

        MusicFile musicFile = new MusicFile(file);
        musicFileCache.put(new Element(file, musicFile));

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
     * @param dir   The directory.
     * @param limit Maximum number of images to return.
     * @return A list of appropriate cover art images for the directory.
     * @throws IOException If an I/O error occurs.
     */
    @SuppressWarnings({"unchecked"})
    public List<File> getCoverArt(MusicFile dir, int limit) throws IOException {

        // Look in cache.
        Element element = coverArtCache.get(dir);
        if (element != null) {
            // Check if cache is obsolete.
            if (element.getCreationTime() > getDirectoryLastModified(dir.getFile())) {
                List<File> result = (List<File>) element.getObjectValue();
                return result.subList(0, Math.min(limit, result.size()));
            }
        }

        List<File> result = new ArrayList<File>();
        listCoverArtRecursively(dir, result, limit);

        coverArtCache.put(new Element(dir, result));
        return result;
    }

    /**
     * Returns the (sorted) child directories of the given parent. If possible, a cached
     * value is returned.
     *
     * @param parent The parent directory.
     * @return The child directories.
     * @throws IOException If an I/O error occurs.
     */
    @SuppressWarnings({"unchecked"})
    public synchronized List<MusicFile> getChildDirectories(MusicFile parent) throws IOException {
        Element element = childDirCache.get(parent);
        if (element != null) {
            // Check if cache is obsolete.
            if (element.getCreationTime() > parent.lastModified()) {
                return (List<MusicFile>) element.getObjectValue();
            }
        }

        List<MusicFile> children = parent.getChildren(true, true);
        childDirCache.put(new Element(parent, children));

        return children;
    }

    private long getDirectoryLastModified(File dir) {
        long lastModified = dir.lastModified();
        File[] subDirs = dir.listFiles((FileFilter) DirectoryFileFilter.INSTANCE);
        for (File subDir : subDirs) {
            lastModified = Math.max(lastModified, subDir.lastModified());
        }
        return lastModified;
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
        for (String mask : settingsService.getCoverArtMaskAsArray()) {
            for (File candidate : candidates) {
                if (candidate.getName().toUpperCase().endsWith(mask.toUpperCase())) {
                    return candidate;
                }
            }
        }

        // Look for embedded images in MP3 files. (Only check first MP3 file encountered).
        Mp3Parser parser = new Mp3Parser();
        for (File candidate : candidates) {
            MusicFile musicFile = getMusicFile(candidate);
            if (parser.isApplicable(musicFile)) {
                if (parser.isImageAvailable(musicFile)) {
                    return candidate;
                } else {
                    return null;
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
