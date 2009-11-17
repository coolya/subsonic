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

import net.sourceforge.subsonic.domain.SBBIRouter;
import net.sourceforge.subsonic.domain.WeUPnPRouter;
import net.sourceforge.subsonic.domain.Router;

/**
 * Provides network-related services, including management of port mappings in the LAN router.
 *
 * @author Sindre Mehus
 */
public class NetworkService {

    public static void main(String[] args) throws Exception {
        Router router = WeUPnPRouter.findRouter();
        System.out.println(router);

        router = SBBIRouter.findRouter();
        System.out.println(router);
    }



}






