package net.sourceforge.subsonic.controller;

import net.sourceforge.subsonic.domain.*;
import net.sourceforge.subsonic.service.*;
import org.springframework.web.servlet.*;
import org.springframework.web.servlet.mvc.multiaction.*;

import javax.servlet.http.*;
import java.util.*;

/**
 * Multi-controller used for wap pages.
 *
 * @author Sindre Mehus
 */
public class WapController extends MultiActionController {

    private SettingsService settingsService;
    private PlayerService playerService;
    private PlaylistService playlistService;

    public ModelAndView wapIndex(HttpServletRequest request, HttpServletResponse response) throws Exception {
        return wap(request, response);
    }

    public ModelAndView wap(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Map<String, Object> map = new HashMap<String, Object>();
        MusicFolder[] folders = settingsService.getAllMusicFolders();

        if (folders.length == 0) {
            map.put("noMusic", true);
        } else {

            String indexString = settingsService.getIndexString();
            String[] ignoredArticles = settingsService.getIgnoredArticlesAsArray();
            String[] shortcuts = new String[0];
            Map<MusicIndex, List<MusicFile>> children = MusicIndex.getIndexedChildren(folders, MusicIndex.createIndexesFromExpression(indexString),
                                                                                      ignoredArticles, shortcuts);
            // If an index is given as parameter, only show music files for this index.
            String index = request.getParameter("index");
            if (index != null) {
                List<MusicFile> musicFiles = children.get(new MusicIndex(index));
                if (musicFiles == null) {
                    map.put("noMusic", true);
                } else {
                    map.put("artists", musicFiles);
                }
            }

            // Otherwise, list all indexes.
            else {
                map.put("indexes", children.keySet());
            }
        }

        return new ModelAndView("wapIndex", "model", map);
    }

    public ModelAndView wapBrowse(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String path = request.getParameter("path");
        MusicFile parent = new MusicFile(path);

        // Create array of file(s) to display.
        MusicFile[] children;
        if (parent.isDirectory()) {
            children = parent.getChildren(false, true);
        } else {
            children = new MusicFile[] {parent};
        }

        Map<String, Object> map = new HashMap<String, Object>();
        map.put("parent", parent);
        map.put("children", children);

        return new ModelAndView("wapBrowse", "model", map);
    }

    public ModelAndView wapPlaylist(HttpServletRequest request, HttpServletResponse response) throws Exception {
        // Create array of players to control. If the "player" attribute is set for this session,
        // only the player with this ID is controlled.  Otherwise, all players are controlled.
        Player[] players = playerService.getAllPlayers();

        String playerId = (String) request.getSession().getAttribute("player");
        if (playerId != null) {
            Player player = playerService.getPlayerById(playerId);
            if (player != null) {
                players = new Player[] {player};
            }
        }

        Map<String, Object> map = new HashMap<String, Object>();

        for (int i = 0; i < players.length; i++) {
            Player player = players[i];
            Playlist playlist = player.getPlaylist();
            map.put("playlist", playlist);

            if (request.getParameter("play") != null) {
                MusicFile file = new MusicFile(request.getParameter("play"));
                playlist.addFile(file, false);
            } else if (request.getParameter("add") != null) {
                MusicFile file = new MusicFile(request.getParameter("add"));
                playlist.addFile(file);
            } else if (request.getParameter("skip") != null) {
                playlist.setIndex(Integer.parseInt(request.getParameter("skip")));
            } else if (request.getParameter("clear") != null) {
                playlist.clear();
            } else if (request.getParameter("load") != null) {
                playlistService.loadPlaylist(playlist, request.getParameter("load"));
            }
        }

        map.put("players", players);
        return new ModelAndView("wapPlaylist", "model", map);
    }

    public void setSettingsService(SettingsService settingsService) {
        this.settingsService = settingsService;
    }

    public void setPlayerService(PlayerService playerService) {
        this.playerService = playerService;
    }

    public void setPlaylistService(PlaylistService playlistService) {
        this.playlistService = playlistService;
    }
}
