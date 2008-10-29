package net.sourceforge.subsonic.controller;

import org.springframework.web.servlet.*;
import org.springframework.web.servlet.mvc.multiaction.*;

import javax.servlet.http.*;
import java.util.*;

import net.sourceforge.subsonic.service.SecurityService;
import net.sourceforge.subsonic.service.SettingsService;
import net.sourceforge.subsonic.domain.User;

/**
 * Multi-controller used for simple pages.
 *
 * @author Sindre Mehus
 */
public class MultiController extends MultiActionController {

    private SecurityService securityService;
    private SettingsService settingsService;

    public ModelAndView login(HttpServletRequest request, HttpServletResponse response) {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("logout", request.getParameter("logout") != null);
        map.put("error", request.getParameter("error") != null);
        map.put("brand", settingsService.getBrand());

        User admin = securityService.getUserByName(User.USERNAME_ADMIN);
        if (User.USERNAME_ADMIN.equals(admin.getPassword())) {
            map.put("insecure", true);
        }

        return new ModelAndView("login", "model", map);
    }

    public ModelAndView index(HttpServletRequest request, HttpServletResponse response) {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("brand", settingsService.getBrand());
        return new ModelAndView("index", "model", map);
    }

    public void setSecurityService(SecurityService securityService) {
        this.securityService = securityService;
    }

    public void setSettingsService(SettingsService settingsService) {
        this.settingsService = settingsService;
    }
}