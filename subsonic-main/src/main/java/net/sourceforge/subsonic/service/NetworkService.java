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

import java.io.IOException;
import java.util.Date;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.PostMethod;

import net.sourceforge.subsonic.Logger;
import net.sourceforge.subsonic.domain.Router;
import net.sourceforge.subsonic.domain.SBBIRouter;
import net.sourceforge.subsonic.domain.WeUPnPRouter;

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

    private static final String URL_REDIRECTION_REGISTER_URL = getBackendUrl() + "/backend/redirect/register.view";
    private static final String URL_REDIRECTION_UNREGISTER_URL = getBackendUrl() + "/backend/redirect/unregister.view";
    private static final String URL_REDIRECTION_TEST_URL = getBackendUrl() + "/backend/redirect/test.view";

    private SettingsService settingsService;
    private int currentPublicPort;
    private final ScheduledExecutorService executor = Executors.newScheduledThreadPool(4);
    private final PortForwardingTask portForwardingTask = new PortForwardingTask();
    private final URLRedirectionTask urlRedirectionTask = new URLRedirectionTask();
    private final TestURLRedirectionTask testUrlRedirectionTask = new TestURLRedirectionTask();
    private Future<?> portForwardingFuture;
    private Future<?> urlRedirectionFuture;
    private Future<?> testUrlRedirectionFuture;

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

    public void testUrlRedirection() {
        urlRedirectionStatus.setText("Idle");
        if (testUrlRedirectionFuture != null) {
            testUrlRedirectionFuture.cancel(true);
        }
        testUrlRedirectionFuture = executor.submit(testUrlRedirectionTask);
    }

    public Status getPortForwardingStatus() {
        return portForwardingStatus;
    }

    public Status getURLRedirecionStatus() {
        return urlRedirectionStatus;
    }

    public static String getBackendUrl() {
        return System.getProperty("subsonic.test") == null ? "http://gosubsonic.com" : "http://localhost:8181";
    }

    public void setSettingsService(SettingsService settingsService) {
        this.settingsService = settingsService;
    }

    private class PortForwardingTask implements Runnable {

        public void run() {

            boolean enabled = settingsService.isPortForwardingEnabled();
            portForwardingStatus.setText("Looking for router...");
            Router router = findRouter();
            if (router == null) {
                LOG.warn("No UPnP router found.");
                portForwardingStatus.setText(enabled ? "No router found." : "Port forwarding disabled.");
                return;
            }
            portForwardingStatus.setText("Router found.");

            // Delete old NAT entry.
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

            //  Don't do it again if disabled.
            if (!enabled && portForwardingFuture != null) {
                portForwardingFuture.cancel(false);
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

            boolean enable = settingsService.isUrlRedirectionEnabled();
            PostMethod method = new PostMethod(enable ? URL_REDIRECTION_REGISTER_URL : URL_REDIRECTION_UNREGISTER_URL);

            int port = settingsService.isPortForwardingEnabled() ?
                    settingsService.getPortForwardingPublicPort() :
                    settingsService.getPortForwardingLocalPort();
            boolean trial = !settingsService.isLicenseValid();
            Date trialExpires = settingsService.getUrlRedirectTrialExpires();

            method.addParameter("serverId", settingsService.getServerId());
            method.addParameter("redirectFrom", settingsService.getUrlRedirectFrom());
            method.addParameter("port", String.valueOf(port));
            method.addParameter("contextPath", settingsService.getUrlRedirectContextPath());
            method.addParameter("trial", String.valueOf(trial));
            if (trial && trialExpires != null) {
                method.addParameter("trialExpires", String.valueOf(trialExpires.getTime()));
            } else {
                method.addParameter("licenseHolder", settingsService.getLicenseEmail());
            }

            HttpClient client = new HttpClient();

            try {
                urlRedirectionStatus.setText(enable ? "Registering web address..." : "Unregistering web address...");
                int statusCode = client.executeMethod(method);

                switch (statusCode) {
                    case HttpStatus.SC_BAD_REQUEST:
                        urlRedirectionStatus.setText(method.getResponseBodyAsString());
                        break;
                    case HttpStatus.SC_OK:
                        urlRedirectionStatus.setText(enable ? "Successfully registered web address." : "Web address disabled.");
                        break;
                    default:
                        throw new IOException(method.getStatusCode() + " " + method.getStatusText());
                }

            } catch (Throwable x) {
                LOG.warn(enable ? "Failed to register web address." : "Failed to unregister web address.", x);
                urlRedirectionStatus.setText(enable ? ("Failed to register web address. " + x.getMessage() +
                        " (" + x.getClass().getSimpleName() + ")") : "Web address disabled.");
            } finally {
                method.releaseConnection();
            }

            //  Don't do it again if disabled.
            if (!enable && urlRedirectionFuture != null) {
                urlRedirectionFuture.cancel(false);
            }
        }
    }

    private class TestURLRedirectionTask implements Runnable {

        public void run() {

            PostMethod method = new PostMethod(URL_REDIRECTION_TEST_URL);
            method.addParameter("redirectFrom", settingsService.getUrlRedirectFrom());
            HttpClient client = new HttpClient();

            try {
                urlRedirectionStatus.setText("Testing web address...");
                int statusCode = client.executeMethod(method);

                if (statusCode == HttpStatus.SC_OK) {
                    urlRedirectionStatus.setText(method.getResponseBodyAsString());
                } else {
                    throw new IOException(method.getStatusCode() + " " + method.getStatusText());
                }

            } catch (Throwable x) {
                LOG.warn("Failed to test web address.", x);
                urlRedirectionStatus.setText("Failed to test web address. " + x.getMessage() + " (" + x.getClass().getSimpleName() + ")");
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
