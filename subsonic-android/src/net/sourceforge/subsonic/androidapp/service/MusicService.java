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
import java.io.InputStream;

import net.sourceforge.subsonic.androidapp.domain.MusicDirectory;
import net.sourceforge.subsonic.androidapp.domain.Indexes;
import net.sourceforge.subsonic.androidapp.domain.Version;
import net.sourceforge.subsonic.androidapp.util.ProgressListener;
import net.sourceforge.subsonic.androidapp.util.Pair;
import android.content.Context;
import android.graphics.Bitmap;

/**
 * @author Sindre Mehus
 */
public interface MusicService {

    void ping(Context context, ProgressListener progressListener) throws Exception;

    boolean isLicenseValid(Context context, ProgressListener progressListener) throws Exception;

    Indexes getIndexes(Context context, ProgressListener progressListener) throws Exception;

    MusicDirectory getMusicDirectory(String id, Context context, ProgressListener progressListener) throws Exception;

    MusicDirectory search(String query, Context context, ProgressListener progressListener) throws Exception;

    MusicDirectory getPlaylist(String id, Context context, ProgressListener progressListener) throws Exception;

    List<Pair<String,String>> getPlaylists(Context context, ProgressListener progressListener) throws Exception;

    Bitmap getCoverArt(Context context, String id, int size, ProgressListener progressListener) throws Exception;

    InputStream getDownloadInputStream(Context context, MusicDirectory.Entry song) throws Exception;

    void cancel(Context context, ProgressListener progressListener);

    Version getLocalVersion(Context context) throws Exception;

    Version getLatestVersion(Context context, ProgressListener progressListener) throws Exception;
}