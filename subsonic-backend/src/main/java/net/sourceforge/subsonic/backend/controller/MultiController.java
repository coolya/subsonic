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

import java.io.PrintWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;

/**
 * Multi-controller used for simple pages.
 *
 * @author Sindre Mehus
 */
public class MultiController extends MultiActionController {

    private static final Logger LOG = Logger.getLogger(RedirectionController.class);

    private static final String SUBSONIC_VERSION = "3.8";
    private static final String SUBSONIC_BETA_VERSION = "3.9.beta1";
    private static final String SUBSONIC_ANDROID_VERSION = "1.1";

    public ModelAndView version(HttpServletRequest request, HttpServletResponse response) throws Exception {

        String localVersion = request.getParameter("v");
        LOG.info(request.getRemoteAddr() + " asked for latest version. Local version: " + localVersion);

        PrintWriter writer = response.getWriter();

        writer.println("SUBSONIC_VERSION_BEGIN" + SUBSONIC_VERSION + "SUBSONIC_VERSION_END");
        writer.println("SUBSONIC_FULL_VERSION_BEGIN" + SUBSONIC_VERSION + "SUBSONIC_FULL_VERSION_END");
        writer.println("SUBSONIC_BETA_VERSION_BEGIN" + SUBSONIC_BETA_VERSION + "SUBSONIC_BETA_VERSION_END");
        writer.println("SUBSONIC_ANDROID_VERSION_BEGIN" + SUBSONIC_ANDROID_VERSION + "SUBSONIC_ANDROID_VERSION_END");

        return null;
    }

}
