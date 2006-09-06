package net.sourceforge.subsonic.controller;

import net.sourceforge.subsonic.domain.*;
import net.sourceforge.subsonic.service.*;
import org.springframework.web.servlet.*;
import org.springframework.web.servlet.mvc.*;

import javax.servlet.http.*;
import java.util.*;

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

        MusicFolder[] allMusicFolders = settingsService.getAllMusicFolders();
        String indexString = settingsService.getIndexString();
        String[] ignoredArticles = settingsService.getIgnoredArticlesAsArray();
        String[] shortcuts = settingsService.getShortcutsAsArray();
        List<MusicIndex> musicIndex = MusicIndex.createIndexesFromExpression(indexString);
        Map<MusicIndex, List<MusicFile>> indexedChildren = MusicIndex.getIndexedChildren(allMusicFolders, musicIndex, ignoredArticles, shortcuts);
        String username = securityService.getCurrentUsername(request);

        map.put("username", username);
        map.put("musicFoldersExist", allMusicFolders.length > 0);
        map.put("indexes", indexedChildren.keySet().toArray(new MusicIndex[0]));

        UserSettings userSettings = settingsService.getUserSettings(username);
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
