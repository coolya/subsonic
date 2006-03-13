package net.sourceforge.subsonic.service;

import junit.framework.*;
import java.io.*;
import java.util.*;

/**
 * Unit test of {@link SettingsService}.
 *
 * @author Sindre Mehus
 * @version $Revision: 1.4 $ $Date: 2006/03/02 19:56:00 $
 */
public class SettingsServiceTestCase extends TestCase {

    private static final File SUBSONIC_HOME = new File("/tmp/subsonic");

    private SettingsService settingsService;

    protected void setUp() throws Exception {
        System.setProperty("subsonic.home", SUBSONIC_HOME.getPath());
        new File(SUBSONIC_HOME, "subsonic.properties").delete();
        settingsService = new SettingsService();
    }

    public void testSubsonicHome() {
        assertEquals("Wrong Subsonic home.", SUBSONIC_HOME, settingsService.getSubsonicHome());
    }

    public void testDefaultValues() {
        assertEquals("Wrong default language.", "en", settingsService.getLocale().getLanguage());
        assertEquals("Wrong default cover art limit.", 30, settingsService.getCoverArtLimit());
        assertEquals("Wrong default index creation interval.", 1, settingsService.getIndexCreationInterval());
        assertEquals("Wrong default index creation hour.", 3, settingsService.getIndexCreationHour());
        assertEquals("Wrong default playlist folder.", "c:/playlists", settingsService.getPlaylistFolder());
    }

    public void testChangeSettings() {
        settingsService.setIndexString("indexString");
        settingsService.setIgnoredArticles("a the foo bar");
        settingsService.setPlaylistFolder("playlistFolder");
        settingsService.setMusicMask(".mp3 .ogg  .aac");
        settingsService.setCoverArtMask(".jpeg .gif  .png");
        settingsService.setCoverArtLimit(99);
        settingsService.setWelcomeMessage("welcomeMessage");
        settingsService.setLocale(Locale.CANADA_FRENCH);
        settingsService.setIndexCreationInterval(4);
        settingsService.setIndexCreationHour(9);
        verifySettings(settingsService);

        settingsService.save();
        verifySettings(settingsService);

        verifySettings(new SettingsService());
    }

    private void verifySettings(SettingsService ss) {
        assertEquals("Wrong index string.", "indexString", ss.getIndexString());
        assertEquals("Wrong ignored articles.", "a the foo bar", ss.getIgnoredArticles());
        assertArrayEquals("Wrong ignored articles array.", new String[] {"a", "the", "foo", "bar"}, ss.getIgnoredArticlesAsArray());
        assertEquals("Wrong playlist folder.", "playlistFolder", ss.getPlaylistFolder());
        assertEquals("Wrong music mask.", ".mp3 .ogg  .aac", ss.getMusicMask());
        assertArrayEquals("Wrong music mask array.", new String[] {".mp3", ".ogg", ".aac"}, ss.getMusicMaskAsArray());
        assertEquals("Wrong cover art mask.", ".jpeg .gif  .png", ss.getCoverArtMask());
        assertArrayEquals("Wrong cover art mask array.", new String[] {".jpeg", ".gif", ".png"}, ss.getCoverArtMaskAsArray());
        assertEquals("Wrong cover art limit.", 99, ss.getCoverArtLimit());
        assertEquals("Wrong welcome message.", "welcomeMessage", ss.getWelcomeMessage());
        assertEquals("Wrong locale.", Locale.CANADA_FRENCH, ss.getLocale());
        assertEquals("Wrong index creation interval.", 4, ss.getIndexCreationInterval());
        assertEquals("Wrong index creation hour.", 9, ss.getIndexCreationHour());
    }

    private void assertArrayEquals(String message, Object[] expected, String[] actual) {
        assertEquals(message + " (wrong array length).", expected.length, actual.length);
        for (int i = 0; i < expected.length; i++) {
            assertEquals(message, expected[i], actual[i]);
        }
    }
}
