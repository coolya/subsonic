package net.sourceforge.subsonic.booter.agent;

import net.sourceforge.subsonic.booter.deployer.DeploymentStatus;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.net.URL;

/**
 * Controls the Subsonic tray icon.
 *
 * @author Sindre Mehus
 */
public class TrayController implements SubsonicListener {

    private final SubsonicAgent subsonicAgent;
    private TrayIcon trayIcon;

    private Action openAction;
    private Action controlPanelAction;
    private Action hideAction;

    public TrayController(SubsonicAgent subsonicAgent) {
        this.subsonicAgent = subsonicAgent;
        try {
            createActions();
            createComponents();
            addBehaviour();
            installComponents();
            subsonicAgent.addListener(this);
        } catch (Throwable x) {
            System.err.println("Disabling tray support.");
        }
    }

    private void createActions() {
        openAction = new AbstractAction("Open Subsonic in Browser") {
            public void actionPerformed(ActionEvent e) {
                subsonicAgent.openBrowser();
            }
        };

        controlPanelAction = new AbstractAction("Subsonic Control Panel") {
            public void actionPerformed(ActionEvent e) {
                subsonicAgent.showControlPanel();
            }
        };


        hideAction = new AbstractAction("Hide Tray Icon") {
            public void actionPerformed(ActionEvent e) {
                subsonicAgent.exit();
            }
        };
    }

    private void createComponents() {
        URL url = getClass().getResource("/images/subsonic-16.png");
        Image image = Toolkit.getDefaultToolkit().createImage(url);

        PopupMenu menu = new PopupMenu();
        menu.add(createMenuItem(openAction));
        menu.add(createMenuItem(controlPanelAction));
        menu.addSeparator();
        menu.add(createMenuItem(hideAction));

        trayIcon = new TrayIcon(image, "Subsonic Music Streamer", menu);
    }

    private MenuItem createMenuItem(Action action) {
        MenuItem menuItem = new MenuItem((String) action.getValue(Action.NAME));
        menuItem.addActionListener(action);
        return menuItem;
    }

    private void addBehaviour() {
        trayIcon.addActionListener(controlPanelAction);
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

    public void notifyDeploymentStatus(DeploymentStatus deploymentStatus) {
        // Nothing here, but could potentially change tray icon and menu.
    }

    public void notifyServiceStatus(String serviceStatus) {
        // Nothing here, but could potentially change tray icon and menu.
    }
}
