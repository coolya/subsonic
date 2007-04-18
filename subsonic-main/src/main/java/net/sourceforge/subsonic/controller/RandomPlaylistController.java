package net.sourceforge.subsonic.controller;

import net.sourceforge.subsonic.domain.*;
import net.sourceforge.subsonic.service.*;
import net.sourceforge.subsonic.util.StringUtil;
import org.springframework.web.servlet.*;
import org.springframework.web.servlet.mvc.*;
import org.springframework.web.bind.ServletRequestUtils;
import org.apache.commons.lang.StringUtils;

import javax.servlet.http.*;
import javax.servlet.ServletRequest;
import java.util.*;

/**
 * Controller for the creating a random playlist.
 *
 * @author Sindre Mehus
 */
public class RandomPlaylistController extends ParameterizableViewController {

    private SearchService searchService;
    private PlayerService playerService;
    private List<ReloadFrame> reloadFrames;

    protected ModelAndView handleRequestInternal(HttpServletRequest request, HttpServletResponse response) throws Exception {

        int size = ServletRequestUtils.getRequiredIntParameter(request, "size");
        String genre = request.getParameter("genre");
        if (StringUtils.equalsIgnoreCase("any", genre)) {
            genre = null;
        }

        Integer fromYear = null;
        Integer toYear = null;

        String year = request.getParameter("year");
        if (!StringUtils.equalsIgnoreCase("any", year)) {
            String[] tmp = StringUtils.split(year);
            fromYear = Integer.parseInt(tmp[0]);
            toYear = Integer.parseInt(tmp[1]);
        }

        Player player = playerService.getPlayer(request, response);
        Playlist playlist = player.getPlaylist();
        playlist.clear();

        List<MusicFile> randomFiles = searchService.getRandomSongs(size, genre, fromYear, toYear);
        for (MusicFile randomFile : randomFiles) {
            playlist.addFile(randomFile);
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

    public void setSearchService(SearchService searchService) {
        this.searchService = searchService;
    }
}
