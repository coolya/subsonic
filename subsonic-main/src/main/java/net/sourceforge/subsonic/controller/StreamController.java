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

import java.awt.Dimension;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.math.LongRange;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

import net.sourceforge.subsonic.Logger;
import net.sourceforge.subsonic.domain.MusicFile;
import net.sourceforge.subsonic.domain.Player;
import net.sourceforge.subsonic.domain.Playlist;
import net.sourceforge.subsonic.domain.TransferStatus;
import net.sourceforge.subsonic.domain.User;
import net.sourceforge.subsonic.domain.VideoTranscodingSettings;
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

            String contentType = StringUtil.getMimeType(request.getParameter("suffix"));
            response.setContentType(contentType);

            Integer maxBitRate = ServletRequestUtils.getIntParameter(request, "maxBitRate");
            if (maxBitRate != null && maxBitRate == 0) {
                maxBitRate = Integer.MAX_VALUE;
            }
            VideoTranscodingSettings videoTranscodingSettings = null;

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

                response.setHeader("ETag", StringUtil.utf8HexEncode(path));
                response.setHeader("Accept-Ranges", "bytes");

                long contentLength;
                range = getRange(request, file);
                if (range != null) {
                    response.setStatus(HttpServletResponse.SC_PARTIAL_CONTENT);
                    contentLength = file.length() - range.getMinimumLong();
                    LOG.info("Got range: " + range);
                } else {
                    contentLength = file.length();
                }

                boolean transcodingRequired = transcodingService.isTranscodingRequired(file, player);
                boolean downsamplingRequired = transcodingService.isDownsamplingRequired(file, player, maxBitRate);
                if (!transcodingRequired && !downsamplingRequired) {
                    Util.setContentLength(response, contentLength);
                }

                String transcodedSuffix = transcodingService.getSuffix(player, file);
                response.setContentType(StringUtil.getMimeType(transcodedSuffix));

                if (file.isVideo()) {
                    videoTranscodingSettings = createVideoTranscodingSettings(file, request);
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

            in = new PlaylistInputStream(player, status, maxBitRate, videoTranscodingSettings, transcodingService, musicInfoService, audioScrobblerService, searchService);
            OutputStream out = RangeOutputStream.wrap(response.getOutputStream(), range);

            // Enabled SHOUTcast, if requested.
            boolean isShoutCastRequested = "1".equals(request.getHeader("icy-metadata"));
            if (isShoutCastRequested && !isSingleFile) {
                response.setHeader("icy-metaint", "" + ShoutCastOutputStream.META_DATA_INTERVAL);
                response.setHeader("icy-notice1", "This stream is served using Subsonic");
                response.setHeader("icy-notice2", "Subsonic - Free media streamer - subsonic.org");
                response.setHeader("icy-name", "Subsonic");
                response.setHeader("icy-genre", "Mixed");
                response.setHeader("icy-url", "http://subsonic.org/");
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
                        if (isPodcast || isSingleFile) {
                            break;
                        } else {
                            sendDummy(buf, out);
                        }
                    } else {
                        out.write(buf, 0, n);
                    }
                }
            }

        } finally {
            if (status != null) {
                securityService.updateUserByteCounts(user, status.getBytesTransfered(), 0L, 0L);
                statusService.removeStreamStatus(status);
            }
            IOUtils.closeQuietly(in);
        }
        return null;
    }

    private LongRange getRange(HttpServletRequest request, MusicFile file) {

        // First, look for "Range" HTTP header.
        LongRange range = StringUtil.parseRange(request.getHeader("Range"));
        if (range != null) {
            return range;
        }

        // Second, look for "offsetSeconds" request parameter.
        String offsetSeconds = request.getParameter("offsetSeconds");
        range = parseAndConvertOffsetSeconds(offsetSeconds, file);
        if (range != null) {
            return range;
        }

        return null;
    }

    private LongRange parseAndConvertOffsetSeconds(String offsetSeconds, MusicFile file) {
        if (offsetSeconds == null) {
            return null;
        }

        try {
            Integer duration = file.getMetaData().getDuration();
            Long fileSize = file.getMetaData().getFileSize();
            if (duration == null || fileSize == null) {
                return null;
            }
            float offset = Float.parseFloat(offsetSeconds);

            // Convert from time offset to byte offset.
            long byteOffset = (long) (fileSize * (offset / duration));
            return new LongRange(byteOffset, Long.MAX_VALUE);

        } catch (Exception x) {
            LOG.error("Failed to parse and convert time offset: " + offsetSeconds, x);
            return null;
        }
    }

    private VideoTranscodingSettings createVideoTranscodingSettings(MusicFile file, HttpServletRequest request) throws ServletRequestBindingException {
        Integer existingWidth = file.getMetaData().getWidth();
        Integer existingHeight = file.getMetaData().getHeight();
        Integer maxBitRate = ServletRequestUtils.getIntParameter(request, "maxBitRate");
        int timeOffset = ServletRequestUtils.getIntParameter(request, "timeOffset", 0);
        Dimension dim = getSuitableVideoSize(existingWidth, existingHeight, maxBitRate);

        return new VideoTranscodingSettings(dim.width, dim.height, timeOffset);
    }

    protected Dimension getSuitableVideoSize(Integer existingWidth, Integer existingHeight, Integer maxBitRate) {
        if (maxBitRate == null) {
            return new Dimension(320, 240);
        }

        int w, h;
        if (maxBitRate <= 600) {
            w = 320; h = 240;
        } else if (maxBitRate <= 1000) {
            w = 480; h = 360;
        } else {
            w = 640; h = 480;
        }

        if (existingWidth == null || existingHeight == null) {
            return new Dimension(w, h);
        }

        if (existingWidth < w || existingHeight < h) {
            return new Dimension(existingWidth, existingHeight);
        }

        double aspectRate = existingWidth.doubleValue() / existingHeight.doubleValue();
        return new Dimension((int) Math.round(h * aspectRate), h);
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
