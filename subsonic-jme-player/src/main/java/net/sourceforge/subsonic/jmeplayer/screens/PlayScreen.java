/*
 * (c) Copyright WesternGeco. Unpublished work, created 2008. All rights
 * reserved under copyright laws. This information is confidential and is
 * the trade property of WesternGeco. Do not use, disclose, or reproduce
 * without the prior written permission of the owner.
 */
package net.sourceforge.subsonic.jmeplayer.screens;

import net.sourceforge.subsonic.jmeplayer.Util;
import net.sourceforge.subsonic.jmeplayer.domain.MusicDirectory;

import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.Form;
import javax.microedition.lcdui.StringItem;
import javax.microedition.media.Manager;
import javax.microedition.media.Player;
import javax.microedition.media.PlayerListener;
import java.io.InputStream;

/**
 * @author Sindre Mehus
 */
public class PlayScreen extends Form implements PlayerListener {

    private StringItem nowPlayingItem;
    private StringItem statusItem;
    private MusicDirectory.Entry musicDirectoryEntry;
    private Player player;

    // TODO: Add player listener

    public PlayScreen() {
        super("Playing");
        addCommand(new Command("Back", Command.BACK, 1));
        addCommand(new Command("Play", Command.ITEM, 1));
        nowPlayingItem = new StringItem("Now playing: ", null);
        statusItem = new StringItem("Status: ", null);
        append(nowPlayingItem);
        append(statusItem);
    }

    public void setMusicDirectoryEntry(MusicDirectory.Entry entry) {
        this.musicDirectoryEntry = entry;
        nowPlayingItem.setText(entry.getName());
    }

    public void start() throws Exception {
        stop();
        createPlayer();
        System.out.println("Player created.");
        player.prefetch();
        System.out.println("Player prefetched.");
        player.start();
        System.out.println("Player started.");
    }

    public void stop() throws Exception {
        if (player != null) {
            // TODO
            try {
                player.stop();
                player.close();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                player = null;
            }
        }
    }

    private void createPlayer() throws Exception {
        String url = musicDirectoryEntry.getUrl();
        if (url.startsWith("resource:")) {
            InputStream in = getClass().getResourceAsStream(url.substring(9));
            String contentType = Util.guessContentType(url);
            player = Manager.createPlayer(in, contentType);
        } else {
            player = Manager.createPlayer(url);
        }

        player.addPlayerListener(this);
    }

    public void playerUpdate(Player player, String event, Object eventData) {
        statusItem.setText(event + " - " + eventData);
    }
}