package net.sourceforge.subsonic.jmeplayer;

import javax.microedition.lcdui.Alert;
import javax.microedition.lcdui.AlertType;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;

/**
 * @author Sindre Mehus
 */
public final class Util {

    private static final long ONE_KILOBYTE = 1024L;

    private Util() {
    }

    public static void showError(Throwable error, Display display, Displayable currentDisplayable) {
        error.printStackTrace();
        Alert alert = new Alert("Error");
        alert.setString(getErrorMessage(error));
        alert.setType(AlertType.ERROR);
        alert.setTimeout(Alert.FOREVER);
        display.setCurrent(alert, currentDisplayable);
    }

    private static String getErrorMessage(Throwable error) {
        String className = error.getClass().getName();
        className = className.substring(className.lastIndexOf('.') + 1);
        return className + " - " + error.getMessage();
    }

    public static String trimToNull(String s) {
        if (s == null) {
            return null;
        }

        s = s.trim();
        return s.length() == 0 ? null : s;
    }

    /**
     * Converts a byte-count to a formatted string suitable for display to the user.
     * For instance:
     * <ul>
     * <li><code>formatBytes(918)</code> returns <em>"918 B"</em>.</li>
     * <li><code>formatBytes(98765)</code> returns <em>"96 KB"</em>.</li>
     * <li><code>formatBytes(1238476)</code> returns <em>"1209 KB"</em>.</li>
     * </ul>
     * This method assumes that 1 KB is 1024 bytes.
     *
     * @param byteCount The number of bytes.
     * @return The formatted string.
     */
    public static String formatBytes(long byteCount) {
        if (byteCount < ONE_KILOBYTE) {
            return byteCount + " B";
        }
        return byteCount / 1024L + " KB";
    }
}
