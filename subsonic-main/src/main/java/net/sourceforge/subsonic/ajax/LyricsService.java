package net.sourceforge.subsonic.ajax;

import net.sourceforge.subsonic.Logger;
import net.sourceforge.subsonic.util.StringUtil;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLEncoder;

/**
 * Provides AJAX-enabled services for retrieving song lyrics, by screen-scraping http://www.lyrc.com.ar.
 * <p/>
 * This class is used by the DWR framework (http://getahead.ltd.uk/dwr/).
 *
 * @author Sindre Mehus
 */
public class LyricsService {

    private static final Logger LOG = Logger.getLogger(LyricsService.class);
    private static final String BASE_URL = "http://www.lyrc.com.ar/en/";

    /**
     * Returns lyrics for the given song and artist.
     *
     * @param artist The artist.
     * @param song   The song.
     * @return The lyrics, or <code>null</code> if not found.
     */
    public String getLyrics(String artist, String song) {
        try {
            String url = BASE_URL + "tema1en.php?artist=" +
                         URLEncoder.encode(artist, StringUtil.ENCODING_UTF8) +
                         "&songname=" +
                         URLEncoder.encode(song, StringUtil.ENCODING_UTF8);
            return getLyrics(new URL(url));
        } catch (Exception x) {
            LOG.warn("Failed to get lyrics for song '" + song + "'.", x);
            return null;
        }
    }

    private String getLyrics(URL url) throws IOException {
        InputStream in = null;
        try {
            in = url.openStream();
            String html = IOUtils.toString(in, StringUtil.ENCODING_LATIN);
            if (html.contains("Suggestions : <br>")) { // More than one posibility, take the first one
                String s = html.substring(html.indexOf("Suggestions : <br>"));

                s = s.substring(s.indexOf("tema1en.php"));
                s = s.substring(0, s.indexOf("\""));

                return getLyrics(new URL(BASE_URL + s));

            }

            // Remove html before lyrics
            html = html.substring(html.indexOf("</table>") + 8);

            // Remove html after lyrics
            int pPos = html.indexOf("<p>");
            int brPos = html.indexOf("<br>");

            if (pPos == -1) {
                pPos = Integer.MAX_VALUE;
            }

            if (brPos == -1) {
                brPos = Integer.MAX_VALUE;
            }

            html = html.substring(0, pPos < brPos ? pPos : brPos);

            // Bad parsing....
            if (html.contains("<head>")) {
                return null;
            }

            return html;

        } finally {
            IOUtils.closeQuietly(in);
        }
    }
}
