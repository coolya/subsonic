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
package net.sourceforge.subsonic.service;

import net.sourceforge.subsonic.Logger;
import net.sourceforge.subsonic.domain.Router;
import net.sourceforge.subsonic.domain.SBBIRouter;
import net.sourceforge.subsonic.domain.WeUPnPRouter;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.PostMethod;

import java.io.IOException;
import java.util.Date;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * Provides network-related services, including port forwarding on UPnP routers and
 * URL redirection from http://xxxx.gosubsonic.com.
 *
 * @author Sindre Mehus
 */
public class NetworkService {

    private static final Logger LOG = Logger.getLogger(NetworkService.class);
    private static final long PORT_FORWARDING_DELAY = 3600L;
    private static final long URL_REDIRECTION_DELAY = 2 * 3600L;
    private static final String URL_REDIRECTION_REGISTRATION_URL = "http://localhost:8181/backend/redirect/register.view"; // TODO: change

    private SettingsService settingsService;
    private int currentPublicPort;
    private final ScheduledExecutorService executor = Executors.newScheduledThreadPool(4);
    private final PortForwardingTask portForwardingTask = new PortForwardingTask();
    private final URLRedirectionTask urlRedirectionTask = new URLRedirectionTask();
    private ScheduledFuture<?> portForwardingFuture;
    private ScheduledFuture<?> urlRedirectionFuture;

    private final Status portForwardingStatus = new Status();
    private final Status urlRedirectionStatus = new Status();

    public void init() {
        initPortForwarding();
        initUrlRedirection();
    }

    /**
     * Configures UPnP port forwarding.
     */
    public synchronized void initPortForwarding() {
        portForwardingStatus.setText("Idle");
        if (portForwardingFuture != null) {
            portForwardingFuture.cancel(true);
        }
        portForwardingFuture = executor.scheduleWithFixedDelay(portForwardingTask, 0L, PORT_FORWARDING_DELAY, TimeUnit.SECONDS);
    }

    /**
     * Configures URL redirection.
     */
    public synchronized void initUrlRedirection() {
        urlRedirectionStatus.setText("Idle");
        if (urlRedirectionFuture != null) {
            urlRedirectionFuture.cancel(true);
        }
        urlRedirectionFuture = executor.scheduleWithFixedDelay(urlRedirectionTask, 0L, URL_REDIRECTION_DELAY, TimeUnit.SECONDS);
    }

    public Status getPortForwardingStatus() {
        return portForwardingStatus;
    }

    public Status getURLRedirecionStatus() {
        return urlRedirectionStatus;
    }

    public void setSettingsService(SettingsService settingsService) {
        this.settingsService = settingsService;
    }

    private class PortForwardingTask implements Runnable {

        public void run() {

            portForwardingStatus.setText("Looking for router...");
            Router router = findRouter();
            if (router == null) {
                LOG.warn("No UPnP router found.");
                portForwardingStatus.setText("No router found.");
                return;
            }
            portForwardingStatus.setText("Router found.");

            // Delete old NAT entry.
            boolean enabled = settingsService.isPortForwardingEnabled();
            if (currentPublicPort != 0 && (!enabled || currentPublicPort != settingsService.getPortForwardingPublicPort())) {
                try {
                    router.deletePortMapping(currentPublicPort);
                    LOG.info("Deleted port mapping for public port " + currentPublicPort);
                } catch (Throwable x) {
                    LOG.warn("Failed to delete port mapping for public port " + currentPublicPort, x);
                }
            }

            // Create new NAT entry.
            if (enabled) {
                currentPublicPort = settingsService.getPortForwardingPublicPort();
                int localPort = 0;
                try {
                    localPort = settingsService.getPortForwardingLocalPort();
                    router.addPortMapping(currentPublicPort, localPort, 0);
                    String message = "Successfully forwarding public port " + currentPublicPort + " to local port " + localPort + ".";
                    LOG.info(message);
                    portForwardingStatus.setText(message);
                } catch (Throwable x) {
                    String message = "Failed to create port forwarding from public port " + currentPublicPort + " to local port " + localPort + ".";
                    LOG.warn(message, x);
                    portForwardingStatus.setText(message + " See log for details.");
                }
            } else {
                portForwardingStatus.setText("Port forwarding disabled.");
            }

        }

        private Router findRouter() {
            try {
                Router router = SBBIRouter.findRouter();
                if (router != null) {
                    return router;
                }
            } catch (Throwable x) {
                LOG.warn("Failed to find UPnP router using SBBI library.", x);
            }

            try {
                Router router = WeUPnPRouter.findRouter();
                if (router != null) {
                    return router;
                }
            } catch (Throwable x) {
                LOG.warn("Failed to find UPnP router using WeUPnP library.", x);
            }

            return null;
        }
    }

    private class URLRedirectionTask implements Runnable {

        public void run() {

            if (!settingsService.isUrlRedirectionEnabled()) {
                urlRedirectionStatus.setText("URL redirection disabled.");
                return;
                // TODO: Handle unregistration?
            }

            PostMethod method = new PostMethod(URL_REDIRECTION_REGISTRATION_URL);

            int port = settingsService.isPortForwardingEnabled() ?
                       settingsService.getPortForwardingPublicPort() :
                       settingsService.getPortForwardingLocalPort();
            boolean trial = !settingsService.isLicenseValid();

            // TODO
            method.addParameter("redirectFrom", settingsService.getUrlRedirectFrom());
            method.addParameter("principal", "sindre@activeobjects.no");
            method.addParameter("port", String.valueOf(port));
            method.addParameter("contextPath", "");
            method.addParameter("trial", String.valueOf(trial));
//            method.addParameter("trialExpires", bar);
            HttpClient client = new HttpClient();

            try {
                urlRedirectionStatus.setText("Registering URL redirection...");
                int statusCode = client.executeMethod(method);

                if (statusCode != HttpStatus.SC_OK) {
                    throw new IOException(method.getStatusLine().getReasonPhrase());
                }
                urlRedirectionStatus.setText("Successfully registered redirection URL.");

            } catch (Throwable x) {
                // TODO
                urlRedirectionStatus.setText(x.getMessage() + " (" + x.getClass().getSimpleName() + ")");
            } finally {
                method.releaseConnection();
            }
        }
    }

    public static class Status {

        private String text;
        private Date date;

        public void setText(String text) {
            this.text = text;
            date = new Date();
        }

        public String getText() {
            return text;
        }

        public Date getDate() {
            return date;
        }
    }
}
