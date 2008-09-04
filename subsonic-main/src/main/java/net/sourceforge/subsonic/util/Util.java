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