package net.sourceforge.subsonic.jmeplayer;

import net.sourceforge.subsonic.jmeplayer.screens.ArtistScreen;
import net.sourceforge.subsonic.jmeplayer.screens.IndexScreen;
import net.sourceforge.subsonic.jmeplayer.screens.MusicDirectoryScreen;
import net.sourceforge.subsonic.jmeplayer.screens.PlayerScreen;
import net.sourceforge.subsonic.jmeplayer.service.MockXMLMusicServiceImpl;
import net.sourceforge.subsonic.jmeplayer.service.MusicService;

import javax.microedition.lcdui.Display;
import javax.microedition.midlet.MIDlet;
import javax.microedition.midlet.MIDletStateChangeException;

/**
 * TODO: Add Exit command to menu
 * TODO: Show blocking dialog while doing lengthy tasks.
 * TODO: MIDlet lifecycle.
 * TODO: Caching of MusicDirectory.  Use stack in MDscreen?
 */
public class SubsonicPlayer extends MIDlet {

    public void startApp() throws MIDletStateChangeException {
        Display display = Display.getDisplay(this);
        MusicService musicService = new MockXMLMusicServiceImpl();

        IndexScreen indexScreen = new IndexScreen(musicService, display);
        ArtistScreen artistScreen = new ArtistScreen(musicService, display);
        MusicDirectoryScreen musicDirectoryScreen = new MusicDirectoryScreen(musicService, display);
        PlayerScreen playerScreen = new PlayerScreen(display);

        indexScreen.setArtistScreen(artistScreen);
        artistScreen.setIndexScreen(indexScreen);
        artistScreen.setMusicDirectoryScreen(musicDirectoryScreen);
        musicDirectoryScreen.setArtistScreen(artistScreen);
        musicDirectoryScreen.setPlayerScreen(playerScreen);
        playerScreen.setMusicDirectoryScreen(musicDirectoryScreen);

        display.setCurrent(indexScreen);
        indexScreen.loadIndexes();
    }

    public void pauseApp() {
        // TODO
    }

    public void destroyApp(boolean unconditional) {
        // TODO
        notifyDestroyed();
    }

}