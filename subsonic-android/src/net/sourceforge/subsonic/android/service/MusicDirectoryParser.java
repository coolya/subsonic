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
                if ("child".equals(parser.getName())) {
                    MusicDirectory.Entry entry = new MusicDirectory.Entry();
                    entry.setId(parser.getAttributeValue(null, "id"));
                    entry.setTitle(parser.getAttributeValue(null, "title"));
                    entry.setDirectory("true".equals(parser.getAttributeValue(null, "isDir")));

                    if (!entry.isDirectory()) {
                        entry.setAlbum(parser.getAttributeValue(null, "album"));
                        entry.setArtist(parser.getAttributeValue(null, "artist"));
                        entry.setContentType(parser.getAttributeValue(null, "contentType"));
                        entry.setSuffix(parser.getAttributeValue(null, "suffix"));
                        entry.setTranscodedContentType(parser.getAttributeValue(null, "transcodedContentType"));
                        entry.setTranscodedSuffix(parser.getAttributeValue(null, "transcodedSuffix"));
                        String size = parser.getAttributeValue(null, "size");
                        if (size != null) {
                            entry.setSize(Long.valueOf(size));
                        }
                    }

                    dir.addChild(entry);
                } else if ("directory".equals(parser.getName())) {
                    dir.setName(parser.getAttributeValue(null, "name"));
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