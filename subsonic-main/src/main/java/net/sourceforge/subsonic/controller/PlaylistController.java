package net.sourceforge.subsonic.controller;

import net.sourceforge.subsonic.domain.MusicFile;
import net.sourceforge.subsonic.domain.Player;
import net.sourceforge.subsonic.domain.Playlist;
import net.sourceforge.subsonic.domain.User;
import net.sourceforge.subsonic.domain.UserSettings;
import net.sourceforge.subsonic.service.MusicFileService;
import net.sourceforge.subsonic.service.PlayerService;
import net.sourceforge.subsonic.service.SecurityService;
import net.sourceforge.subsonic.service.SettingsService;
import net.sourceforge.subsonic.util.StringUtil;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;
import org.springframework.web.servlet.mvc.ParameterizableViewController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Controller for the playlist frame.
 *
 * @author Sindre Mehus
 */
public class PlaylistController extends ParameterizableViewController {

    private PlayerService playerService;
    private SecurityService securityService;
    private SettingsService settingsService;
    private MusicFileService musicFileService;

    @Override
    protected ModelAndView handleRequestInternal(HttpServletRequest request, HttpServletResponse response) throws Exception {

        User user = securityService.getCurrentUser(request);
        UserSettings userSettings = settingsService.getUserSettings(user.getUsername());
        Player player = playerService.getPlayer(request, response);
        Playlist playlist = player.getPlaylist();

        Map<String, Object> map = new HashMap<String, Object>();
        handleParameters(request, playlist, player, map);

        if (userSettings.isWebPlayerDefault()) {
            return new ModelAndView(new RedirectView("webPlayer.view?"));
        }

        map.put("user", user);
        map.put("player", player);
        map.put("songs", getSongs(playlist));
        map.put("players", getPlayers(user));
        map.put("repeatEnabled", playlist.isRepeatEnabled());
        map.put("isPlaying", playlist.getStatus() == Playlist.Status.PLAYING);
        map.put("visibility", userSettings.getPlaylistVisibility());
        map.put("partyMode", userSettings.isPartyModeEnabled());
        ModelAndView result = super.handleRequestInternal(request, response);
        result.addObject("model", map);
        return result;
    }

    private List<Song> getSongs(Playlist playlist) {
        List<Song> result = new ArrayList<Song>();
        MusicFile currentFile = playlist.getCurrentFile();

        MusicFile[] files = playlist.getFiles();
        for (int i = 0; i < files.length; i++) {
            MusicFile file = files[i];
            boolean isCurrent = file.equals(currentFile) && i == playlist.getIndex();
            Song song = new Song();
            song.setMusicFile(file);
            song.setCurrent(isCurrent);
            result.add(song);
        }
        return result;
    }

    private void handleParameters(HttpServletRequest request, Playlist playlist, Player player, Map<String, Object> map) throws IOException {

        // Whether a new M3U file should be sent, forcing the remote player to reconnect.
        boolean sendM3U = false;
        boolean serverSidePlaylist = !player.isClientSidePlaylist();

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
            MusicFile file = musicFileService.getMusicFile(request.getParameter("play"));
            playlist.addFile(file, false);
            playlist.setRandomSearchCriteria(null);
        } else if (request.getParameter("add") != null) {
            MusicFile file = musicFileService.getMusicFile(request.getParameter("add"));
            playlist.addFile(file);
            index = playlist.size() - 1;
            playlist.setRandomSearchCriteria(null);
        } else if (request.getParameter("clear") != null) {
            sendM3U = serverSidePlaylist;
            playlist.clear();
            playlist.setRandomSearchCriteria(null);
        } else if (request.getParameter("shuffle") != null) {
            index = -1;
            playlist.shuffle();
        } else if (request.getParameter("sortByTrack") != null) {
            index = -1;
            playlist.sort(Playlist.SortOrder.TRACK);
        } else if (request.getParameter("sortByArtist") != null) {
            index = -1;
            playlist.sort(Playlist.SortOrder.ARTIST);
        } else if (request.getParameter("sortByAlbum") != null) {
            index = -1;
            playlist.sort(Playlist.SortOrder.ALBUM);
        } else if (request.getParameter("repeat") != null) {
            index = -1;
            playlist.setRepeatEnabled(!playlist.isRepeatEnabled());
        } else if (request.getParameter("skip") != null) {
            sendM3U = serverSidePlaylist;
            playlist.setIndex(Integer.parseInt(request.getParameter("skip")));
        } else if (request.getParameter("remove") != null) {
            int[] indexes = StringUtil.parseInts(request.getParameter("remove"));
            if (indexes.length > 0) {
                index = indexes[0];
            }
            for (int i = indexes.length - 1; i >= 0; i--) {
                playlist.removeFileAt(indexes[i]);
            }
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
            sendM3U = serverSidePlaylist;
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

    private List<Player> getPlayers(User user) {
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

    public void setMusicFileService(MusicFileService musicFileService) {
        this.musicFileService = musicFileService;
    }

    /** Contains information about a single song in the playlist. */
    public static class Song {
        private MusicFile musicFile;
        private boolean isCurrent;

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
    }
}