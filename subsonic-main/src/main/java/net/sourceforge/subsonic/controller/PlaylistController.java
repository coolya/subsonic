package net.sourceforge.subsonic.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.ParameterizableViewController;

import net.sourceforge.subsonic.domain.Player;
import net.sourceforge.subsonic.domain.User;
import net.sourceforge.subsonic.domain.UserSettings;
import net.sourceforge.subsonic.service.PlayerService;
import net.sourceforge.subsonic.service.SecurityService;
import net.sourceforge.subsonic.service.SettingsService;

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

        Map<String, Object> map = new HashMap<String, Object>();
        map.put("user", user);
        map.put("player", player);
        map.put("players", getPlayers(user));
        map.put("visibility", userSettings.getPlaylistVisibility());
        map.put("partyMode", userSettings.isPartyModeEnabled());
        ModelAndView result = super.handleRequestInternal(request, response);
        result.addObject("model", map);
        return result;
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