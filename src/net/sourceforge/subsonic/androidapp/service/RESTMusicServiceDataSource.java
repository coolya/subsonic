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
package net.sourceforge.subsonic.androidapp.service;

import android.content.Context;
import android.util.Log;
import net.sourceforge.subsonic.androidapp.util.Constants;
import net.sourceforge.subsonic.androidapp.util.ProgressListener;
import net.sourceforge.subsonic.androidapp.util.Util;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import java.util.Arrays;
import java.util.Collections;

/**
 * @author Sindre Mehus
 */
public class RESTMusicServiceDataSource implements MusicServiceDataSource {

    private static final String TAG = RESTMusicServiceDataSource.class.getSimpleName();

    @Override
    public Reader getPingReader(Context context, ProgressListener progressListener) throws Exception {
        return getReader(context, progressListener, "ping");
    }

    @Override
    public Reader getLicenseReader(Context context, ProgressListener progressListener) throws Exception {
        return getReader(context, progressListener, "getLicense");
    }

    @Override
    public Reader getIndexesReader(Context context, ProgressListener progressListener) throws Exception {
        return getReader(context, progressListener, "getIndexes");
    }

    @Override
    public Reader getMusicDirectoryReader(String id, Context context, ProgressListener progressListener) throws Exception {
        return getReader(context, progressListener, "getMusicDirectory", "id", id);
    }

    @Override
    public Reader getSearchResultReader(String query, Context context, ProgressListener progressListener) throws Exception {
        return getReader(context, progressListener, "search", "any", query);
    }

    private Reader getReader(Context context, ProgressListener progressListener, String method) throws Exception {
        return getReader(context, progressListener, method, Collections.<String>emptyList(), Collections.emptyList());
    }

    private Reader getReader(Context context, ProgressListener progressListener, String method,
                            String parameterName, Object parameterValue) throws Exception {
        return getReader(context, progressListener, method, Arrays.asList(parameterName), Arrays.<Object>asList(parameterValue));
    }

    private Reader getReader(Context context, ProgressListener progressListener, String method,
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
            progressListener.updateProgress("Contacting server.");
        }

        Log.i(TAG, "Using URL " + url.toExternalForm());
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