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
import net.sourceforge.subsonic.androidapp.domain.Artist;
import net.sourceforge.subsonic.androidapp.domain.Indexes;
import net.sourceforge.subsonic.androidapp.domain.MusicDirectory;
import net.sourceforge.subsonic.androidapp.util.FileUtil;
import net.sourceforge.subsonic.androidapp.util.Pair;
import net.sourceforge.subsonic.androidapp.util.ProgressListener;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Sindre Mehus
 */
public class OfflineMusicService extends RESTMusicService {

    @Override
    public boolean isLicenseValid(Context context, ProgressListener progressListener) throws Exception {
        return true;
    }

    @Override
    public Indexes getIndexes(Context context, ProgressListener progressListener) throws Exception {
        // TODO: Update progress listener.
        // TODO: Sort artists
        List<Artist> artists = new ArrayList<Artist>();
        File root = FileUtil.getMusicDirectory();
        for (File file : FileUtil.listFiles(root)) {
            if (file.isDirectory()) {
                Artist artist = new Artist();
                artist.setId(file.getPath());
                artist.setIndex(file.getName().substring(0, 1));
                artist.setName(file.getName());
                artists.add(artist);
            }
        }
        return new Indexes(0L, Collections.<Artist>emptyList(), artists);
    }

    @Override
    public MusicDirectory getMusicDirectory(String id, Context context, ProgressListener progressListener) throws Exception {
        File dir = new File(id);
        MusicDirectory result = new MusicDirectory();
        result.setName(dir.getName());

        for (File file : FileUtil.listFiles(dir)) {
            if (file.isDirectory() || file.getName().endsWith(".mp3")) { // TODO
                result.addChild(createEntry(file));
            }
        }
        return result;
    }

    private MusicDirectory.Entry createEntry(File file) {
        MusicDirectory.Entry entry = new MusicDirectory.Entry();
        entry.setDirectory(file.isDirectory());
        entry.setId(file.getPath());
        entry.setSize(file.length());
        entry.setArtist(file.getParentFile().getParentFile().getName());
        entry.setAlbum(file.getParentFile().getName());
        entry.setTitle(file.getName().replace(".mp3", "")); //TODO
        entry.setSuffix(FileUtil.getSuffix(file.getName()));

        // TODO: set cover art etc.
        return entry;
    }

    @Override
    public byte[] getCoverArt(Context context, String id, int size, ProgressListener progressListener) throws Exception {
        //  TODO
        return super.getCoverArt(context, id, size, progressListener);
    }


    @Override
    public MusicDirectory search(String query, Context context, ProgressListener progressListener) throws Exception {
        throw new RuntimeException("Search not available in offline mode.");
    }

    @Override
    public List<Pair<String, String>> getPlaylists(Context context, ProgressListener progressListener) throws Exception {
        throw new RuntimeException("Playlists not available in offline mode.");
    }

    @Override
    public MusicDirectory getPlaylist(String id, Context context, ProgressListener progressListener) throws Exception {
        throw new RuntimeException("Playlists not available in offline mode.");
    }

}
