package net.sourceforge.subsonic.booter;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.net.URL;

/**
 * Controls the Subsonic tray icon.
 *
 * @author Sindre Mehus
 */
public class TrayController {

    private final SubsonicController subsonicController;
    private TrayIcon trayIcon;

    private Action openAction;
    private Action statusAction;
    private Action settingsAction;
    private Action exitAction;

    public TrayController(SubsonicController subsonicController) {
        this.subsonicController = subsonicController;
        try {
            createActions();
            createComponents();
            addBehaviour();
            installComponents();
        } catch (Throwable x) {
            System.err.println("Disabling tray support.");
        }
    }

    private void createActions() {
        openAction = new AbstractAction("Open") {
            public void actionPerformed(ActionEvent e) {
                subsonicController.openBrowser();
            }
        };

        statusAction = new AbstractAction("Status") {
            public void actionPerformed(ActionEvent e) {
                subsonicController.showStatus();
            }
        };

        settingsAction = new AbstractAction("Settings") {
            public void actionPerformed(ActionEvent e) {
                subsonicController.showSettings();
            }
        };

        exitAction = new AbstractAction("Exit") {
            public void actionPerformed(ActionEvent e) {
                subsonicController.exit();
            }
        };
    }

    private void createComponents() {
        URL url = Main.class.getResource("/images/subsonic-16.png");
        Image image = Toolkit.getDefaultToolkit().createImage(url);

        PopupMenu menu = new PopupMenu();
        menu.add(createMenuItem(openAction));
        menu.addSeparator();
        menu.add(createMenuItem(statusAction));
        menu.add(createMenuItem(settingsAction));
        menu.addSeparator();
        menu.add(createMenuItem(exitAction));

        trayIcon = new TrayIcon(image, "Subsonic Media Streamer", menu);
    }

    private MenuItem createMenuItem(Action action) {
        MenuItem menuItem = new MenuItem((String) action.getValue(Action.NAME));
        menuItem.addActionListener(action);
        return menuItem;
    }

    private void addBehaviour() {
        trayIcon.addActionListener(statusAction);
    }

    private void installComponents() throws Throwable {
        SystemTray.getSystemTray().add(trayIcon);
        trayIcon.displayMessage("Subsonic", "Subsonic is now running. Click this balloon to get started.",
                                TrayIcon.MessageType.INFO);
    }

    public void uninstallComponents() {
        try {
            SystemTray.getSystemTray().remove(trayIcon);
        } catch (Throwable x) {
            System.err.println("Disabling tray support.");
        }
    }
}
