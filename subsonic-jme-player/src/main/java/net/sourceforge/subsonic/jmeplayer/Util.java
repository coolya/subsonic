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

    //TODO: Include exception class name.
    public static void showError(Throwable error, Display display, Displayable currentDisplayable) {
        error.printStackTrace();
        Alert alert = new Alert("Error");
        alert.setString(error.getMessage());
        alert.setType(AlertType.ERROR);
        alert.setTimeout(Alert.FOREVER);
        display.setCurrent(alert, currentDisplayable);
    }
}
