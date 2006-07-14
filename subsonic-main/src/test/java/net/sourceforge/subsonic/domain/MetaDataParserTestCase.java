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
            public int getBitRate(MusicFile file) { return 0; }
            public MusicFile.MetaData getMetaData(MusicFile file) { return null; }
            public boolean isApplicable(MusicFile file) { return false; }
        };

        assertEquals("", parser.removeTrackNumberFromTitle(""));
        assertEquals("kokos", parser.removeTrackNumberFromTitle("kokos"));
        assertEquals("kokos", parser.removeTrackNumberFromTitle("01 kokos"));
        assertEquals("kokos", parser.removeTrackNumberFromTitle("01 - kokos"));
        assertEquals("kokos", parser.removeTrackNumberFromTitle("01-kokos"));
        assertEquals("kokos", parser.removeTrackNumberFromTitle("01 - kokos"));
        assertEquals("kokos", parser.removeTrackNumberFromTitle("99 - kokos"));
        assertEquals("kokos", parser.removeTrackNumberFromTitle("99.- kokos"));
        assertEquals("kokos", parser.removeTrackNumberFromTitle(" 01 kokos"));
        assertEquals("400 years", parser.removeTrackNumberFromTitle("400 years"));
        assertEquals("49ers", parser.removeTrackNumberFromTitle("49ers"));
        assertEquals("01", parser.removeTrackNumberFromTitle("01"));
        assertEquals("01", parser.removeTrackNumberFromTitle("01 "));
        assertEquals("01", parser.removeTrackNumberFromTitle(" 01 "));
        assertEquals("01", parser.removeTrackNumberFromTitle(" 01"));
    }
}