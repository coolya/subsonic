package net.sourceforge.subsonic;

import net.sourceforge.subsonic.util.*;

import java.util.*;

/**
 * Wrapper around log4j logger.
 *
 * @author Sindre Mehus
 * @version $Revision: 1.1 $ $Date: 2005/05/09 19:58:26 $
 */
public class Logger {

    /** The wrapped log4j logger. */
    private org.apache.log4j.Logger logger;
    private String category;

    private static List<Entry> entries = Collections.synchronizedList(new BoundedList<Entry>(50));

    /**
     * Creates a logger for the given class.
     * @param clazz The class.
     * @return A logger for the class.
     */
    public static Logger getLogger(Class clazz) {
        return new Logger(clazz.getName());
    }

    /**
     * Creates a logger for the given namee.
     * @param name The name.
     * @return A logger for the name.
     */
    public static Logger getLogger(String name) {
        return new Logger(name);
    }

    /**
     * Returns the last few log entries.
     * @return The last few log entries.
     */
    public static Entry[] getLatestLogEntries() {
        return entries.toArray(new Entry[0]);
    }

    private Logger(String name) {
        this.logger = org.apache.log4j.Logger.getLogger(name);

        int lastDot = name.lastIndexOf('.');
        if (lastDot == -1) {
            category = name;
        } else {
            category = name.substring(lastDot + 1);
        }
    }

    /**
     * Logs a debug message.
     * @param message The log message.
     */
    public void debug(Object message) {
        debug(message, null);
    }

    /**
     * Logs a debug message.
     * @param message The message.
     * @param error The optional exception.
     */
    public void debug(Object message, Throwable error) {
        logger.debug(message, error);
        add(Level.DEBUG, message, error);
    }

    /**
     * Logs an info message.
     * @param message The message.
     */
    public void info(Object message) {
        info(message, null);
    }

    /**
     * Logs an info message.
     * @param message The message.
     * @param error The optional exception.
     */
    public void info(Object message, Throwable error) {
        logger.info(message, error);
        add(Level.INFO, message, error);
    }

    /**
     * Logs a warning message.
     * @param message The message.
     */
    public void warn(Object message) {
        warn(message, null);
    }

    /**
     * Logs a warning message.
     * @param message The message.
     * @param error The optional exception.
     */
    public void warn(Object message, Throwable error) {
        logger.warn(message, error);
        add(Level.WARN, message, error);
    }

    /**
     * Logs an error message.
     * @param message The message.
     */
    public void error(Object message) {
        error(message, null);
    }

    /**
     * Logs an error message.
     * @param message The message.
     * @param error The optional exception.
     */
    public void error(Object message, Throwable error) {
        logger.error(message, error);
        add(Level.ERROR, message, error);
    }

    /**
     * Returns whether debug logging is enabled for this logger.
     * @return Whether debug logging is enabled.
     */
    public boolean isDebugEnabled() {
        return logger.isDebugEnabled();
    }

    private void add(Level level, Object message, Throwable error) {
        entries.add(new Entry(category, level, message, error));
    }

    /**
     * Log level.
     */
    public enum Level {
        DEBUG, INFO, WARN, ERROR
    }

    /**
     * Log entry.
     */
    public static class Entry {
        private String category;
        private Date date;
        private Level level;
        private Object message;
        private Throwable error;

        public Entry(String category, Level level, Object message, Throwable error) {
            this.date = new Date();
            this.category = category;
            this.level = level;
            this.message = message;
            this.error = error;
        }

        public String getCategory() {
            return category;
        }

        public Date getDate() {
            return date;
        }

        public Level getLevel() {
            return level;
        }

        public Object getMessage() {
            return message;
        }

        public Throwable getError() {
            return error;
        }
    }
}
