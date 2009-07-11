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
package net.sourceforge.subsonic.android.service;

import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

import net.sourceforge.subsonic.android.domain.MusicDirectory;
import net.sourceforge.subsonic.android.domain.Artist;
import net.sourceforge.subsonic.android.util.ProgressListener;

/**
 * @author Sindre Mehus
 */
public class XMLMusicService implements MusicService {

    private final MusicServiceDataSource dataSource;
    private final ArtistParser artistParser;
    private final MusicDirectoryParser musicDirectoryParser;
    private final List<Reader> readers = new ArrayList<Reader>(10);

    public XMLMusicService(MusicServiceDataSource dataSource) {
        this.dataSource = dataSource;
        artistParser = new ArtistParser();
        musicDirectoryParser = new MusicDirectoryParser();
    }

    public List<Artist> getArtists(ProgressListener progressListener) throws Exception {
        Reader reader = dataSource.getArtistsReader(progressListener);
        addReader(reader);
        try {
            return artistParser.parse(reader, progressListener);
        } finally {
            closeReader(reader);
        }
    }

    public MusicDirectory getMusicDirectory(String path, ProgressListener progressListener) throws Exception {
        Reader reader = dataSource.getMusicDirectoryReader(path, progressListener);
        addReader(reader);
        try {
            return musicDirectoryParser.parse(reader, progressListener);
        } finally {
            closeReader(reader);
        }
    }

    private synchronized void addReader(Reader reader) {
        readers.add(reader);
    }

    private synchronized void closeReader(Reader reader) {
        try {
            reader.close();
        } catch (IOException x) {
            x.printStackTrace();
        }
        readers.remove(reader);
    }

    public synchronized void cancel(ProgressListener progressListener) {
        while (!readers.isEmpty()) {
            Reader reader = readers.get(readers.size() - 1);
            closeReader(reader);
        }
    }
}