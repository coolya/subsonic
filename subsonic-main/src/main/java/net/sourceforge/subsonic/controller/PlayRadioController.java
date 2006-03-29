package net.sourceforge.subsonic.controller;

import net.sourceforge.subsonic.domain.*;
import net.sourceforge.subsonic.service.*;
import org.springframework.web.servlet.*;
import org.springframework.web.servlet.mvc.*;
import org.springframework.web.servlet.view.*;

import javax.servlet.http.*;

/**
 * Controller which forwards to the URL of an Internet radio/tv station.
 *
 * @author Sindre Mehus
 */
public class PlayRadioController extends AbstractController {
    private SettingsService settingsService;

    protected ModelAndView handleRequestInternal(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Integer id = new Integer(request.getParameter("id"));
        InternetRadio radio = settingsService.getInternetRadioById(id);
        return new ModelAndView(new RedirectView(radio.getStreamUrl()));
    }

    public void setSettingsService(SettingsService settingsService) {
        this.settingsService = settingsService;
    }
}
