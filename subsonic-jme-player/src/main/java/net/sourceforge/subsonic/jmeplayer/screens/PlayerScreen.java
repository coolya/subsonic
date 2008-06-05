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

import javax.microedition.lcdui.Alert;
import javax.microedition.lcdui.AlertType;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Form;
import javax.microedition.lcdui.StringItem;
import java.util.Timer;
import java.util.TimerTask;

/**
 * @author Sindre Mehus
 */
public class PlayerScreen extends Form implements PlayerControllerListener, CommandListener {

    private final PlayerController playerController;
    private final StringItem nowPlayingItem;
    private final StringItem stateItem;
    private final StringItem bytesReadItem;
    private final Display display;
    private MusicDirectoryScreen musicDirectoryScreen;

    private final Command backCommand = new Command("Back", Command.BACK, 1);
    private final Command playCommand = new Command("Play", Command.ITEM, 2);
    private final Command resumeCommand = new Command("Resume", Command.ITEM, 3);
    private final Command pauseCommand = new Command("Pause", Command.ITEM, 4);
    private final Command stopCommand = new Command("Stop", Command.ITEM, 5);
    private final Command nextCommand = new Command("Next", Command.ITEM, 6);
    private final Command previousCommand = new Command("Previous", Command.ITEM, 7);

    public PlayerScreen(Display display) {
        super("Player");
        this.display = display;
        addCommand(backCommand);

        nowPlayingItem = new StringItem("Now playing: ", null);
        stateItem = new StringItem("Status: ", null);
        bytesReadItem = new StringItem("Bytes read: ", null);

        append(nowPlayingItem);
        append(stateItem);
        append(bytesReadItem);

        playerController = new PlayerController(this);

        setCommandListener(this);

        TimerTask task = new TimerTask() {
            public void run() {
                updateBytesRead();
            }
        };
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(task, 0L, 1000L);
    }

    public void setMusicDirectoryEntries(MusicDirectory.Entry[] entries) {
        playerController.setEntries(entries);
    }

    private void updateBytesRead() {
        bytesReadItem.setText(String.valueOf(playerController.getBytesRead()));
    }

    public void stateChanged(int state) {
        String text;
        switch (state) {
            case PlayerController.STOPPED:
                addCommand(playCommand);
                removeCommand(pauseCommand);
                removeCommand(resumeCommand);
                addCommand(previousCommand);
                addCommand(nextCommand);
                removeCommand(stopCommand);
                text = "Stopped";
                break;
            case PlayerController.CONNECTING:
                removeCommand(playCommand);
                removeCommand(pauseCommand);
                removeCommand(resumeCommand);
                addCommand(previousCommand);
                addCommand(nextCommand);
                addCommand(stopCommand);
                text = "Connecting";
                break;
            case PlayerController.BUFFERING:
                removeCommand(playCommand);
                removeCommand(pauseCommand);
                removeCommand(resumeCommand);
                addCommand(previousCommand);
                addCommand(nextCommand);
                addCommand(stopCommand);
                text = "Buffering";
                break;
            case PlayerController.PLAYING:
                removeCommand(playCommand);
                addCommand(pauseCommand);
                removeCommand(resumeCommand);
                addCommand(previousCommand);
                addCommand(nextCommand);
                addCommand(stopCommand);
                text = "Playing";
                break;
            case PlayerController.PAUSED:
                removeCommand(playCommand);
                removeCommand(pauseCommand);
                addCommand(resumeCommand);
                addCommand(previousCommand);
                addCommand(nextCommand);
                addCommand(stopCommand);
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
        x.printStackTrace();
        Alert alert = new Alert("Error");
        alert.setString(x.getMessage());
        alert.setType(AlertType.ERROR);
        alert.setTimeout(Alert.FOREVER);
        display.setCurrent(alert);
    }

    public void busy(boolean busy) {
        // TODO
    }

    public void setMusicDirectoryScreen(MusicDirectoryScreen musicDirectoryScreen) {
        this.musicDirectoryScreen = musicDirectoryScreen;
    }

    public void commandAction(Command command, Displayable displayable) {
        // TODO: Implement all commands
        if (command == backCommand) {
            playerController.stop();
            display.setCurrent(musicDirectoryScreen);
        } else if (command == playCommand) {
            playerController.play();
        } else if (command == pauseCommand) {
            playerController.pause();
        } else if (command == resumeCommand) {
            playerController.resume();
        } else if (command == previousCommand) {
            playerController.previous();
        } else if (command == nextCommand) {
            playerController.next();
        } else if (command == stopCommand) {
            playerController.stop();
        }
    }
}