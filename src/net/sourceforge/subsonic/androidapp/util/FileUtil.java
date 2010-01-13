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
package net.sourceforge.subsonic.androidapp.util;

import android.os.Environment;
import android.util.Log;
import net.sourceforge.subsonic.androidapp.domain.MusicDirectory;

import java.io.File;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.Comparator;
import java.util.Arrays;

/**
 * @author Sindre Mehus
 */
public class FileUtil {

    private static final String TAG = FileUtil.class.getSimpleName();

    // Used by fileSystemSafe()
    private static final String[] FILE_SYSTEM_UNSAFE = {"/", "\\", "..", ":", "\"", "?", "*"};

    private static final File musicDir = createDirectory("music");

    public static File getSongFile(MusicDirectory.Entry song, boolean createDir) {
        File dir = getAlbumDirectory(song, createDir);

        String title = fileSystemSafe(song.getTitle());

        // TODO: Use transcodedSuffix, if possible.
        return new File(dir, title + "." + song.getSuffix());
    }

    protected static File getAlbumDirectory(MusicDirectory.Entry song, boolean create) {
        String artist = fileSystemSafe(song.getArtist());
        String album = fileSystemSafe(song.getAlbum());

        File dir = new File(musicDir.getPath() + "/" + artist + "/" + album);
        if (create && !dir.exists()) {
            dir.mkdirs();
        }
        return dir;
    }

    private static File createDirectory(String name) {
        File subsonicDir = new File(Environment.getExternalStorageDirectory(), "subsonic");
        File dir = new File(subsonicDir, name);
        if (!dir.exists() && !dir.mkdirs()) {
            Log.e(TAG, "Failed to create " + name);
        }
        return dir;
    }

    public static File getMusicDirectory() {
        return musicDir;
    }

    /**
     * Makes a given filename safe by replacing special characters like slashes ("/" and "\")
     * with dashes ("-").
     *
     * @param filename The filename in question.
     * @return The filename with special characters replaced by underscores.
     */
    private static String fileSystemSafe(String filename) {
        if (filename == null || filename.trim().length() == 0) {
            return "unnamed";
        }

        for (String s : FILE_SYSTEM_UNSAFE) {
            filename = filename.replace(s, "-");
        }
        return filename;
    }

    /**
     * Similar to {@link File#listFiles()}, but returns a sorted set.
     * Never returns {@code null}, instead a warning is logged, and an empty set is returned.
     */
    public static SortedSet<File> listFiles(File dir) {
        File[] files = dir.listFiles();
        if (files == null) {
            Log.w(TAG, "Failed to list children for " + dir.getPath());
            return new TreeSet<File>();
        }

        return new TreeSet<File>(Arrays.asList(files));
    }

    /**
     * Returns the suffix (the substring after the last dot) of the given string. The dot
     * is not included in the returned suffix.
     *
     * @param s The string in question.
     * @return The suffix, or an empty string if no suffix is found.
     */
    public static String getSuffix(String s) {
        int index = s.lastIndexOf('.');
        return index == -1 ? "" : s.substring(index + 1);
    }

    public static String getPrefix(String s) {
        int index = s.lastIndexOf('.');
        return index == -1 ? s : s.substring(0, index);
    }
}