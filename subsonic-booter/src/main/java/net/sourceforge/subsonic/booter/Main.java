package net.sourceforge.subsonic.booter;

import com.jgoodies.looks.plastic.PlasticXPLookAndFeel;

import javax.swing.*;

/**
 * Application entry point for Subsonic booter.
 * <p/>
 * The following system properties may be used to customize the behaviour:
 * <ul>
 * <li><code>subsonic.contextPath</code> - The context path at which Subsonic is deployed.  Default "/".</li>
 * <li><code>subsonic.port</code> - The port Subsonic will listen to.  Default 80.</li>
 * <li><code>subsonic.war</code> - Subsonic WAR file, or exploded directory.  Default "subsonic.war".</li>
 * </ul>
 *
 * @author Sindre Mehus
 */
public class Main {

    public Main() {

        System.setProperty("javax.swing.adjustPopupLocationToFit", "false");

        // Set look-and-feel.
        try {
            UIManager.setLookAndFeel(new PlasticXPLookAndFeel());
            System.err.println(UIManager.getLookAndFeel());
        } catch (Exception x) {
            System.err.println("Failed to set look-and-feel.\n" + x);
        }

        SubsonicController controller = new SubsonicController();
        System.err.println("Subsonic running on " + controller.getURL());
    }

    public static void main(String[] args) {
        Runnable runnable = new Runnable() {
            public void run() {
                new Main();
            }
        };

        SwingUtilities.invokeLater(runnable);
    }
}
