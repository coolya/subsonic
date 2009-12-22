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
package net.sourceforge.subsonic.service;

import net.sourceforge.subsonic.Logger;
import net.sourceforge.subsonic.util.StringUtil;
import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.io.IOUtils;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.GZIPInputStream;

/**
 * Provides services for searching for resources at Discogs.
 *
 * @author Sindre Mehus
 */
public class DiscogsSearchService {
    private static final Logger LOG = Logger.getLogger(DiscogsSearchService.class);
    private static final String DISCOGS_API_KEY = "53ee0045b6";
    private static final Pattern RELEASE_URL_PATTERN = Pattern.compile("http://www.discogs.com/(.*)/release/(\\d+)");
    // This pattern will match information appended in parentheses or brackets such as "(disk 1)" and "[disk 1]".
    // These appendages will typically not give any hits when searching Discogs, and can be removed.
    private static final Pattern PATTERN_APPENDED_INFO = Pattern.compile("(.*)[\\[\\(].*[\\]\\)]\\s*");

    /**
     * Returns a list of URLs of cover art images from Discogs for the given artist and album.
     *
     * @param artist The artist to search for.
     * @param album  The album to search for.
     * @return A list of URLs of cover art images from Discogs.com.
     * @throws Exception If anything goes wrong.
     */
    public String[] getCoverArtImages(String artist, String album) throws Exception {
        long t0 = System.currentTimeMillis();

        List<Integer> releaseIds = searchForReleases(artist, album);
        if (releaseIds.size() == 0 && album != null) {
            Matcher appendedInfoMatcher = PATTERN_APPENDED_INFO.matcher(album);
            if (appendedInfoMatcher.matches()) {
                // retry the search with postfixes such as "(disk 1)" removed from the album title
                releaseIds = searchForReleases(artist, appendedInfoMatcher.group(1));
            }
        }

        List<DiscogsImage> discogsImages = new ArrayList<DiscogsImage>();
        for (Integer releaseId : releaseIds) {
            discogsImages.addAll(getImagesForRelease(releaseId));
            if (discogsImages.size() >= 10) {
                break;
            }
        }
        Collections.sort(discogsImages);
        String[] result = new String[discogsImages.size()];
        for (int i = 0; i < result.length; ++i) {
            result[i] = discogsImages.get(i).getUrl();
        }

        long t1 = System.currentTimeMillis();
        LOG.info("Found " + result.length + " cover image(s) at Discogs.com in " + (t1 - t0) + " ms.");
        return result;
    }

    private List<Integer> searchForReleases(String artist, String album) throws Exception {
        String query = URLEncoder.encode(artist + " " + album, StringUtil.ENCODING_UTF8);
        String url = "http://www.discogs.com/search?type=all&q=" + query + "&f=xml&api_key=" + DISCOGS_API_KEY;
        String searchResult = executeRequest(url);
        return parseReleaseIds(searchResult);
    }

    private List<DiscogsImage> getImagesForRelease(int releaseId) throws Exception {
        String url = "http://www.discogs.com/release/" + releaseId + "?f=xml&api_key=" + DISCOGS_API_KEY;
        String searchResult = executeRequest(url);

        SAXBuilder builder = new SAXBuilder();
        Document document = builder.build(new StringReader(searchResult));

        List<DiscogsImage> imageUrls = new ArrayList<DiscogsImage>();

        Element root = document.getRootElement();
        Element release = root.getChild("release");
        Element images = release.getChild("images");

        if (images != null) {
            List<?> imageList = images.getChildren("image");
            for (Object obj : imageList) {
                Element image = (Element) obj;
                String imageUrl = image.getAttributeValue("uri");
                if (imageUrl != null) {
                    imageUrls.add(new DiscogsImage(image));
                }
            }
        }

        return imageUrls;
    }

    private List<Integer> parseReleaseIds(String xml) throws Exception {
        SAXBuilder builder = new SAXBuilder();
        Document document = builder.build(new StringReader(xml));

        Element root = document.getRootElement();
        Element searchResults = root.getChild("searchresults");
        List<?> results = searchResults.getChildren("result");
        List<Integer> releaseIds = new ArrayList<Integer>();

        for (Object obj : results) {
            Element result = (Element) obj;
            String uri = result.getChildText("uri");

            // Grep release ID from url of the following form:
            //  <uri>http://www.discogs.com/U2-No-Line-On-The-Horizon/release/1670031</uri>
            if (uri != null) {
                Matcher matcher = RELEASE_URL_PATTERN.matcher(uri);
                if (matcher.matches()) {
                    String relaseId = matcher.group(2);
                    releaseIds.add(new Integer(relaseId));
                }
            }
        }

        return releaseIds;
    }

    private String executeRequest(String url) throws IOException {
        HttpMethod method = new GetMethod(url);
        HttpClient client = new HttpClient();
//        client.getParams().setContentCharset(StringUtil.ENCODING_UTF8);

        method.addRequestHeader("Accept-Encoding", "gzip");

        String result;
        InputStream in = null;
        try {
            int statusCode = client.executeMethod(method);

            if (statusCode != HttpStatus.SC_OK) {
                throw new IOException("Method failed: " + method.getStatusLine());
            }

            if (isGzipResponse(method)) {
                in = new GZIPInputStream(method.getResponseBodyAsStream());
            } else {
                in = method.getResponseBodyAsStream();
            }
            result = IOUtils.toString(in);

        } finally {
            IOUtils.closeQuietly(in);
            method.releaseConnection();
        }
        return result;
    }

    private boolean isGzipResponse(HttpMethod httpMethod) {
        boolean isGzip = false;
        Header encodingHeader = httpMethod.getResponseHeader("Content-Encoding");
        if (encodingHeader != null && encodingHeader.getValue() != null) {
            isGzip = encodingHeader.getValue().toLowerCase().indexOf("gzip") != -1;
        }
        return isGzip;
    }

    private static class DiscogsImage implements Comparable<DiscogsImage> {
        private String url;
        private int sortOrder;
        private static final String TYPE_PRIMARY = "primary";
        private static final String FORMAT_CD = "CD";
        private static final String FORMAT_VINYL = "Vinyl";

        private DiscogsImage(Element imageElement) {
            url = imageElement.getAttributeValue("uri");
            setSortOrder(imageElement);
        }

        /**
         * Set the sort order based on the given image data. Primary images are preferred over secondary. CD and then
         * Vinyl are preferred over other formats.
         *
         * @param imageElement image data.
         */
        private void setSortOrder(Element imageElement) {
            String type = imageElement.getAttributeValue("type");
            String format = null;

            Element releaseElement = (Element) imageElement.getParent().getParent();
            Element formatsElement = releaseElement.getChild("formats");
            if (formatsElement != null) {
                List<?> children = formatsElement.getChildren();
                if (children.size() > 0) {
                    Element formatElement = (Element) children.get(0);
                    format = formatElement.getAttributeValue("name");
                }
            }

            if (TYPE_PRIMARY.equalsIgnoreCase(type) && FORMAT_CD.equalsIgnoreCase(format)) {
                sortOrder = 1;
            } else if (TYPE_PRIMARY.equalsIgnoreCase(type) && FORMAT_VINYL.equalsIgnoreCase(format)) {
                sortOrder = 2;
            } else if (TYPE_PRIMARY.equalsIgnoreCase(type)) {
                sortOrder = 3;
            } else if (FORMAT_CD.equalsIgnoreCase(format)) {
                sortOrder = 4;
            } else if (FORMAT_VINYL.equalsIgnoreCase(format)) {
                sortOrder = 5;
            } else {
                sortOrder = 6;
            }
        }

        public String getUrl() {
            return url;
        }

        public int compareTo(DiscogsImage otherImage) {
            int result = 0;
            if (sortOrder > otherImage.sortOrder) {
                result = 1;
            } else if (sortOrder < otherImage.sortOrder) {
                result = -1;
            }
            return result;
        }
    }
}