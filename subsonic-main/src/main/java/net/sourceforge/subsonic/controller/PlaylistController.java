package net.sourceforge.subsonic.controller;

import net.sourceforge.subsonic.domain.*;
import net.sourceforge.subsonic.service.*;
import net.sourceforge.subsonic.util.*;
import org.springframework.web.servlet.*;
import org.springframework.web.servlet.mvc.*;

import javax.servlet.http.*;
import java.util.*;
import java.io.*;

/**
 * Controller for the playlist frame.
 *
 * @author Sindre Mehus
 */
public class PlaylistController extends ParameterizableViewController {

    private PlayerService playerService;
    private SecurityService securityService;

    protected ModelAndView handleRequestInternal(HttpServletRequest request, HttpServletResponse response) throws Exception {

        User user = securityService.getCurrentUser(request);
        Player player = playerService.getPlayer(request, response);
        Playlist playlist = player.getPlaylist();

        Map<String, Object> map = new HashMap<String, Object>();
        handleParameters(request, playlist, player, map);

        map.put("user", user);
        map.put("player", player);
        map.put("songs", getSongs(playlist));
        map.put("players", getPlayers(user));
        map.put("repeatEnabled", playlist.isRepeatEnabled());
        map.put("isPlaying", playlist.getStatus() == Playlist.Status.PLAYING);
        ModelAndView result = super.handleRequestInternal(request, response);
        result.addObject("model", map);
        return result;
    }

    private List<Song> getSongs(Playlist playlist) throws IOException {
        List<Song> result = new ArrayList<Song>();
        MusicFile currentFile = playlist.getCurrentFile();

        MusicFile[] files = playlist.getFiles();
        for (int i = 0; i < files.length; i++) {
            MusicFile file = files[i];
            Song song = new Song();
            song.setPath(file.getPath());
            song.setParentPath(file.getParent().getPath());
            song.setArtistAlbumYear(getArtistAlbumYear(file.getMetaData()));
            song.setTitle(file.getTitle());
            song.setBitRate(file.getBitRate());
            song.setVariableBitRate(file.isVariableBitRate());

            boolean isCurrent = file.equals(currentFile) && i == playlist.getIndex();
            song.setCurrent(isCurrent);
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

    private String getArtistAlbumYear(MusicFile.MetaData metaData) {

        String artist = metaData.getArtist();
        String album  = metaData.getAlbum();
        String year   = metaData.getYear();

        if ("".equals(artist)) { artist = null; }
        if ("".equals(album)) { album = null; }
        if ("".equals(year)) { year = null; }

        StringBuffer buf = new StringBuffer();

        if (artist != null) {
            buf.append("<em>").append(StringUtil.toHtml(artist)).append("</em>");
        }

        if (artist != null && album != null) {
            buf.append(" - ");
        }

        if (album != null) {
            buf.append(StringUtil.toHtml(album));
        }

        if (year != null) {
            buf.append(" (").append(StringUtil.toHtml(year)).append(')');
        }

        return buf.toString();
    }

    public void setPlayerService(PlayerService playerService) {
        this.playerService = playerService;
    }

    public void setSecurityService(SecurityService securityService) {
        this.securityService = securityService;
    }

    /**
     * Contains information about a single song in the playlist.
     */
    public static class Song {
        private String title;
        private String artistAlbumYear;
        private int bitRate;
        private boolean isVariableBitRate;
        private String path;
        private String parentPath;
        private boolean isCurrent;

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getArtistAlbumYear() {
            return artistAlbumYear;
        }

        public void setArtistAlbumYear(String artistAlbumYear) {
            this.artistAlbumYear = artistAlbumYear;
        }

        public String getPath() {
            return path;
        }

        public void setPath(String path) {
            this.path = path;
        }

        public String getParentPath() {
            return parentPath;
        }

        public void setParentPath(String parentPath) {
            this.parentPath = parentPath;
        }

        public boolean isCurrent() {
            return isCurrent;
        }

        public void setCurrent(boolean current) {
            isCurrent = current;
        }

        public int getBitRate() {
            return bitRate;
        }

        public void setBitRate(int bitRate) {
            this.bitRate = bitRate;
        }

        public boolean isVariableBitRate() {
            return isVariableBitRate;
        }

        public void setVariableBitRate(boolean variableBitRate) {
            isVariableBitRate = variableBitRate;
        }

    }
}
