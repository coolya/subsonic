package net.sourceforge.subsonic.controller;

import net.sourceforge.subsonic.domain.*;
import net.sourceforge.subsonic.service.*;
import net.sourceforge.subsonic.util.*;
import org.springframework.web.servlet.*;
import org.springframework.web.servlet.view.*;
import org.springframework.web.servlet.mvc.*;

import javax.servlet.http.*;

/**
 * Controller for updating music file metadata.
 *
 * @author Sindre Mehus
 */
public class SetMusicFileInfoController extends AbstractController {

    private MusicInfoService musicInfoService;

    protected ModelAndView handleRequestInternal(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String path = request.getParameter("path");
        String action = request.getParameter("action");

        MusicFileInfo musicFileInfo = musicInfoService.getMusicFileInfoForPath(path);
        boolean exists = musicFileInfo != null;
        if (!exists) {
            musicFileInfo = new MusicFileInfo(path);
        }

        if ("rating".equals(action)) {
            int rating = Integer.parseInt(request.getParameter("rating"));
            musicFileInfo.setRating(rating);
        } else if ("comment".equals(action)) {
            musicFileInfo.setComment(StringUtil.toHtml(request.getParameter("comment")));
        }

        if (exists) {
            musicInfoService.updateMusicFileInfo(musicFileInfo);
        } else {
            musicInfoService.createMusicFileInfo(musicFileInfo);
        }

        String url = "main.jsp?path=" + StringUtil.urlEncode(path);
        return new ModelAndView(new RedirectView(url));
    }

    public void setMusicInfoService(MusicInfoService musicInfoService) {
        this.musicInfoService = musicInfoService;
    }
}
