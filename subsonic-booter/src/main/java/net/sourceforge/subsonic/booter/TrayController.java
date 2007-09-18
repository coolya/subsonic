package net.sourceforge.subsonic.booter;

import org.jdesktop.jdic.tray.SystemTray;
import org.jdesktop.jdic.tray.TrayIcon;

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
        createActions();
        createComponents();
        addBehaviour();
        installComponents();
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

        JPopupMenu menu = new JPopupMenu();
        JMenuItem item = menu.add(statusAction);
        item.setFont(item.getFont().deriveFont(Font.BOLD));
        menu.add(settingsAction);
        menu.addSeparator();
        menu.add(openAction);
        menu.addSeparator();
        menu.add(exitAction);

        trayIcon = new TrayIcon(new ImageIcon(image), "Subsonic Media Streamer", menu);
    }

    private void addBehaviour() {
        trayIcon.addActionListener(statusAction);
        trayIcon.addBalloonActionListener(statusAction);
    }

    private void installComponents() {
        SystemTray.getDefaultSystemTray().addTrayIcon(trayIcon);
        trayIcon.displayMessage("Subsonic", "Subsonic is now running. Click this balloon to get started.",
                                TrayIcon.INFO_MESSAGE_TYPE);
    }

    public void uninstallComponents() {
        SystemTray.getDefaultSystemTray().removeTrayIcon(trayIcon);
    }

}
