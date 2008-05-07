package net.sourceforge.subsonic.ajax;

import net.sourceforge.subsonic.domain.MusicFile;
import net.sourceforge.subsonic.domain.Player;
import net.sourceforge.subsonic.domain.TransferStatus;
import net.sourceforge.subsonic.domain.UserSettings;
import net.sourceforge.subsonic.service.MusicFileService;
import net.sourceforge.subsonic.service.PlayerService;
import net.sourceforge.subsonic.service.SettingsService;
import net.sourceforge.subsonic.service.StatusService;
import net.sourceforge.subsonic.util.StringUtil;
import org.apache.commons.lang.StringUtils;
import uk.ltd.getahead.dwr.WebContext;
import uk.ltd.getahead.dwr.WebContextFactory;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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
    private SettingsService settingsService;

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

        HttpServletRequest request = WebContextFactory.get().getHttpServletRequest();
        String url = request.getRequestURL().toString();

        List<NowPlayingInfo> result = new ArrayList<NowPlayingInfo>();
        for (TransferStatus status : statusService.getAllStreamStatuses()) {

            Player player = status.getPlayer();
            File file = status.getFile();

            if (player != null && player.getUsername() != null && file != null) {

                String username = player.getUsername();
                UserSettings userSettings = settingsService.getUserSettings(username);
                if (!userSettings.isNowPlayingAllowed()) {
                    continue;
                }

                MusicFile musicFile = musicFileService.getMusicFile(file);
                List<File> coverArts = musicFileService.getCoverArt(musicFile.getParent(), 1);

                String artist = musicFile.getMetaData().getArtist();
                String title = musicFile.getMetaData().getTitle();
                String albumUrl = url.replaceFirst("/dwr/.*", "/main.view?pathUtf8Hex=" +
                                                              StringUtil.utf8HexEncode(musicFile.getParent().getPath()));
                String lyricsUrl = url.replaceFirst("/dwr/.*", "/lyrics.view?artistUtf8Hex=" +
                                                               StringUtil.utf8HexEncode(musicFile.getMetaData().getArtist()) +
                                                               "&songUtf8Hex=" +
                                                               StringUtil.utf8HexEncode(musicFile.getMetaData().getTitle()));
                String coverArtUrl = coverArts.isEmpty() ? null :
                                     url.replaceFirst("/dwr/.*", "/coverArt.view?size=32&pathUtf8Hex=" +
                                                                 StringUtil.utf8HexEncode(coverArts.get(0).getPath()));

                // Rewrite URLs in case we're behind a proxy.
                if (settingsService.isRewriteUrlEnabled()) {
                    String referer = request.getHeader("referer");
                    albumUrl = StringUtil.rewriteUrl(albumUrl, referer);
                    lyricsUrl = StringUtil.rewriteUrl(lyricsUrl, referer);
                    coverArtUrl = StringUtil.rewriteUrl(coverArtUrl, referer);
                }

                String tooltip = artist + " &ndash; " + title;

                if (StringUtils.isNotBlank(player.getName())) {
                    username += "@" + player.getName();
                }
                artist = StringUtils.abbreviate(artist, 25);
                title = StringUtils.abbreviate(title, 25);
                username = StringUtils.abbreviate(username, 25);

                result.add(new NowPlayingInfo(username, artist, title, tooltip, albumUrl, lyricsUrl, coverArtUrl));
            }
        }

        return result.toArray(new NowPlayingInfo[result.size()]);
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

    public void setSettingsService(SettingsService settingsService) {
        this.settingsService = settingsService;
    }
}
