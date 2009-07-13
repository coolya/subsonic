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
package net.sourceforge.subsonic.android.service;

import net.sourceforge.subsonic.android.util.ProgressListener;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;

import android.content.Context;

/**
 * @author Sindre Mehus
 */
public class HTTPMusicServiceDataSource implements MusicServiceDataSource {

    private final SettingsService settingsService;

    public HTTPMusicServiceDataSource(SettingsService settingsService) {
        this.settingsService = settingsService;
    }

    public Reader getArtistsReader(ProgressListener progressListener) throws Exception {
        String urlString = getBaseUrl() + "getIndexes.view?u=" + settingsService.getUsername() + "&p=" + settingsService.getPassword();

        URL url = new URL(urlString);
        if (progressListener != null) {
            progressListener.updateProgress("Contacting server " + url.getAuthority());
        }

        InputStream in = url.openStream();
        return new InputStreamReader(in);
    }

    public Reader getMusicDirectoryReader(String path, ProgressListener progressListener) throws Exception {
        String urlString = getBaseUrl() + "getMusicDirectory.view?pathUtf8Hex=" + path + "&u=" + settingsService.getUsername() + "&p=" + settingsService.getPassword();

        int player = settingsService.getPlayer();
        if (player > 0) {
            urlString += "&player=" + player;
        }

        URL url = new URL(urlString);
        if (progressListener != null) {
            progressListener.updateProgress("Contacting server " + url.getAuthority());
        }

        InputStream in = url.openStream();
        return new InputStreamReader(in);
    }

    private String getBaseUrl() {
        String baseUrl = settingsService.getBaseUrl();
        if (!baseUrl.endsWith("/")) {
            baseUrl += "/";
        }
        return baseUrl + "mobile/";
    }
}