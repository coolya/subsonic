package net.sourceforge.subsonic.controller;

import net.sourceforge.subsonic.domain.MusicFile;
import net.sourceforge.subsonic.service.MusicFileService;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.ParameterizableViewController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

/**
 * Controller for saving playlists.
 *
 * @author Sindre Mehus
 */
public class ChangeCoverArtController extends ParameterizableViewController {

    private MusicFileService musicFileService;

    @Override
    protected ModelAndView handleRequestInternal(HttpServletRequest request, HttpServletResponse response) throws Exception {

        String path = request.getParameter("path");
        String artist = request.getParameter("artist");
        String album = request.getParameter("album");
        MusicFile dir = musicFileService.getMusicFile(path);

        MusicFile child = dir.getFirstChild();
        if (child != null) {
            MusicFile.MetaData metaData = child.getMetaData();
            if (artist == null) {
                artist = metaData.getArtist();
            }
            if (album == null) {
                album = metaData.getAlbum();
            }

        }

        Map<String, Object> map = new HashMap<String, Object>();
        map.put("path", path);
        map.put("artist", artist);
        map.put("album", album);

        ModelAndView result = super.handleRequestInternal(request, response);
        result.addObject("model", map);

        return result;
    }

    public void setMusicFileService(MusicFileService musicFileService) {
        this.musicFileService = musicFileService;
    }
}
