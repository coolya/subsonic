package net.sourceforge.subsonic.controller;

import net.sourceforge.subsonic.domain.MusicFile;
import net.sourceforge.subsonic.domain.Player;
import net.sourceforge.subsonic.domain.Playlist;
import net.sourceforge.subsonic.service.PlayerService;
import net.sourceforge.subsonic.service.MusicFileService;
import net.sourceforge.subsonic.Logger;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.ParameterizableViewController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.io.IOException;
import java.io.File;

/**
 * Controller for the creating the XSPF playlist, used be the Flash-based embedded player.
 *
 * @author Sindre Mehus
 */
public class XspfPlaylistController extends ParameterizableViewController {

    private static final Logger LOG = Logger.getLogger(XspfPlaylistController.class);

    private PlayerService playerService;
    private MusicFileService musicFileService;

    protected ModelAndView handleRequestInternal(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Player player = playerService.getPlayer(request, response);
        Playlist playlist = player.getPlaylist();

        Map<String, Object> map = new HashMap<String, Object>();
        map.put("songs", getSongs(playlist));
        ModelAndView result = super.handleRequestInternal(request, response);
        result.addObject("model", map);
        return result;
    }

    private List<Song> getSongs(Playlist playlist) {
        List<Song> result = new ArrayList<Song>();

        MusicFile[] files = playlist.getFiles();
        for (MusicFile file : files) {
            Song song = new Song();
            song.setMusicFile(file);
            try {
                List<File> list = musicFileService.getCoverArt(file.getParent(), 1);
                if (!list.isEmpty()) {
                    song.setCoverArtFile(list.get(0));
                }
            } catch (IOException x) {
                LOG.warn("Failed to get cover art for " + file);
            }
            result.add(song);
        }
        return result;
    }

    public void setPlayerService(PlayerService playerService) {
        this.playerService = playerService;
    }

    public void setMusicFileService(MusicFileService musicFileService) {
        this.musicFileService = musicFileService;
    }

    /**
     * Contains information about a single song in the playlist.
     */
    public static class Song {
        private MusicFile musicFile;
        private File coverArtFile;

        public MusicFile getMusicFile() {
            return musicFile;
        }

        public void setMusicFile(MusicFile musicFile) {
            this.musicFile = musicFile;
        }

        public File getCoverArtFile() {
            return coverArtFile;
        }

        public void setCoverArtFile(File coverArtFile) {
            this.coverArtFile = coverArtFile;
        }
    }
}
