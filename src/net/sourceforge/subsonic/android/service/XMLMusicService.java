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
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.net.URLConnection;
import java.net.URL;

import net.sourceforge.subsonic.android.domain.MusicDirectory;
import net.sourceforge.subsonic.android.domain.Artist;
import net.sourceforge.subsonic.android.util.ProgressListener;
import net.sourceforge.subsonic.android.util.Constants;
import net.sourceforge.subsonic.android.util.Util;
import android.content.Context;

/**
 * @author Sindre Mehus
 */
public class XMLMusicService implements MusicService {

    private final MusicServiceDataSource dataSource;
    private final ArtistParser artistParser = new ArtistParser();
    private final MusicDirectoryParser musicDirectoryParser = new MusicDirectoryParser();
    private final LicenseParser licenseParser = new LicenseParser();
    private final ErrorParser errorParser = new ErrorParser();
    private final List<Reader> readers = new ArrayList<Reader>(10);

    public XMLMusicService(MusicServiceDataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public void ping(Context context, ProgressListener progressListener) throws Exception {
        Reader reader = dataSource.getPingReader(context, progressListener);
        addReader(reader);
        try {
            errorParser.parse(reader);
        } finally {
            closeReader(reader);
        }
    }

    @Override
    public boolean isLicenseValid(Context context, ProgressListener progressListener) throws Exception {
        Reader reader = dataSource.getLicenseReader(context, progressListener);
        addReader(reader);
        try {
            return licenseParser.parse(reader, progressListener);
        } finally {
            closeReader(reader);
        }
    }

    public List<Artist> getArtists(Context context, ProgressListener progressListener) throws Exception {
        Reader reader = dataSource.getArtistsReader(context, progressListener);
        addReader(reader);
        try {
            return artistParser.parse(reader, progressListener);
        } finally {
            closeReader(reader);
        }
    }

    public MusicDirectory getMusicDirectory(String id, Context context, ProgressListener progressListener) throws Exception {
        Reader reader = dataSource.getMusicDirectoryReader(id, context, progressListener);
        addReader(reader);
        try {
            return musicDirectoryParser.parse(reader, progressListener);
        } finally {
            closeReader(reader);
        }
    }

    public byte[] getCoverArt(Context context, String id, int size, ProgressListener progressListener) throws Exception {
        String url = Util.getRestUrl(context, "getCoverArt") + "&id=" + id + "&size=" + size;
        URLConnection connection = new URL(url).openConnection();
        connection.setConnectTimeout(Constants.SOCKET_TIMEOUT);
        connection.setReadTimeout(Constants.SOCKET_TIMEOUT);
        connection.connect();
        InputStream in = connection.getInputStream();

        try {
            // If content type is XML, an error occured.  Get it.
            String contentType = connection.getContentType();
            if (contentType != null && contentType.startsWith("text/xml")) {
                new ErrorParser().parse(new InputStreamReader(in, Constants.UTF_8));
                return null; // Never reached.
            }

            return Util.toByteArray(in);
        } finally {
            Util.close(in);
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

    public synchronized void cancel(Context context, ProgressListener progressListener) {
        while (!readers.isEmpty()) {
            Reader reader = readers.get(readers.size() - 1);
            closeReader(reader);
        }
    }
}