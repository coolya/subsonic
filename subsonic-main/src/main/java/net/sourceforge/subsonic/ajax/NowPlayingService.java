package net.sourceforge.subsonic.ajax;

import net.sourceforge.subsonic.domain.MusicFile;
import net.sourceforge.subsonic.domain.Player;
import net.sourceforge.subsonic.domain.TransferStatus;
import net.sourceforge.subsonic.service.MusicFileService;
import net.sourceforge.subsonic.service.PlayerService;
import net.sourceforge.subsonic.service.StatusService;
import net.sourceforge.subsonic.util.StringUtil;
import uk.ltd.getahead.dwr.WebContext;
import uk.ltd.getahead.dwr.WebContextFactory;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

/**
 * Provides AJAX-enabled services for retrieving the currently playing file and directory.
 * This class is used by the DWR framework (http://getahead.ltd.uk/dwr/).
 *
 * @author Sindre Mehus
 */
public class NowPlayingService {

    private PlayerService playerService;
    private StatusService statusService;
    private MusicFileService musicFileService;

    /**
     * Returns the path of the currently playing file (for the current user).
     *
     * @return The path of the currently playing file, or the string <code>"nil"</code> if no file is playing.
     */
    public String getFile() {
        MusicFile current = getCurrentMusicFile();
        return current == null ? "nil" : current.getPath();
    }

    /**
     * Returns the path of the directory of the currently playing file (for the current user).
     *
     * @return The path of the directory of the currently playing file, or the string <code>"nil"</code> if no file is playing.
     */
    public String getDirectory() throws IOException {
        MusicFile current = getCurrentMusicFile();
        return current == null || current.getParent() == null ? "nil" : current.getParent().getPath();
    }

    /**
     * Returns details about what all users are currently playing.
     *
     * @return Details about what all users are currently playing.
     */
    public NowPlayingInfo[] getNowPlaying() throws Exception {

        WebContext webContext = WebContextFactory.get();
        String url = webContext.getHttpServletRequest().getRequestURL().toString();

        List<NowPlayingInfo> result = new ArrayList<NowPlayingInfo>();
        for (TransferStatus status : statusService.getAllStreamStatuses()) {

            Player player = status.getPlayer();
            File file = status.getFile();

            if (player != null && player.getUsername() != null && file != null) {
                MusicFile musicFile = musicFileService.getMusicFile(file);
                String artist = musicFile.getMetaData().getArtist();
                String title = musicFile.getMetaData().getTitle();
                String albumUrl = url.replaceFirst("/dwr/.*", "/main.view?pathUtf8Hex=" +
                                                              StringUtil.utf8HexEncode(musicFile.getParent().getPath()));

                String tooltip = artist + " &ndash; " + title;
                artist = StringUtils.abbreviate(artist, 30);
                title = StringUtils.abbreviate(title, 30);

                result.add(new NowPlayingInfo(player.getUsername(), artist, title, tooltip, albumUrl));
            }
        }

        return result.toArray(new NowPlayingInfo[0]);
    }

    private MusicFile getCurrentMusicFile() {
        WebContext webContext = WebContextFactory.get();
        Player player = playerService.getPlayer(webContext.getHttpServletRequest(), webContext.getHttpServletResponse());
        return player.getPlaylist().getCurrentFile();
    }

    public void setPlayerService(PlayerService playerService) {
        this.playerService = playerService;
    }

    public void setStatusService(StatusService statusService) {
        this.statusService = statusService;
    }

    public void setMusicFileService(MusicFileService musicFileService) {
        this.musicFileService = musicFileService;
    }
}
