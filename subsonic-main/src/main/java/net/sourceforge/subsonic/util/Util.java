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
     * by the presence of a system property "subsonic.ripserver" set to
     * the value "true".
     */
    public static boolean isRipserver() {
        return "true".equals(System.getProperty("subsonic.ripserver"));
    }
}