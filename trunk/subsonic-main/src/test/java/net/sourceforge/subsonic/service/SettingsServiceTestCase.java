package net.sourceforge.subsonic.service;

import junit.framework.*;
import java.io.*;
import java.util.*;

/**
 * Unit test of {@link SettingsService}.
 *
 * @author Sindre Mehus
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
        assertEquals("Wrong Subsonic home.", SUBSONIC_HOME, SettingsService.getSubsonicHome());
    }

    public void testDefaultValues() {
        assertEquals("Wrong default language.", "en", settingsService.getLocale().getLanguage());
        assertEquals("Wrong default cover art limit.", 30, settingsService.getCoverArtLimit());
        assertEquals("Wrong default index creation interval.", 1, settingsService.getIndexCreationInterval());
        assertEquals("Wrong default index creation hour.", 3, settingsService.getIndexCreationHour());
        assertEquals("Wrong default playlist folder.", "c:/playlists", settingsService.getPlaylistFolder());
        assertEquals("Wrong default theme.", "default", settingsService.getThemeId());
        assertEquals("Wrong default stream port.", 0, settingsService.getStreamPort());
    }

    public void testChangeSettings() {
        settingsService.setIndexString("indexString");
        settingsService.setIgnoredArticles("a the foo bar");
        settingsService.setShortcuts("new incoming \"rock 'n' roll\"");
        settingsService.setPlaylistFolder("playlistFolder");
        settingsService.setMusicMask(".mp3 .ogg  .aac");
        settingsService.setCoverArtMask(".jpeg .gif  .png");
        settingsService.setCoverArtLimit(99);
        settingsService.setWelcomeMessage("welcomeMessage");
        settingsService.setLocale(Locale.CANADA_FRENCH);
        settingsService.setThemeId("dark");
        settingsService.setIndexCreationInterval(4);
        settingsService.setIndexCreationHour(9);
        settingsService.setStreamPort(8080);

        verifySettings(settingsService);

        settingsService.save();
        verifySettings(settingsService);

        verifySettings(new SettingsService());
    }

    private void verifySettings(SettingsService ss) {
        assertEquals("Wrong index string.", "indexString", ss.getIndexString());
        assertEquals("Wrong ignored articles.", "a the foo bar", ss.getIgnoredArticles());
        assertEquals("Wrong shortcuts.", "new incoming \"rock 'n' roll\"", ss.getShortcuts());
        assertTrue("Wrong ignored articles array.", Arrays.equals(new String[] {"a", "the", "foo", "bar"}, ss.getIgnoredArticlesAsArray()));
        assertTrue("Wrong shortcut array.", Arrays.equals(new String[] {"new", "incoming", "rock 'n' roll"}, ss.getShortcutsAsArray()));
        assertEquals("Wrong playlist folder.", "playlistFolder", ss.getPlaylistFolder());
        assertEquals("Wrong music mask.", ".mp3 .ogg  .aac", ss.getMusicMask());
        assertTrue("Wrong music mask array.", Arrays.equals(new String[] {".mp3", ".ogg", ".aac"}, ss.getMusicMaskAsArray()));
        assertEquals("Wrong cover art mask.", ".jpeg .gif  .png", ss.getCoverArtMask());
        assertTrue("Wrong cover art mask array.", Arrays.equals(new String[] {".jpeg", ".gif", ".png"}, ss.getCoverArtMaskAsArray()));
        assertEquals("Wrong cover art limit.", 99, ss.getCoverArtLimit());
        assertEquals("Wrong welcome message.", "welcomeMessage", ss.getWelcomeMessage());
        assertEquals("Wrong locale.", Locale.CANADA_FRENCH, ss.getLocale());
        assertEquals("Wrong theme.", "dark", ss.getThemeId());
        assertEquals("Wrong index creation interval.", 4, ss.getIndexCreationInterval());
        assertEquals("Wrong index creation hour.", 9, ss.getIndexCreationHour());
        assertEquals("Wrong stream port.", 8080, ss.getStreamPort());
    }
}
