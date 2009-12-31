package net.sourceforge.subsonic.ajax;

import junit.framework.TestCase;
import org.apache.commons.io.IOUtils;

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

    public void testGetLyrics() throws Exception {
        InputStream in = getClass().getResourceAsStream("lyricsfly-found.xml");
        try {
            String xml = IOUtils.toString(in);
            LyricsInfo lyricsInfo = lyricsService.parse(xml);
            assertEquals("Wrong lyrics header.", "U2 - Beautiful Day", lyricsInfo.getHeader());
            assertTrue("Wrong lyrics.", lyricsInfo.getLyrics().startsWith("The heart is a bloom<br>"));
        } finally {
            IOUtils.closeQuietly(in);
        }
    }

    public void testGetLyricsNotFound() throws Exception {
        InputStream in = getClass().getResourceAsStream("lyricsfly-notfound.xml");
        try {
            String xml = IOUtils.toString(in);
            lyricsService.parse(xml);
            fail("Expected exception.");
        } catch (Exception x) {
            // Expected
        } finally {
            IOUtils.closeQuietly(in);
        }
    }
}