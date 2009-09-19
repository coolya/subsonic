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
import net.sourceforge.subsonic.android.util.Constants;
import net.sourceforge.subsonic.android.util.ProgressListener;
import net.sourceforge.subsonic.android.util.Util;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import java.util.Arrays;

/**
 * @author Sindre Mehus
 */
public class HTTPMusicServiceDataSource implements MusicServiceDataSource {

    @Override
    public Reader getPingReader(Context context, ProgressListener progressListener) throws Exception {
        return getReader(context, progressListener, "ping", null, null);
    }

    @Override
    public Reader getLicenseReader(Context context, ProgressListener progressListener) throws Exception {
        return getReader(context, progressListener, "getLicense", null, null);
    }

    @Override
    public Reader getArtistsReader(Context context, ProgressListener progressListener) throws Exception {
        return getReader(context, progressListener, "getIndexes", null, null);
    }

    @Override
    public Reader getMusicDirectoryReader(String id, Context context, ProgressListener progressListener) throws Exception {
        return getReader(context, progressListener, "getMusicDirectory", Arrays.asList("id"), Arrays.<Object>asList(id));
    }

    public Reader getReader(Context context, ProgressListener progressListener, String method,
                            List<String> parameterNames, List<Object> parameterValues) throws Exception {

        StringBuilder urlString = new StringBuilder();
        urlString.append(Util.getRestUrl(context, method));

        if (parameterNames != null) {
            for (int i = 0; i < parameterNames.size(); i++) {
                urlString.append("&");
                urlString.append(parameterNames.get(i)).append("=");
                urlString.append(parameterValues.get(i));
            }
        }

        URL url = new URL(urlString.toString());
        if (progressListener != null) {
            progressListener.updateProgress("Contacting server " + url.getHost());
        }

        return openURL(url);
    }

    private Reader openURL(URL url) throws IOException {
        URLConnection connection = url.openConnection();
        connection.setConnectTimeout(Constants.SOCKET_CONNECT_TIMEOUT);
        connection.setReadTimeout(Constants.SOCKET_READ_TIMEOUT);
        InputStream in = connection.getInputStream();
        return new InputStreamReader(in, Constants.UTF_8);
    }
}