package net.sourceforge.subsonic.booter;

import org.jdesktop.jdic.desktop.Desktop;
import org.jdesktop.jdic.tray.SystemTray;
import org.jdesktop.jdic.tray.TrayIcon;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.nio.SelectChannelConnector;
import org.mortbay.jetty.webapp.WebAppContext;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;

/**
 * @author Sindre Mehus
 */
public class Main {
    private static TrayIcon trayIcon;


    public Main() throws Exception {

//        int port = 80;
        int port = 8080;
        String portString = System.getProperty("subsonic.port");
        if (portString != null) {
            port = Integer.parseInt(portString);
        }

        Server server = new Server();
        SelectChannelConnector connector = new SelectChannelConnector();
        connector.setPort(port);
        server.addConnector(connector);

        WebAppContext context = new WebAppContext();
//        context.setTempDirectory(new File("/tmp/jetty"));
        context.setContextPath("/subsonic");  // TODO: Use "/"
//        context.setWar("file:///projects/subsonic/trunk/subsonic-main/target/subsonic.war");
        context.setWar("subsonic.war");

        server.addHandler(context);
        server.start();
    }

    public static void main(String[] args) throws Exception {
        new Main();
        SystemTray tray = SystemTray.getDefaultSystemTray();

        URL url = Main.class.getResource("/images/subsonic.png");
        Image image = Toolkit.getDefaultToolkit().createImage(url);

        trayIcon = new TrayIcon(new ImageIcon(image), "Subsonic Media Streamer", createTrayPopupMenu());
        tray.addTrayIcon(trayIcon);

        trayIcon.displayMessage("Subsonic", "Subsonic is now running. Click this balloon to get started.",
                                TrayIcon.INFO_MESSAGE_TYPE);
        trayIcon.addBalloonActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    Desktop.browse(new URL("http://localhost:8080/subsonic/"));
                } catch (Exception x) {
                    x.printStackTrace();
                }
            }
        });

        //
//        JFrame frame = new JFrame("Subsonic");
//
//        frame.addWindowListener(new WindowAdapter() {
//            public void windowClosing(WindowEvent e) {
//                System.exit(0);
//            }
//        });
//
//        String dir = System.getProperty("user.dir");
//        frame.getContentPane().add(new JLabel(dir));
//        frame.setBounds(300, 300, 300, 200);
//        frame.setVisible(true);
    }

    private static JPopupMenu createTrayPopupMenu() {
        JPopupMenu popupMenu = new JPopupMenu("Subsonic");
        popupMenu.add("Subsonic");
        popupMenu.addSeparator();
        JMenuItem exitMenuItem = new JMenuItem("Exit");
        JMenuItem settingsMenuItem = new JMenuItem("Settings...");

        exitMenuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                exit();
            }
        });

        popupMenu.add(settingsMenuItem);
        popupMenu.add(exitMenuItem);

        return popupMenu;
    }

    private static void exit() {
        if (trayIcon != null) {
            SystemTray.getDefaultSystemTray().removeTrayIcon(trayIcon);
        }

        System.exit(0);
    }
}
