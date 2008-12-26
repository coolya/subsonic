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
    private final ChoiceGroup optionsChoiceGroup;

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
        optionsChoiceGroup = new ChoiceGroup("Options", Choice.MULTIPLE);

        optionsChoiceGroup.append("Debug", null);
        optionsChoiceGroup.append("Mock", null);

        baseUrlTextField.setLayout(Item.LAYOUT_NEWLINE_AFTER);
        usernameTextField.setLayout(Item.LAYOUT_NEWLINE_AFTER);
        passwordTextField.setLayout(Item.LAYOUT_NEWLINE_AFTER);
        playerChoiceGroup.setLayout(Item.LAYOUT_NEWLINE_AFTER);
        optionsChoiceGroup.setLayout(Item.LAYOUT_NEWLINE_AFTER);

        append(baseUrlTextField);
        append(usernameTextField);
        append(passwordTextField);
        append(playerChoiceGroup);
        append(optionsChoiceGroup);

        final Command saveCommand = new Command("Save", Command.OK, 1);
        final Command cancelCommand = new Command("Cancel", Command.CANCEL, 2);

        addCommand(saveCommand);
        addCommand(cancelCommand);

        setCommandListener(new CommandListener() {
            public void commandAction(Command command, Displayable displayable) {
                if (command == cancelCommand) {
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
        optionsChoiceGroup.setSelectedFlags(new boolean[]{settingsController.isDebug(), settingsController.isMock()});
    }

    private void save() {
        boolean[] selectedFlags = new boolean[2];
        optionsChoiceGroup.getSelectedFlags(selectedFlags);

        settingsController.setBaseUrl(Util.trimToNull(baseUrlTextField.getString()));
        settingsController.setUsername(Util.trimToNull(usernameTextField.getString()));
        settingsController.setPassword(Util.trimToNull(passwordTextField.getString()));
        settingsController.setPlayer(playerChoiceGroup.getSelectedIndex());
        settingsController.setDebug(selectedFlags[0]);
        settingsController.setMock(selectedFlags[1]);

        display.setCurrent(mainScreen);
    }

    public void setMainScreen(MainScreen mainScreen) {
        this.mainScreen = mainScreen;
    }
}