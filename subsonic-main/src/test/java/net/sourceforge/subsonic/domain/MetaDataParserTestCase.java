package net.sourceforge.subsonic.domain;

/**
 * Unit test of {@link MetaDataParser}.
 *
 * @author Sindre Mehus
 * @version $Revision: 1.2 $ $Date: 2005/05/18 15:33:24 $
 */

import junit.framework.*;
import net.sourceforge.subsonic.domain.MusicFile.*;

public class MetaDataParserTestCase extends TestCase {

    public void testRemoveTrackNumberFromTitle() throws Exception {

        MetaDataParser parser = new MetaDataParser() {
            public int getBitRate(MusicFile file) { return 0; }
            public MetaData getMetaData(MusicFile file) { return null; }
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
        assertEquals("01", parser.removeTrackNumberFromTitle("01"));
        assertEquals("01", parser.removeTrackNumberFromTitle("01 "));
        assertEquals("01", parser.removeTrackNumberFromTitle(" 01 "));
        assertEquals("01", parser.removeTrackNumberFromTitle(" 01"));
    }
}