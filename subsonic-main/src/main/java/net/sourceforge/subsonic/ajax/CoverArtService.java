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

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.io.IOUtils;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.HttpResponse;
import org.directwebremoting.WebContextFactory;

import net.sourceforge.subsonic.Logger;
import net.sourceforge.subsonic.domain.MusicFile;
import net.sourceforge.subsonic.service.DiscogsSearchService;
import net.sourceforge.subsonic.service.MusicFileService;
import net.sourceforge.subsonic.service.SecurityService;
import net.sourceforge.subsonic.util.StringUtil;

/**
 * Provides AJAX-enabled services for retrieving cover art images.
 * <p/>
 * This class is used by the DWR framework (http://getahead.ltd.uk/dwr/).
 *
 * @author Sindre Mehus
 */
public class CoverArtService {

    private static final Logger LOG = Logger.getLogger(CoverArtService.class);

    private DiscogsSearchService discogsSearchService;
    private SecurityService securityService;
    private MusicFileService musicFileService;

    /**
     * Returns a list of URLs of cover art images for the given artist and album.
     *
     * @param service Where to search for images. Supported values are: "discogs".
     * @param artist  The artist to search for.
     * @param album   The album to search for.
     * @return A possibly empty array of URLs of cover art images.
     */
    public CoverArtInfo[] getCoverArtImages(String service, String artist, String album) {
        if ("discogs".equals(service)) {
            return getDiscogsCoverArtImages(artist, album);
        }

        LOG.warn("Unsupported cover art service: " + service);
        return new CoverArtInfo[0];
    }

    private CoverArtInfo[] getDiscogsCoverArtImages(String artist, String album) {
        try {
            String[] urls = discogsSearchService.getCoverArtImages(artist, album);
            CoverArtInfo[] result = new CoverArtInfo[urls.length];
            for (int i = 0; i < urls.length; i++) {
                // Must fetch Discogs images thru proxy, since Discogs doesn't allow the
                // HTTP "referer" request header.
                result[i] = new CoverArtInfo(toProxyURL(urls[i]), urls[i]);
            }
            return result;
        } catch (Exception x) {
            LOG.warn("Failed to search for images at Discogs.", x);
            return new CoverArtInfo[0];
        }
    }

    private String toProxyURL(String url) throws UnsupportedEncodingException {
        HttpServletRequest request = WebContextFactory.get().getHttpServletRequest();
        String requestUrl = request.getRequestURL().toString();
        String proxyUrl = requestUrl.replaceFirst("/dwr/.*", "/proxy.view?url=");
        return proxyUrl + URLEncoder.encode(url, StringUtil.ENCODING_UTF8);
    }

    /**
     * Downloads and saves the cover art at the given URL.
     *
     * @param path The directory in which to save the image.
     * @param url  The image URL.
     * @return The error string if something goes wrong, <code>null</code> otherwise.
     */
    public String setCoverArtImage(String path, String url) {
        try {
            saveCoverArt(path, url);
            return null;
        } catch (Exception x) {
            LOG.warn("Failed to save cover art for " + path, x);
            return x.toString();
        }
    }

    private void saveCoverArt(String path, String url) throws Exception {
        InputStream input = null;
        HttpClient client = new DefaultHttpClient();

        try {
            HttpConnectionParams.setConnectionTimeout(client.getParams(), 20 * 1000); // 20 seconds
            HttpConnectionParams.setSoTimeout(client.getParams(), 20 * 1000); // 20 seconds
            HttpGet method = new HttpGet(url);

            HttpResponse response = client.execute(method);
            input = response.getEntity().getContent();

            // Attempt to resolve proper suffix.
            String suffix = "jpg";
            if (url.toLowerCase().endsWith(".gif")) {
                suffix = "gif";
            } else if (url.toLowerCase().endsWith(".png")) {
                suffix = "png";
            }

            // Check permissions.
            File newCoverFile = new File(path, "folder." + suffix);
            if (!securityService.isWriteAllowed(newCoverFile)) {
                throw new Exception("Permission denied: " + StringUtil.toHtml(newCoverFile.getPath()));
            }

            // If file exists, create a backup.
            backup(newCoverFile, new File(path, "folder.backup." + suffix));

            // Write file.
            IOUtils.copy(input, new FileOutputStream(newCoverFile));

            // Rename existing cover file if new cover file is not the preferred.
            try {
                MusicFile musicFile = musicFileService.getMusicFile(path);
                List<File> coverFiles = musicFileService.getCoverArt(musicFile, 1);
                if (!coverFiles.isEmpty()) {
                    if (!newCoverFile.equals(coverFiles.get(0))) {
                        coverFiles.get(0).renameTo(new File(coverFiles.get(0).getCanonicalPath() + ".old"));
                        LOG.info("Renamed old image file " + coverFiles.get(0));
                    }
                }
            } catch (Exception x) {
                LOG.warn("Failed to rename existing cover file.", x);
            }

        } finally {
            IOUtils.closeQuietly(input);
            client.getConnectionManager().shutdown();
        }
    }

    private void backup(File newCoverFile, File backup) {
        if (newCoverFile.exists()) {
            if (backup.exists()) {
                backup.delete();
            }
            if (newCoverFile.renameTo(backup)) {
                LOG.info("Backed up old image file to " + backup);
            } else {
                LOG.warn("Failed to create image file backup " + backup);
            }
        }
    }

    public void setSecurityService(SecurityService securityService) {
        this.securityService = securityService;
    }

    public void setMusicFileService(MusicFileService musicFileService) {
        this.musicFileService = musicFileService;
    }

    public void setDiscogsSearchService(DiscogsSearchService discogsSearchService) {
        this.discogsSearchService = discogsSearchService;
    }
}