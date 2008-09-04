package net.sourceforge.subsonic.util;

import junit.framework.TestCase;

import java.util.Locale;
import java.util.Arrays;

import org.apache.commons.lang.math.LongRange;

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