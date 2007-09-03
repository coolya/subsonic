package net.sourceforge.subsonic.booter;

import com.jgoodies.forms.builder.DefaultFormBuilder;
import com.jgoodies.forms.factories.Borders;
import com.jgoodies.forms.layout.FormLayout;

import javax.swing.*;
import java.util.Date;

/**
 * @author Sindre Mehus
 */
public class StatusPanel extends JPanel {
    private JTextField startedTextField;
    private JTextField memoryTextField;
    private JTextField errorTextField;
    private final SubsonicController subsonicController;


    public StatusPanel(SubsonicController subsonicController) {
        this.subsonicController = subsonicController;
        createComponents();
        configureComponents();
        layoutComponents();

    }

    private void createComponents() {
        startedTextField = new JTextField("" + new Date()); // TODO
        memoryTextField = new JTextField("34 MB"); // TODO
        errorTextField = new JTextField(subsonicController.getErrorMessage());
    }

    private void configureComponents() {
        startedTextField.setEditable(false);
        memoryTextField.setEditable(false);
        errorTextField.setEditable(false);
    }

    private void layoutComponents() {

        FormLayout layout = new FormLayout("right:d, 6dlu, max(d;30dlu):grow");
        DefaultFormBuilder builder = new DefaultFormBuilder(layout, this);
        builder.append("Started on", startedTextField);
        builder.append("Memory used", memoryTextField);
        builder.append("Error message", errorTextField);

        setBorder(Borders.DIALOG_BORDER);
    }
}
