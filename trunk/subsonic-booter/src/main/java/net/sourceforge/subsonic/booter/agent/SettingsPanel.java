package net.sourceforge.subsonic.booter.agent;

import com.jgoodies.forms.builder.DefaultFormBuilder;
import com.jgoodies.forms.factories.Borders;
import com.jgoodies.forms.factories.ButtonBarFactory;
import com.jgoodies.forms.layout.FormLayout;
import net.sourceforge.subsonic.booter.deployer.DeploymentStatus;
import net.sourceforge.subsonic.booter.deployer.SubsonicDeployer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Panel displaying the settings of the Subsonic service.
 *
 * @author Sindre Mehus
 */
public class SettingsPanel extends JPanel implements SubsonicListener {

    private JTextField portTextField;
    private JComboBox contextPathComboBox;
    private JTextField memoryTextField;
    private JButton defaultButton;
    private JButton saveButton;

    public SettingsPanel(SubsonicAgent subsonicAgent) {
        createComponents();
        configureComponents();
        layoutComponents();
        addBehaviour();
        setValues();
        subsonicAgent.addListener(this);
    }

    public void setValues() {
        portTextField.setText(String.valueOf(getPortFromOptionsFile()));
        memoryTextField.setText(String.valueOf(getMemoryLimitFromOptionsFile()));
        contextPathComboBox.setSelectedItem(getContextPathFromOptionsFile());
    }

    private int getPortFromOptionsFile() {
        try {
            String s = grep("-Dsubsonic.port=(\\d+)");
            return Integer.parseInt(s);
        } catch (Exception x) {
            x.printStackTrace();
            return SubsonicDeployer.DEFAULT_PORT;
        }
    }

    private int getMemoryLimitFromOptionsFile() {
        try {
            String s = grep("-Xmx(\\d+)m");
            return Integer.parseInt(s);
        } catch (Exception x) {
            x.printStackTrace();
            return SubsonicDeployer.DEFAULT_MEMORY_LIMIT;
        }
    }

    private String getContextPathFromOptionsFile() {
        try {
            String s = grep("-Dsubsonic.contextPath=(.*)");
            if (s == null) {
                throw new NullPointerException();
            }
            return s;
        } catch (Exception x) {
            x.printStackTrace();
            return SubsonicDeployer.DEFAULT_CONTEXT_PATH;
        }
    }

    private void createComponents() {
        portTextField = new JTextField();
        contextPathComboBox = new JComboBox();
        memoryTextField = new JTextField();
        defaultButton = new JButton("Restore defaults");
        saveButton = new JButton("Save settings");
    }

    private void configureComponents() {
        contextPathComboBox.setEditable(true);
        contextPathComboBox.addItem("/");
        contextPathComboBox.addItem("/subsonic");
        contextPathComboBox.addItem("/music");
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
        add(ButtonBarFactory.buildCenteredBar(defaultButton, saveButton), BorderLayout.SOUTH);
    }

    private void addBehaviour() {
        saveButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    saveSettings(getMemoryLimit(), getPort(), getContextPath());

                    JOptionPane.showMessageDialog(SettingsPanel.this,
                                                  "Please restart Subsonic for the new settings to take effect.",
                                                  "Settings changed", JOptionPane.INFORMATION_MESSAGE);

                } catch (Exception x) {
                    JOptionPane.showMessageDialog(SettingsPanel.this, x.getMessage(), "Error", JOptionPane.WARNING_MESSAGE);
                }
            }
        });

        defaultButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                portTextField.setText(String.valueOf(SubsonicDeployer.DEFAULT_PORT));
                memoryTextField.setText(String.valueOf(SubsonicDeployer.DEFAULT_MEMORY_LIMIT));
                contextPathComboBox.setSelectedItem(SubsonicDeployer.DEFAULT_CONTEXT_PATH);
            }
        });
    }

    private String getContextPath() throws SettingsException {
        String contextPath = (String) contextPathComboBox.getSelectedItem();
        if (contextPath.contains(" ") || !contextPath.startsWith("/")) {
            throw new SettingsException("Please specify a valid context path.");
        }
        return contextPath;
    }

    private int getMemoryLimit() throws SettingsException {
        int memoryLimit;
        try {
            memoryLimit = Integer.parseInt(memoryTextField.getText().trim());
            if (memoryLimit < 5) {
                throw new Exception();
            }
        } catch (Exception x) {
            throw new SettingsException("Please specify a valid memory limit.", x);
        }
        return memoryLimit;
    }

    private int getPort() throws SettingsException {
        int port;
        try {
            port = Integer.parseInt(portTextField.getText().trim());
            if (port < 1 || port > 65535) {
                throw new Exception();
            }
        } catch (Exception x) {
            throw new SettingsException("Please specify a valid port number.", x);
        }
        return port;
    }

    private void saveSettings(int memoryLimit, int port, String contextPath) throws SettingsException {
        File file = getOptionsFile();

        java.util.List<String> lines = readLines(file);
        java.util.List<String> newLines = new ArrayList<String>();

        boolean memoryLimitAdded = false;
        boolean portAdded = false;
        boolean contextPathAdded = false;

        for (String line : lines) {
            if (line.startsWith("-Xmx")) {
                newLines.add("-Xmx" + memoryLimit + "m");
                memoryLimitAdded = true;
            } else if (line.startsWith("-Dsubsonic.port=")) {
                newLines.add("-Dsubsonic.port=" + port);
                portAdded = true;
            } else if (line.startsWith("-Dsubsonic.contextPath=")) {
                newLines.add("-Dsubsonic.contextPath=" + contextPath);
                contextPathAdded = true;
            } else {
                newLines.add(line);
            }
        }

        if (!memoryLimitAdded) {
            newLines.add("-Xmx" + memoryLimit + "m");
        }
        if (!portAdded) {
            newLines.add("-Dsubsonic.port=" + port);
        }
        if (!contextPathAdded) {
            newLines.add("-Dsubsonic.contextPath=" + contextPath);
        }

        writeLines(file, newLines);
    }

    private File getOptionsFile() throws SettingsException {
        File file = new File("subsonic-service.exe.vmoptions");
        if (!file.isFile() || !file.exists()) {
            throw new SettingsException("File " + file.getAbsolutePath() + " not found.");
        }
        return file;
    }

    private List<String> readLines(File file) throws SettingsException {
        List<String> lines = new ArrayList<String>();
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(file));
            for (String line = reader.readLine(); line != null; line = reader.readLine()) {
                lines.add(line);
            }
            return lines;
        } catch (IOException x) {
            throw new SettingsException("Failed to read from file " + file.getAbsolutePath(), x);
        } finally {
            closeQuietly(reader);
        }
    }

    private void writeLines(File file, List<String> lines) throws SettingsException {
        PrintWriter writer = null;
        try {
            writer = new PrintWriter(new FileWriter(file));
            for (String line : lines) {
                writer.println(line);
            }
        } catch (IOException x) {
            throw new SettingsException("Failed to write to file " + file.getAbsolutePath(), x);
        } finally {
            closeQuietly(writer);
        }
    }

    private String grep(String regexp) throws SettingsException {
        Pattern pattern = Pattern.compile(regexp);
        File file = getOptionsFile();
        for (String line : readLines(file)) {
            Matcher matcher = pattern.matcher(line);
            if (matcher.matches()) {
                return matcher.group(1);
            }
        }
        return null;
    }

    private void closeQuietly(Reader reader) {
        if (reader == null) {
            return;
        }

        try {
            reader.close();
        } catch (IOException x) {
            // Intentionally ignored.
        }
    }

    private void closeQuietly(Writer writer) {
        if (writer == null) {
            return;
        }

        try {
            writer.close();
        } catch (IOException x) {
            // Intentionally ignored.
        }
    }

    public void notifyDeploymentStatus(DeploymentStatus deploymentStatus) {
        // Nothing here yet.
    }

    public void notifyServiceStatus(String serviceStatus) {
        // Nothing here yet.
    }

    private static class SettingsException extends Exception {

        public SettingsException(String message, Throwable cause) {
            super(message, cause);
        }

        public SettingsException(String message) {
            this(message, null);
        }

        @Override
        public String getMessage() {
            if (getCause() == null || getCause().getMessage() == null) {
                return super.getMessage();
            }
            return super.getMessage() + ". " + getCause().getMessage();
        }
    }
}
