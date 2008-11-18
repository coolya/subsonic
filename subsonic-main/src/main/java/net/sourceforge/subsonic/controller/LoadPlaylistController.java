package net.sourceforge.subsonic.controller;

import net.sourceforge.subsonic.service.*;
import net.sourceforge.subsonic.domain.*;
import net.sourceforge.subsonic.util.StringUtil;
import org.springframework.web.servlet.*;
import org.springframework.web.servlet.mvc.multiaction.*;
import org.springframework.web.servlet.view.*;

import javax.servlet.http.*;
import java.io.*;
import java.util.*;

/**
 * Controller for listing, loading, appending and deleting playlists.
 *
 * @author Sindre Mehus
 */
public class LoadPlaylistController extends MultiActionController {

    private PlaylistService playlistService;
    private SecurityService securityService;
    private PlayerService playerService;

    public ModelAndView loadPlaylist(HttpServletRequest request, HttpServletResponse response) {
        return loadOrAppendPlaylist(request, true);
    }

    public ModelAndView appendPlaylist(HttpServletRequest request, HttpServletResponse response) {
        return loadOrAppendPlaylist(request, false);
    }

    private ModelAndView loadOrAppendPlaylist(HttpServletRequest request, boolean load) {
        Map<String, Object> map = new HashMap<String, Object>();
        List<String> playlistNames = new ArrayList<String>();

        if (playlistService.getPlaylistDirectory().exists()) {
            File[] playlists = playlistService.getSavedPlaylists();
            for (File file : playlists) {
                playlistNames.add(file.getName());
            }
        }

        map.put("load", load);
        map.put("songIndexes", request.getParameter("indexes"));
        map.put("playlistDirectory", playlistService.getPlaylistDirectory());
        map.put("playlistDirectoryExists", playlistService.getPlaylistDirectory().exists());
        map.put("playlists", playlistNames);
        map.put("user", securityService.getCurrentUser(request));
        return new ModelAndView("loadPlaylist", "model", map);
    }

    public ModelAndView loadPlaylistConfirm(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Player player = playerService.getPlayer(request, response);
        Playlist playlist = player.getPlaylist();

        String name = request.getParameter("name");
        playlistService.loadPlaylist(playlist, name);

        return reload();
    }

    public ModelAndView appendPlaylistConfirm(HttpServletRequest request, HttpServletResponse response) throws IOException {

        // Load the existing playlist.
        Playlist savedPlaylist = new Playlist();
        String name = request.getParameter("name");
        playlistService.loadPlaylist(savedPlaylist, name);

        // Update the existing playlist with new entries.
        Player player = playerService.getPlayer(request, response);
        Playlist playlist = player.getPlaylist();
        int[] indexes = StringUtil.parseInts(request.getParameter("indexes"));
        for (int index : indexes) {
            savedPlaylist.addFile(playlist.getFile(index));
        }

        // Save the playlist again.
        playlistService.savePlaylist(savedPlaylist);

        return reload();
    }

    private ModelAndView reload() {
        List<ReloadFrame> reloadFrames = new ArrayList<ReloadFrame>();
        reloadFrames.add(new ReloadFrame("playlist", "playlist.view?"));
        reloadFrames.add(new ReloadFrame("main", "nowPlaying.view?"));

        Map<String, Object> map = new HashMap<String, Object>();
        map.put("reloadFrames", reloadFrames);

        return new ModelAndView("reload", "model", map);
    }

    public ModelAndView deletePlaylist(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String name = request.getParameter("name");
        playlistService.deletePlaylist(name);

        return new ModelAndView(new RedirectView("loadPlaylist.view?"));
    }

    public void setPlaylistService(PlaylistService playlistService) {
        this.playlistService = playlistService;
    }

    public void setSecurityService(SecurityService securityService) {
        this.securityService = securityService;
    }

    public void setPlayerService(PlayerService playerService) {
        this.playerService = playerService;
    }
}
