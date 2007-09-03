package net.sourceforge.subsonic.booter;

import org.mortbay.jetty.Server;
import org.mortbay.jetty.nio.SelectChannelConnector;
import org.mortbay.jetty.webapp.WebAppContext;
import sun.plugin.viewer.context.AxBridgeAppletContext;

import javax.swing.*;

/**
 * @author Sindre Mehus
 */
public class SubsonicController {

    private static final int DEFAULT_PORT = 8080;
    private static final String DEFAULT_CONTEXT_PATH = "/";

    private TrayController trayController;
    private Exception exception;
    private SubsonicFrame frame;

    public SubsonicController() {
//        deployWebApp();
        trayController = new TrayController(this);

    }

    private void deployWebApp() {
        try {
            int port = DEFAULT_PORT;

            String portString = System.getProperty("subsonic.port");
            if (portString != null) {
                port = Integer.parseInt(portString);
            }

            String contextPath = System.getProperty("subsonic.contextPath");
            if (contextPath == null) {
                contextPath = DEFAULT_CONTEXT_PATH;
            }

            Server server = new Server();
            SelectChannelConnector connector = new SelectChannelConnector();
            connector.setPort(port);
            server.addConnector(connector);

            WebAppContext context = new WebAppContext();
            context.setContextPath(contextPath);
            context.setWar("subsonic.war");

            server.addHandler(context);
            server.start();
        } catch (Exception x) {
            exception = x;
        }
    }

    public int getPort() {
        return 80;
    }

    public int getMemoryLimit() {
        return 100;
    }

    public String getContextPath() {
        return "/";
    }

    public String getErrorMessage() {
        return exception == null ? null : exception.getMessage(); // TODO
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
}
