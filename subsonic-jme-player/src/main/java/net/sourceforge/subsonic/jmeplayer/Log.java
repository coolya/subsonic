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

import java.util.Date;

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
            buf.append(new Date()).append(" ");
            buf.append(severity).append(" ").append(name).append(" - ").append(message);
            System.out.println(buf);
            if (throwable != null) {
                throwable.printStackTrace();
            }
        }
    }

}
