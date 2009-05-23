package net.sourceforge.subsonic.ajax;

import junit.framework.TestCase;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;

/**
 * Unit test of {@link LyricsService}.
 *
 * @author Sindre Mehus
 */
public class LyricsServiceTestCase extends TestCase {

    private LyricsService lyricsService;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        lyricsService = new LyricsService();
    }

    public void testGetLyricsUrl() throws IOException {
        InputStream in = getClass().getResourceAsStream("metrolyrics_search_result.html");
        try {
            String html = IOUtils.toString(in);
            assertEquals("Error in getLyricsUrl().", "http://www.metrolyrics.com/a-song-for-departure-lyrics-manic-street-preachers.html",
                         lyricsService.getLyricsUrl(html));
        } finally {
            IOUtils.closeQuietly(in);
        }
    }

    public void testGetLyricsUrl_NotFound() throws IOException {
        InputStream in = getClass().getResourceAsStream("metrolyrics_search_result_not_found.html");
        try {
            String html = IOUtils.toString(in);
            assertNull("Error in getLyricsUrl().", lyricsService.getLyricsUrl(html));
        } finally {
            IOUtils.closeQuietly(in);
        }
    }

    public void testGetLyrics1() throws IOException {
        InputStream in = getClass().getResourceAsStream("metrolyrics_lyrics_1.html");
        try {
            String html = IOUtils.toString(in);
            String lyrics = lyricsService.getLyrics(html);
            assertNotNull("Error in getLyrics().", lyrics);
            assertTrue("Error in getLyrics().", lyrics.startsWith("&#65;&#110;&#100;"));
            assertTrue("Error in getLyrics().", lyrics.endsWith("&#110;&#103;<br /><br />"));
            assertFalse("Error in getLyrics().", lyrics.contains("<class id=\"NoSteal\">"));
            assertFalse("Error in getLyrics().", lyrics.contains("http://www.metrolyrics.com"));
        } finally {
            IOUtils.closeQuietly(in);
        }
    }

    public void testGetHeader1() throws IOException {
        InputStream in = getClass().getResourceAsStream("metrolyrics_lyrics_1.html");
        try {
            String html = IOUtils.toString(in);
            String header = lyricsService.getHeader(html);
            assertEquals("Error in getHeader().", "A Song For Departure", header);
        } finally {
            IOUtils.closeQuietly(in);
        }
    }
}