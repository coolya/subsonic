package net.sourceforge.subsonic.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.ParameterizableViewController;

import net.sourceforge.subsonic.domain.MusicFolder;
import net.sourceforge.subsonic.domain.User;
import net.sourceforge.subsonic.domain.UserSettings;
import net.sourceforge.subsonic.service.SecurityService;
import net.sourceforge.subsonic.service.SettingsService;
import net.sourceforge.subsonic.service.VersionService;

/**
 * Controller for the top frame.
 *
 * @author Sindre Mehus
 */
public class TopController extends ParameterizableViewController {

    private SettingsService settingsService;
    private VersionService versionService;
    private SecurityService securityService;

    protected ModelAndView handleRequestInternal(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Map<String, Object> map = new HashMap<String, Object>();

        List<MusicFolder> allMusicFolders = settingsService.getAllMusicFolders();
        User user = securityService.getCurrentUser(request);

        map.put("user", user);
        map.put("musicFoldersExist", !allMusicFolders.isEmpty());
        map.put("brand", settingsService.getBrand());

        UserSettings userSettings = settingsService.getUserSettings(user.getUsername());
        if (userSettings.isFinalVersionNotificationEnabled() && versionService.isNewFinalVersionAvailable()) {
            map.put("newVersionAvailable", true);
            map.put("latestVersion", versionService.getLatestFinalVersion());

        } else if (userSettings.isBetaVersionNotificationEnabled() && versionService.isNewBetaVersionAvailable()) {
            map.put("newVersionAvailable", true);
            map.put("latestVersion", versionService.getLatestBetaVersion());
        }

        ModelAndView result = super.handleRequestInternal(request, response);
        result.addObject("model", map);
        return result;
    }

    public void setSettingsService(SettingsService settingsService) {
        this.settingsService = settingsService;
    }

    public void setVersionService(VersionService versionService) {
        this.versionService = versionService;
    }

    public void setSecurityService(SecurityService securityService) {
        this.securityService = securityService;
    }
}
