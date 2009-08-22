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
import net.sourceforge.subsonic.command.UserSettingsCommand;
import net.sourceforge.subsonic.domain.MusicFile;
import net.sourceforge.subsonic.domain.MusicIndex;
import net.sourceforge.subsonic.domain.Player;
import net.sourceforge.subsonic.domain.User;
import net.sourceforge.subsonic.domain.TranscodeScheme;
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
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.bind.ServletRequestBindingException;

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
    private UserSettingsController userSettingsController;

    /**
     * Request parameters:
     * None
     * <p/>
     * Returns:
     * XML document with no elements.
     */
    public ModelAndView ping(HttpServletRequest request, HttpServletResponse response) throws Exception {
        createXMLBuilder(response, true).endAll();
        return null;
    }

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

        MusicFile dir = null;
        try {
            String path = StringUtil.utf8HexDecode(ServletRequestUtils.getRequiredStringParameter(request, "id"));
            dir = musicFileService.getMusicFile(path);
        } catch (Exception x) {
            error(response, ErrorCode.GENERIC, x.getMessage());
            return null;
        }

        builder.add("directory", false,
                    new Attribute("id", StringUtil.utf8HexEncode(dir.getPath())),
                    new Attribute("name", dir.getName()));

        List<File> coverArt = musicFileService.getCoverArt(dir, 1);

        for (MusicFile musicFile : dir.getChildren(true, true)) {

            List<Attribute> attributes = new ArrayList<Attribute>();
            attributes.add(new Attribute("id", StringUtil.utf8HexEncode(musicFile.getPath())));
            attributes.add(new Attribute("title", musicFile.getTitle()));
            attributes.add(new Attribute("isDir", musicFile.isDirectory()));

            if (musicFile.isFile()) {
                MusicFile.MetaData metaData = musicFile.getMetaData();
                attributes.add(new Attribute("album", metaData.getAlbum()));
                attributes.add(new Attribute("artist", metaData.getArtist()));

                Integer track = metaData.getTrackNumber();
                if (track != null) {
                    attributes.add(new Attribute("track", track));
                }

                String year = metaData.getYear();
                if (year != null) {
                    try {
                        attributes.add(new Attribute("year", Integer.valueOf(year)));
                    } catch (NumberFormatException x) {
                        LOG.warn("Invalid year: " + year, x);
                    }
                }

                String genre = metaData.getGenre();
                if (genre != null) {
                    attributes.add(new Attribute("genre", genre));
                }

                attributes.add(new Attribute("size", musicFile.length()));
                String suffix = musicFile.getSuffix();
                attributes.add(new Attribute("suffix", suffix));
                attributes.add(new Attribute("contentType", StringUtil.getMimeType(suffix)));

                if (!coverArt.isEmpty()) {
                    attributes.add(new Attribute("coverArt", StringUtil.utf8HexEncode(coverArt.get(0).getPath())));
                }

                if (transcodingService.isTranscodingRequired(musicFile, player)) {
                    String transcodedSuffix = transcodingService.getSuffix(player, musicFile);
                    attributes.add(new Attribute("transcodedSuffix", transcodedSuffix));
                    attributes.add(new Attribute("transcodedContentType", StringUtil.getMimeType(transcodedSuffix)));
                }
            } else {

                List<File> childCoverArt = musicFileService.getCoverArt(musicFile, 1);
                if (!childCoverArt.isEmpty()) {
                    attributes.add(new Attribute("coverArt", StringUtil.utf8HexEncode(childCoverArt.get(0).getPath())));
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
     * Request parameters:
     * username - Username of the new user.
     * password - Password of the new user.
     * ldapAuthenticated - optional, default false.
     * adminRole - optional, default false.
     * commentRole - optional, default false.
     * coverArtRole - optional, default false.
     * streamRole - optional, default true.
     * downloadRole - optional, default false.
     * uploadRole - optional, default false.
     * jukeboxRole - optional, default false.
     * playlistRole - optional, default false.
     * podcastRole - optional, default false.
     * settingsRole - optional, default true.
     * transcodeScheme - OFF, MAX_32, MAX_40, MAX_48 etc. optional, default OFF.
     * <p/>
     * Returns:
     * Success or failure status.
     */
    public ModelAndView createUser(HttpServletRequest request, HttpServletResponse response) throws Exception {
        User user = securityService.getCurrentUser(request);
        if (!user.isAdminRole()) {
            error(response, ErrorCode.NOT_AUTHORIZED, user.getUsername() + " is not authorized to create new users.");
            return null;
        }

        try {
            UserSettingsCommand command = new UserSettingsCommand();
            command.setUsername(ServletRequestUtils.getRequiredStringParameter(request, "username"));
            command.setPassword(ServletRequestUtils.getRequiredStringParameter(request, "password"));
            command.setLdapAuthenticated(ServletRequestUtils.getBooleanParameter(request, "ldapAuthenticated", false));
            command.setAdminRole(ServletRequestUtils.getBooleanParameter(request, "adminRole", false));
            command.setCommentRole(ServletRequestUtils.getBooleanParameter(request, "commentRole", false));
            command.setCoverArtRole(ServletRequestUtils.getBooleanParameter(request, "coverArtRole", false));
            command.setDownloadRole(ServletRequestUtils.getBooleanParameter(request, "downloadRole", false));
            command.setStreamRole(ServletRequestUtils.getBooleanParameter(request, "streamRole", true));
            command.setUploadRole(ServletRequestUtils.getBooleanParameter(request, "uploadRole", false));
            command.setJukeboxRole(ServletRequestUtils.getBooleanParameter(request, "jukeboxRole", false));
            command.setPlaylistRole(ServletRequestUtils.getBooleanParameter(request, "playlistRole", false));
            command.setPodcastRole(ServletRequestUtils.getBooleanParameter(request, "podcastRole", false));
            command.setSettingsRole(ServletRequestUtils.getBooleanParameter(request, "settingsRole", true));
            command.setTranscodeSchemeName(ServletRequestUtils.getStringParameter(request, "transcodeScheme", TranscodeScheme.OFF.name()));

            userSettingsController.createUser(command);
            createXMLBuilder(response, true).endAll();

        } catch (ServletRequestBindingException x) {
            error(response, ErrorCode.MISSING_PARAMETER, x.getMessage());
        } catch (Exception x) {
            error(response, ErrorCode.GENERIC, x.getMessage());
        }

        return null;
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

    public void setUserSettingsController(UserSettingsController userSettingsController) {
        this.userSettingsController = userSettingsController;
    }

    public static enum ErrorCode {

        GENERIC(0, "A generic error"),
        MISSING_PARAMETER(1, "Required parameter is missing"),
        PROTOCOL_MISMATCH(10, "Wrong Subsonic REST protocol version"),
        NOT_AUTHENTICATED(11, "Wrong username or password"),
        NOT_AUTHORIZED(12, "User is not authorized for the given operation");

        private final int code;
        private final String message;

        ErrorCode(int code, String message) {
            this.code = code;
            this.message = message;
        }

        public int getCode() {
            return code;
        }

        public String getMessage() {
            return message;
        }
    }
}