package net.sourceforge.subsonic.domain;

/**
 * Unit test of {@link Version}.
 * @author Sindre Mehus
 */

import junit.framework.*;

public class VersionTestCase extends TestCase {

    /**
     * Tests that equals(), hashCode(), toString() and compareTo() works.
     */
    public void testVersion()  {
        doTestVersion("0.0", "0.1");
        doTestVersion("1.5", "2.3");
        doTestVersion("2.3", "2.34");

        doTestVersion("1.5", "1.5.1");
        doTestVersion("1.5.1", "1.5.2");
        doTestVersion("1.5.2", "1.5.11");

        doTestVersion("1.4", "1.5.beta1");
        doTestVersion("1.4.1", "1.5.beta1");
        doTestVersion("1.5.beta1", "1.5");
        doTestVersion("1.5.beta1", "1.5.1");
        doTestVersion("1.5.beta1", "1.6");
        doTestVersion("1.5.beta1", "1.5.beta2");
        doTestVersion("1.5.beta2", "1.5.beta11");
    }

    /**
     * Tests that equals(), hashCode(), toString() and compareTo() works.
     * @param v1 A lower version.
     * @param v2 A higher version.
     */
    private void doTestVersion(String v1, String v2) {
        Version ver1 = new Version(v1);
        Version ver2 = new Version(v2);

        assertEquals("Error in toString().", v1, ver1.toString());
        assertEquals("Error in toString().", v2, ver2.toString());

        assertEquals("Error in equals().", ver1, ver1);

        assertEquals("Error in compareTo().", 0, ver1.compareTo(ver1));
        assertEquals("Error in compareTo().", 0, ver2.compareTo(ver2));
        assertTrue("Error in compareTo().", ver1.compareTo(ver2) < 0);
        assertTrue("Error in compareTo().", ver2.compareTo(ver1) > 0);
    }
}