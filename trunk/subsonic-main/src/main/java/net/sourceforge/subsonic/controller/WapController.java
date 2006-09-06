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
    private SearchService searchService;
    private SecurityService securityService;

    public ModelAndView index(HttpServletRequest request, HttpServletResponse response) throws Exception {
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

        return new ModelAndView("wap/index", "model", map);
    }

    public ModelAndView browse(HttpServletRequest request, HttpServletResponse response) throws Exception {
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

        return new ModelAndView("wap/browse", "model", map);
    }

    public ModelAndView playlist(HttpServletRequest request, HttpServletResponse response) throws Exception {
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
        return new ModelAndView("wap/playlist", "model", map);
    }

    public ModelAndView loadPlaylist(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("playlists", playlistService.getSavedPlaylists());
        return new ModelAndView("wap/loadPlaylist", "model", map);
    }

    public ModelAndView search(HttpServletRequest request, HttpServletResponse response) throws Exception {
        return new ModelAndView("wap/search");
    }

    public ModelAndView searchResult(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String query = request.getParameter("query");
        if (!searchService.isIndexCreated()) {
            searchService.createIndex();
        }
        boolean creatingIndex = searchService.isIndexBeingCreated();

        Map<String, Object> map = new HashMap<String, Object>();
        map.put("creatingIndex", creatingIndex);

        if (!creatingIndex) {
            map.put("hits", searchService.heuristicSearch(query, 50, false, false, true, null));
        }

        return new ModelAndView("wap/searchResult", "model", map);
    }

    public ModelAndView settings(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String playerId = (String) request.getSession().getAttribute("playerId");

        Player[] allPlayers = playerService.getAllPlayers();
        User user = securityService.getCurrentUser(request);
        List<Player> players = new ArrayList<Player>();
        Map<String, Object> map = new HashMap<String, Object>();

        for (Player player : allPlayers) {
            // Only display authorized players.
            if (user.isAdminRole() || user.getUsername().equals(player.getUsername())) {
                players.add(player);
            }

        }
        map.put("playerId", playerId);
        map.put("players", players);
        return new ModelAndView("wap/settings", "model", map);
    }

    public ModelAndView selectPlayer(HttpServletRequest request, HttpServletResponse response) throws Exception {
        request.getSession().setAttribute("playerId", request.getParameter("playerId"));
        return settings(request, response);
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

    public void setSearchService(SearchService searchService) {
        this.searchService = searchService;
    }

    public void setSecurityService(SecurityService securityService) {
        this.securityService = securityService;
    }
}
