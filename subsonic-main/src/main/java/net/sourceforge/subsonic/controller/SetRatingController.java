package net.sourceforge.subsonic.controller;

import net.sourceforge.subsonic.domain.*;
import net.sourceforge.subsonic.service.*;
import net.sourceforge.subsonic.util.*;
import net.sourceforge.subsonic.filter.ParameterDecodingFilter;
import org.springframework.web.bind.*;
import org.springframework.web.servlet.*;
import org.springframework.web.servlet.mvc.*;
import org.springframework.web.servlet.view.*;

import javax.servlet.http.*;

/**
 * Controller for updating music file ratings.
 *
 * @author Sindre Mehus
 */
public class SetRatingController extends AbstractController {

    private MusicInfoService musicInfoService;
    private MusicFileService musicFileService;
    private SecurityService securityService;

    protected ModelAndView handleRequestInternal(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String path = request.getParameter("path");
        int rating = ServletRequestUtils.getIntParameter(request, "rating");

        MusicFile musicFile = musicFileService.getMusicFile(path);
        String username = securityService.getCurrentUsername(request);
        musicInfoService.setRatingForUser(username, musicFile, rating);

        String url = "main.view?path" + ParameterDecodingFilter.PARAM_SUFFIX  + "=" + StringUtil.utf8HexEncode(path);
        return new ModelAndView(new RedirectView(url));
    }

    public void setMusicInfoService(MusicInfoService musicInfoService) {
        this.musicInfoService = musicInfoService;
    }

    public void setMusicFileService(MusicFileService musicFileService) {
        this.musicFileService = musicFileService;
    }

    public void setSecurityService(SecurityService securityService) {
        this.securityService = securityService;
    }
}
