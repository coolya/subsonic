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

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Provides network-related services, including port forwarding on UPnP routers.
 *
 * @author Sindre Mehus
 */
public class NetworkService {

    private static final Logger LOG = Logger.getLogger(NetworkService.class);

    private SettingsService settingsService;
    private int currentPublicPort;
    private final AtomicBoolean initRunning = new AtomicBoolean();

    /**
     * Configures UPnP port forwarding.
     */
    public synchronized void initPortForwarding() {
        if (initRunning.get()) {
            return;
        }
        initRunning.set(true);

        Thread thread = new Thread("UPnP init") {
            @Override
            public void run() {
                try {
                    doInitPortForwarding();
                } finally {
                    initRunning.set(false);
                }
            }
        };

        thread.setPriority(Thread.MIN_PRIORITY);
        thread.start();
    }

    private void doInitPortForwarding() {

        Router router = findRouter();
        if (router == null) {
            LOG.warn("No UPnP router found.");
            return;
        }

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
                LOG.info("Created port mapping from public port " + currentPublicPort + " to local port " + localPort);
            } catch (Throwable x) {
                LOG.warn("Failed to create port mapping from public port " + currentPublicPort + " to local port " + localPort, x);
            }
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

    public void setSettingsService(SettingsService settingsService) {
        this.settingsService = settingsService;
    }
}
