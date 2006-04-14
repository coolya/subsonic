package net.sourceforge.subsonic.controller;

import net.sourceforge.subsonic.*;
import net.sourceforge.subsonic.domain.*;
import net.sourceforge.subsonic.service.*;
import net.sourceforge.subsonic.util.*;
import org.springframework.web.servlet.*;
import org.springframework.web.servlet.mvc.*;
import org.springframework.web.servlet.view.*;

import javax.servlet.http.*;
import java.io.*;
import java.net.*;
import java.util.*;

/**
 * Controller for saving playlists.
 *
 * @author Sindre Mehus
 */
public class ChangeCoverArtController extends AbstractController {

    private static final Logger LOG = Logger.getLogger(ChangeCoverArtController.class);

    private AmazonSearchService amazonSearchService;
    private SecurityService securityService;

    protected ModelAndView handleRequestInternal(HttpServletRequest request, HttpServletResponse response) throws Exception {

        String path = request.getParameter("path");
        String url = request.getParameter("url");

        if (url != null) {
            // TODO: handle error and redirect.
            saveCoverArt(path, url);
            return new ModelAndView(new RedirectView("main.view?path=" + StringUtil.urlEncode(path)));
        } else {

            return new ModelAndView("changeCoverArt", "model", createModel(request));
        }


    }

    private Map createModel(HttpServletRequest request) throws Exception {
        String path = request.getParameter("path");
        String artist = request.getParameter("artist");
        String album = request.getParameter("album");
        MusicFile dir = new MusicFile(path);

        MusicFile[] children = dir.getChildren(false);
        String[] coverArtUrls = new String[0];
        if (children.length > 0) {
            try {
                MusicFile.MetaData metaData = children[0].getMetaData();
                if (artist == null) {
                    artist = metaData.getArtist();
                }
                if (album == null) {
                    album = metaData.getAlbum();
                }

                coverArtUrls = amazonSearchService.getCoverArtImages(artist, album);
            } catch (Exception x) {
                LOG.warn("Failed to search for cover images at Amazon.com.", x);
            }
        }

        Map<String, Object> map = new HashMap<String, Object>();
        map.put("coverArtUrls", coverArtUrls);
        map.put("path", path);
        map.put("artist", artist);
        map.put("album", album);

        return map;
    }

    private void saveCoverArt(String path, String url) throws Exception {
        InputStream input = null;
        OutputStream output = null;

        try {
            input = new URL(url).openStream();

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

            // Write file.
            output = new FileOutputStream(newCoverFile);
            byte[] buf = new byte[8192];
            while (true) {
                int n = input.read(buf);
                if (n == -1) {
                    break;
                }
                output.write(buf, 0, n);
            }

            // Rename existing cover file if new cover file is not the preferred.
            try {
                File[] coverFiles = new MusicFile(path).getCoverArt(1);
                if (coverFiles.length > 0) {
                    if (!newCoverFile.equals(coverFiles[0])) {
                        coverFiles[0].renameTo(new File(coverFiles[0].getCanonicalPath() + ".old"));
                        LOG.info("Renamed old image file " + coverFiles[0]);
                    }
                }
            } catch (Exception x) {
                LOG.warn("Failed to rename existing cover file.", x);
            }

        } finally {
            try { input.close(); } catch (Exception x) {/* Ignored */}
            try { output.close(); } catch (Exception x) {/* Ignored */}
        }
    }

    public void setAmazonSearchService(AmazonSearchService amazonSearchService) {
        this.amazonSearchService = amazonSearchService;
    }

    public void setSecurityService(SecurityService securityService) {
        this.securityService = securityService;
    }
}
