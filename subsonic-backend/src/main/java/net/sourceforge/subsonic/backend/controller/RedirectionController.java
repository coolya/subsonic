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
 * Redirects vanity URLs (such as http://sindre.gosubsonic.com).
 *
 * @author Sindre Mehus
 */
public class RedirectionController implements Controller {

    private static final Logger LOG = Logger.getLogger(RedirectionController.class);
    private RedirectionDao redirectionDao;

    public ModelAndView handleRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {

        String redirectFrom = getRedirectFrom(request);
        Redirection redirection = redirectFrom == null ? null : redirectionDao.getRedirection(redirectFrom);

        if (redirection == null) {
            return new ModelAndView(new RedirectView("http://gosubsonic.com/pages"));
        }

        redirection.setLastRead(new Date());
        redirectionDao.updateRedirection(redirection);

        if (redirection.isTrial() && redirection.getTrialExpires() != null && redirection.getTrialExpires().before(new Date())) {
            return new ModelAndView(new RedirectView("http://gosubsonic.com/pages/redirect-expired.jsp?redirectFrom=" + redirectFrom));
        }
        String requestUrl = getFullRequestURL(request);
        // TODO: care about missing trailing slash?
        StringBuilder redirectTo = new StringBuilder(redirection.getRedirectTo());
        if (redirectTo.charAt(redirectTo.length() - 1) != '/') {
            redirectTo.append("/");
        }
        redirectTo.append(StringUtils.substringAfterLast(requestUrl, "/"));

        LOG.info("Redirecting from " + requestUrl + " to " + redirectTo);

        return new ModelAndView(new RedirectView(redirectTo.toString()));
    }

    private String getFullRequestURL(HttpServletRequest request) {
        StringBuilder builder = new StringBuilder(request.getRequestURL());
        if (request.getQueryString() != null) {
            builder.append("?").append(request.getQueryString());
        }
        return builder.toString();
    }

    private String getRedirectFrom(HttpServletRequest request) throws MalformedURLException {
        URL url = new URL(request.getRequestURL().toString());
        String host = url.getHost();

        String redirectFrom;
        if (host.contains(".")) {
            redirectFrom = StringUtils.substringBefore(host, ".");
        } else {
            // For testing.
            redirectFrom = request.getParameter("redirectFrom");
        }

        return StringUtils.lowerCase(redirectFrom);
    }

    public void setRedirectionDao(RedirectionDao redirectionDao) {
        this.redirectionDao = redirectionDao;
    }
}
