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

/**
 * @author Sindre Mehus
 * @version $Id$
 */
public interface Router {

    /**
     * Configures a nat entry on the UPNP device.
     *
     * @param internalPort         the internal client port where data should be redirected
     * @param externalPort         the external port to open on the UPNP device an map on the internal client, 0 for a wildcard value.
     * @param internalClient       the internal client IP where data should be redirected.
     * @param leaseDurationSeconds the lease duration in seconds, or 0 for an infinite time.
     */
    void addPortMapping(int externalPort, String internalClient,
                        int internalPort, int leaseDurationSeconds) throws Exception;
}
