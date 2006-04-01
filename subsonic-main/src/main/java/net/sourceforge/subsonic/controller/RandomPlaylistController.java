package net.sourceforge.subsonic.controller;

import net.sourceforge.subsonic.domain.*;
import net.sourceforge.subsonic.service.*;
import org.springframework.web.servlet.*;
import org.springframework.web.servlet.mvc.*;

import javax.servlet.http.*;
import java.util.*;

/**
 * Controller for the creating a random playlist.
 *
 * @author Sindre Mehus
 */
public class RandomPlaylistController extends ParameterizableViewController {

    private PlayerService playerService;
    private List<ReloadFrame> reloadFrames;

    protected ModelAndView handleRequestInternal(HttpServletRequest request, HttpServletResponse response) throws Exception {

        int size = Integer.parseInt(request.getParameter("size"));

        Player player = playerService.getPlayer(request, response);
        Playlist playlist = player.getPlaylist();
        playlist.clear();

        List randomFiles = ServiceFactory.getSearchService().getRandomMusicFiles(size);
        for (int i = 0; i < randomFiles.size(); i++) {
            playlist.addFile((MusicFile) randomFiles.get(i));
        }

        Map<String, Object> map = new HashMap<String, Object>();
        map.put("reloadFrames", reloadFrames);

        ModelAndView result = super.handleRequestInternal(request, response);
        result.addObject("model", map);
        return result;
    }

    public void setPlayerService(PlayerService playerService) {
        this.playerService = playerService;
    }

    public void setReloadFrames(List<ReloadFrame> reloadFrames) {
        this.reloadFrames = reloadFrames;
    }
}
