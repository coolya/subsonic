/*
 This file is part of Subsonic.

 Subsonic is free software: you can redistribute it and/or modify
 it under the terms of the GNU General Public License as published by
 the Free Software Foundation, either version 3 of the License, or
 (at your option) any later version.

 Subsonic is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with Subsonic.  If not, see <http://www.gnu.org/licenses/>.

 Copyright 2009 (C) Sindre Mehus
 */
package net.sourceforge.subsonic.jmeplayer;

import javax.microedition.lcdui.Alert;
import javax.microedition.lcdui.AlertType;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import java.io.InputStream;

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

    public static void closeQuietly(InputStream input) {
        if (input == null) {
            return;
        }
        try {
            input.close();
        } catch (Exception x) {
            // Ignored.
        }
    }
}
