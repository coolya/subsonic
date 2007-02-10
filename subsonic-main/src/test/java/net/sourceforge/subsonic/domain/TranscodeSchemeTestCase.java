package net.sourceforge.subsonic.domain;

import junit.framework.TestCase;
import static net.sourceforge.subsonic.domain.TranscodeScheme.*;

/**
 * Unit test of {@link TranscodeScheme}.
 *
 * @author Sindre Mehus
 */
public class TranscodeSchemeTestCase extends TestCase {

    /**
     * Tests {@link TranscodeScheme#strictest}.
     */
    public void testStrictest() {
        assertSame("Error in strictest().", OFF, OFF.strictest(null));
        assertSame("Error in strictest().", OFF, OFF.strictest(OFF));
        assertSame("Error in strictest().", MAX_32, OFF.strictest(MAX_32));
        assertSame("Error in strictest().", MAX_32, MAX_32.strictest(null));
        assertSame("Error in strictest().", MAX_32, MAX_32.strictest(OFF));
        assertSame("Error in strictest().", MAX_32, MAX_32.strictest(MAX_64));
        assertSame("Error in strictest().", MAX_32, MAX_64.strictest(MAX_32));
    }
}
