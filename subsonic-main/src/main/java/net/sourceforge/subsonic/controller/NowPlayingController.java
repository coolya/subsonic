package net.sourceforge.subsonic.controller;

import net.sourceforge.subsonic.domain.*;
import net.sourceforge.subsonic.service.*;
import org.springframework.web.servlet.*;
import org.springframework.web.servlet.mvc.*;
import org.springframework.web.servlet.view.*;

import javax.servlet.http.*;

/**
 * Controller for showing what's currently playing.
 *
 * @author Sindre Mehus
 */
public class NowPlayingController extends AbstractController {

    private PlayerService playerService;

    protected ModelAndView handleRequestInternal(HttpServletRequest request, HttpServletResponse response) throws Exception {

        Player player = playerService.getPlayer(request, response);
        Playlist playlist = player.getPlaylist();

        MusicFile current = playlist.getCurrentFile();
        String url;
        if (current != null && !current.getParent().isRoot()) {
            url = "main.view?path=" + current.getParent().urlEncode() + "&updateNowPlaying=true";
        } else {
            url = "home.view";
        }

        return new ModelAndView(new RedirectView(url));
    }

    public void setPlayerService(PlayerService playerService) {
        this.playerService = playerService;
    }
}
