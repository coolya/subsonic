package net.sourceforge.subsonic.jmeplayer;

import javax.microedition.lcdui.Alert;
import javax.microedition.lcdui.AlertType;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;

/**
 * @author Sindre Mehus
 */
public final class Util {

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

}
