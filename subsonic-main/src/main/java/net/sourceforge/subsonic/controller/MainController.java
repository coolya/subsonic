package net.sourceforge.subsonic.controller;

import net.sourceforge.subsonic.domain.*;
import net.sourceforge.subsonic.service.*;
import org.springframework.web.servlet.*;
import org.springframework.web.servlet.mvc.*;

import javax.servlet.http.*;
import java.util.*;
import java.io.*;

/**
 * Controller for the main page.
 *
 * @author Sindre Mehus
 */
public class MainController extends ParameterizableViewController {

    private SecurityService securityService;
    private PlayerService playerService;
    private SettingsService settingsService;
    private MusicInfoService musicInfoService;

    protected ModelAndView handleRequestInternal(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Map<String, Object> map = new HashMap<String, Object>();

        Player player = playerService.getPlayer(request, response);
        String path = request.getParameter("path");
        MusicFile dir = new MusicFile(path);
        map.put("dir", dir);
        map.put("children", dir.getChildren(false, true));
        map.put("player", player);

        map.put("user", securityService.getCurrentUser(request));
        map.put("updateNowPlaying", request.getParameter("updateNowPlaying") != null);

        MusicFileInfo musicInfo = musicInfoService.getMusicFileInfoForPath(path);
        int rating = musicInfo == null ? 0 : musicInfo.getRating();
        int playCount = musicInfo == null ? 0 : musicInfo.getPlayCount();
        String comment = musicInfo == null  ? null : musicInfo.getComment();
        Date lastPlayed = musicInfo == null  ? null : musicInfo.getLastPlayed();
        map.put("rating", rating);
        map.put("playCount", playCount);
        map.put("comment", comment);
        map.put("lastPlayed", lastPlayed);

        CoverArtScheme scheme = player.getCoverArtScheme();
        if (scheme != CoverArtScheme.OFF) {
            int limit = settingsService.getCoverArtLimit();
            if (limit == 0) {
                limit = Integer.MAX_VALUE;
            }
            File[] coverArts = dir.getCoverArt(limit);
            int size = coverArts.length > 1 ? scheme.getSize() : scheme.getSize() * 2;
            map.put("coverArts", coverArts);
            map.put("coverArtSize", size);
            if (coverArts.length == 0 && dir.isAlbum()) {
                map.put("showGenericCoverArt", true);
            }

        }

        ModelAndView result = super.handleRequestInternal(request, response);
        result.addObject("model", map);
        return result;
    }

    public void setSecurityService(SecurityService securityService) {
        this.securityService = securityService;
    }

    public void setPlayerService(PlayerService playerService) {
        this.playerService = playerService;
    }

    public void setSettingsService(SettingsService settingsService) {
        this.settingsService = settingsService;
    }

    public void setMusicInfoService(MusicInfoService musicInfoService) {
        this.musicInfoService = musicInfoService;
    }
}
