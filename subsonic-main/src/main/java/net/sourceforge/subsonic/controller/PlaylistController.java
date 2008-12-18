package net.sourceforge.subsonic.controller;

import net.sourceforge.subsonic.domain.Player;
import net.sourceforge.subsonic.domain.Playlist;
import net.sourceforge.subsonic.domain.User;
import net.sourceforge.subsonic.domain.UserSettings;
import net.sourceforge.subsonic.service.PlayerService;
import net.sourceforge.subsonic.service.SecurityService;
import net.sourceforge.subsonic.service.SettingsService;
import net.sourceforge.subsonic.util.StringUtil;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.ParameterizableViewController;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
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

    @Override
    protected ModelAndView handleRequestInternal(HttpServletRequest request, HttpServletResponse response) throws Exception {

        User user = securityService.getCurrentUser(request);
        UserSettings userSettings = settingsService.getUserSettings(user.getUsername());
        Player player = playerService.getPlayer(request, response);
        Playlist playlist = player.getPlaylist();

        Map<String, Object> map = new HashMap<String, Object>();
        handleParameters(request, playlist);

        if (userSettings.isWebPlayerDefault()) {
            return new ModelAndView(new RedirectView("webPlayer.view?"));
        }

        map.put("user", user);
        map.put("player", player);
        map.put("players", getPlayers(user));
        map.put("visibility", userSettings.getPlaylistVisibility());
        map.put("partyMode", userSettings.isPartyModeEnabled());
        ModelAndView result = super.handleRequestInternal(request, response);
        result.addObject("model", map);
        return result;
    }

    private void handleParameters(HttpServletRequest request, Playlist playlist) throws Exception {
        if (request.getParameter("remove") != null) {
            int[] indexes = StringUtil.parseInts(request.getParameter("remove"));
            for (int i = indexes.length - 1; i >= 0; i--) {
                playlist.removeFileAt(indexes[i]);
            }
        }
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
}