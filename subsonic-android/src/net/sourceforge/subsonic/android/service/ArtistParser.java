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

import java.io.Reader;
import java.util.List;
import java.util.ArrayList;

import org.xmlpull.v1.XmlPullParser;

import net.sourceforge.subsonic.android.domain.Artist;
import net.sourceforge.subsonic.android.util.ProgressListener;
import android.util.Xml;
import android.util.Log;

/**
 * @author Sindre Mehus
 */
public class ArtistParser extends AbstractParser {
    private static final String TAG = ArtistParser.class.getSimpleName();

    public List<Artist> parse(Reader reader, ProgressListener progressListener) throws Exception {
        if (progressListener != null) {
            progressListener.updateProgress("Reading from server.");
        }

        long t0 = System.currentTimeMillis();
        XmlPullParser parser = Xml.newPullParser();
        parser.setInput(reader);

        List<Artist> artists = new ArrayList<Artist>();
        int eventType;
        String index = "#";
        do {
            eventType = parser.next();
            if (eventType == XmlPullParser.START_TAG) {
                String name = parser.getName();
                if ("index".equals(name)) {
                    index = parser.getAttributeValue(null, "name");
                } else if ("artist".equals(name)) {
                    Artist artist = new Artist();
                    artist.setId(parser.getAttributeValue(null, "id"));
                    artist.setName(parser.getAttributeValue(null, "name"));
                    artist.setIndex(index);
                    artists.add(artist);

                    if (progressListener != null && artists.size() % 10 == 0) {
                        progressListener.updateProgress("Got " + artists.size() + " artists.");
                    }
                } else if ("error".equals(name)) {
                    handleError(parser);
                }
            }
        } while (eventType != XmlPullParser.END_DOCUMENT);

        long t1 = System.currentTimeMillis();
        Log.d(TAG, "Got " + artists.size() + " artist(s) in " + (t1 - t0) + "ms.");

        if (progressListener != null) {
            progressListener.updateProgress("Got " + artists.size() + " artists.");
        }

        return artists;
    }
}