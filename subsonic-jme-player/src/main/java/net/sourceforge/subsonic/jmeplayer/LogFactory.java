package net.sourceforge.subsonic.jmeplayer;

/**
 * @author Sindre Mehus
 */
public class LogFactory {
    private static boolean loggingEnabled = true;

    private LogFactory() {
    }

    public static Log create(Class clazz) {
        return create(clazz.getName());
    }

    public static Log create(String name) {
        return new Log(name, loggingEnabled);
    }

    public static void setLoggingEnabled(boolean loggingEnabled) {
        LogFactory.loggingEnabled = loggingEnabled;
    }

    public static boolean isLoggingEnabled() {
        return loggingEnabled;
    }
}
