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
package net.sourceforge.subsonic.domain;

import java.io.IOException;

import net.sbbi.upnp.impls.InternetGatewayDevice;

/**
 * @author Sindre Mehus
 */
public class SBBIRouter implements Router {

    /**
	 * The timeout in milliseconds for finding a router device.
	 */
    private static final int DISCOVERY_TIMEOUT = 5000;

    private final InternetGatewayDevice router;

    private SBBIRouter(InternetGatewayDevice router) {
        this.router = router;
    }


    public static SBBIRouter findRouter() throws Exception {
        InternetGatewayDevice[] routers;
		try {
			routers = InternetGatewayDevice.getDevices(DISCOVERY_TIMEOUT);
		} catch (IOException e) {
			throw new Exception("Could not find router", e);
		}

		if (routers == null || routers.length == 0) {
			throw new Exception("No routers found");
		}

		if (routers.length != 1) {
            throw new Exception("Found more than one router (" + routers.length + ")");
		}

        return new SBBIRouter(routers[0]);
    }


    public void addPortMapping(int externalPort, String internalClient,
            int internalPort, int leaseDurationSeconds) throws Exception {

        router.addPortMapping("Subsonic", null, internalPort, externalPort,
                internalClient, leaseDurationSeconds, "TCP");
    }
}
