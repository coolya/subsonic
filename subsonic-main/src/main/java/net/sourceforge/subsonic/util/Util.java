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
package net.sourceforge.subsonic.util;

import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;

/**
 * Miscellaneous general utility methods.
 *
 * @author Sindre Mehus
 */
public final class Util {

    /**
     * Disallow external instantiation.
     */
    private Util() {
    }


    /**
     * Returns whether this is a Ripserver installation of Subsonic.
     * See http://www.ripfactory.com/ripserver.html
     *
     * @return Whether this is a Ripserver installation, as determined
     *         by the presence of a system property "subsonic.ripserver" set to
     *         the value "true".
     */
    public static boolean isRipserver() {
        return "true".equals(System.getProperty("subsonic.ripserver"));
    }

    /**
     * Similar to {@link ServletResponse#setContentLength(int)}, but this
     * method supports lengths bigger than 2GB.
     * <p/>
     * See http://blogger.ziesemer.com/2008/03/suns-version-of-640k-2gb.html
     *
     * @param response The HTTP response.
     * @param length   The content length.
     */
    public static void setContentLength(HttpServletResponse response, long length) {
        if (length <= Integer.MAX_VALUE) {
            response.setContentLength((int) length);
        } else {
            response.setHeader("Content-Length", String.valueOf(length));
        }
    }
}