package net.sourceforge.subsonic.jmeplayer.screens;

import net.sourceforge.subsonic.jmeplayer.SubsonicMIDlet;
import net.sourceforge.subsonic.jmeplayer.domain.MusicDirectory;
import net.sourceforge.subsonic.jmeplayer.player.DownloadController;
import net.sourceforge.subsonic.jmeplayer.player.DownloadControllerListener;
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
    private DownloadController downloadController;

    public MainScreen(MusicService musicService, final SubsonicMIDlet midlet, final Display display) {
        super("Subsonic", IMPLICIT);
        this.display = display;

        append("Music", null);
        append("Download", null);
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
                            download();
                            break;
                        case 2:
                            settings();
                            break;
                        case 3:
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
        indexScreen.loadIndexes();
    }

    private void download() {
        // TODO: Testing
        downloadController.setListener(new DownloadControllerListener() {
            public void stateChanged(int state) {
            }

            public void bytesRead(long n) {
            }
        });

        downloadController.download(new MusicDirectory.Entry("Real Life", null, false, "http://localhost/subsonic/stream?player=2&pathUtf8Hex=653a5c6d757369635c5468652053657074656d626572205768656e5c487567676572204d75676765725c3130202d205265616c204c6966652e6d7033&suffix=.mp3", null));
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

    public void setDownloadController(DownloadController downloadController) {
        this.downloadController = downloadController;
    }
}