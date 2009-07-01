/*
 This file is part of Subsonic.

 Subsonic is free software: you can redistribute it and/or modify
 it under the terms of the GNU General Public License as published by
 the Free Software Foundation, either version 3 of the License, or
 (at your option) any later version.

 Subsonic is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with Subsonic.  If not, see <http://www.gnu.org/licenses/>.

 Copyright 2009 (C) Sindre Mehus
 */
package net.sourceforge.subsonic.controller;

import org.springframework.web.servlet.*;
import org.springframework.web.servlet.view.RedirectView;
import org.springframework.web.servlet.mvc.multiaction.*;

import javax.servlet.http.*;
import java.util.*;
import java.net.URLEncoder;

import net.sourceforge.subsonic.service.SecurityService;
import net.sourceforge.subsonic.service.SettingsService;
import net.sourceforge.subsonic.domain.User;
import net.sourceforge.subsonic.util.StringUtil;

/**
 * Multi-controller used for simple pages.
 *
 * @author Sindre Mehus
 */
public class MultiController extends MultiActionController {

    private SecurityService securityService;
    private SettingsService settingsService;

    public ModelAndView login(HttpServletRequest request, HttpServletResponse response) throws Exception {

        // Auto-login if "user" and "password" parameters are given.
        String username = request.getParameter("user");
        String password = request.getParameter("password");
        if (username != null && password != null) {
            username = URLEncoder.encode(username, StringUtil.ENCODING_UTF8);
            password = URLEncoder.encode(password, StringUtil.ENCODING_UTF8);
            return new ModelAndView(new RedirectView("j_acegi_security_check?j_username=" + username +
                                                     "&j_password=" + password + "&_acegi_security_remember_me=checked"));
        }

        Map<String, Object> map = new HashMap<String, Object>();
        map.put("logout", request.getParameter("logout") != null);
        map.put("error", request.getParameter("error") != null);
        map.put("brand", settingsService.getBrand());
        map.put("loginMessage", settingsService.getLoginMessage());

        User admin = securityService.getUserByName(User.USERNAME_ADMIN);
        if (User.USERNAME_ADMIN.equals(admin.getPassword())) {
            map.put("insecure", true);
        }

        return new ModelAndView("login", "model", map);
    }

    public ModelAndView accessDenied(HttpServletRequest request, HttpServletResponse response) {
        return new ModelAndView("accessDenied");
    }

    public ModelAndView index(HttpServletRequest request, HttpServletResponse response) {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("brand", settingsService.getBrand());
        return new ModelAndView("index", "model", map);
    }

    public ModelAndView test(HttpServletRequest request, HttpServletResponse response) {
        return new ModelAndView("test");
    }

    public void setSecurityService(SecurityService securityService) {
        this.securityService = securityService;
    }

    public void setSettingsService(SettingsService settingsService) {
        this.settingsService = settingsService;
    }
}