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
public class LicenseParser extends AbstractParser {

    public boolean parse(Reader reader, ProgressListener progressListener) throws Exception {
        XmlPullParser parser = Xml.newPullParser();
        parser.setInput(reader);
        int eventType;
        do {
            eventType = parser.next();
            if (eventType == XmlPullParser.START_TAG) {
                String name = parser.getName();
                if ("license".equals(name)) {
                    return getBoolean(parser, "valid");
                } else if ("error".equals(name)) {
                    handleError(parser);
                }
            }
        } while (eventType != XmlPullParser.END_DOCUMENT);

        return false;
    }
}