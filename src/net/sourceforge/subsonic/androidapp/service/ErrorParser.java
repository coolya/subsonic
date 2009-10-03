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

import android.util.Xml;
import org.xmlpull.v1.XmlPullParser;

import java.io.Reader;

/**
 * @author Sindre Mehus
 */
public class ErrorParser extends AbstractParser {

    public void parse(Reader reader) throws Exception {

        XmlPullParser parser = Xml.newPullParser();
        parser.setInput(reader);

        int eventType;
        do {
            eventType = parser.next();
            if (eventType == XmlPullParser.START_TAG && "error".equals(parser.getName())) {
                handleError(parser);
            }
        } while (eventType != XmlPullParser.END_DOCUMENT);

    }
}