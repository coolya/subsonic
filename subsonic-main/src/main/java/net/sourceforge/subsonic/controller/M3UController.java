package net.sourceforge.subsonic.controller;

import net.sourceforge.subsonic.*;
import net.sourceforge.subsonic.domain.*;
import net.sourceforge.subsonic.service.*;
import net.sourceforge.subsonic.util.*;
import org.springframework.web.servlet.*;
import org.springframework.web.servlet.mvc.*;

import javax.servlet.http.*;

/**
 * Controller which produces the M3U playlist.
 *
 * @author Sindre Mehus
 */
public class M3UController implements Controller {

    private PlayerService playerService;
    private SettingsService settingsService;

    private static final Logger LOG = Logger.getLogger(M3UController.class);

    public ModelAndView handleRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
        response.setContentType("audio/x-mpegurl");
        Player player = playerService.getPlayer(request, response);
        Playlist playlist = player.getPlaylist();

        String s = "stream?player=" + player.getId() + '&';

        // Get suffix of current file, e.g., ".mp3".
        String suffix = playlist.getSuffix();
        if (suffix != null) {
            s += "suffix=" + suffix;
        }

        String url = request.getRequestURL().toString();
        url = url.replaceFirst("play.m3u.*", s);

        // Change protocol and port, if specified. (To make it work with players that don't support SSL.)
        int streamPort = settingsService.getStreamPort();
        if (streamPort != 0) {
            url = StringUtil.toHttpUrl(url, streamPort);
            LOG.info("Using non-SSL port " + streamPort + " in m3u playlist.");
        }

        response.getOutputStream().println("#EXTM3U");
        response.getOutputStream().println("#EXTINF:-1,subsonic");
        response.getOutputStream().println(url);

        return null;
    }

    public void setPlayerService(PlayerService playerService) {
        this.playerService = playerService;
    }

    public void setSettingsService(SettingsService settingsService) {
        this.settingsService = settingsService;
    }
}
