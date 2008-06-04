/*
 * (c) Copyright WesternGeco. Unpublished work, created 2008. All rights
 * reserved under copyright laws. This information is confidential and is
 * the trade property of WesternGeco. Do not use, disclose, or reproduce
 * without the prior written permission of the owner.
 */
package net.sourceforge.subsonic.jmeplayer.screens;

import net.sourceforge.subsonic.jmeplayer.domain.MusicDirectory;

import javax.microedition.io.Connector;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.Form;
import javax.microedition.lcdui.StringItem;
import javax.microedition.media.Manager;
import javax.microedition.media.Player;
import javax.microedition.media.PlayerListener;
import java.io.InputStream;
import java.util.Timer;
import java.util.TimerTask;

/**
 * @author Sindre Mehus
 */
public class PlayScreen extends Form implements PlayerListener {

    private final StringItem nowPlayingItem;
    private final StringItem statusItem;
    private MusicDirectory.Entry entry;
    private Player player;
    private MonitoredInputStream input;
    private String status = "Stopped";

    // TODO: Add player listener

    public PlayScreen() {
        super("Playing");
        addCommand(new Command("Back", Command.BACK, 1));
        addCommand(new Command("Play", Command.ITEM, 1));
        nowPlayingItem = new StringItem("Now playing: ", null);
        statusItem = new StringItem("Status: ", null);
        append(nowPlayingItem);
        append(statusItem);

        TimerTask task = new TimerTask() {
            public void run() {
                updateStatus();
            }
        };
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(task, 0L, 1000L);
    }

    public void setMusicDirectoryEntry(MusicDirectory.Entry entry) {
        this.entry = entry;
        nowPlayingItem.setText(entry.getName());
    }

    public synchronized void start() throws Exception {
        stop();
        createPlayer();
        status = "Buffering";
        player.start();
    }

    public synchronized void stop() throws Exception {
        status = "Stopped";
        if (player != null) {
            // TODO
            try {
                player.stop();
                player.close();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                player = null;
                input = null;
            }
        }
    }

    private void createPlayer() throws Exception {
        status = "Connecting";
        String url = entry.getUrl();
        InputStream in;
        if (url.startsWith("resource:")) {
            in = getClass().getResourceAsStream(url.substring(9));
        } else {
            in = Connector.openInputStream(url);
        }

        input = new MonitoredInputStream(in);
        player = Manager.createPlayer(input, entry.getContentType());
        player.addPlayerListener(this);
    }

    private void updateStatus() {
        String s = status;
        if (input != null) {
            s += " - " + input.getBytesRead() + " bytes read.";
        }
        statusItem.setText(s);
    }

    public void playerUpdate(Player player, String event, Object eventData) {
        switch (player.getState()) {
            case Player.PREFETCHED:
                status = "Ready";
                break;
            case Player.STARTED:
                status = "Playing";
                break;
            case Player.CLOSED:
                status = "Stopped";
                break;
            default:
                System.out.println("state: " + player.getState());
                break;
        }
    }

}