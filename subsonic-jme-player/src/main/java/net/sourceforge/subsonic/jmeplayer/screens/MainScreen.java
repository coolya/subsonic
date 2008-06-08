package net.sourceforge.subsonic.jmeplayer.screens;

import net.sourceforge.subsonic.jmeplayer.SubsonicMIDlet;
import net.sourceforge.subsonic.jmeplayer.service.MusicService;

import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.List;

/**
 * Main screen.
 *
 * @author Sindre Mehus
 */
public class MainScreen extends List {

    private final Display display;
    private IndexScreen indexScreen;
    private SettingsScreen settingsScreen;

    public MainScreen(MusicService musicService, final SubsonicMIDlet midlet, final Display display) {
        super("Subsonic", IMPLICIT);
        this.display = display;

        append("Music", null);
        append("Settings", null);
        append("Exit", null);

        final Command selectCommand = new Command("Select", Command.ITEM, 1);
        final Command exitCommand = new Command("Exit", Command.EXIT, 2);

        addCommand(selectCommand);
        addCommand(exitCommand);
        setSelectCommand(selectCommand);

        setCommandListener(new CommandListener() {
            public void commandAction(Command command, Displayable displayable) {
                if (command == selectCommand) {
                    switch (getSelectedIndex()) {
                        case 0:
                            music();
                            break;
                        case 1:
                            settings();
                            break;
                        case 2:
                            midlet.exit();
                            break;
                        default:
                            break;
                    }
                } else if (command == exitCommand) {
                    midlet.exit();
                }
            }
        });
    }

    private void music() {
        display.setCurrent(indexScreen);
        indexScreen.loadIndexes();
    }

    private void settings() {
        display.setCurrent(settingsScreen);
        settingsScreen.load();
    }

    public void setSettingsScreen(SettingsScreen settingsScreen) {
        this.settingsScreen = settingsScreen;
    }

    public void setIndexScreen(IndexScreen indexScreen) {
        this.indexScreen = indexScreen;
    }
}