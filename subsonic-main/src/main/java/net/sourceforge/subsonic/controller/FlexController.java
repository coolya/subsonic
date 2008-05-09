package net.sourceforge.subsonic.controller;

import net.sourceforge.subsonic.Logger;
import net.sourceforge.subsonic.domain.MusicFile;
import net.sourceforge.subsonic.domain.MusicFolder;
import net.sourceforge.subsonic.domain.MusicIndex;
import net.sourceforge.subsonic.service.MusicFileService;
import net.sourceforge.subsonic.service.SettingsService;
import net.sourceforge.subsonic.service.MusicIndexService;
import net.sourceforge.subsonic.util.StringUtil;
import net.sourceforge.subsonic.util.XMLBuilder;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.util.List;
import java.util.SortedMap;
import java.util.SortedSet;

/**
 * Multi-controller used for remote Flex services.
 *
 * @author Sindre Mehus
 */
public class FlexController extends MultiActionController {

    private static final Logger LOG = Logger.getLogger(FlexController.class);

    private MusicFileService musicFileService;
    private MusicIndexService musicIndexService;
    private SettingsService settingsService;

    public ModelAndView getArtists(HttpServletRequest request, HttpServletResponse response) throws Exception {

        MusicFolder[] allMusicFolders = settingsService.getAllMusicFolders();
        SortedMap<MusicIndex, SortedSet<MusicIndex.Artist>> indexedArtists = musicIndexService.getIndexedArtists(allMusicFolders);

        XMLBuilder builder = createXMLBuilder();
        builder.add("artists");

        for (SortedSet<MusicIndex.Artist> list : indexedArtists.values()) {
            for (MusicIndex.Artist artist : list) {
                builder.add("artist");
                builder.add("name", StringUtil.toHtml(artist.getName()));
                builder.add("path", StringUtil.toHtml(artist.getMusicFiles().get(0).getPath()));
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

    public void setMusicIndexService(MusicIndexService musicIndexService) {
        this.musicIndexService = musicIndexService;
    }
}
