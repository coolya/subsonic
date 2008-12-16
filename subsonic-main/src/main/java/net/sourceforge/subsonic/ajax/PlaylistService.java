package net.sourceforge.subsonic.ajax;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.directwebremoting.WebContext;
import org.directwebremoting.WebContextFactory;

import net.sourceforge.subsonic.domain.MusicFile;
import net.sourceforge.subsonic.domain.Player;
import net.sourceforge.subsonic.service.PlayerService;
import net.sourceforge.subsonic.service.SettingsService;
import net.sourceforge.subsonic.util.StringUtil;

/**
 * Provides AJAX-enabled services for manipulating the playlist of a player.
 * This class is used by the DWR framework (http://getahead.ltd.uk/dwr/).
 *
 * @author Sindre Mehus
 */
public class PlaylistService {

    private PlayerService playerService;
    private SettingsService settingsService;

    /**
     * Returns the playlist for the player of the current user.
     *
     * @return The playlist.
     */
    public PlaylistInfo getPlaylist() throws Exception {
        WebContext webContext = WebContextFactory.get();
        HttpServletRequest request = webContext.getHttpServletRequest();
        Player player = playerService.getPlayer(request, webContext.getHttpServletResponse());
        String url = request.getRequestURL().toString();

        List<PlaylistInfo.Entry> entries = new ArrayList<PlaylistInfo.Entry>();
        for (MusicFile file : player.getPlaylist().getFiles()) {
            MusicFile.MetaData metaData = file.getMetaData();
            String albumUrl = url.replaceFirst("/dwr/.*", "/main.view?pathUtf8Hex=" +
                    StringUtil.utf8HexEncode(file.getParent().getPath()));

            entries.add(new PlaylistInfo.Entry(metaData.getArtist(), metaData.getAlbum(), metaData.getTitle(), albumUrl));
        }

        return new PlaylistInfo(entries);
    }

    public void setPlayerService(PlayerService playerService) {
        this.playerService = playerService;
    }

    public void setSettingsService(SettingsService settingsService) {
        this.settingsService = settingsService;
    }
}