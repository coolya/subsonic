package net.sourceforge.subsonic.controller;

import net.sourceforge.subsonic.domain.*;
import net.sourceforge.subsonic.service.*;
import org.springframework.web.servlet.*;
import org.springframework.web.servlet.mvc.*;
import org.springframework.web.servlet.view.*;

import javax.servlet.http.*;

/**
 * Controller for saving playlists.
 *
 * @author Sindre Mehus
 */
public class SavePlaylistController extends SimpleFormController {

    private PlaylistService playlistService;
    private PlayerService playerService;

    public ModelAndView onSubmit(Object command) throws Exception {
        Playlist playlist = (Playlist) command;
        playlistService.savePlaylist(playlist);

        return new ModelAndView(new RedirectView(getSuccessView()));
    }

    protected Object formBackingObject(HttpServletRequest request) throws Exception {
        Player player = playerService.getPlayer(request, null);
        return player.getPlaylist();
    }

    public void setPlaylistService(PlaylistService playlistService) {
        this.playlistService = playlistService;
    }

    public void setPlayerService(PlayerService playerService) {
        this.playerService = playerService;
    }
}
