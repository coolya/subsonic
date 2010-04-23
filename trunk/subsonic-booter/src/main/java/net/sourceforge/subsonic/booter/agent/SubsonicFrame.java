package net.sourceforge.subsonic.booter.agent;

import com.jgoodies.forms.factories.Borders;
import com.jgoodies.forms.factories.ButtonBarFactory;
import net.sourceforge.subsonic.booter.Main;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;

/**
 * Frame that is activated by the tray icon. Contains a tabbed pane
 * with status and settings panels.
 *
 * @author Sindre Mehus
 */
public class SubsonicFrame extends JFrame {

    private final SubsonicAgent subsonicAgent;

    private JTabbedPane tabbedPane;
    private StatusPanel statusPanel;
    private SettingsPanel settingsPanel;
    private JButton closeButton;

    public SubsonicFrame(SubsonicAgent subsonicAgent) {
        super("Subsonic Control Panel");
        this.subsonicAgent = subsonicAgent;
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
        statusPanel = new StatusPanel(subsonicAgent);
        settingsPanel = new SettingsPanel(subsonicAgent);

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

    @Override
    public void setVisible(boolean b) {
        super.setVisible(b);
        subsonicAgent.setServiceStatusPollingEnabled(b);
    }

    public void showControlPanel() {
        settingsPanel.setValues();
        tabbedPane.setSelectedComponent(statusPanel);
        pack();
        setVisible(true);
        toFront();
    }
}
