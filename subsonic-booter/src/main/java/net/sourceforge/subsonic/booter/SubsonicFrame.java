package net.sourceforge.subsonic.booter;

import com.jgoodies.forms.factories.ButtonBarFactory;
import com.jgoodies.forms.factories.Borders;

import javax.swing.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.*;
import java.net.URL;

/**
 * Frame that is activated by the tray icon. Contains a tabbed pane
 * with status and settings panels.
 *
 * @author Sindre Mehus
 */
public class SubsonicFrame extends JFrame {

    private final SubsonicController subsonicController;

    private JTabbedPane tabbedPane;
    private StatusPanel statusPanel;
    private SettingsPanel settingsPanel;

    private JButton closeButton;

    public SubsonicFrame(SubsonicController subsonicController) {
        super("Subsonic");
        this.subsonicController = subsonicController;
        createComponents();
        layoutComponents();
        addBehaviour();

        URL url = Main.class.getResource("/images/subsonic-32.png");
        setIconImage(Toolkit.getDefaultToolkit().createImage(url));

        pack();
        centerComponent();
    }

    public void centerComponent() {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        setLocation(screenSize.width / 2 - getWidth() / 2,
                    screenSize.height / 2 - getHeight() / 2);
    }

    private void createComponents() {
        statusPanel = new StatusPanel(subsonicController);
        settingsPanel = new SettingsPanel(subsonicController);
        tabbedPane = new JTabbedPane();
        closeButton = new JButton("Close");
    }

    private void layoutComponents() {
        tabbedPane.add("Status", statusPanel);
        tabbedPane.add("Settings", settingsPanel);

        JPanel pane = (JPanel) getContentPane();
        pane.setLayout(new BorderLayout(10, 10));
        pane.add(tabbedPane, BorderLayout.CENTER);
        pane.add(ButtonBarFactory.buildCloseBar(closeButton), BorderLayout.SOUTH);

        pane.setBorder(Borders.TABBED_DIALOG_BORDER);
    }

    private void addBehaviour() {
        closeButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                setVisible(false);
            }
        });
    }

    public void showStatus() {
        settingsPanel.setValues();
        tabbedPane.setSelectedComponent(statusPanel);
        pack();
        setVisible(true);
        toFront();
    }

    public void showSettings() {
        settingsPanel.setValues();
        tabbedPane.setSelectedComponent(settingsPanel);
        pack();
        setVisible(true);
        toFront();
    }
}
