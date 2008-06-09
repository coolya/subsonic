package net.sourceforge.subsonic.jmeplayer.screens;

import net.sourceforge.subsonic.jmeplayer.SettingsController;
import net.sourceforge.subsonic.jmeplayer.Util;

import javax.microedition.lcdui.Choice;
import javax.microedition.lcdui.ChoiceGroup;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Form;
import javax.microedition.lcdui.Item;
import javax.microedition.lcdui.TextField;

/**
 * @author Sindre Mehus
 */
public class SettingsScreen extends Form {

    private static final int MAX_PLAYER = 100;

    private final SettingsController settingsController;
    private final Display display;
    private MainScreen mainScreen;

    private final TextField baseUrlTextField;
    private final TextField usernameTextField;
    private final TextField passwordTextField;
    private final ChoiceGroup playerChoiceGroup;

    public SettingsScreen(final Display display, SettingsController settingsController) {
        super("Settings");
        this.display = display;
        this.settingsController = settingsController;

        String[] playerChoices = new String[MAX_PLAYER];
        playerChoices[0] = "Auto";
        for (int i = 1; i < MAX_PLAYER; i++) {
            playerChoices[i] = String.valueOf(i);
        }

        baseUrlTextField = new TextField("Subsonic Server URL", null, 300, TextField.URL);
        usernameTextField = new TextField("Username", null, 50, TextField.NON_PREDICTIVE);
        passwordTextField = new TextField("Password", null, 50, TextField.PASSWORD | TextField.NON_PREDICTIVE);
        playerChoiceGroup = new ChoiceGroup("Player ID", Choice.POPUP, playerChoices, null);

        baseUrlTextField.setLayout(Item.LAYOUT_NEWLINE_AFTER);
        usernameTextField.setLayout(Item.LAYOUT_NEWLINE_AFTER);
        passwordTextField.setLayout(Item.LAYOUT_NEWLINE_AFTER);
        playerChoiceGroup.setLayout(Item.LAYOUT_NEWLINE_AFTER);

        append(baseUrlTextField);
        append(usernameTextField);
        append(passwordTextField);
        append(playerChoiceGroup);

        final Command saveCommand = new Command("Save", Command.OK, 1);
        final Command backCommand = new Command("Back", Command.BACK, 2);

        addCommand(saveCommand);
        addCommand(backCommand);

        setCommandListener(new CommandListener() {
            public void commandAction(Command command, Displayable displayable) {
                if (command == backCommand) {
                    display.setCurrent(mainScreen);
                } else if (command == saveCommand) {
                    save();
                }
            }
        });
    }

    public void load() {
        baseUrlTextField.setString(settingsController.getBaseUrl());
        usernameTextField.setString(settingsController.getUsername());
        passwordTextField.setString(settingsController.getPassword());
        playerChoiceGroup.setSelectedIndex(Math.min(settingsController.getPlayer(), MAX_PLAYER - 1), true);
    }

    private void save() {
        settingsController.setBaseUrl(Util.trimToNull(baseUrlTextField.getString()));
        settingsController.setUsername(Util.trimToNull(usernameTextField.getString()));
        settingsController.setPassword(Util.trimToNull(passwordTextField.getString()));
        settingsController.setPlayer(playerChoiceGroup.getSelectedIndex());
        display.setCurrent(mainScreen);
    }

    public void setMainScreen(MainScreen mainScreen) {
        this.mainScreen = mainScreen;
    }
}