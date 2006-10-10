package net.sourceforge.subsonic.controller;

import net.sourceforge.subsonic.domain.*;
import net.sourceforge.subsonic.service.*;
import org.springframework.web.servlet.*;
import org.springframework.web.servlet.view.*;
import org.springframework.web.servlet.mvc.*;

import javax.servlet.http.*;

/**
 * Controller for the main settings page.
 *
 * @author Sindre Mehus
 */
public class SettingsController extends AbstractController {

    private SecurityService securityService;


    protected ModelAndView handleRequestInternal(HttpServletRequest request, HttpServletResponse response) throws Exception {

        User user = securityService.getCurrentUser(request);

        // Redirect to music folder settings if admin.
        String view = user.isAdminRole() ? "musicFolderSettings.view" : "appearanceSettings.view";

        return new ModelAndView(new RedirectView(view));
     }

    public void setSecurityService(SecurityService securityService) {
        this.securityService = securityService;
    }
}
