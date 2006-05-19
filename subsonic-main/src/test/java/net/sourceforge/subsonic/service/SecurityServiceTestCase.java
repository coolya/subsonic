package net.sourceforge.subsonic.service;

import junit.framework.*;
/**
 * Unit test of {@link SecurityService}.
 *
 * @author Sindre Mehus
 */
public class SecurityServiceTestCase extends TestCase {

    public void testIsFileInFolder() {
        SecurityService service = new SecurityService();

        assertTrue(service.isFileInFolder("/music/foo.mp3", "\\"));
        assertTrue(service.isFileInFolder("/music/foo.mp3", "/"));

        assertTrue(service.isFileInFolder("/music/foo.mp3", "/music"));
        assertTrue(service.isFileInFolder("\\music\\foo.mp3", "/music"));
        assertTrue(service.isFileInFolder("/music/foo.mp3", "\\music"));
        assertTrue(service.isFileInFolder("/music/foo.mp3", "\\music\\"));

        assertFalse(service.isFileInFolder("", "/tmp"));
        assertFalse(service.isFileInFolder("foo.mp3", "/tmp"));
        assertFalse(service.isFileInFolder("/music/foo.mp3", "/tmp"));
        assertFalse(service.isFileInFolder("/music/foo.mp3", "/tmp/music"));

        // Test that references to the parent directory (..) is not allowed.
        assertTrue(service.isFileInFolder("/music/foo..mp3", "/music"));
        assertTrue(service.isFileInFolder("/music/foo..", "/music"));
        assertTrue(service.isFileInFolder("/music/foo.../", "/music"));
        assertFalse(service.isFileInFolder("/music/foo/..", "/music"));
        assertFalse(service.isFileInFolder("../music/foo", "/music"));
        assertFalse(service.isFileInFolder("/music/../foo", "/music"));
        assertFalse(service.isFileInFolder("/music/../bar/../foo", "/music"));
        assertFalse(service.isFileInFolder("/music\\foo\\..", "/music"));
        assertFalse(service.isFileInFolder("..\\music/foo", "/music"));
        assertFalse(service.isFileInFolder("/music\\../foo", "/music"));
        assertFalse(service.isFileInFolder("/music/..\\bar/../foo", "/music"));
    }
}

