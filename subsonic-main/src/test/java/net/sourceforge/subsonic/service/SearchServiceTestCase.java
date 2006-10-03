package net.sourceforge.subsonic.service;

import junit.framework.*;
import net.sourceforge.subsonic.domain.*;
import org.apache.commons.lang.*;

import java.io.*;
import java.util.*;


/**
 * Unit test of {@link SearchService}.
 *
 * @author Sindre Mehus
 */
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
                MetaData metaData = new MetaData();
                metaData.setArtist(artist);
                metaData.setAlbum(album);
                metaData.setTitle(title);
                metaData.setYear(year);
                return metaData;
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
                          StringUtils.upperCase(artist) + SearchService.Line.SEPARATOR +
                          StringUtils.upperCase(album) + SearchService.Line.SEPARATOR +
                          StringUtils.upperCase(title) + SearchService.Line.SEPARATOR +
                          yearString;

        assertEquals("Error in toString().", expected, line.toString());
        assertEquals("Error in forFile().",  expected, SearchService.Line.forFile(file, new HashMap<File, SearchService.Line>()).toString());
        assertEquals("Error in parse().",    expected, SearchService.Line.parse(expected).toString());
    }
}