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

import android.content.Context;
import net.sourceforge.subsonic.android.util.ProgressListener;
import net.sourceforge.subsonic.android.util.Util;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.net.URLConnection;

/**
 * @author Sindre Mehus
 */
public class HTTPMusicServiceDataSource implements MusicServiceDataSource {

    private static final int CONNECT_TIMEOUT = 10000;

    @Override
    public Reader getArtistsReader(Context context, ProgressListener progressListener) throws Exception {
        String urlString = Util.getRestUrl(context, "getIndexes");

        URL url = new URL(urlString);
        if (progressListener != null) {
            progressListener.updateProgress("Contacting server " + url.getHost());
        }

        return openURL(url);
    }

    @Override
    public Reader getMusicDirectoryReader(String id, Context context, ProgressListener progressListener) throws Exception {
        String urlString = Util.getRestUrl(context, "getMusicDirectory") + "&id=" + id;

        URL url = new URL(urlString);
        if (progressListener != null) {
            progressListener.updateProgress("Contacting server " + url.getHost());
        }

        return openURL(url);
    }

    private Reader openURL(URL url) throws IOException {
        URLConnection connection = url.openConnection();
        connection.setConnectTimeout(CONNECT_TIMEOUT);
        InputStream in = connection.getInputStream();
        return new InputStreamReader(in, "UTF-8");
    }
}