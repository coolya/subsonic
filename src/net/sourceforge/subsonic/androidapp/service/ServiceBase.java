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

import android.app.Service;
import android.os.Environment;
import android.util.Log;
import net.sourceforge.subsonic.androidapp.domain.MusicDirectory;
import net.sourceforge.subsonic.androidapp.util.Util;

import java.io.File;

/**
 * @author Sindre Mehus
 */
public abstract class ServiceBase extends Service {

    private static final String TAG = DownloadService.class.getSimpleName();
    private File musicDir;

    @Override
    public void onCreate() {
        super.onCreate();
        musicDir = createDirectory("music");
    }

    public File getSongFile(MusicDirectory.Entry song, boolean createDir) {
        File dir = getAlbumDirectory(song, createDir);

        String title = Util.fileSystemSafe(song.getTitle());
        return new File(dir, title + "." + song.getSuffix());
    }

    protected File getAlbumDirectory(MusicDirectory.Entry song, boolean create) {
        String artist = Util.fileSystemSafe(song.getArtist());
        String album = Util.fileSystemSafe(song.getAlbum());

        File dir = new File(musicDir.getPath() + "/" + artist + "/" + album);
        if (create && !dir.exists()) {
            dir.mkdirs();
        }
        return dir;
    }

    protected File createDirectory(String name) {
        File subsonicDir = new File(Environment.getExternalStorageDirectory(), "subsonic");
        File dir = new File(subsonicDir, name);
        if (!dir.exists() && !dir.mkdirs()) {
            Log.e(TAG, "Failed to create " + name);
        }
        return dir;
    }
}
