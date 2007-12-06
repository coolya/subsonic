package net.sourceforge.subsonic.controller;

import net.sourceforge.subsonic.domain.MusicFile;
import net.sourceforge.subsonic.domain.MusicFolder;
import net.sourceforge.subsonic.domain.MusicIndex;
import net.sourceforge.subsonic.service.MusicFileService;
import net.sourceforge.subsonic.service.SettingsService;
import net.sourceforge.subsonic.util.StringUtil;
import net.sourceforge.subsonic.util.XMLBuilder;
import net.sourceforge.subsonic.Logger;
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

    private static final Logger LOG = Logger.getLogger(FlexController.class);

    private MusicFileService musicFileService;
    private SettingsService settingsService;

    public ModelAndView getArtists(HttpServletRequest request, HttpServletResponse response) throws Exception {

        MusicFolder[] allMusicFolders = settingsService.getAllMusicFolders();
        String indexString = settingsService.getIndexString();
        String[] ignoredArticles = settingsService.getIgnoredArticlesAsArray();
        String[] shortcuts = settingsService.getShortcutsAsArray();
        List<MusicIndex> musicIndex = MusicIndex.createIndexesFromExpression(indexString);
        Map<MusicIndex, List<MusicFile>> indexedChildren = MusicIndex.getIndexedChildren(allMusicFolders, musicIndex, ignoredArticles, shortcuts);

        XMLBuilder builder = createXMLBuilder();
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

        initResponse(response);
        PrintWriter writer = response.getWriter();
        writer.print(builder.toString());

        return null;
    }

    public ModelAndView getMusicFiles(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String path = request.getParameter("path");
        LOG.info("getMusicFiles: " + path);
        MusicFile dir = musicFileService.getMusicFile(path);
        if (dir.isFile()) {
            dir = dir.getParent();
        }

        List<MusicFile> children = dir.getChildren(true, true);

        XMLBuilder builder = createXMLBuilder();
        builder.add("musicFiles");
        for (MusicFile child : children) {
            builder.add("musicFile");
            builder.add("name", StringUtil.toHtml(child.getNameWithoutSuffix()));
            builder.add("path", StringUtil.toHtml(child.getPath()));
            builder.add("isDirectory", child.isDirectory());
            builder.end();
        }
        builder.end();

        initResponse(response);
        PrintWriter writer = response.getWriter();
        writer.print(builder.toString());

        return null;
    }

    private XMLBuilder createXMLBuilder() {
        XMLBuilder builder = new XMLBuilder();
        builder.preamble("UTF-8");
        return builder;
    }

    private void initResponse(HttpServletResponse response) {
        response.setContentType("text/xml;charset=UTF-8");
    }

    public void setMusicFileService(MusicFileService musicFileService) {
        this.musicFileService = musicFileService;
    }

    public void setSettingsService(SettingsService settingsService) {
        this.settingsService = settingsService;
    }
}
