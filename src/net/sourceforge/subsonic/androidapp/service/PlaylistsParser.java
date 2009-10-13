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

import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

import org.xmlpull.v1.XmlPullParser;

import android.util.Xml;
import net.sourceforge.subsonic.androidapp.util.Pair;
import net.sourceforge.subsonic.androidapp.util.ProgressListener;

/**
 * @author Sindre Mehus
 */
public class PlaylistsParser extends AbstractParser {

    public List<Pair<String, String>> parse(Reader reader, ProgressListener progressListener) throws Exception {
        if (progressListener != null) {
            progressListener.updateProgress("Reading from server.");
        }

        XmlPullParser parser = Xml.newPullParser();
        parser.setInput(reader);

        List<Pair<String, String>> result = new ArrayList<Pair<String, String>>();
        int eventType;
        do {
            eventType = parser.next();
            if (eventType == XmlPullParser.START_TAG) {
                String tag = parser.getName();
                if ("playlist".equals(tag)) {
                    String name = get(parser, "name");
                    String id = get(parser, "id");
                    result.add(new Pair<String, String>(id, name));
                } else if ("error".equals(tag)) {
                    handleError(parser);
                }
            }
        } while (eventType != XmlPullParser.END_DOCUMENT);

        if (progressListener != null) {
            progressListener.updateProgress("Reading from server. Done!");
        }
        return result;
    }

}