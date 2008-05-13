package net.sourceforge.subsonic.service;

import net.sourceforge.subsonic.Logger;
import net.sourceforge.subsonic.util.StringUtil;
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
import java.util.List;
import java.util.zip.GZIPInputStream;

/**
 * Provides services for searching for resources at Discogs.
 *
 * @author Sindre Mehus
 */
public class DiscogsSearchService {

    private static final Logger LOG = Logger.getLogger(DiscogsSearchService.class);
    private static final String DISCOGS_API_KEY = "53ee0045b6";

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

        String query = URLEncoder.encode(artist + " " + album, StringUtil.ENCODING_UTF8);
        String url = "http://www.discogs.com/search?type=release&q=" + query + "&f=xml&api_key" + DISCOGS_API_KEY;
        String searchResult = executeRequest(url);
//        System.out.println(searchResult);

        List<String> result = new ArrayList<String>();
        List<Integer> releaseIds = parseReleaseIds(searchResult);
        for (Integer releaseId : releaseIds) {
            System.out.println(releaseId);
            result.addAll(getImagesForRelease(releaseId));
            if (result.size() >= 10) {
                break;
            }
        }

        long t1 = System.currentTimeMillis();
        LOG.info("Found " + result.size() + " cover image(s) at Discogs.com in " + (t1 - t0) + " ms.");
        return result.toArray(new String[result.size()]);
    }

    private List<String> getImagesForRelease(int releaseId) throws Exception {
        String url = "http://www.discogs.com/release/" + releaseId + "?f=xml&api_key" + DISCOGS_API_KEY;
        String searchResult = executeRequest(url);
//        System.out.println(searchResult);

        SAXBuilder builder = new SAXBuilder();
        Document document = builder.build(new StringReader(searchResult));

        List<String> imageUrls = new ArrayList<String>();

        Element root = document.getRootElement();
        Element release = root.getChild("release");
        Element images = release.getChild("images");

        if (images != null) {
            List<?> imageList = images.getChildren("image");
            for (Object obj : imageList) {
                Element image = (Element) obj;
                String imageUrl = image.getAttributeValue("uri");
                if (imageUrl != null) {
                    imageUrls.add(imageUrl);
                    System.out.println(imageUrl);
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
            if (uri != null && uri.startsWith("http://www.discogs.com/release/")) {
                String relaseId = uri.replaceFirst("http://www.discogs.com/release/", "");
                releaseIds.add(new Integer(relaseId));
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

            in = new GZIPInputStream(method.getResponseBodyAsStream());
            result = IOUtils.toString(in);

        } finally {
            IOUtils.closeQuietly(in);
            method.releaseConnection();
        }
        return result;
    }

    public static void main(String[] args) throws Exception {
        DiscogsSearchService service = new DiscogsSearchService();
        service.getCoverArtImages("REM", "Monster");
    }
}