package net.sourceforge.subsonic.service;

/**
 * Unit test of {@link SearchService}.
 *
 * @author Sindre Mehus
 * @version $Revision: 1.4 $ $Date: 2005/12/25 09:52:56 $
 */

import junit.framework.*;
import net.sourceforge.subsonic.domain.*;

import java.io.*;
import java.util.*;

public class SearchServiceTestCase extends TestCase {

    public void testLine() {
        doTestLine("myArtist", "myAlbum", "myTitle", "myYear", "foo.mp3", 12345678, 2394872834L);
        doTestLine("myArtist", "myAlbum", "myTitle", "",       "foo.mp3", 12345678, 2394872834L);
        doTestLine("myArtist", "myAlbum", "myTitle", null,     "foo.mp3", 12345678, 2394872834L);
        doTestLine("",         "myAlbum", "myTitle", null,     "foo.mp3", 12345678, 2394872834L);
        doTestLine("",         "",        "myTitle", null,     "foo.mp3", 12345678, 2394872834L);
        doTestLine("",         "",        "",        null,     "foo.mp3", 12345678, 2394872834L);
        doTestLine("",         "",        "",        "",       "foo.mp3", 12345678, 2394872834L);
    }

    private void doTestLine(final String artist, final String album, final String title, final String year,
                            final String path, final long lastModified, final long length) {

        MusicFile file = new MusicFile() {
            public synchronized MetaData getMetaData() {
                return new MetaData(artist, album, title, year);
            }
            public File getFile() {
                return new File(path);
            }
            public boolean isFile() {
                return true;
            }
            public boolean isDirectory() {
                return false;
            }
            public long lastModified() {
                return lastModified;
            }
            public long length() {
                return length;
            }
        };

        SearchService.Line line = SearchService.Line.forFile(file, new HashMap<File,SearchService.Line>());
        String yearString = year == null ? "" : year;
        String expected = 'F' + SearchService.Line.SEPARATOR +
                          lastModified + SearchService.Line.SEPARATOR +
                          path + SearchService.Line.SEPARATOR +
                          length + SearchService.Line.SEPARATOR +
                          artist + SearchService.Line.SEPARATOR + album +
                          SearchService.Line.SEPARATOR +
                          title + SearchService.Line.SEPARATOR +
                          yearString;

        assertEquals("Error in toString().", expected, line.toString());
        assertEquals("Error in forFile().",  expected, SearchService.Line.forFile(file, new HashMap<File, SearchService.Line>()).toString());
        assertEquals("Error in parse().",    expected, SearchService.Line.parse(expected).toString());
    }

    public void testSplitQuery() {
        doTestSplitQuery("u2 rem \"greatest hits\"", "u2", "rem", "greatest hits");
        doTestSplitQuery("u2", "u2");
        doTestSplitQuery("u2 rem", "u2", "rem");
        doTestSplitQuery(" u2  \t rem ", "u2", "rem");
        doTestSplitQuery("u2 \"rem\"", "u2", "rem");
        doTestSplitQuery("u2 \"rem", "u2", "\"rem");
        doTestSplitQuery("\"", "\"");

        SearchService service = ServiceFactory.getSearchService();
        assertEquals(0, service.splitQuery("").length);
        assertEquals(0, service.splitQuery(" ").length);
        assertEquals(0, service.splitQuery(null).length);
    }

    private void doTestSplitQuery(String query, String... expected) {
        SearchService service = ServiceFactory.getSearchService();
        String[] actual = service.splitQuery(query);
        assertEquals("Wrong number of criteria.", expected.length, actual.length);

        for (int i = 0; i < expected.length; i++) {
            assertEquals("Wrong criteria.", expected[i], actual[i]);
        }
    }
}