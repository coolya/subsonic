package net.sourceforge.subsonic.booter.mac;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;

import javax.swing.*;

import com.jgoodies.forms.factories.Borders;
import com.jgoodies.forms.factories.ButtonBarFactory;

import net.sourceforge.subsonic.booter.Main;
import net.sourceforge.subsonic.booter.deployer.SubsonicDeployerService;

/**
 * Frame with Subsonic status.  Used on Mac installs.
 *
 * @author Sindre Mehus
 */
public class SubsonicFrame extends JFrame {

    private final SubsonicDeployerService deployer;
    private StatusPanel statusPanel;
    private JButton hideButton;
    private JButton exitButton;
    private TrayIcon trayIcon;
    private Action openAction;
    private Action controlPanelAction;
    private Action quitAction;

    public SubsonicFrame(SubsonicDeployerService deployer) {
        super("Subsonic");
        this.deployer = deployer;
        createActions();
        createComponents();
        layoutComponents();
        addBehaviour();

        URL url = Main.class.getResource("/images/subsonic-512.png");
        setIconImage(Toolkit.getDefaultToolkit().createImage(url));

        pack();
        centerComponent();
        setVisible(true);
    }

    public void centerComponent() {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        setLocation(screenSize.width / 2 - getWidth() / 2,
                screenSize.height / 2 - getHeight() / 2);
    }

    private void createActions() {
        openAction = new AbstractAction("Open Subsonic in Browser") {
            public void actionPerformed(ActionEvent e) {
                statusPanel.openBrowser();
            }
        };

        controlPanelAction = new AbstractAction("Subsonic Control Panel") {
            public void actionPerformed(ActionEvent e) {
                setVisible(true);
                // TODO: Bring to front
            }
        };

        quitAction = new AbstractAction("Quit Subsonic") {
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        };
    }

    private void createComponents() {
        statusPanel = new StatusPanel(deployer);
        hideButton = new JButton("Hide");
        exitButton = new JButton("Exit");

        PopupMenu menu = new PopupMenu();
        menu.add(createMenuItem(openAction));
        menu.add(createMenuItem(controlPanelAction));
        menu.addSeparator();
        menu.add(createMenuItem(quitAction));

        // TODO: Use different icon.
        URL url = getClass().getResource("/images/subsonic-512.png");
        Image image = Toolkit.getDefaultToolkit().createImage(url);
        trayIcon = new TrayIcon(image, "Subsonic Music Streamer", menu);
        trayIcon.setImageAutoSize(true);
    }

    private void layoutComponents() {
        JPanel pane = (JPanel) getContentPane();
        pane.setLayout(new BorderLayout(10, 10));
        pane.add(statusPanel, BorderLayout.CENTER);
        pane.add(ButtonBarFactory.buildRightAlignedBar(hideButton, exitButton), BorderLayout.SOUTH);

        pane.setBorder(Borders.DIALOG_BORDER);

        try {
            SystemTray.getSystemTray().add(trayIcon);
        } catch (Throwable x) {
            System.err.println("Failed to add tray icon.");
        }
    }

    private void addBehaviour() {
        hideButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                setExtendedState(Frame.ICONIFIED);
            }
        });
        exitButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });
    }

    private MenuItem createMenuItem(Action action) {
        MenuItem menuItem = new MenuItem((String) action.getValue(Action.NAME));
        menuItem.addActionListener(action);
        return menuItem;
    }
}