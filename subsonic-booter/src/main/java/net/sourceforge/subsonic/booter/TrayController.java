package net.sourceforge.subsonic.booter;

import org.jdesktop.jdic.desktop.Desktop;
import org.jdesktop.jdic.tray.SystemTray;
import org.jdesktop.jdic.tray.TrayIcon;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;

/**
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
                openBrowser();
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
                SystemTray.getDefaultSystemTray().removeTrayIcon(trayIcon);
                System.exit(0);
            }
        };
    }

    private void createComponents() {
        URL url = Main.class.getResource("/images/subsonic.png");
        Image image = Toolkit.getDefaultToolkit().createImage(url);

        JPopupMenu menu = new JPopupMenu();
        JMenuItem item = menu.add(openAction);
        item.setFont(item.getFont().deriveFont(Font.BOLD));

        menu.addSeparator();
        menu.add(statusAction);
        menu.add(settingsAction);
        menu.addSeparator();
        menu.add(exitAction);

        trayIcon = new TrayIcon(new ImageIcon(image), "Subsonic Media Streamer", menu);
    }

    private void addBehaviour() {
        trayIcon.addBalloonActionListener(openAction);

        // TODO: REMOVE
        trayIcon.addBalloonActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                System.out.println("Balloon action listener");
            }
        });

        trayIcon.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                System.out.println("Action listener");
            }
        });
    }

    private void installComponents() {
        SystemTray.getDefaultSystemTray().addTrayIcon(trayIcon);
        trayIcon.displayMessage("Subsonic", "Subsonic is now running. Click this balloon to get started.",
                                TrayIcon.INFO_MESSAGE_TYPE);
    }

    private void openBrowser() {
        try {
            String url = "http://localhost:" + subsonicController.getPort() + subsonicController.getContextPath();
            Desktop.browse(new URL(url));
        } catch (Exception x) {
            x.printStackTrace();
        }
    }

}
