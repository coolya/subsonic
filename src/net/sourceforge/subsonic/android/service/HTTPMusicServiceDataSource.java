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
import net.sourceforge.subsonic.android.util.Constants;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * @author Sindre Mehus
 */
public class HTTPMusicServiceDataSource implements MusicServiceDataSource {

    @Override
    public Reader getArtistsReader(Context context, ProgressListener progressListener) throws Exception {
        String urlString = getBaseUrl(context) + "getIndexes.view?u=" + getUsername(context) + "&p=" + getPassword(context);

        URL url = new URL(urlString);
        if (progressListener != null) {
            progressListener.updateProgress("Contacting server " + url.getAuthority());
        }

        InputStream in = url.openStream();
        return new InputStreamReader(in, "UTF-8");
    }

    @Override
    public Reader getMusicDirectoryReader(String id, Context context, ProgressListener progressListener) throws Exception {
        String urlString = getBaseUrl(context) + "getMusicDirectory.view?id=" + id + "&u=" + getUsername(context) + "&p=" + getPassword(context);

//        int player = settingsService.getPlayer();
//        if (player > 0) {
//            urlString += "&player=" + player;
//        }

        URL url = new URL(urlString);
        if (progressListener != null) {
            progressListener.updateProgress("Contacting server " + url.getHost());
        }

        InputStream in = url.openStream();
        return new InputStreamReader(in, "UTF-8");
    }

    private String getUsername(Context context) {
        return getSharedPreferences(context).getString(Constants.PREFERENCES_KEY_USERNAME, null);
    }

    private String getPassword(Context context) {
        return getSharedPreferences(context).getString(Constants.PREFERENCES_KEY_PASSWORD, null);
    }

    private SharedPreferences getSharedPreferences(Context context) {
        return context.getSharedPreferences(Constants.PREFERENCES_FILE_NAME, 0);
    }

    private String getBaseUrl(Context context) {
        String baseUrl = getSharedPreferences(context).getString(Constants.PREFERENCES_KEY_SERVER_URL, null);
        if (!baseUrl.endsWith("/")) {
            baseUrl += "/";
        }
        return baseUrl + "rest/";
    }
}