package net.sourceforge.subsonic.util;

/**
 * Unit test of {@link StringUtil}.
 *
 * @author Sindre Mehus
 * @version $Revision: 1.3 $ $Date: 2006/01/10 22:39:35 $
 */

import junit.framework.*;

import java.util.*;

public class StringUtilTestCase extends TestCase {

    public void testToHtml() throws Exception {
        assertEquals(null, StringUtil.toHtml(null));
        assertEquals("", StringUtil.toHtml(""));
        assertEquals(" ", StringUtil.toHtml(" "));
        assertEquals("q &amp; a", StringUtil.toHtml("q & a"));
        assertEquals("q &amp; a &lt;&gt; b", StringUtil.toHtml("q & a <> b"));
    }

    public void testGetSuffix() {
        assertEquals("Error in getSuffix().", ".mp3", StringUtil.getSuffix("foo.mp3"));
        assertEquals("Error in getSuffix().", ".mp3", StringUtil.getSuffix(".mp3"));
        assertEquals("Error in getSuffix().", ".mp3", StringUtil.getSuffix("foo.bar.mp3"));
        assertEquals("Error in getSuffix().", ".mp3", StringUtil.getSuffix("foo..mp3"));
        assertEquals("Error in getSuffix().", "", StringUtil.getSuffix("foo"));
        assertEquals("Error in getSuffix().", "", StringUtil.getSuffix(""));
    }

    public void testRemoveSuffix() {
        assertEquals("Error in removeSuffix().", "foo", StringUtil.removeSuffix("foo.mp3"));
        assertEquals("Error in removeSuffix().", "", StringUtil.removeSuffix(".mp3"));
        assertEquals("Error in removeSuffix().", "foo.bar", StringUtil.removeSuffix("foo.bar.mp3"));
        assertEquals("Error in removeSuffix().", "foo.", StringUtil.removeSuffix("foo..mp3"));
        assertEquals("Error in removeSuffix().", "foo", StringUtil.removeSuffix("foo"));
        assertEquals("Error in removeSuffix().", "", StringUtil.removeSuffix(""));
    }

    public void testGetMimeType() {
        assertEquals("Error in getMimeType().", "audio/mpeg", StringUtil.getMimeType(".mp3"));
        assertEquals("Error in getMimeType().", "audio/mpeg", StringUtil.getMimeType(".MP3"));
        assertEquals("Error in getMimeType().", "application/octet-stream", StringUtil.getMimeType("koko"));
        assertEquals("Error in getMimeType().", "application/octet-stream", StringUtil.getMimeType(""));
        assertEquals("Error in getMimeType().", "application/octet-stream", StringUtil.getMimeType(null));
    }

    public void testFormatBytes() throws Exception {
        Locale locale = Locale.ENGLISH;
        assertEquals("Error in formatBytes().", "918 B", StringUtil.formatBytes(918, locale));
        assertEquals("Error in formatBytes().", "1023 B", StringUtil.formatBytes(1023, locale));
        assertEquals("Error in formatBytes().", "1 KB", StringUtil.formatBytes(1024, locale));
        assertEquals("Error in formatBytes().", "96 KB", StringUtil.formatBytes(98765, locale));
        assertEquals("Error in formatBytes().", "1024 KB", StringUtil.formatBytes(1048575, locale));
        assertEquals("Error in formatBytes().", "1.2 MB", StringUtil.formatBytes(1238476, locale));
        assertEquals("Error in formatBytes().", "3.50 GB", StringUtil.formatBytes(3758096384L, locale));

        locale = new Locale("no", "", "");
        assertEquals("Error in formatBytes().", "918 B", StringUtil.formatBytes(918, locale));
        assertEquals("Error in formatBytes().", "1023 B", StringUtil.formatBytes(1023, locale));
        assertEquals("Error in formatBytes().", "1 KB", StringUtil.formatBytes(1024, locale));
        assertEquals("Error in formatBytes().", "96 KB", StringUtil.formatBytes(98765, locale));
        assertEquals("Error in formatBytes().", "1024 KB", StringUtil.formatBytes(1048575, locale));
        assertEquals("Error in formatBytes().", "1,2 MB", StringUtil.formatBytes(1238476, locale));
        assertEquals("Error in formatBytes().", "3,50 GB", StringUtil.formatBytes(3758096384L, locale));
    }
}