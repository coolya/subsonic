package net.sourceforge.subsonic.util;

import java.io.*;
import java.net.*;
import java.text.*;
import java.util.*;

/**
 * Miscellaneous string utility methods.
 *
 * @author Sindre Mehus
 * @version $Revision: 1.4 $ $Date: 2006/03/01 17:23:15 $
 */
public final class StringUtil {

    private static final String ENCODING_LATIN = "ISO-8859-1";
    private static final String ENCODING_UTF   = "UTF-8";

    private static final String[][] HTML_SUBSTITUTIONS = {
            {"&",  "&amp;"},
            {"<",  "&lt;"},
            {">",  "&gt;"},
            {"'",  "&#39;"},
            {"\"", "&#34;"},
    };

    private static final String[][] MIME_TYPES = {
            {".mp3",  "audio/mpeg"},
            {".mpg",  "video/mpeg"},
            {".mpeg", "video/mpeg"},
            {".mp4",  "audio/mp4"},
            {".m4a",  "audio/mp4"},
            {".mpg4", "audio/mp4"},
            {".ogg",  "application/ogg"},
    };

    /**
     * Disallow external instantiation.
     */
    private StringUtil() {}

    /**
     * URL-encodes the given string.
     * @param s The input string.
     * @return The converted string.
     * @throws UnsupportedEncodingException If the encoding used is not supported.
     */
    public static String urlEncode(String s) throws UnsupportedEncodingException {
        return URLEncoder.encode(s, ENCODING_LATIN);
    }

    /**
     * Returns the specified string converted to a format suitable for
     * HTML. All single-quote, double-quote, greater-than, less-than and
     * ampersand characters are replaces with their corresponding HTML
     * Character Entity code.
     *
     * @param s the string to convert
     * @return the converted string
     */
    public static String toHtml(String s) {
        if (s == null) {
            return null;
        }
        for (String[] substitution : HTML_SUBSTITUTIONS) {
            if (s.contains(substitution[0])) {
                s = s.replaceAll(substitution[0], substitution[1]);
            }
        }
        return s;
    }

    /**
     * Converts a string from UTF-8 to ISO-8859-1 encoding.
     * @param s The string to convert.
     * @return The converted string.
     */
    public static String utfToLatin(String s) {
        try {
            return new String(s.getBytes(ENCODING_UTF), ENCODING_LATIN);
        } catch (UnsupportedEncodingException x) {
            return s;
        }
    }

    /**
    * Returns the suffix (the substring after the last dot) of the given string. The dot
    * is included in the returned suffix.
    * @param s The string in question.
    * @return The suffix, or an empty string if no suffix is found.
    */
    public static String getSuffix(String s) {
        int index = s.lastIndexOf('.');
        return index == -1 ? "" : s.substring(index);
    }

    /**
     * Removes the suffix (the substring after the last dot) of the given string. The dot is
     * also removed.
     * @param s The string in question, e.g., "foo.mp3".
     * @return The string without the suffix, e.g., "foo".
     */
    public static String removeSuffix(String s) {
        int index = s.lastIndexOf('.');
        return index == -1 ? s : s.substring(0, index);
    }

    /**
     * Returns the proper MIME type for the given suffix.
     * @param suffix The suffix, e.g., ".mp3".
     * @return The corresponding MIME type, e.g., "audio/mpeg". If no MIME type is found,
     *  <code>application/octet-stream</code> is returned.
     */
    public static String getMimeType(String suffix) {
        for (String[] map : MIME_TYPES) {
            if (map[0].equalsIgnoreCase(suffix)) {
                return map[1];
            }
        }
        return "application/octet-stream";
    }

    /**
    * Converts a byte-count to a formatted string suitable for display to the user.
    * For instance:
    * <ul>
    * <li><code>format(918)</code> returns <em>"918 B"</em>.</li>
    * <li><code>format(98765)</code> returns <em>"96 KB"</em>.</li>
    * <li><code>format(1238476)</code> returns <em>"1.2 MB"</em>.</li>
    * </ul>
    * This method assumes that 1 KB is 1024 bytes.
    *
    * @param byteCount The number of bytes.
    * @param locale The locale used for formatting.
    * @return The formatted string.
    */
    public static synchronized String formatBytes(long byteCount, Locale locale) {

        // More than 1 GB?
        if (byteCount >= 1024 * 1024 * 1024) {
            NumberFormat gigaByteFormat = new DecimalFormat("0.00 GB", new DecimalFormatSymbols(locale));
            return gigaByteFormat.format((double) byteCount / (1024 * 1024 * 1024));
        }

        // More than 1 MB?
        if (byteCount >= 1024 * 1024) {
            NumberFormat megaByteFormat = new DecimalFormat("0.0 MB", new DecimalFormatSymbols(locale));
            return megaByteFormat.format((double) byteCount / (1024 * 1024));
        }

        // More than 1 KB?
        if (byteCount >= 1024) {
            NumberFormat kiloByteFormat = new DecimalFormat("0 KB", new DecimalFormatSymbols(locale));
            return kiloByteFormat.format((double) byteCount / 1024);
        }

        return byteCount + " B";
    }

}
