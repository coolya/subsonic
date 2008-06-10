package net.sourceforge.subsonic.jmeplayer;

/**
 * @author Sindre Mehus
 */
public class Log {

    private final String name;
    private final boolean loggingEnabled;

    protected Log(String name, boolean loggingEnabled) {
        this.name = name;
        this.loggingEnabled = loggingEnabled;
    }

    public void debug(String message) {
        log(message, "DEBUG", null);
    }

    public void info(String message) {
        log(message, "INFO", null);
    }

    public void warn(String message) {
        log(message, "WARN", null);
    }

    public void error(String message) {
        log(message, "ERROR", null);
    }

    public void error(String message, Throwable throwable) {
        log(message, "ERROR", throwable);
    }

    private void log(String message, String severity, Throwable throwable) {
        if (loggingEnabled) {
            StringBuffer buf = new StringBuffer(64);
            buf.append(severity).append(" ").append(name).append(" ").append(message);
            System.out.println(buf);
            if (throwable != null) {
                throwable.printStackTrace();
            }
        }
    }

}
