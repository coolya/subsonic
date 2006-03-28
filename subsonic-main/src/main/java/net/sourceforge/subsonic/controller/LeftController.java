package net.sourceforge.subsonic.controller;

import net.sourceforge.subsonic.service.*;
import net.sourceforge.subsonic.domain.*;
import net.sourceforge.subsonic.util.*;
import org.springframework.web.servlet.*;
import org.springframework.web.servlet.support.*;
import org.springframework.web.servlet.mvc.*;

import javax.servlet.http.*;
import java.util.*;

/**
 * Controller for the left index page.
 *
 * @author Sindre Mehus
 */
public class LeftController extends AbstractController {
    private SearchService searchService;
    private SettingsService settingsService;
    private SecurityService securityService;

    protected ModelAndView handleRequestInternal(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Map<String, Object> map = new HashMap<String, Object>();

        MediaLibraryStatistics statistics = searchService.getStatistics();
        Locale locale = RequestContextUtils.getLocale(request);

        MusicFolder[] allMusicFolders = settingsService.getAllMusicFolders();
        MusicFolder selectedMusicFolder = null;
        if (request.getParameter("musicFolderId") != null) {
            int musicFolderId = Integer.parseInt(request.getParameter("musicFolderId"));
            for (MusicFolder musicFolder : allMusicFolders) {
                if (musicFolderId == musicFolder.getId()) {
                    selectedMusicFolder = musicFolder;
                    break;
                }
            }
        }
        MusicFolder[] musicFoldersToUse = selectedMusicFolder == null ? allMusicFolders : new MusicFolder[] {selectedMusicFolder};
        String indexString = settingsService.getIndexString();
        String[] ignoredArticles = settingsService.getIgnoredArticlesAsArray();
        List<MusicIndex> musicIndex = MusicIndex.createIndexesFromExpression(indexString);
        Map<MusicIndex, List<MusicFile>> indexedChildren = MusicIndex.getIndexedChildren(musicFoldersToUse, musicIndex, ignoredArticles);

        map.put("musicFolders", allMusicFolders);
        map.put("selectedMusicFolder", selectedMusicFolder);
        map.put("radios", settingsService.getAllInternetRadios());
        map.put("ignoredArticles", settingsService.getIgnoredArticlesAsArray());
        if (statistics != null) {
            map.put("statistics", statistics);
            long bytes = statistics.getTotalLengthInBytes();
            long hours = bytes * 8 / 1024 / 150 / 3600;
            map.put("hours", hours);
            map.put("bytes", StringUtil.formatBytes(bytes, locale));
        }

        map.put("indexedChildren", indexedChildren);
        map.put("downloadEnabled", securityService.getCurrentUser(request).isDownloadRole());

        return new ModelAndView("left", "model", map);
    }

    public void setSearchService(SearchService searchService) {
        this.searchService = searchService;
    }

    public void setSettingsService(SettingsService settingsService) {
        this.settingsService = settingsService;
    }

    public void setSecurityService(SecurityService securityService) {
        this.securityService = securityService;
    }
}
