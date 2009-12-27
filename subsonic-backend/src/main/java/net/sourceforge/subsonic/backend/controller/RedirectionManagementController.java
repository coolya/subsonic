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
import org.apache.log4j.Logger;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;

/**
 * @author Sindre Mehus
 */
public class RedirectionManagementController extends MultiActionController {

    private static final Logger LOG = Logger.getLogger(RedirectionManagementController.class);
    private RedirectionDao redirectionDao;

    public ModelAndView register(HttpServletRequest request, HttpServletResponse response) throws Exception {

        String redirectFrom = ServletRequestUtils.getRequiredStringParameter(request, "redirectFrom");
        String principal = ServletRequestUtils.getRequiredStringParameter(request, "principal");
        String redirectTo = "http://www.google.com"; // TODO
        boolean trial = ServletRequestUtils.getBooleanParameter(request, "trial", false);
        Date lastUpdated = new Date();
        Date trialExpires = null;
        if (trial) {
            trialExpires = new Date(ServletRequestUtils.getRequiredLongParameter(request, "trialExpires"));
        }

        Redirection redirection = redirectionDao.getRedirection(redirectFrom);

        // TODO: Check principal, trial expiration etc.

        if (redirection == null) {
            redirection = new Redirection(0, principal, redirectFrom, redirectTo, trial, trialExpires, lastUpdated, null);
            redirectionDao.createRedirection(redirection);
            LOG.info("Created " + redirection); // TODO
        } else {
            redirection.setRedirectFrom(redirectFrom);
            redirection.setRedirectTo(redirectTo);
            redirection.setTrial(trial);
            redirection.setTrialExpires(trialExpires);
            redirection.setLastUpdated(lastUpdated);
            redirectionDao.updateRedirection(redirection);
            LOG.info("Updated " + redirection); // TODO
        }

        return null;
    }

    public ModelAndView unregister(HttpServletRequest request, HttpServletResponse response) throws Exception {
        return null; // TODO
    }

    public void setRedirectionDao(RedirectionDao redirectionDao) {
        this.redirectionDao = redirectionDao;
    }
}