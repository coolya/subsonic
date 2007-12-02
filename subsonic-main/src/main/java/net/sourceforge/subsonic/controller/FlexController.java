package net.sourceforge.subsonic.controller;

import net.sourceforge.subsonic.domain.MusicFile;
import net.sourceforge.subsonic.domain.MusicFolder;
import net.sourceforge.subsonic.domain.MusicIndex;
import net.sourceforge.subsonic.service.MusicFileService;
import net.sourceforge.subsonic.service.SettingsService;
import net.sourceforge.subsonic.util.StringUtil;
import net.sourceforge.subsonic.util.XMLBuilder;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.util.List;
import java.util.Map;

/**
 * Multi-controller used for remote Flex services.
 *
 * @author Sindre Mehus
 */
public class FlexController extends MultiActionController {

    private MusicFileService musicFileService;
    private SettingsService settingsService;

    public ModelAndView getArtists(HttpServletRequest request, HttpServletResponse response) throws Exception {

        MusicFolder[] allMusicFolders = settingsService.getAllMusicFolders();
        String indexString = settingsService.getIndexString();
        String[] ignoredArticles = settingsService.getIgnoredArticlesAsArray();
        String[] shortcuts = settingsService.getShortcutsAsArray();
        List<MusicIndex> musicIndex = MusicIndex.createIndexesFromExpression(indexString);
        Map<MusicIndex, List<MusicFile>> indexedChildren = MusicIndex.getIndexedChildren(allMusicFolders, musicIndex, ignoredArticles, shortcuts);

        // TODO: XML preamble
        XMLBuilder builder = new XMLBuilder();
        builder.add("artists");

        for (List<MusicFile> list : indexedChildren.values()) {
            for (MusicFile musicFile : list) {
                builder.add("artist");
                builder.add("name", StringUtil.toHtml(musicFile.getName()));
                builder.add("path", StringUtil.toHtml(musicFile.getPath()));
                builder.end();
            }
        }

        builder.end();

        PrintWriter writer = response.getWriter();
        writer.print(builder.toString());

        return null;
    }

    public void setMusicFileService(MusicFileService musicFileService) {
        this.musicFileService = musicFileService;
    }

    public void setSettingsService(SettingsService settingsService) {
        this.settingsService = settingsService;
    }
}
