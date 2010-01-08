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

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.util.Date;
import java.util.List;
import java.util.Arrays;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.HttpStatus;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.params.HttpConnectionParams;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;

import net.sourceforge.subsonic.backend.dao.RedirectionDao;
import net.sourceforge.subsonic.backend.domain.Redirection;

/**
 * @author Sindre Mehus
 */
public class RedirectionManagementController extends MultiActionController {

    private static final Logger LOG = Logger.getLogger(RedirectionManagementController.class);
    private static final List<String> RESERVED_REDIRECTS = Arrays.asList("www", "web", "demo");

    private RedirectionDao redirectionDao;

    public ModelAndView register(HttpServletRequest request, HttpServletResponse response) throws Exception {

        String redirectFrom = StringUtils.lowerCase(ServletRequestUtils.getRequiredStringParameter(request, "redirectFrom"));
        String licenseHolder = ServletRequestUtils.getStringParameter(request, "licenseHolder");
        String serverId = ServletRequestUtils.getRequiredStringParameter(request, "serverId");
        int port = ServletRequestUtils.getRequiredIntParameter(request, "port");
        String contextPath = ServletRequestUtils.getRequiredStringParameter(request, "contextPath");
        boolean trial = ServletRequestUtils.getBooleanParameter(request, "trial", false);
        Date lastUpdated = new Date();
        Date trialExpires = null;
        if (trial) {
            trialExpires = new Date(ServletRequestUtils.getRequiredLongParameter(request, "trialExpires"));
        }

        if (RESERVED_REDIRECTS.contains(redirectFrom)) {
            sendError(response, "\"" + redirectFrom + "\" is a reserved address. Please select another.");
            return null;
        }

        if (!redirectFrom.matches("(\\w|\\-)+")) {
            sendError(response, "Illegal characters present in \"" + redirectFrom + "\". Please select another.");
            return null;
        }

        String host = request.getRemoteAddr();
        URL url = new URL("http", host, port, "/" + contextPath);
        String redirectTo = url.toExternalForm();

        Redirection redirection = redirectionDao.getRedirection(redirectFrom);
        if (redirection == null) {

            // Delete other redirects for same server ID.
            redirectionDao.deleteRedirectionsByServerId(serverId);

            redirection = new Redirection(0, licenseHolder, serverId, redirectFrom, redirectTo, trial, trialExpires, lastUpdated, null);
            redirectionDao.createRedirection(redirection);
            LOG.info("Created " + redirection);

        } else {

            boolean sameServerId = serverId.equals(redirection.getServerId());
            boolean sameLicenseHolder = licenseHolder != null && licenseHolder.equals(redirection.getLicenseHolder());

            if (sameServerId || sameLicenseHolder) {
                redirection.setLicenseHolder(licenseHolder);
                redirection.setServerId(serverId);
                redirection.setRedirectFrom(redirectFrom);
                redirection.setRedirectTo(redirectTo);
                redirection.setTrial(trial);
                redirection.setTrialExpires(trialExpires);
                redirection.setLastUpdated(lastUpdated);
                redirectionDao.updateRedirection(redirection);
                LOG.info("Updated " + redirection);
            } else {
                sendError(response, "The web address \"" + redirectFrom + "\" is already in use. Please select another.");
                return null;
            }
        }

        return null;
    }

    public ModelAndView unregister(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String serverId = ServletRequestUtils.getRequiredStringParameter(request, "serverId");
        redirectionDao.deleteRedirectionsByServerId(serverId);
        return null;
    }

    public ModelAndView test(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String redirectFrom = StringUtils.lowerCase(ServletRequestUtils.getRequiredStringParameter(request, "redirectFrom"));
        PrintWriter writer = response.getWriter();

        Redirection redirection = redirectionDao.getRedirection(redirectFrom);
        String webAddress = redirectFrom + ".gosubsonic.com";
        if (redirection == null) {
            writer.print("Web address " + webAddress + " not registered.");
            return null;
        }

        if (redirection.getTrialExpires() != null && redirection.getTrialExpires().before(new Date())) {
            writer.print("Trial period expired. Please donate to activate web address.");
            return null;
        }

        String url = redirection.getRedirectTo();
        HttpClient client = new DefaultHttpClient();
        HttpConnectionParams.setConnectionTimeout(client.getParams(), 15000);
        HttpConnectionParams.setSoTimeout(client.getParams(), 15000);
        HttpGet method = new HttpGet(url);

        try {
            HttpResponse resp = client.execute(method);
            StatusLine status = resp.getStatusLine();

            if (status.getStatusCode() == HttpStatus.SC_OK) {
                writer.print(webAddress + " responded successfully.");
            } else {
                writer.print(webAddress + " returned HTTP error code " + status.getStatusCode() + " " + status.getReasonPhrase());
            }

        } catch (Throwable x) {
            writer.print(webAddress + " is registered, but could not connect to it. (" + x.getClass().getSimpleName() + ")");
        } finally {
            client.getConnectionManager().shutdown();
        }
        return null;
    }

    private void sendError(HttpServletResponse response, String message) throws IOException {
        response.getWriter().print(message);
        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
    }

    public ModelAndView dump(HttpServletRequest request, HttpServletResponse response) throws Exception {

        File file = File.createTempFile("redirections", ".txt");
        PrintWriter writer = new PrintWriter(file, "UTF-8");
        try {
            int offset = 0;
            int count = 100;
            while (true) {
                List<Redirection> redirections = redirectionDao.getAllRedirections(offset, count);
                if (redirections.isEmpty()) {
                    break;
                }
                offset += redirections.size();
                for (Redirection redirection : redirections) {
                    writer.println(redirection);
                }
            }
            LOG.info("Dumped redirections to " + file.getAbsolutePath());
        } finally {
            IOUtils.closeQuietly(writer);
        }
        return null;
    }

    public void setRedirectionDao(RedirectionDao redirectionDao) {
        this.redirectionDao = redirectionDao;
    }
}