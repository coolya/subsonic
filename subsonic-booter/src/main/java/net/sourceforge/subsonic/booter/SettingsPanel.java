package net.sourceforge.subsonic.booter;

import com.jgoodies.forms.builder.DefaultFormBuilder;
import com.jgoodies.forms.factories.Borders;
import com.jgoodies.forms.factories.ButtonBarFactory;
import com.jgoodies.forms.layout.FormLayout;

import javax.swing.*;
import java.awt.*;

/**
 * @author Sindre Mehus
 */
public class SettingsPanel extends JPanel {

    private final SubsonicController subsonicController;

    private JTextField portTextField;
    private JComboBox contextPathComboBox;
    private JTextField memoryTextField;
    private JButton saveButton;

    public SettingsPanel(SubsonicController subsonicController) {
        this.subsonicController = subsonicController;
        createComponents();
        configureComponents();
        layoutComponents();

    }

    private void createComponents() {
        portTextField = new JTextField(String.valueOf(subsonicController.getPort()));
        contextPathComboBox = new JComboBox();
        memoryTextField = new JTextField(String.valueOf(subsonicController.getMemoryLimit()));
        saveButton = new JButton("Save settings");
    }

    private void configureComponents() {
        contextPathComboBox.setEditable(true);
        contextPathComboBox.addItem("/");
        contextPathComboBox.addItem("/subsonic");
        contextPathComboBox.setSelectedItem(subsonicController.getContextPath());
    }

    private void layoutComponents() {

        FormLayout layout = new FormLayout("right:d, 6dlu, max(d;30dlu):grow");
        DefaultFormBuilder builder = new DefaultFormBuilder(layout);
        builder.append("Port number", portTextField);
        builder.append("Memory limit (MB)", memoryTextField);
        builder.append("Context path", contextPathComboBox);

        setBorder(Borders.DIALOG_BORDER);

        setLayout(new BorderLayout(12, 12));
        add(builder.getPanel(), BorderLayout.CENTER);
        add(ButtonBarFactory.buildCenteredBar(saveButton), BorderLayout.SOUTH);
    }
}
