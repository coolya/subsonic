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
 * <li><code>subsonic.createLinkFile</code> - If set to "true", a Subsonic.url file is created in the working directory.</li>
 * </ul>
 * <p/>
 * If invoked with command line argument "-settings", no deployment is done, but the settings dialog is displayed.
 *
 * @author Sindre Mehus
 */
public class Main {

    public Main(boolean settingsMode) {

        // Set look-and-feel.
        try {
            UIManager.setLookAndFeel(new PlasticXPLookAndFeel());
            System.err.println(UIManager.getLookAndFeel());
        } catch (Throwable x) {
            System.err.println("Failed to set look-and-feel.\n" + x);
        }

        SubsonicController controller = new SubsonicController(settingsMode);
        if (settingsMode) {
            controller.showSettings();
        }
    }

    public static void main(String[] args) throws InterruptedException {
        final boolean settingsMode = args.length > 0 && "-settings".equals(args[0]);

        Runnable runnable = new Runnable() {
            public void run() {
                new Main(settingsMode);
            }
        };

        SwingUtilities.invokeLater(runnable);

        final Object o = new Object();
        synchronized (o) {
            o.wait();
        }
    }
}
