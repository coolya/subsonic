package net.sourceforge.subsonic.controller;

import net.sourceforge.subsonic.domain.MusicFile;
import net.sourceforge.subsonic.domain.MusicFolder;
import net.sourceforge.subsonic.domain.MusicIndex;
import net.sourceforge.subsonic.domain.Player;
import net.sourceforge.subsonic.domain.Playlist;
import net.sourceforge.subsonic.domain.RandomSearchCriteria;
import net.sourceforge.subsonic.domain.User;
import net.sourceforge.subsonic.service.MusicFileService;
import net.sourceforge.subsonic.service.MusicIndexService;
import net.sourceforge.subsonic.service.PlayerService;
import net.sourceforge.subsonic.service.PlaylistService;
import net.sourceforge.subsonic.service.SearchService;
import net.sourceforge.subsonic.service.SecurityService;
import net.sourceforge.subsonic.service.SettingsService;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.SortedSet;

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
    private MusicFileService musicFileService;
    private MusicIndexService musicIndexService;

    public ModelAndView index(HttpServletRequest request, HttpServletResponse response) throws Exception {
        return wap(request, response);
    }

    public ModelAndView wap(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Map<String, Object> map = new HashMap<String, Object>();
        MusicFolder[] folders = settingsService.getAllMusicFolders();

        if (folders.length == 0) {
            map.put("noMusic", true);
        } else {

            SortedMap<MusicIndex, SortedSet<MusicIndex.Artist>> allArtists = musicIndexService.getIndexedArtists(folders);

            // If an index is given as parameter, only show music files for this index.
            String index = request.getParameter("index");
            if (index != null) {
                SortedSet<MusicIndex.Artist> artists = allArtists.get(new MusicIndex(index));
                if (artists == null) {
                    map.put("noMusic", true);
                } else {
                    map.put("artists", artists);
                }
            }

            // Otherwise, list all indexes.
            else {
                map.put("indexes", allArtists.keySet());
            }
        }

        return new ModelAndView("wap/index", "model", map);
    }

    public ModelAndView browse(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String path = request.getParameter("path");
        MusicFile parent = musicFileService.getMusicFile(path);

        // Create array of file(s) to display.
        List<MusicFile> children;
        if (parent.isDirectory()) {
            children = parent.getChildren(true, true);
        } else {
            children = new ArrayList<MusicFile>();
            children.add(parent);
        }

        Map<String, Object> map = new HashMap<String, Object>();
        map.put("parent", parent);
        map.put("children", children);
        map.put("user", securityService.getCurrentUser(request));

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
                players = new Player[]{player};
            }
        }

        Map<String, Object> map = new HashMap<String, Object>();

        for (Player player : players) {
            Playlist playlist = player.getPlaylist();
            map.put("playlist", playlist);

            if (request.getParameter("play") != null) {
                MusicFile file = musicFileService.getMusicFile(request.getParameter("play"));
                playlist.addFile(file, false);
            } else if (request.getParameter("add") != null) {
                MusicFile file = musicFileService.getMusicFile(request.getParameter("add"));
                playlist.addFile(file);
            } else if (request.getParameter("skip") != null) {
                playlist.setIndex(Integer.parseInt(request.getParameter("skip")));
            } else if (request.getParameter("clear") != null) {
                playlist.clear();
            } else if (request.getParameter("load") != null) {
                playlistService.loadPlaylist(playlist, request.getParameter("load"));
            } else if (request.getParameter("random") != null) {
                List<MusicFile> randomFiles = searchService.getRandomSongs(new RandomSearchCriteria(20, null, null, null, null));
                playlist.clear();
                for (MusicFile randomFile : randomFiles) {
                    playlist.addFile(randomFile);
                }
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
        boolean creatingIndex = searchService.isIndexBeingCreated();

        Map<String, Object> map = new HashMap<String, Object>();
        map.put("creatingIndex", creatingIndex);

        if (!creatingIndex) {
            map.put("hits", searchService.heuristicSearch(query, 50, false, false, true, null));
        }

        return new ModelAndView("wap/searchResult", "model", map);
    }

    public ModelAndView settings(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String playerId = (String) request.getSession().getAttribute("player");

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
        request.getSession().setAttribute("player", request.getParameter("playerId"));
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

    public void setMusicFileService(MusicFileService musicFileService) {
        this.musicFileService = musicFileService;
    }

    public void setMusicIndexService(MusicIndexService musicIndexService) {
        this.musicIndexService = musicIndexService;
    }
}
