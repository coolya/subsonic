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
package net.sourceforge.subsonic.jmeplayer.service;

import net.sourceforge.subsonic.jmeplayer.SettingsController;

import javax.microedition.io.Connector;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

/**
 * @author Sindre Mehus
 */
public class HTTPMusicServiceDataSource implements MusicServiceDataSource {

    private final SettingsController settingsController;

    public HTTPMusicServiceDataSource(SettingsController settingsController) {
        this.settingsController = settingsController;
    }

    public Reader getIndexesReader() throws Exception {
        String url = getBaseUrl() + "getIndexes.view?u=" + settingsController.getUsername() + "&p=" + settingsController.getPassword();
        InputStream in = Connector.openInputStream(url);
        return new InputStreamReader(in);
    }

    public Reader getMusicDirectoryReader(String path) throws Exception {
        String url = getBaseUrl() + "getMusicDirectory.view?pathUtf8Hex=" + path + "&u=" + settingsController.getUsername() + "&p=" + settingsController.getPassword();

        int player = settingsController.getPlayer();
        if (player > 0) {
            url += "&player=" + player;
        }

        InputStream in = Connector.openInputStream(url);
        return new InputStreamReader(in);
    }

    private String getBaseUrl() {
        String baseUrl = settingsController.getBaseUrl();
        if (!baseUrl.endsWith("/")) {
            baseUrl += "/";
        }
        return baseUrl + "mobile/";
    }
}
