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
package net.sourceforge.subsonic.ajax;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.lang.StringUtils;

import net.sourceforge.subsonic.Logger;
import net.sourceforge.subsonic.util.StringUtil;

/**
 * Provides AJAX-enabled services for retrieving song lyrics, by screen-scraping http://www.lyrc.com.ar.
 * <p/>
 * This class is used by the DWR framework (http://getahead.ltd.uk/dwr/).
 *
 * @author Sindre Mehus
 */
public class LyricsService {

    private static final Logger LOG = Logger.getLogger(LyricsService.class);

    /**
     * Returns lyrics for the given song and artist.
     *
     * @param artist The artist.
     * @param song   The song.
     * @return The lyrics, never <code>null</code> .
     */
    public LyricsInfo getLyrics(String artist, String song) {
        try {

            Map<String, String> params = new HashMap<String, String>();
            params.put("search", artist + " " + song);
            params.put("category", "artisttitle");

            String searchResultHtml = executePostRequest("http://www.metrolyrics.com/search.php", params);
            String lyricsUrl = getLyricsUrl(searchResultHtml);
            if (lyricsUrl == null) {
                return new LyricsInfo();
            }
            String lyricsHtml = executeGetRequest(lyricsUrl);
            return new LyricsInfo(getLyrics(lyricsHtml), getHeader(lyricsHtml));

        } catch (Exception x) {
            LOG.warn("Failed to get lyrics for song '" + song + "'.", x);
            return new LyricsInfo();
        }
    }

    /**
     * Extracts the lyrics URL from the given HTML text.
     *
     * @param html The HTML containing search results from http://www.metrolyrics.com/search.php
     * @return The first lyrics URL in the HTML, or <code>null</code> if not found.
     */
    protected String getLyricsUrl(String html) {

        // Grep for the following pattern:
        // <td class="First"><a href="http://www.metrolyrics.com/a-song-for-departure-lyrics-manic-street-preachers.html">A Song For Departure</a></td>

        Pattern pattern = Pattern.compile("<td class=\"First\"><a href=\"(.*?)\"");
        Matcher matcher = pattern.matcher(html);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return null;
    }

    /**
     * Extracts the lyrics from the given HTML text.
     *
     * @param html The HTML containing the lyrics, e.g., http://www.metrolyrics.com/a-song-for-departure-lyrics-manic-street-preachers.html
     * @return The extracted lyrics.
     */
    protected String getLyrics(String html) {

        String startPattern = "<span id=\"lyrics\">";
        String stopPattern = "</span>";

        int startIndex = html.indexOf(startPattern);
        if (startIndex == -1) {
            return null;
        }

        int stopIndex = StringUtils.indexOf(html, stopPattern, startIndex);
        return StringUtils.trimToNull(html.substring(startIndex + startPattern.length(), stopIndex));
    }

    /**
     * Extracts the header (containing the matching artist/song) from the given HTML text.
     *
     * @param html The HTML containing the lyrics, e.g., http://www.metrolyrics.com/a-song-for-departure-lyrics-manic-street-preachers.html
     * @return The extracted header text.
     */
    protected String getHeader(String html) {

        // Grep for the following pattern:
        // 	<h3>A Song For Departure Lyrics</h3>

        Pattern pattern = Pattern.compile("<h3>(.*) Lyrics</h3>");
        Matcher matcher = pattern.matcher(html);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return null;
    }

    private String executeGetRequest(String url) throws IOException {
        return executeRequest(new GetMethod(url));
    }

    private String executePostRequest(String url, Map<String, String> parameters) throws IOException {
        PostMethod method = new PostMethod(url);

        for (Map.Entry<String, String> entry : parameters.entrySet()) {
            method.addParameter(entry.getKey(), entry.getValue());
        }
        return executeRequest(method);
    }

    private String executeRequest(HttpMethod method) throws IOException {
        HttpClient client = new HttpClient();
        client.getParams().setContentCharset(StringUtil.ENCODING_UTF8);

        try {
            int statusCode = client.executeMethod(method);

            if (statusCode != HttpStatus.SC_OK) {
                throw new IOException("Method failed: " + method.getStatusLine());
            }

            return method.getResponseBodyAsString();

        } finally {
            method.releaseConnection();
        }

    }
}
