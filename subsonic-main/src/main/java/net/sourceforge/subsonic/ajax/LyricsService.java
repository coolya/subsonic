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

import net.sourceforge.subsonic.Logger;
import net.sourceforge.subsonic.util.StringUtil;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.lang.CharUtils;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;

import java.io.IOException;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * Provides AJAX-enabled services for retrieving song lyrics from lyricsfly.com.
 * <p/>
 * See http://lyricsfly.com/api/ for details.
 * <p/>
 * This class is used by the DWR framework (http://getahead.ltd.uk/dwr/).
 *
 * @author Sindre Mehus
 */
public class LyricsService {

    private static final Logger LOG = Logger.getLogger(LyricsService.class);
    private static final String KEY_ENC = "35353630393138353835362d737562736f6e69632e736f75726365666f7267652e6e6574";

    /**
     * Returns lyrics for the given song and artist.
     *
     * @param artist The artist.
     * @param song   The song.
     * @return The lyrics, never <code>null</code> .
     */
    public LyricsInfo getLyrics(String artist, String song) {
        try {

            artist = encode(artist);
            song = encode(song);

            String url = "http://api.lyricsfly.com/api/api.php?i=" + StringUtil.utf8HexDecode(KEY_ENC) + "&a=" + artist + "&t=" + song;
            String xml = executeGetRequest(url);
            System.out.println(xml);
            return parse(xml);

        } catch (Exception x) {
            LOG.warn("Failed to get lyrics for song '" + song + "'.", x);
            return new LyricsInfo();
        }
    }

    private String encode(String s) throws UnsupportedEncodingException {
        StringBuilder builder = new StringBuilder(s.length());

        // Replace non-alphanumeric characters with "%".
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (CharUtils.isAsciiAlphanumeric(c) || Character.isWhitespace(c)) {
                builder.append(c);
            } else {
                builder.append("%");
            }
        }

        return URLEncoder.encode(s, StringUtil.ENCODING_UTF8);
    }

    protected LyricsInfo parse(String xml) throws Exception {
        SAXBuilder builder = new SAXBuilder();
        Document document = builder.build(new StringReader(xml));

        Element root = document.getRootElement();
        String status = root.getChildText("status");
        if (!"200".equals(status)) {
            throw new Exception("lyricsfly.com returned status " + status);
        }
        Element song = root.getChild("sg");
        String lyrics = song.getChildText("tx").replace("[br]", "<br>");
        String header = song.getChildText("ar") + " - " + song.getChildText("tt");
        return new LyricsInfo(lyrics, header);
    }


    private String executeGetRequest(String url) throws IOException {
        HttpMethod method = new GetMethod(url);
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
