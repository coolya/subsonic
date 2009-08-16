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

import org.xmlpull.v1.XmlPullParser;

/**
 * @author Sindre Mehus
 */
public abstract class AbstractParser {

    protected void handleError(XmlPullParser parser) throws Exception {
        throw new Exception(get(parser, "message"));
    }

    protected String get(XmlPullParser parser, String name) {
        return parser.getAttributeValue(null, name);
    }

    protected boolean getBoolean(XmlPullParser parser, String name) {
        return "true".equals(get(parser, name));
    }

    protected Integer getInteger(XmlPullParser parser, String name) {
        String s = get(parser, name);
        return s == null ? null : Integer.valueOf(s);
    }

    protected Long getLong(XmlPullParser parser, String name) {
        String s = get(parser, name);
        return s == null ? null : Long.valueOf(s);
    }
}