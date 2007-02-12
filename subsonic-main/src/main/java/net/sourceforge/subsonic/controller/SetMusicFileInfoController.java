package net.sourceforge.subsonic.controller;

import net.sourceforge.subsonic.domain.*;
import net.sourceforge.subsonic.service.*;
import net.sourceforge.subsonic.util.*;
import net.sourceforge.subsonic.filter.ParameterDecodingFilter;
import org.springframework.web.servlet.*;
import org.springframework.web.servlet.view.*;
import org.springframework.web.servlet.mvc.*;

import javax.servlet.http.*;

/**
 * TODO: Rename to SetCommentController?
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

        if ("comment".equals(action)) {
            musicFileInfo.setComment(StringUtil.toHtml(request.getParameter("comment")));
        }

        if (exists) {
            musicInfoService.updateMusicFileInfo(musicFileInfo);
        } else {
            musicInfoService.createMusicFileInfo(musicFileInfo);
        }

        String url = "main.view?path" + ParameterDecodingFilter.PARAM_SUFFIX  + "=" + StringUtil.utf8HexEncode(path);
        return new ModelAndView(new RedirectView(url));
    }

    public void setMusicInfoService(MusicInfoService musicInfoService) {
        this.musicInfoService = musicInfoService;
    }
}
