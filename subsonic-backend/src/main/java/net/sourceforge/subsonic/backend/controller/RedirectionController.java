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
package net.sourceforge.subsonic.backend.controller;

import net.sourceforge.subsonic.backend.dao.RedirectionDao;
import net.sourceforge.subsonic.backend.domain.Redirection;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;

/**
 * @author Sindre Mehus
 */
public class RedirectionController implements Controller {

    private static final Logger LOG = Logger.getLogger(RedirectionController.class);
    private RedirectionDao redirectionDao;

    public ModelAndView handleRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {

        String redirectFrom = getRedirectFrom(request);
        // TODO: Handle null.

        Redirection redirection = redirectionDao.getRedirection(redirectFrom);
        // TODO: Handle null.

        redirection.setLastUpdated(new Date());
        redirectionDao.updateRedirection(redirection);

        // TODO: Handle expired trial.
        String requestUrl = request.getRequestURL().toString();
        // TODO: care about missing trailing slash?
        String redirectTo = redirection.getRedirectTo() + "/" + StringUtils.substringAfterLast(requestUrl, "/");
        return new ModelAndView(new RedirectView(redirectTo));
    }

    private String getRedirectFrom(HttpServletRequest request) throws MalformedURLException {
        URL url = new URL(request.getRequestURL().toString());
        String host = url.getHost();
        if (host.contains(".gosubsonic.com")) {
            return StringUtils.substringBefore(host, ".gosubsonic.com");
        }

        // For testing.
        return request.getParameter("redirectFrom");
    }

    public void setRedirectionDao(RedirectionDao redirectionDao) {
        this.redirectionDao = redirectionDao;
    }
}
