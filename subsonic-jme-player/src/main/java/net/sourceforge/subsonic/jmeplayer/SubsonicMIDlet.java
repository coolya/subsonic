package net.sourceforge.subsonic.jmeplayer;

import net.sourceforge.subsonic.jmeplayer.screens.ArtistScreen;
import net.sourceforge.subsonic.jmeplayer.screens.IndexScreen;
import net.sourceforge.subsonic.jmeplayer.screens.MainScreen;
import net.sourceforge.subsonic.jmeplayer.screens.MusicDirectoryScreen;
import net.sourceforge.subsonic.jmeplayer.screens.PlayerScreen;
import net.sourceforge.subsonic.jmeplayer.service.MockXMLMusicServiceImpl;
import net.sourceforge.subsonic.jmeplayer.service.MusicService;

import javax.microedition.lcdui.Display;
import javax.microedition.midlet.MIDlet;
import javax.microedition.midlet.MIDletStateChangeException;

/**
 * TODO: Caching of MusicDirectory.  Use stack in MDscreen?
 */
public class SubsonicMIDlet extends MIDlet {

    private final Display display;
    private PlayerScreen playerScreen;

    public SubsonicMIDlet() {
        display = Display.getDisplay(this);
        MusicService musicService = new MockXMLMusicServiceImpl();

        MainScreen mainScreen = new MainScreen(musicService, this, display);
        IndexScreen indexScreen = new IndexScreen(musicService, display);
        ArtistScreen artistScreen = new ArtistScreen(musicService, display);
        MusicDirectoryScreen musicDirectoryScreen = new MusicDirectoryScreen(musicService, display);
        playerScreen = new PlayerScreen(display);

        mainScreen.setIndexScreen(indexScreen);
        indexScreen.setMainScreen(mainScreen);
        indexScreen.setArtistScreen(artistScreen);
        artistScreen.setIndexScreen(indexScreen);
        artistScreen.setMusicDirectoryScreen(musicDirectoryScreen);
        musicDirectoryScreen.setArtistScreen(artistScreen);
        musicDirectoryScreen.setPlayerScreen(playerScreen);
        playerScreen.setMusicDirectoryScreen(musicDirectoryScreen);

        display.setCurrent(mainScreen);
    }

    /**
     * Called when this MIDlet is started for the first time,
     * or when it returns from paused mode.
     */
    public void startApp() throws MIDletStateChangeException {
    }

    /**
     * Called when this MIDlet is paused.
     */
    public void pauseApp() {
        playerScreen.stop();
    }

    /**
     * Destroy must cleanup everything not handled
     * by the garbage collector.
     */
    public void destroyApp(boolean unconditional) {
        playerScreen.stop();
        display.setCurrent(null);
    }

    public void exit() {
        destroyApp(true);
        notifyDestroyed();
    }
}