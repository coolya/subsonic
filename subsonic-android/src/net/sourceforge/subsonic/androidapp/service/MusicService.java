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

import android.content.Context;
import android.graphics.Bitmap;
import net.sourceforge.subsonic.androidapp.domain.Indexes;
import net.sourceforge.subsonic.androidapp.domain.MusicDirectory;
import net.sourceforge.subsonic.androidapp.domain.Playlist;
import net.sourceforge.subsonic.androidapp.domain.Version;
import net.sourceforge.subsonic.androidapp.util.ProgressListener;
import net.sourceforge.subsonic.androidapp.activity.SelectAlbumActivity;
import org.apache.http.HttpResponse;

import java.util.List;

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

    List<Playlist> getPlaylists(Context context, ProgressListener progressListener) throws Exception;

    MusicDirectory getAlbumList(String type, int size, int offset, Context context, ProgressListener progressListener) throws Exception;

    Bitmap getCoverArt(Context context, String id, int size, ProgressListener progressListener) throws Exception;

    HttpResponse getDownloadInputStream(Context context, MusicDirectory.Entry song, long offset) throws Exception;

    Version getLocalVersion(Context context) throws Exception;

    Version getLatestVersion(Context context, ProgressListener progressListener) throws Exception;

}