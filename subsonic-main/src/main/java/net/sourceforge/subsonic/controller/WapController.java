package net.sourceforge.subsonic.controller;

import net.sourceforge.subsonic.domain.*;
import net.sourceforge.subsonic.service.*;
import org.springframework.web.servlet.*;
import org.springframework.web.servlet.mvc.multiaction.*;

import javax.servlet.http.*;
import java.util.*;

/**
 * Multi-controller used for wap pages.
 *
 * @author Sindre Mehus
 */
public class WapController extends MultiActionController {

    private SettingsService settingsService;

    public ModelAndView wapIndex(HttpServletRequest request, HttpServletResponse response) throws Exception {
        return wap(request, response);
    }

    public ModelAndView wap(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Map<String, Object> map = new HashMap<String, Object>();
        MusicFolder[] folders = settingsService.getAllMusicFolders();

        if (folders.length == 0) {
            map.put("noMusic", true);
        } else {

            String indexString = settingsService.getIndexString();
            String[] ignoredArticles = settingsService.getIgnoredArticlesAsArray();
            String[] shortcuts = new String[0];
            Map<MusicIndex, List<MusicFile>> children = MusicIndex.getIndexedChildren(folders, MusicIndex.createIndexesFromExpression(indexString),
                                                                                      ignoredArticles, shortcuts);
            // If an index is given as parameter, only show music files for this index.
            String index = request.getParameter("index");
            if (index != null) {
                List<MusicFile> musicFiles = children.get(new MusicIndex(index));
                if (musicFiles == null) {
                    map.put("noMusic", true);
                } else {
                    map.put("artists", musicFiles);
                }
            }

            // Otherwise, list all indexes.
            else {
                map.put("indexes", children.keySet());
            }
        }

        return new ModelAndView("wapIndex", "model", map);
    }

    public void setSettingsService(SettingsService settingsService) {
        this.settingsService = settingsService;
    }
}
