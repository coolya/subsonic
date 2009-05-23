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
package net.sourceforge.subsonic.domain;

import junit.framework.*;

/**
 * Unit test of {@link MetaDataParser}.
 *
 * @author Sindre Mehus
 */
public class MetaDataParserTestCase extends TestCase {

    public void testRemoveTrackNumberFromTitle() throws Exception {

        MetaDataParser parser = new MetaDataParser() {
            public MusicFile.MetaData getRawMetaData(MusicFile file) { return null; }
            public void setMetaData(MusicFile file, MusicFile.MetaData metaData) {}
            public boolean isEditingSupported() { return false; }
            public boolean isApplicable(MusicFile file) { return false; }
        };

        assertEquals("", parser.removeTrackNumberFromTitle("", null));
        assertEquals("kokos", parser.removeTrackNumberFromTitle("kokos", null));
        assertEquals("kokos", parser.removeTrackNumberFromTitle("01 kokos", null));
        assertEquals("kokos", parser.removeTrackNumberFromTitle("01 - kokos", null));
        assertEquals("kokos", parser.removeTrackNumberFromTitle("01-kokos", null));
        assertEquals("kokos", parser.removeTrackNumberFromTitle("01 - kokos", null));
        assertEquals("kokos", parser.removeTrackNumberFromTitle("99 - kokos", null));
        assertEquals("kokos", parser.removeTrackNumberFromTitle("99.- kokos", null));
        assertEquals("kokos", parser.removeTrackNumberFromTitle(" 01 kokos", null));
        assertEquals("400 years", parser.removeTrackNumberFromTitle("400 years", null));
        assertEquals("49ers", parser.removeTrackNumberFromTitle("49ers", null));
        assertEquals("01", parser.removeTrackNumberFromTitle("01", null));
        assertEquals("01", parser.removeTrackNumberFromTitle("01 ", null));
        assertEquals("01", parser.removeTrackNumberFromTitle(" 01 ", null));
        assertEquals("01", parser.removeTrackNumberFromTitle(" 01", null));

        assertEquals("", parser.removeTrackNumberFromTitle("", 1));
        assertEquals("kokos", parser.removeTrackNumberFromTitle("01 kokos", 1));
        assertEquals("kokos", parser.removeTrackNumberFromTitle("01 - kokos", 1));
        assertEquals("kokos", parser.removeTrackNumberFromTitle("01-kokos", 1));
        assertEquals("kokos", parser.removeTrackNumberFromTitle("99 - kokos", 99));
        assertEquals("kokos", parser.removeTrackNumberFromTitle("99.- kokos", 99));
        assertEquals("01 kokos", parser.removeTrackNumberFromTitle("01 kokos", 2));
        assertEquals("1 kokos", parser.removeTrackNumberFromTitle("1 kokos", 2));
        assertEquals("50 years", parser.removeTrackNumberFromTitle("50 years", 1));
        assertEquals("years", parser.removeTrackNumberFromTitle("50 years", 50));

        assertEquals("49ers", parser.removeTrackNumberFromTitle("49ers", 1));
        assertEquals("49ers", parser.removeTrackNumberFromTitle("49ers", 49));
        assertEquals("01", parser.removeTrackNumberFromTitle("01", 1));
        assertEquals("01", parser.removeTrackNumberFromTitle("01 ", 1));
        assertEquals("01", parser.removeTrackNumberFromTitle(" 01 ", 1));
        assertEquals("01", parser.removeTrackNumberFromTitle(" 01", 1));
        assertEquals("01", parser.removeTrackNumberFromTitle("01", 2));
        assertEquals("01", parser.removeTrackNumberFromTitle("01 ", 2));
        assertEquals("01", parser.removeTrackNumberFromTitle(" 01 ", 2));
        assertEquals("01", parser.removeTrackNumberFromTitle(" 01", 2));
    }
}