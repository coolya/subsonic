package net.sourceforge.subsonic.controller;

import net.sourceforge.subsonic.*;
import net.sourceforge.subsonic.domain.*;
import net.sourceforge.subsonic.service.*;
import net.sourceforge.subsonic.util.*;
import org.springframework.web.servlet.*;
import org.springframework.web.servlet.mvc.*;
import org.springframework.web.servlet.view.*;
import org.apache.commons.io.*;

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
    private MusicFileService musicFileService;

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
        MusicFile dir = musicFileService.getMusicFile(path);

        MusicFile child = dir.getFirstChild();
        String[] coverArtUrls = new String[0];
        if (child != null) {
            try {
                MusicFile.MetaData metaData = child.getMetaData();
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
            IOUtils.closeQuietly(output);
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

    public void setAmazonSearchService(AmazonSearchService amazonSearchService) {
        this.amazonSearchService = amazonSearchService;
    }

    public void setSecurityService(SecurityService securityService) {
        this.securityService = securityService;
    }

    public void setMusicFileService(MusicFileService musicFileService) {
        this.musicFileService = musicFileService;
    }
}
