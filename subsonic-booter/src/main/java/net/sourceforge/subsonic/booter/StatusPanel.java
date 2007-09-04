package net.sourceforge.subsonic.booter;

import com.jgoodies.forms.builder.DefaultFormBuilder;
import com.jgoodies.forms.factories.Borders;
import com.jgoodies.forms.layout.FormLayout;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.*;
import java.text.DateFormat;
import java.util.Locale;

/**
 * Panel displaying the status of the Subsonic service.
 *
 * @author Sindre Mehus
 */
public class StatusPanel extends JPanel {

    private static final DateFormat DATE_FORMAT = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.MEDIUM, Locale.US);

    private final SubsonicController subsonicController;

    private JTextField startedTextField;
    private JTextField memoryTextField;
    private JTextArea errorTextField;
    private JButton urlButton;

    public StatusPanel(SubsonicController subsonicController) {
        this.subsonicController = subsonicController;
        createComponents();
        configureComponents();
        layoutComponents();
        addBehaviour();
    }

    private void createComponents() {
        startedTextField = new JTextField();
        memoryTextField = new JTextField();
        errorTextField = new JTextArea(3, 24);
        urlButton = new JButton();
    }

    private void configureComponents() {
        startedTextField.setEditable(false);
        memoryTextField.setEditable(false);
        errorTextField.setEditable(false);

        errorTextField.setLineWrap(true);
        errorTextField.setBorder(startedTextField.getBorder());

        startedTextField.setText(DATE_FORMAT.format(subsonicController.getStartTime()));
        memoryTextField.setText(subsonicController.getMemoryUsed() + " MB");
        errorTextField.setText(subsonicController.getErrorMessage());
        urlButton.setText(subsonicController.getURL());

        urlButton.setBorderPainted(false);
        urlButton.setContentAreaFilled(false);
        urlButton.setForeground(Color.BLUE.darker());
        urlButton.setHorizontalAlignment(SwingConstants.LEFT);
    }

    private void layoutComponents() {
        FormLayout layout = new FormLayout("right:d, 6dlu, max(d;30dlu):grow");
        DefaultFormBuilder builder = new DefaultFormBuilder(layout, this);
        builder.append("Started on", startedTextField);
        builder.append("Memory used", memoryTextField);
        builder.append("Error message", errorTextField);
        builder.append("Server address", urlButton);

        setBorder(Borders.DIALOG_BORDER);
    }

    private void addBehaviour() {
        Timer timer = new Timer(5000, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                memoryTextField.setText(subsonicController.getMemoryUsed() + " MB");
            }
        });
        timer.start();

        urlButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                subsonicController.openBrowser();
            }
        });
    }
}
