package net.sourceforge.subsonic.booter.deployer;

import org.mortbay.jetty.Server;
import org.mortbay.jetty.nio.SelectChannelConnector;
import org.mortbay.jetty.webapp.WebAppContext;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.net.BindException;
import java.util.Date;

/**
 * Responsible for deploying the Subsonic web app in
 * the embedded Jetty container.
 * <p/>
 * The following system properties may be used to customize the behaviour:
 * <ul>
 * <li><code>subsonic.contextPath</code> - The context path at which Subsonic is deployed.  Default "/".</li>
 * <li><code>subsonic.port</code> - The port Subsonic will listen to.  Default 80.</li>
 * <li><code>subsonic.war</code> - Subsonic WAR file, or exploded directory.  Default "subsonic.war".</li>
 * <li><code>subsonic.createLinkFile</code> - If set to "true", a Subsonic.url file is created in the working directory.</li>
 * </ul>
 *
 * @author Sindre Mehus
 */
public class SubsonicDeployer implements SubsonicDeployerService {

    public static final String DEFAULT_HOST = "0.0.0.0";
    public static final int DEFAULT_PORT = 80;
    public static final int DEFAULT_MEMORY_LIMIT = 64;
    public static final String DEFAULT_CONTEXT_PATH = "/";
    public static final String DEFAULT_WAR = "subsonic.war";
    private static final int MAX_IDLE_TIME_MILLIS = 7 * 24 * 60 * 60 * 1000; // One week.

    // Subsonic home directory.
    private static final File SUBSONIC_HOME_WINDOWS = new File("c:/subsonic");
    private static final File SUBSONIC_HOME_OTHER = new File("/var/subsonic");

    private Throwable exception;
    private File subsonicHome;
    private final Date startTime;

    public SubsonicDeployer() {

        // Enable shutdown hook for Ehcache.
        System.setProperty("net.sf.ehcache.enableShutdownHook", "true");

        startTime = new Date();
        createLinkFile();
        deployWebApp();
    }

    private void createLinkFile() {
        if ("true".equals(System.getProperty("subsonic.createLinkFile"))) {
            Writer writer = null;
            try {
                writer = new FileWriter("subsonic.url");
                writer.append("[InternetShortcut]");
                writer.append(System.getProperty("line.separator"));
                writer.append("URL=").append(getURL());
                writer.flush();
            } catch (Throwable x) {
                System.err.println("Failed to create subsonic.url.");
                x.printStackTrace();
            } finally {
                if (writer != null) {
                    try {
                        writer.close();
                    } catch (IOException x) {
                        // Ignored
                    }
                }
            }
        }
    }

    private void deployWebApp() {
        try {
            Server server = new Server();
            SelectChannelConnector connector = new SelectChannelConnector();
            connector.setMaxIdleTime(MAX_IDLE_TIME_MILLIS);
            connector.setHost(getHost());
            connector.setPort(getPort());
            server.addConnector(connector);

            WebAppContext context = new WebAppContext();
            context.setTempDirectory(new File(getSubsonicHome(), "jetty"));
            context.setContextPath(getContextPath());
            context.setWar(getWar());

            server.addHandler(context);
            server.start();

            System.err.println("Subsonic running on " + getURL());
        } catch (Throwable x) {
            x.printStackTrace();
            exception = x;
        }
    }

    private String getContextPath() {
        return System.getProperty("subsonic.contextPath", DEFAULT_CONTEXT_PATH);
    }


    private String getWar() {
        String war = System.getProperty("subsonic.war");
        if (war == null) {
            war = DEFAULT_WAR;
        }

        File file = new File(war);
        if (file.exists()) {
            System.err.println("Using WAR file: " + file.getAbsolutePath());
        } else {
            System.err.println("Error: WAR file not found: " + file.getAbsolutePath());
        }

        return war;
    }

    private String getHost() {
        return System.getProperty("subsonic.host", DEFAULT_HOST);
    }

    private int getPort() {
        int port = DEFAULT_PORT;

        String portString = System.getProperty("subsonic.port");
        if (portString != null) {
            port = Integer.parseInt(portString);
        }
        return port;
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

    public Date getStartTime() {
        return startTime;
    }

    public int getMemoryUsed() {
        long freeBytes = Runtime.getRuntime().freeMemory();
        long totalBytes = Runtime.getRuntime().totalMemory();
        long usedBytes = totalBytes - freeBytes;
        return (int) Math.round(usedBytes / 1024.0 / 1024.0);
    }

    private String getURL() {

        String host = DEFAULT_HOST.equals(getHost()) ? "localhost" : getHost();
        StringBuffer url = new StringBuffer("http://").append(host);
        if (getPort() != 80) {
            url.append(":").append(getPort());
        }
        url.append(getContextPath());
        return url.toString();
    }

    /**
     * Returns the Subsonic home directory.
     *
     * @return The Subsonic home directory, if it exists.
     * @throws RuntimeException If directory doesn't exist.
     */
    private File getSubsonicHome() {

        if (subsonicHome != null) {
            return subsonicHome;
        }

        File home;

        String overrideHome = System.getProperty("subsonic.home");
        if (overrideHome != null) {
            home = new File(overrideHome);
        } else {
            boolean isWindows = System.getProperty("os.name", "Windows").toLowerCase().startsWith("windows");
            home = isWindows ? SUBSONIC_HOME_WINDOWS : SUBSONIC_HOME_OTHER;
        }

        // Attempt to create home directory if it doesn't exist.
        if (!home.exists() || !home.isDirectory()) {
            boolean success = home.mkdirs();
            if (success) {
                subsonicHome = home;
            } else {
                String message = "The directory " + home + " does not exist. Please create it and make it writable. " +
                                 "(You can override the directory location by specifying -Dsubsonic.home=... when " +
                                 "starting the servlet container.)";
                System.err.println("ERROR: " + message);
            }
        } else {
            subsonicHome = home;
        }

        return home;
    }

    public DeploymentStatus getDeploymentInfo() {
        return new DeploymentStatus(startTime, getURL(), getMemoryUsed(), getErrorMessage());
    }
}
