package net.sourceforge.subsonic.util;

import junit.framework.TestCase;

/**
 * Unit test of {@link Util}.
 *
 * @author Sindre Mehus
 */
public class UtilTestCase extends TestCase {

    public void testIsRipserver() {
        System.clearProperty("subsonic.ripserver");
        assertFalse("Error in isRipserver().", Util.isRipserver());

        System.setProperty("subsonic.ripserver", "true");
        assertTrue("Error in isRipserver().", Util.isRipserver());

        System.setProperty("subsonic.ripserver", "false");
        assertFalse("Error in isRipserver().", Util.isRipserver());

        System.setProperty("subsonic.ripserver", "");
        assertFalse("Error in isRipserver().", Util.isRipserver());

        System.setProperty("subsonic.ripserver", "foo");
        assertFalse("Error in isRipserver().", Util.isRipserver());

        System.clearProperty("subsonic.ripserver");
        assertFalse("Error in isRipserver().", Util.isRipserver());
    }
}