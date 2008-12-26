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

    private final StringItem trackItem;
    private final StringItem titleItem;
    private final StringItem artistItem;
    private final StringItem stateItem;
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

    private int state;
    private long bytesRead;

    public PlayerScreen(Display display) {
        super("Subsonic Player");
        this.display = display;
        addCommand(backCommand);

        trackItem = new StringItem(null, null);
        titleItem = new StringItem(null, null);
        artistItem = new StringItem(null, null);
        stateItem = new StringItem(null, null);
        Spacer spacer = new Spacer(1, 12);

        trackItem.setLayout(Item.LAYOUT_NEWLINE_AFTER);
        titleItem.setLayout(Item.LAYOUT_NEWLINE_AFTER);
        artistItem.setLayout(Item.LAYOUT_NEWLINE_AFTER);
        stateItem.setLayout(Item.LAYOUT_NEWLINE_AFTER);
        spacer.setLayout(Item.LAYOUT_NEWLINE_AFTER);

        append(trackItem);
        append(titleItem);
        append(artistItem);
        append(spacer);
        append(stateItem);

        setCommandListener(this);
    }

    public void setPlayerController(PlayerController playerController) {
        this.playerController = playerController;
        playerController.setListener(this);
    }

    public void setMusicDirectoryEntries(MusicDirectory musicDirectory, MusicDirectory.Entry[] entries) {
        artistItem.setLabel(musicDirectory.getLongName());
        playerController.setEntries(entries);
    }

    public void stateChanged(int state) {
        int size = playerController.size();
        int index = playerController.getCurrentIndex();
        boolean nextEnabled = index < size - 1;
        boolean previousEnabled = index > 0;

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
                break;
            default:
                break;
        }
        this.state = state;
        updateState();
    }

    public void songChanged(MusicDirectory.Entry entry) {
        updateTrack();
        updateTitle();
        stateChanged(playerController.getState());
    }

    public void bytesRead(long n) {
        bytesRead = n;
        updateState();
    }

    private void updateState() {
        StringBuffer text = new StringBuffer(32);
        switch (state) {
            case PlayerController.STOPPED:
                text.append("Stopped");
                break;
            case PlayerController.CONNECTING:
                text.append("Connecting");
                break;
            case PlayerController.BUFFERING:
                text.append("Buffering");
                break;
            case PlayerController.PLAYING:
                text.append("Playing");
                break;
            case PlayerController.PAUSED:
                text.append("Paused");
                break;
            default:
                text.append("Unknown");
                break;
        }

        long kilobytesRead = bytesRead / 1024L;
        if (kilobytesRead > 0L) {
            text.append(" (").append(kilobytesRead).append(" KB)");
        }
        stateItem.setText(text.toString());
    }

    private void updateTrack() {
        int size = playerController.size();
        int index = playerController.getCurrentIndex();
        if (size > 1) {
            trackItem.setText((index + 1) + " of " + size);
        } else {
            trackItem.setText(null);
        }
    }

    private void updateTitle() {
        MusicDirectory.Entry entry = playerController.getCurrent();
        titleItem.setLabel(entry == null ? null : entry.getName());
    }

    public void error(Throwable x) {
        Util.showError(x, display, this);
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