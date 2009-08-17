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

import android.util.Log;
import android.util.Xml;
import net.sourceforge.subsonic.android.domain.MusicDirectory;
import net.sourceforge.subsonic.android.util.ProgressListener;
import org.xmlpull.v1.XmlPullParser;

import java.io.Reader;

/**
 * @author Sindre Mehus
 */
public class MusicDirectoryParser extends AbstractParser {

    private static final String TAG = MusicDirectoryParser.class.getSimpleName();

    public MusicDirectory parse(Reader reader, ProgressListener progressListener) throws Exception {
        if (progressListener != null) {
            progressListener.updateProgress("Reading from server.");
        }

        long t0 = System.currentTimeMillis();
        XmlPullParser parser = Xml.newPullParser();
        parser.setInput(reader);

        MusicDirectory dir = new MusicDirectory();
        int eventType;
        do {
            eventType = parser.next();
            if (eventType == XmlPullParser.START_TAG) {
                String name = parser.getName();
                if ("child".equals(name)) {
                    MusicDirectory.Entry entry = new MusicDirectory.Entry();
                    entry.setId(get(parser, "id"));
                    entry.setTitle(get(parser, "title"));
                    entry.setDirectory(getBoolean(parser, "isDir"));
                    entry.setCoverArt(get(parser, "coverArt"));

                    if (!entry.isDirectory()) {
                        entry.setAlbum(get(parser, "album"));
                        entry.setArtist(get(parser, "artist"));
                        entry.setTrack(getInteger(parser, "track"));
                        entry.setYear(getInteger(parser, "year"));
                        entry.setGenre(get(parser, "genre"));
                        entry.setArtist(get(parser, "artist"));
                        entry.setContentType(get(parser, "contentType"));
                        entry.setSuffix(get(parser, "suffix"));
                        entry.setTranscodedContentType(get(parser, "transcodedContentType"));
                        entry.setTranscodedSuffix(get(parser, "transcodedSuffix"));
                        entry.setSize(getLong(parser, "size"));
                    }

                    dir.addChild(entry);
                } else if ("directory".equals(name)) {
                    dir.setName(get(parser, "name"));
                } else if ("error".equals(name)) {
                    handleError(parser);
                }
            }
        } while (eventType != XmlPullParser.END_DOCUMENT);

        long t1 = System.currentTimeMillis();
        Log.d(TAG, "Got music directory in " + (t1 - t0) + "ms.");

        if (progressListener != null) {
            progressListener.updateProgress("Reading from server. Done!");
        }
        return dir;
    }
}