package net.sourceforge.subsonic.booter;

import org.jdesktop.jdic.desktop.Desktop;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.nio.SelectChannelConnector;
import org.mortbay.jetty.webapp.WebAppContext;

import java.net.BindException;
import java.net.URL;
import java.util.Date;

/**
 * Responsible for deploying the Subsonic web app in
 * the embedded Jetty container.
 *
 * @author Sindre Mehus
 */
public class SubsonicController {

    public static final int DEFAULT_PORT = 80;
    public static final int DEFAULT_MEMORY_LIMIT = 64;
    public static final String DEFAULT_CONTEXT_PATH = "/";
    private static final int MAX_IDLE_TIME_MILLIS = 2 * 60 * 60 * 1000;

    private TrayController trayController;
    private Throwable exception;
    private SubsonicFrame frame;

    public SubsonicController() {
        deployWebApp();
        trayController = new TrayController(this);
    }

    private void deployWebApp() {
        try {

            Server server = new Server();
            SelectChannelConnector connector = new SelectChannelConnector();
            connector.setMaxIdleTime(MAX_IDLE_TIME_MILLIS);
            connector.setPort(getPort());
            server.addConnector(connector);

            WebAppContext context = new WebAppContext();
            context.setContextPath(getContextPath());
            context.setWar("subsonic.war");

            server.addHandler(context);
            server.start();
        } catch (Throwable x) {
            x.printStackTrace();
            exception = x;
        }
    }

    public String getContextPath() {
        String contextPath = System.getProperty("subsonic.contextPath");
        if (contextPath == null) {
            contextPath = DEFAULT_CONTEXT_PATH;
        }
        return contextPath;
    }

    public int getPort() {
        int port = DEFAULT_PORT;

        String portString = System.getProperty("subsonic.port");
        if (portString != null) {
            port = Integer.parseInt(portString);
        }
        return port;
    }

    public int getMemoryLimit() {
        return (int) Math.round((Runtime.getRuntime().maxMemory() / 1024.0 / 1024.0));
    }

    public String getErrorMessage() {
        if (exception == null) {
            return null;
        }
        if (exception instanceof BindException) {
            return "Address already in use. Please change port number.";
        }

        return exception.toString();
    }

    public void showStatus() {
        getFrame().showStatus();
    }

    public void showSettings() {
        getFrame().showSettings();
    }

    private synchronized SubsonicFrame getFrame() {
        if (frame == null) {
            frame = new SubsonicFrame(this);
        }
        return frame;
    }

    public Date getStartTime() {
        return new Date();
    }

    public int getMemoryUsed() {
        long freeBytes = Runtime.getRuntime().freeMemory();
        long totalBytes = Runtime.getRuntime().totalMemory();
        long usedBytes = totalBytes - freeBytes;
        return (int) Math.round(usedBytes / 1024.0 / 1024.0);
    }

    public void exit() {
        trayController.uninstallComponents();
        System.exit(0);
    }

    public String getURL() {
        StringBuffer url = new StringBuffer("http://localhost");
        if (getPort() != 80) {
            url.append(":").append(getPort());
        }
        url.append(getContextPath());
        return url.toString();
    }

    public void openBrowser() {
        try {
            Desktop.browse(new URL(getURL()));
        } catch (Exception x) {
            x.printStackTrace();
        }
    }
}
