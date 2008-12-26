/*
 This file is part of Subsonic.

 Subsonic is free software: you can redistribute it and/or modify
 it under the terms of the GNU General Public License as published by
 the Free Software Foundation, either version 3 of the License, or
 (at your option) any later version.

 Subsonic is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with Subsonic.  If not, see <http://www.gnu.org/licenses/>.

 Copyright 2009 (C) Sindre Mehus
 */
package net.sourceforge.subsonic.jmeplayer;

import net.sourceforge.subsonic.jmeplayer.player.PlayerController;
import net.sourceforge.subsonic.jmeplayer.screens.ArtistScreen;
import net.sourceforge.subsonic.jmeplayer.screens.IndexScreen;
import net.sourceforge.subsonic.jmeplayer.screens.MainScreen;
import net.sourceforge.subsonic.jmeplayer.screens.MusicDirectoryScreen;
import net.sourceforge.subsonic.jmeplayer.screens.PlayerScreen;
import net.sourceforge.subsonic.jmeplayer.screens.SettingsScreen;
import net.sourceforge.subsonic.jmeplayer.service.CachedMusicService;
import net.sourceforge.subsonic.jmeplayer.service.HTTPMusicServiceDataSource;
import net.sourceforge.subsonic.jmeplayer.service.MockMusicServiceDataSource;
import net.sourceforge.subsonic.jmeplayer.service.MusicService;
import net.sourceforge.subsonic.jmeplayer.service.MusicServiceDataSource;
import net.sourceforge.subsonic.jmeplayer.service.XMLMusicService;

import javax.microedition.lcdui.Display;
import javax.microedition.midlet.MIDlet;
import javax.microedition.midlet.MIDletStateChangeException;

/**
 * @author Sindre Mehus
 */
public class SubsonicMIDlet extends MIDlet {

    private static final Log LOG = LogFactory.create("SubsonicMIDlet");

    private final Display display;
    private PlayerScreen playerScreen;

    public SubsonicMIDlet() {
        LOG.info("Creating midlet");

        display = Display.getDisplay(this);
        SettingsController settingsController = new SettingsController(this);

        PlayerController playerController = new PlayerController();
        playerController.setSettingsController(settingsController);

        boolean mock = settingsController.isMock();
        MusicServiceDataSource dataSource;
        if (mock) {
            dataSource = new MockMusicServiceDataSource();
        } else {
            dataSource = new HTTPMusicServiceDataSource(settingsController);
        }
        MusicService musicService = new CachedMusicService(new XMLMusicService(dataSource));

        MainScreen mainScreen = new MainScreen(musicService, this, display);
        SettingsScreen settingsScreen = new SettingsScreen(display, settingsController);
        IndexScreen indexScreen = new IndexScreen(musicService, display);
        ArtistScreen artistScreen = new ArtistScreen(display);
        MusicDirectoryScreen musicDirectoryScreen = new MusicDirectoryScreen(musicService, display);
        playerScreen = new PlayerScreen(display);

        mainScreen.setIndexScreen(indexScreen);
        mainScreen.setSettingsScreen(settingsScreen);
        settingsScreen.setMainScreen(mainScreen);
        indexScreen.setMainScreen(mainScreen);
        indexScreen.setArtistScreen(artistScreen);
        artistScreen.setIndexScreen(indexScreen);
        artistScreen.setMusicDirectoryScreen(musicDirectoryScreen);
        musicDirectoryScreen.setArtistScreen(artistScreen);
        musicDirectoryScreen.setPlayerScreen(playerScreen);
        playerScreen.setMusicDirectoryScreen(musicDirectoryScreen);
        playerScreen.setPlayerController(playerController);

        display.setCurrent(mainScreen);
    }

    /**
     * Called when this MIDlet is started for the first time,
     * or when it returns from paused mode.
     */
    public void startApp() throws MIDletStateChangeException {
        LOG.info("Starting midlet");
    }

    /**
     * Called when this MIDlet is paused.
     */
    public void pauseApp() {
        LOG.info("Pausing midlet");
        playerScreen.stop();
    }

    /**
     * Destroy must cleanup everything not handled
     * by the garbage collector.
     */
    public void destroyApp(boolean unconditional) {
        LOG.info("Destroying midlet");
        playerScreen.stop();
        display.setCurrent(null);
    }

    public void exit() {
        destroyApp(true);
        notifyDestroyed();
    }
}