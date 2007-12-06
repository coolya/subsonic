package net.sourceforge.subsonic.controller;

import net.sourceforge.subsonic.*;
import net.sourceforge.subsonic.domain.*;
import net.sourceforge.subsonic.service.*;
import net.sourceforge.subsonic.util.*;
import org.springframework.web.servlet.*;
import org.springframework.web.servlet.mvc.*;

import javax.servlet.http.*;
import javax.servlet.ServletOutputStream;
import java.io.IOException;

/**
 * Controller which produces the M3U playlist.
 *
 * @author Sindre Mehus
 */
public class M3UController implements Controller {

    private PlayerService playerService;
    private SettingsService settingsService;
    private TranscodingService transcodingService;

    private static final Logger LOG = Logger.getLogger(M3UController.class);

    public ModelAndView handleRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
        response.setContentType("audio/x-mpegurl");
        Player player = playerService.getPlayer(request, response);

        String url = request.getRequestURL().toString();
        url = url.replaceFirst("play.m3u.*", "stream?");

        // Rewrite URLs in case we're behind a proxy.
        if (settingsService.isRewriteUrlEnabled()) {
            String referer = request.getHeader("referer");
            url = StringUtil.rewriteUrl(url, referer);
        }

        // Change protocol and port, if specified. (To make it work with players that don't support SSL.)
        int streamPort = settingsService.getStreamPort();
        if (streamPort != 0) {
            url = StringUtil.toHttpUrl(url, streamPort);
            LOG.info("Using non-SSL port " + streamPort + " in m3u playlist.");
        }

        if (player.isClientSidePlaylist()) {
            createClientSidePlaylist(response.getOutputStream(), player, url);
        } else {
            createServerSidePlaylist(response.getOutputStream(), player, url);
        }
        return null;
    }

    private void createClientSidePlaylist(ServletOutputStream out, Player player, String url) throws Exception {
        out.println("#EXTM3U");
        for (MusicFile musicFile : player.getPlaylist().getFiles()) {
            MusicFile.MetaData metaData = musicFile.getMetaData();
            Integer duration = metaData.getDuration();
            if (duration == null) {
                duration = -1;
            }
            out.println("#EXTINF:" + duration + "," + metaData.getArtist() + " - " + metaData.getTitle());
            out.println(url + "player=" + player.getId() + "&pathUtf8Hex=" + StringUtil.utf8HexEncode(musicFile.getPath()) + "&suffix=." +  musicFile.getSuffix());
        }
    }

    private void createServerSidePlaylist(ServletOutputStream out, Player player, String url) throws IOException {

        url += "player=" + player.getId();

        // Get suffix of current file, e.g., ".mp3".
        String suffix = getSuffix(player);
        if (suffix != null) {
            url += "&suffix=." + suffix;
        }

        out.println("#EXTM3U");
        out.println("#EXTINF:-1,Subsonic");
        out.println(url);
    }

    private String getSuffix(Player player) {
        Playlist playlist = player.getPlaylist();
        if (playlist.isEmpty()) {
            return null;
        }
        MusicFile file = playlist.getFile(0);
        Transcoding transcoding = transcodingService.getTranscoding(file, player);
        if (transcoding != null) {
            return transcoding.getTargetFormat();
        }

        return file.getSuffix();
    }

    public void setPlayerService(PlayerService playerService) {
        this.playerService = playerService;
    }

    public void setSettingsService(SettingsService settingsService) {
        this.settingsService = settingsService;
    }

    public void setTranscodingService(TranscodingService transcodingService) {
        this.transcodingService = transcodingService;
    }
}
