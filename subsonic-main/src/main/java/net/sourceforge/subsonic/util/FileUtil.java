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