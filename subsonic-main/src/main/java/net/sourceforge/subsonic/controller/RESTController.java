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
package net.sourceforge.subsonic.controller;

import net.sourceforge.subsonic.Logger;
import net.sourceforge.subsonic.domain.MusicFile;
import net.sourceforge.subsonic.domain.MusicIndex;
import net.sourceforge.subsonic.domain.Player;
import net.sourceforge.subsonic.domain.User;
import net.sourceforge.subsonic.service.MusicFileService;
import net.sourceforge.subsonic.service.MusicIndexService;
import net.sourceforge.subsonic.service.PlayerService;
import net.sourceforge.subsonic.service.SettingsService;
import net.sourceforge.subsonic.service.TranscodingService;
import net.sourceforge.subsonic.service.SecurityService;
import net.sourceforge.subsonic.util.StringUtil;
import net.sourceforge.subsonic.util.XMLBuilder;
import static net.sourceforge.subsonic.util.XMLBuilder.Attribute;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequestWrapper;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.SortedSet;

/**
 * Multi-controller used for the REST API.
 *
 * @author Sindre Mehus
 */
public class RESTController extends MultiActionController {

    private static final Logger LOG = Logger.getLogger(RESTController.class);

    private SettingsService settingsService;
    private SecurityService securityService;
    private PlayerService playerService;
    private MusicFileService musicFileService;
    private MusicIndexService musicIndexService;
    private TranscodingService transcodingService;
    private DownloadController downloadController;
    private CoverArtController coverArtController;

    /**
     * Request parameters:
     * None
     * <p/>
     * Returns:
     * XML document with "indexes" element.
     */
    public ModelAndView getIndexes(HttpServletRequest request, HttpServletResponse response) throws Exception {

        XMLBuilder builder = createXMLBuilder(response, true);

        long lastModified = 0L; // TODO
        builder.add("indexes", "lastModified", lastModified, false);

        SortedMap<MusicIndex, SortedSet<MusicIndex.Artist>> indexedArtists = musicIndexService.getIndexedArtists(settingsService.getAllMusicFolders());

        for (Map.Entry<MusicIndex, SortedSet<MusicIndex.Artist>> entry : indexedArtists.entrySet()) {
            builder.add("index", "name", entry.getKey().getIndex(), false);

            for (MusicIndex.Artist artist : entry.getValue()) {
                for (MusicFile musicFile : artist.getMusicFiles()) {
                    if (musicFile.isDirectory()) {
                        builder.add("artist", true,
                                    new Attribute("name", artist.getName()),
                                    new Attribute("id", StringUtil.utf8HexEncode(musicFile.getPath())));
                    }
                }
            }
            builder.end();
        }
        builder.endAll();

        return null;
    }

    /**
     * Request parameters:
     * id - Identifies the music directory.
     * <p/>
     * Returns:
     * XML document with "directory" element.
     */
    public ModelAndView getMusicDirectory(HttpServletRequest request, HttpServletResponse response) throws Exception {
        XMLBuilder builder = createXMLBuilder(response, true);
        Player player = playerService.getPlayer(request, response);

        String path = StringUtil.utf8HexDecode(request.getParameter("id"));
        MusicFile dir = musicFileService.getMusicFile(path);
        // TODO: Handle non-existing dir.

        builder.add("directory", false,
                    new Attribute("id", StringUtil.utf8HexEncode(dir.getPath())),
                    new Attribute("name", dir.getName()));

        for (MusicFile musicFile : dir.getChildren(true, true)) {

            List<Attribute> attributes = new ArrayList<Attribute>();
            attributes.add(new Attribute("id", StringUtil.utf8HexEncode(musicFile.getPath())));
            attributes.add(new Attribute("title", musicFile.getTitle()));
            attributes.add(new Attribute("isDir", musicFile.isDirectory()));

            if (musicFile.isFile()) {
                attributes.add(new Attribute("album", musicFile.getMetaData().getAlbum()));
                attributes.add(new Attribute("artist", musicFile.getMetaData().getArtist()));
                attributes.add(new Attribute("size", musicFile.length()));
                String suffix = musicFile.getSuffix();
                attributes.add(new Attribute("suffix", suffix));
                attributes.add(new Attribute("contentType", StringUtil.getMimeType(suffix)));

                List<File> coverArt = musicFileService.getCoverArt(dir, 1);
                if (!coverArt.isEmpty()) {
                    attributes.add(new Attribute("coverArt", StringUtil.utf8HexEncode(coverArt.get(0).getPath())));
                }

                if (transcodingService.isTranscodingRequired(musicFile, player)) {
                    String transcodedSuffix = transcodingService.getSuffix(player, musicFile);
                    attributes.add(new Attribute("transcodedSuffix", transcodedSuffix));
                    attributes.add(new Attribute("transcodedContentType", StringUtil.getMimeType(transcodedSuffix)));
                }
            }
            builder.add("child", attributes, true);
        }
        builder.endAll();

        return null;
    }

    /**
     * Request parameters:
     * id - Identifies the file to download.
     * <p/>
     * Returns:
     * Binary data.
     */
    public ModelAndView download(HttpServletRequest request, HttpServletResponse response) throws Exception {
        User user = securityService.getCurrentUser(request);
        if (!user.isDownloadRole()) {
            error(response, ErrorCode.NOT_AUTHORIZED, user.getUsername() + " is not authorized to download files.");
            return null;
        }

        return downloadController.handleRequest(wrapRequest(request), response);
    }

    /**
     * Request parameters:
     * id - Identifies the cover art file to retrieve.
     * <p/>
     * Returns:
     * Binary data.
     */
    public ModelAndView getCoverArt(HttpServletRequest request, HttpServletResponse response) throws Exception {
        return coverArtController.handleRequest(wrapRequest(request), response);
    }

    /**
     * Renames "id" request parameter to "path".
     */
    private HttpServletRequest wrapRequest(final HttpServletRequest request) {
        return new HttpServletRequestWrapper(request) {
            @Override
            public String getParameter(String name) {
                if ("path".equals(name)) {
                    try {
                        return StringUtil.utf8HexDecode(request.getParameter("id"));
                    } catch (Exception e) {
                        return null;
                    }
                }
                return super.getParameter(name);
            }
        };
    }

    private void error(HttpServletResponse response, ErrorCode code, String message) throws IOException {
        XMLBuilder builder = createXMLBuilder(response, false);
        builder.add("error", true,
                    new XMLBuilder.Attribute("code", code.getCode()),
                    new XMLBuilder.Attribute("message", message));
        builder.end();
    }

    private XMLBuilder createXMLBuilder(HttpServletResponse response, boolean ok) throws IOException {
        response.setContentType("text/xml");
        response.setCharacterEncoding(StringUtil.ENCODING_UTF8);

        XMLBuilder builder = new XMLBuilder(response.getWriter());
        builder.preamble(StringUtil.ENCODING_UTF8);
        builder.add("subsonic-response", false,
                    new Attribute("xlmns", "http://subsonic.sourceforge.net/restapi"),
                    new Attribute("status", ok ? "ok" : "failed"),
                    new Attribute("version", StringUtil.getRESTProtocolVersion()));
        return builder;
    }

    public void setSettingsService(SettingsService settingsService) {
        this.settingsService = settingsService;
    }

    public void setSecurityService(SecurityService securityService) {
        this.securityService = securityService;
    }

    public void setPlayerService(PlayerService playerService) {
        this.playerService = playerService;
    }

    public void setMusicFileService(MusicFileService musicFileService) {
        this.musicFileService = musicFileService;
    }

    public void setMusicIndexService(MusicIndexService musicIndexService) {
        this.musicIndexService = musicIndexService;
    }

    public void setTranscodingService(TranscodingService transcodingService) {
        this.transcodingService = transcodingService;
    }

    public void setDownloadController(DownloadController downloadController) {
        this.downloadController = downloadController;
    }

    public void setCoverArtController(CoverArtController coverArtController) {
        this.coverArtController = coverArtController;
    }

    public static enum ErrorCode {

        NOT_AUTHENTICATED(10),
        NOT_AUTHORIZED(11);

        private final int code;

        ErrorCode(int code) {
            this.code = code;
        }

        public int getCode() {
            return code;
        }
    }
}