package net.sourceforge.subsonic.controller;

import net.sourceforge.subsonic.domain.*;
import net.sourceforge.subsonic.service.*;
import net.sourceforge.subsonic.util.*;
import org.springframework.web.servlet.*;
import org.springframework.web.servlet.support.*;
import org.springframework.web.servlet.mvc.*;

import javax.servlet.http.*;
import java.io.*;
import java.util.*;

/**
 * Controller for the playlist frame.
 *
 * @author Sindre Mehus
 */
public class PlaylistController extends ParameterizableViewController {

    private PlayerService playerService;
    private SecurityService securityService;
    private SettingsService settingsService;

    protected ModelAndView handleRequestInternal(HttpServletRequest request, HttpServletResponse response) throws Exception {

        User user = securityService.getCurrentUser(request);
        Player player = playerService.getPlayer(request, response);
        Playlist playlist = player.getPlaylist();

        Map<String, Object> map = new HashMap<String, Object>();
        handleParameters(request, playlist, player, map);

        map.put("user", user);
        map.put("player", player);
        map.put("songs", getSongs(playlist, request));
        map.put("players", getPlayers(user));
        map.put("repeatEnabled", playlist.isRepeatEnabled());
        map.put("isPlaying", playlist.getStatus() == Playlist.Status.PLAYING);
        map.put("visibility", settingsService.getUserSettings(user.getUsername()).getPlaylistVisibility());
        ModelAndView result = super.handleRequestInternal(request, response);
        result.addObject("model", map);
        return result;
    }

    private List<Song> getSongs(Playlist playlist, HttpServletRequest request) {
        List<Song> result = new ArrayList<Song>();
        MusicFile currentFile = playlist.getCurrentFile();
        Locale locale = RequestContextUtils.getLocale(request);

        MusicFile[] files = playlist.getFiles();
        for (int i = 0; i < files.length; i++) {
            MusicFile file = files[i];
            boolean isCurrent = file.equals(currentFile) && i == playlist.getIndex();
            Song song = new Song();
            song.setMusicFile(file);
            song.setCurrent(isCurrent);
            song.setSize(StringUtil.formatBytes(file.length(), locale));
            result.add(song);
        }
        return result;
    }

    private void handleParameters(HttpServletRequest request, Playlist playlist, Player player, Map<String, Object> map) throws IOException {

        // Whether a new M3U file should be sent, forcing the remote player to reconnect.
        boolean sendM3U = false;

        // The index of interest. Either the index of the currently playing song, or the index of the song
        // the user has done an operation on (add, remove, move up/down, skip).  Used to jump to
        // the right place on the page.
        int index = -2;

        if (request.getParameter("start") != null) {
            index = -1;
            sendM3U = true;
            playlist.setStatus(Playlist.Status.PLAYING);
        } else if (request.getParameter("stop") != null) {
            index = -1;
            sendM3U = true;
            playlist.setStatus(Playlist.Status.STOPPED);
        } else if (request.getParameter("play") != null) {
            sendM3U = true;
            MusicFile file = new MusicFile(request.getParameter("play"));
            playlist.addFile(file, false);
        } else if (request.getParameter("add") != null) {
            MusicFile file = new MusicFile(request.getParameter("add"));
            playlist.addFile(file);
            index = playlist.size() - 1;
        } else if (request.getParameter("clear") != null) {
            sendM3U = true;
            playlist.clear();
        } else if (request.getParameter("shuffle") != null) {
            index = -1;
            playlist.shuffle();
        } else if (request.getParameter("repeat") != null) {
            index = -1;
            playlist.setRepeatEnabled(!playlist.isRepeatEnabled());
        } else if (request.getParameter("skip") != null) {
            sendM3U = true;
            playlist.setIndex(Integer.parseInt(request.getParameter("skip")));
        } else if (request.getParameter("remove") != null) {
            index = Integer.parseInt(request.getParameter("remove"));
            playlist.removeFileAt(index);
        } else if (request.getParameter("up") != null) {
            index = Integer.parseInt(request.getParameter("up"));
            playlist.moveUp(index);
            index--;
        } else if (request.getParameter("down") != null) {
            index = Integer.parseInt(request.getParameter("down"));
            playlist.moveDown(index);
            index++;
        } else if (request.getParameter("undo") != null) {
            index = -1;
            sendM3U = true;
            playlist.undo();
        }

        if (index == -2) {
            index = playlist.getIndex();
        }
        String anchor = "#" + Math.max(-1, index);

        boolean isCurrentPlayer = player.getIpAddress() != null && player.getIpAddress().equals(request.getRemoteAddr());

        map.put("sendM3U", player.isAutoControlEnabled() && isCurrentPlayer && sendM3U);
        map.put("anchor", anchor);
    }

    private List<Player> getPlayers(User user){
        List<Player> result = new ArrayList<Player>();
        for (Player player : playerService.getAllPlayers()) {

            // Only display authorized players.
            if (user.isAdminRole() || user.getUsername().equals(player.getUsername())) {
                result.add(player);
            }
        }
        return result;
    }

    public void setPlayerService(PlayerService playerService) {
        this.playerService = playerService;
    }

    public void setSecurityService(SecurityService securityService) {
        this.securityService = securityService;
    }

    public void setSettingsService(SettingsService settingsService) {
        this.settingsService = settingsService;
    }

    /**
     * Contains information about a single song in the playlist.
     */
    public static class Song {
        private MusicFile musicFile;
        private boolean isCurrent;
        private String size;

        public MusicFile getMusicFile() {
            return musicFile;
        }

        public void setMusicFile(MusicFile musicFile) {
            this.musicFile = musicFile;
        }

        public boolean isCurrent() {
            return isCurrent;
        }

        public void setCurrent(boolean current) {
            isCurrent = current;
        }

        public String getSize() {
            return size;
        }

        public void setSize(String size) {
            this.size = size;
        }
    }
}