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
import net.sourceforge.subsonic.domain.Player;
import net.sourceforge.subsonic.domain.Playlist;
import net.sourceforge.subsonic.domain.TransferStatus;
import net.sourceforge.subsonic.domain.User;
import net.sourceforge.subsonic.io.PlaylistInputStream;
import net.sourceforge.subsonic.io.RangeOutputStream;
import net.sourceforge.subsonic.io.ShoutCastOutputStream;
import net.sourceforge.subsonic.service.AudioScrobblerService;
import net.sourceforge.subsonic.service.MusicFileService;
import net.sourceforge.subsonic.service.MusicInfoService;
import net.sourceforge.subsonic.service.PlayerService;
import net.sourceforge.subsonic.service.PlaylistService;
import net.sourceforge.subsonic.service.SearchService;
import net.sourceforge.subsonic.service.SecurityService;
import net.sourceforge.subsonic.service.SettingsService;
import net.sourceforge.subsonic.service.StatusService;
import net.sourceforge.subsonic.service.TranscodingService;
import net.sourceforge.subsonic.util.StringUtil;
import net.sourceforge.subsonic.util.Util;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.math.LongRange;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;

/**
 * A controller which streams the content of a {@link Playlist} to a remote
 * {@link Player}.
 *
 * @author Sindre Mehus
 */
public class StreamController implements Controller {

    private static final Logger LOG = Logger.getLogger(StreamController.class);

    private StatusService statusService;
    private PlayerService playerService;
    private PlaylistService playlistService;
    private SecurityService securityService;
    private MusicInfoService musicInfoService;
    private SettingsService settingsService;
    private TranscodingService transcodingService;
    private AudioScrobblerService audioScrobblerService;
    private MusicFileService musicFileService;
    private SearchService searchService;

    public ModelAndView handleRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {

        TransferStatus status = null;
        PlaylistInputStream in = null;
        Player player = playerService.getPlayer(request, response, false, true);
        User user = securityService.getUserByName(player.getUsername());

        try {

            if (!user.isStreamRole()) {
                response.sendError(HttpServletResponse.SC_FORBIDDEN, "Streaming is forbidden for user " + user.getUsername());
                return null;
            }

            // If "playlist" request parameter is set, this is a Podcast request. In that case, create a separate
            // playlist (in order to support multiple parallel Podcast streams).
            String playlistName = request.getParameter("playlist");
            boolean isPodcast = playlistName != null;
            if (isPodcast) {
                Playlist playlist = new Playlist();
                playlistService.loadPlaylist(playlist, playlistName);
                player.setPlaylist(playlist);
                Util.setContentLength(response, playlist.length());
                LOG.info("Incoming Podcast request for playlist " + playlistName);
            }

            // If "path" request parameter is set, this is a request for a single file
            // (typically from the embedded Flash player). In that case, create a separate
            // playlist (in order to support multiple parallel streams). Also, enable
            // partial download (HTTP byte range).
            String path = request.getParameter("path");
            boolean isSingleFile = path != null;
            LongRange range = null;

            if (isSingleFile) {
                Playlist playlist = new Playlist();
                MusicFile file = musicFileService.getMusicFile(path);
                playlist.addFiles(true, file);
                player.setPlaylist(playlist);

                boolean transcodingRequired = transcodingService.isTranscodingRequired(file, player);
                if (!transcodingRequired) {
                    response.setHeader("ETag", StringUtil.utf8HexEncode(path));
                    response.setHeader("Accept-Ranges", "bytes");

                    range = StringUtil.parseRange(request.getHeader("Range"));
                    if (range != null) {
                        response.setStatus(HttpServletResponse.SC_PARTIAL_CONTENT);
                        Util.setContentLength(response, file.length() - range.getMinimumLong());
                        LOG.info("Got range: " + range);
                    } else {
                        Util.setContentLength(response, file.length());
                    }
                }
            }

            Playlist playlist = player.getPlaylist();

            // Terminate any other streams to this player.
            if (!isPodcast && !isSingleFile) {
                for (TransferStatus streamStatus : statusService.getStreamStatusesForPlayer(player)) {
                    if (streamStatus.isActive()) {
                        streamStatus.terminate();
                    }
                }
            }

            status = statusService.createStreamStatus(player);

            String contentType = StringUtil.getMimeType(request.getParameter("suffix"));
            response.setContentType(contentType);

            in = new PlaylistInputStream(player, status, transcodingService, musicInfoService, audioScrobblerService, searchService);
            OutputStream out = RangeOutputStream.wrap(response.getOutputStream(), range);

            // Enabled SHOUTcast, if requested.
            boolean isShoutCastRequested = "1".equals(request.getHeader("icy-metadata"));
            if (isShoutCastRequested && !isSingleFile) {
                response.setHeader("icy-metaint", "" + ShoutCastOutputStream.META_DATA_INTERVAL);
                response.setHeader("icy-notice1", "This stream is served using Subsonic");
                response.setHeader("icy-notice2", "Subsonic - Free media streamer - subsonic.sourceforge.net");
                response.setHeader("icy-name", "Subsonic");
                response.setHeader("icy-genre", "Mixed");
                response.setHeader("icy-url", "http://subsonic.sourceforge.net/");
                out = new ShoutCastOutputStream(out, playlist, settingsService);
            }

            final int BUFFER_SIZE = 2048;
            byte[] buf = new byte[BUFFER_SIZE];

            while (true) {

                // Check if stream has been terminated.
                if (status.terminated()) {
                    return null;
                }

                if (playlist.getStatus() == Playlist.Status.STOPPED) {
                    if (isPodcast || isSingleFile) {
                        break;
                    } else {
                        sendDummy(buf, out);
                    }
                } else {

                    int n = in.read(buf);
                    if (n == -1) {
                        sendDummy(buf, out);

                    } else {
                        out.write(buf, 0, n);
                    }
                }
            }

        } finally {
            if (status != null) {
                statusService.removeStreamStatus(status);
                securityService.updateUserByteCounts(user, status.getBytesTransfered(), 0L, 0L);
            }
            IOUtils.closeQuietly(in);
        }
        return null;
    }

    /**
     * Feed the other end with some dummy data to keep it from reconnecting.
     */
    private void sendDummy(byte[] buf, OutputStream out) throws IOException {
        try {
            Thread.sleep(2000);
        } catch (InterruptedException x) {
            LOG.warn("Interrupted in sleep.", x);
        }
        Arrays.fill(buf, (byte) 0xFF);
        out.write(buf);
        out.flush();
    }

    public void setStatusService(StatusService statusService) {
        this.statusService = statusService;
    }

    public void setPlayerService(PlayerService playerService) {
        this.playerService = playerService;
    }

    public void setPlaylistService(PlaylistService playlistService) {
        this.playlistService = playlistService;
    }

    public void setSecurityService(SecurityService securityService) {
        this.securityService = securityService;
    }

    public void setMusicFileService(MusicFileService musicFileService) {
        this.musicFileService = musicFileService;
    }

    public void setMusicInfoService(MusicInfoService musicInfoService) {
        this.musicInfoService = musicInfoService;
    }

    public void setSettingsService(SettingsService settingsService) {
        this.settingsService = settingsService;
    }

    public void setTranscodingService(TranscodingService transcodingService) {
        this.transcodingService = transcodingService;
    }

    public void setAudioScrobblerService(AudioScrobblerService audioScrobblerService) {
        this.audioScrobblerService = audioScrobblerService;
    }

    public void setSearchService(SearchService searchService) {
        this.searchService = searchService;
    }
}
