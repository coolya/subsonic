package net.sourceforge.subsonic.jmeplayer.screens;

import net.sourceforge.subsonic.jmeplayer.Util;
import net.sourceforge.subsonic.jmeplayer.domain.MusicDirectory;
import net.sourceforge.subsonic.jmeplayer.player.PlayerController;
import net.sourceforge.subsonic.jmeplayer.player.PlayerControllerListener;

import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Form;
import javax.microedition.lcdui.Item;
import javax.microedition.lcdui.Spacer;
import javax.microedition.lcdui.StringItem;

/**
 * @author Sindre Mehus
 */
public class PlayerScreen extends Form implements PlayerControllerListener, CommandListener {

    private final StringItem nowPlayingItem;
    private final StringItem stateItem;
    private final StringItem bytesReadItem;
    private final Display display;
    private PlayerController playerController;
    private MusicDirectoryScreen musicDirectoryScreen;

    private final Command backCommand = new Command("Back", Command.BACK, 1);
    private final Command playCommand = new Command("Play", Command.ITEM, 2);
    private final Command resumeCommand = new Command("Resume", Command.ITEM, 3);
    private final Command pauseCommand = new Command("Pause", Command.ITEM, 4);
    private final Command stopCommand = new Command("Stop", Command.ITEM, 5);
    private final Command nextCommand = new Command("Next", Command.ITEM, 6);
    private final Command previousCommand = new Command("Previous", Command.ITEM, 7);

    public PlayerScreen(Display display) {
        super("Subsonic Player");
        this.display = display;
        addCommand(backCommand);

        nowPlayingItem = new StringItem(null, null);
        stateItem = new StringItem(null, null);
        bytesReadItem = new StringItem("Bytes read: ", null);
        Spacer spacer = new Spacer(1, 12);

        nowPlayingItem.setLayout(Item.LAYOUT_NEWLINE_AFTER);
        stateItem.setLayout(Item.LAYOUT_NEWLINE_AFTER);
        bytesReadItem.setLayout(Item.LAYOUT_NEWLINE_AFTER);
        spacer.setLayout(Item.LAYOUT_NEWLINE_AFTER);

        append(nowPlayingItem);
        append(spacer);
        append(stateItem);
        append(bytesReadItem);

        setCommandListener(this);
    }

    public void setPlayerController(PlayerController playerController) {
        this.playerController = playerController;
        playerController.setListener(this);
    }

    public void setMusicDirectoryEntries(MusicDirectory.Entry[] entries) {
        playerController.setEntries(entries);
    }

    public void stateChanged(int state) {
        int size = playerController.size();
        int index = playerController.getCurrentIndex();
        boolean nextEnabled = index < size - 1;
        boolean previousEnabled = index > 0;

        String text;
        switch (state) {
            case PlayerController.STOPPED:
                addCommand(playCommand);
                removeCommand(pauseCommand);
                removeCommand(resumeCommand);
                if (previousEnabled) {
                    addCommand(previousCommand);
                } else {
                    removeCommand(previousCommand);
                }
                if (nextEnabled) {
                    addCommand(nextCommand);
                } else {
                    removeCommand(nextCommand);
                }
                removeCommand(stopCommand);
                text = "Stopped";
                break;
            case PlayerController.CONNECTING:
                removeCommand(playCommand);
                removeCommand(pauseCommand);
                removeCommand(resumeCommand);
                if (previousEnabled) {
                    addCommand(previousCommand);
                } else {
                    removeCommand(previousCommand);
                }
                if (nextEnabled) {
                    addCommand(nextCommand);
                } else {
                    removeCommand(nextCommand);
                }
                addCommand(stopCommand);
                text = "Connecting";
                break;
            case PlayerController.BUFFERING:
                removeCommand(playCommand);
                removeCommand(pauseCommand);
                removeCommand(resumeCommand);
                if (previousEnabled) {
                    addCommand(previousCommand);
                } else {
                    removeCommand(previousCommand);
                }
                if (nextEnabled) {
                    addCommand(nextCommand);
                } else {
                    removeCommand(nextCommand);
                }
                addCommand(stopCommand);
                text = "Buffering";
                break;
            case PlayerController.PLAYING:
                removeCommand(playCommand);
                addCommand(pauseCommand);
                removeCommand(resumeCommand);
                if (previousEnabled) {
                    addCommand(previousCommand);
                } else {
                    removeCommand(previousCommand);
                }
                if (nextEnabled) {
                    addCommand(nextCommand);
                } else {
                    removeCommand(nextCommand);
                }
                addCommand(stopCommand);
                text = "Playing";
                break;
            case PlayerController.PAUSED:
                removeCommand(playCommand);
                removeCommand(pauseCommand);
                addCommand(resumeCommand);
                if (previousEnabled) {
                    addCommand(previousCommand);
                } else {
                    removeCommand(previousCommand);
                }
                if (nextEnabled) {
                    addCommand(nextCommand);
                } else {
                    removeCommand(nextCommand);
                }
                addCommand(stopCommand);
                text = "Paused";
                break;
            default:
                text = "Unknown";
                break;
        }
        stateItem.setText(text);
    }

    public void songChanged(MusicDirectory.Entry entry) {
        String text = "";
        int size = playerController.size();
        int index = playerController.getCurrentIndex();
        if (size > 1) {
            text = (index + 1) + " of " + size + " - ";
        }
        if (entry != null) {
            text += entry.getName();
        }
        nowPlayingItem.setLabel(text);

        stateChanged(playerController.getState());
    }

    public void bytesRead(long n) {
        bytesReadItem.setText(Util.formatBytes(n));
    }

    public void error(Throwable x) {
        Util.showError(x, display, this);
    }

    public void busy(boolean busy) {
    }

    public void setMusicDirectoryScreen(MusicDirectoryScreen musicDirectoryScreen) {
        this.musicDirectoryScreen = musicDirectoryScreen;
    }

    public void commandAction(Command command, Displayable displayable) {
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

    public void stop() {
        playerController.stop();
    }
}