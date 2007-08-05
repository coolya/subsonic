package net.sourceforge.subsonic.util;

import net.sourceforge.subsonic.Logger;

import java.io.File;
import java.io.FileFilter;
import java.io.FilenameFilter;

/**
 * Miscellaneous file utility methods.
 *
 * @author Sindre Mehus
 */
public final class FileUtil {

    private static final Logger LOG = Logger.getLogger(FileUtil.class);

    /**
     * Disallow external instantiation.
     */
    private FileUtil() {
    }

    /**
     * Similar to {@link File#listFiles()}, but never returns null.
     * Instead a warning is logged, and an empty array is returned.
     */
    public static File[] listFiles(File dir) {
        File[] files = dir.listFiles();
        if (files == null) {
            LOG.warn("Failed to list children for " + dir.getPath());
            return new File[0];
        }
        return files;
    }

    /**
     * Similar to {@link File#listFiles(FileFilter)}, but never returns null.
     * Instead a warning is logged, and an empty array is returned.
     */
    public static File[] listFiles(File dir, FileFilter filter) {
        File[] files = dir.listFiles(filter);
        if (files == null) {
            LOG.warn("Failed to list children for " + dir.getPath());
            return new File[0];
        }
        return files;
    }

    /**
     * Similar to {@link File#listFiles(FilenameFilter)}, but never returns null.
     * Instead a warning is logged, and an empty array is returned.
     */
    public static File[] listFiles(File dir, FilenameFilter filter) {
        File[] files = dir.listFiles(filter);
        if (files == null) {
            LOG.warn("Failed to list children for " + dir.getPath());
            return new File[0];
        }
        return files;
    }
}