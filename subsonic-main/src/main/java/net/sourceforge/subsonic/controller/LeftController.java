package net.sourceforge.subsonic.controller;

import net.sourceforge.subsonic.service.*;
import net.sourceforge.subsonic.domain.*;
import net.sourceforge.subsonic.util.*;
import org.springframework.web.servlet.*;
import org.springframework.web.servlet.support.*;
import org.springframework.web.servlet.mvc.*;

import javax.servlet.http.*;
import java.util.*;
import java.io.*;

/**
 * Controller for the left index frame.
 *
 * @author Sindre Mehus
 */
public class LeftController extends ParameterizableViewController {
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
        String[] quickLinks = settingsService.getQuickLinksAsArray();
        List<MusicIndex> musicIndex = MusicIndex.createIndexesFromExpression(indexString);
        Map<MusicIndex, List<MusicFile>> indexedChildren = MusicIndex.getIndexedChildren(musicFoldersToUse, musicIndex, ignoredArticles, quickLinks);

        map.put("musicFolders", allMusicFolders);
        map.put("selectedMusicFolder", selectedMusicFolder);
        map.put("radios", settingsService.getAllInternetRadios());
        map.put("quickLinks", getQuickLinks(musicFoldersToUse, quickLinks));

        if (statistics != null) {
            map.put("statistics", statistics);
            long bytes = statistics.getTotalLengthInBytes();
            long hours = bytes * 8 / 1024 / 150 / 3600;
            map.put("hours", hours);
            map.put("bytes", StringUtil.formatBytes(bytes, locale));
        }

        map.put("indexedChildren", indexedChildren);
        map.put("downloadEnabled", securityService.getCurrentUser(request).isDownloadRole());

        ModelAndView result = super.handleRequestInternal(request, response);
        result.addObject("model", map);
        return result;
    }

    private List<MusicFile> getQuickLinks(MusicFolder[] musicFoldersToUse, String[] quickLinks) {
        List<MusicFile> result = new ArrayList<MusicFile>();

        for (String link : quickLinks) {
            for (MusicFolder musicFolder : musicFoldersToUse) {
                File file = new File(musicFolder.getPath(), link);
                if (file.exists()) {
                    result.add(new MusicFile(file));
                }
            }
        }

        return result;
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
