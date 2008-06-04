/*
 * (c) Copyright WesternGeco. Unpublished work, created 2008. All rights
 * reserved under copyright laws. This information is confidential and is
 * the trade property of WesternGeco. Do not use, disclose, or reproduce
 * without the prior written permission of the owner.
 */
package net.sourceforge.subsonic.jmeplayer.screens;

import net.sourceforge.subsonic.jmeplayer.PlayerController;
import net.sourceforge.subsonic.jmeplayer.PlayerControllerListener;
import net.sourceforge.subsonic.jmeplayer.domain.MusicDirectory;

import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.Form;
import javax.microedition.lcdui.StringItem;
import java.util.Timer;
import java.util.TimerTask;

/**
 * @author Sindre Mehus
 */
public class PlayScreen extends Form implements PlayerControllerListener {

    private final PlayerController playerController;
    private final StringItem nowPlayingItem;
    private final StringItem stateItem;
    private final StringItem bytesReadItem;

    public PlayScreen() {
        super("Player");
        addCommand(new Command("Back", Command.BACK, 1));
        addCommand(new Command("Play", Command.ITEM, 1));
        nowPlayingItem = new StringItem("Now playing: ", null);
        stateItem = new StringItem("Status: ", null);
        bytesReadItem = new StringItem("Bytes read: ", null);

        append(nowPlayingItem);
        append(stateItem);
        append(bytesReadItem);

        playerController = new PlayerController(this);

        TimerTask task = new TimerTask() {
            public void run() {
                updateBytesRead();
            }
        };
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(task, 0L, 1000L);
    }

    public void setMusicDirectoryEntry(MusicDirectory.Entry entry) {
        playerController.setEntries(new MusicDirectory.Entry[]{entry});
//        nowPlayingItem.setText(entry.getName());
    }

    public void start() {
        playerController.play();
    }

    public void stop() throws Exception {
        playerController.stop();
    }

    private void updateBytesRead() {
        bytesReadItem.setText(String.valueOf(playerController.getBytesRead()));
    }

    public void stateChanged(int state) {
        String text;
        switch (state) {
            case PlayerController.STOPPED:
                text = "Stopped";
                break;
            case PlayerController.CONNECTING:
                text = "Connecting";
                break;
            case PlayerController.BUFFERING:
                text = "Buffering";
                break;
            case PlayerController.PLAYING:
                text = "Playing";
                break;
            case PlayerController.PAUSED:
                text = "Paused";
                break;
            default:
                text = "Unknown state";
                break;
        }
        stateItem.setText(text);
    }

    public void songChanged(MusicDirectory.Entry entry) {
        nowPlayingItem.setText(entry == null ? null : entry.getName());
    }

    public void error(Exception x) {
        // TODO
    }

    public void busy(boolean busy) {
        // TODO
    }
}