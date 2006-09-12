package net.sourceforge.subsonic.service;

import net.sourceforge.subsonic.domain.*;
import net.sourceforge.subsonic.*;
import net.sf.ehcache.*;

import java.io.*;

/**
 * Provides services for creating and caching music file instances.
 *
 * @author Sindre Mehus
 */
public class MusicFileService {

    private static final Logger LOG = Logger.getLogger(MusicFileService.class);
    public static final int CACHE_SIZE = 1000;

    private SecurityService securityService;
    private Cache cache;

    public MusicFileService() {
        CacheManager cacheManager = CacheManager.create();
        cache = new Cache("MusicFileCache", CACHE_SIZE, false, true, 0, 0);
        cacheManager.addCache(cache);
    }

    /**
     * Creates a new music file instance for the given file.  If possible, a cached value is returned.
     *
     * @param file A file on the local file system.
     * @return A music file instance.
     * @throws SecurityException If access is denied to the given file.
     */
    public MusicFile createMusicFile(File file) {
        Element element = cache.get(file);
        if (element != null) {
            // Check if cache is obsolete.
            if (element.getCreationTime() > file.lastModified()) {
                LOG.debug("HIT : " + file); // TODO: Remove
                return (MusicFile) element.getObjectValue();
            } else {
                LOG.debug("EXP : " + file);
            }
        }

        if (!securityService.isReadAllowed(file)) {
            throw new SecurityException("Access denied to file " + file);
        }

        MusicFile musicFile = new MusicFile(file);
        cache.put(new Element(file, musicFile));
        LOG.debug("MISS: " + file);

        return musicFile;
    }

    /**
     * Creates a new music file instance for the given path name.
     *
     * @param pathName A path name for a file on the local file system.
     * @return A music file instance.
     * @throws SecurityException If access is denied to the given file.
     */
    public MusicFile createMusicFile(String pathName) {
        return createMusicFile(new File(pathName));
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
}
