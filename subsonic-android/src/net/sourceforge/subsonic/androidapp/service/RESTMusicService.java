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
import android.content.pm.PackageInfo;
import android.util.Log;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import net.sourceforge.subsonic.androidapp.domain.Indexes;
import net.sourceforge.subsonic.androidapp.domain.MusicDirectory;
import net.sourceforge.subsonic.androidapp.domain.Version;
import net.sourceforge.subsonic.androidapp.util.Constants;
import net.sourceforge.subsonic.androidapp.util.Pair;
import net.sourceforge.subsonic.androidapp.util.ProgressListener;
import net.sourceforge.subsonic.androidapp.util.Util;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.apache.http.params.HttpConnectionParams;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.HttpClient;
import org.apache.http.HttpResponse;
import org.apache.http.impl.client.DefaultHttpClient;

/**
 * @author Sindre Mehus
 */
public class RESTMusicService implements MusicService {

    private static final String TAG = RESTMusicService.class.getSimpleName();

    /**
     * URL from which to fetch latest versions.
     */
    private static final String VERSION_URL = "http://gosubsonic.com/backend/version.view";

    private final IndexesParser indexesParser = new IndexesParser();
    private final MusicDirectoryParser musicDirectoryParser = new MusicDirectoryParser();
    private final SearchResultParser searchResultParser = new SearchResultParser();
    private final PlaylistParser playlistParser = new PlaylistParser();
    private final PlaylistsParser playlistsParser = new PlaylistsParser();
    private final LicenseParser licenseParser = new LicenseParser();
    private final VersionParser versionParser = new VersionParser();
    private final ErrorParser errorParser = new ErrorParser();
    private final List<Reader> readers = new ArrayList<Reader>(10);
    private final HttpClient httpClient = new DefaultHttpClient();

    public RESTMusicService() {
        HttpConnectionParams.setConnectionTimeout(httpClient.getParams(), Constants.SOCKET_CONNECT_TIMEOUT);
        HttpConnectionParams.setSoTimeout(httpClient.getParams(), Constants.SOCKET_READ_TIMEOUT);
    }

    @Override
    public void ping(Context context, ProgressListener progressListener) throws Exception {
        Reader reader = getReader(context, progressListener, "ping");
        addReader(reader);
        try {
            errorParser.parse(reader);
        } finally {
            closeReader(reader);
        }
    }

    @Override
    public boolean isLicenseValid(Context context, ProgressListener progressListener) throws Exception {
        Reader reader = getReader(context, progressListener, "getLicense");
        addReader(reader);
        try {
            return licenseParser.parse(reader, progressListener);
        } finally {
            closeReader(reader);
        }
    }

    @Override
    public Indexes getIndexes(Context context, ProgressListener progressListener) throws Exception {
        Reader reader = getReader(context, progressListener, "getIndexes");
        addReader(reader);
        try {
            return indexesParser.parse(reader, progressListener);
        } finally {
            closeReader(reader);
        }
    }

    @Override
    public MusicDirectory getMusicDirectory(String id, Context context, ProgressListener progressListener) throws Exception {
        Reader reader = getReader(context, progressListener, "getMusicDirectory", "id", id);
        addReader(reader);
        try {
            return musicDirectoryParser.parse(reader, progressListener);
        } finally {
            closeReader(reader);
        }
    }

    @Override
    public MusicDirectory search(String query, Context context, ProgressListener progressListener) throws Exception {
        Reader reader = getReader(context, progressListener, "search", "any", query);
        addReader(reader);
        try {
            return searchResultParser.parse(reader, progressListener);
        } finally {
            closeReader(reader);
        }
    }

    @Override
    public MusicDirectory getPlaylist(String id, Context context, ProgressListener progressListener) throws Exception {
        Reader reader = getReader(context, progressListener, "getPlaylist", "id", id);
        addReader(reader);
        try {
            return playlistParser.parse(reader, progressListener);
        } finally {
            closeReader(reader);
        }
    }

    @Override
    public List<Pair<String, String>> getPlaylists(Context context, ProgressListener progressListener) throws Exception {
        Reader reader = getReader(context, progressListener, "getPlaylists");
        addReader(reader);
        try {
            return playlistsParser.parse(reader, progressListener);
        } finally {
            closeReader(reader);
        }
    }

    @Override
    public Version getLocalVersion(Context context) throws Exception {
        PackageInfo packageInfo = context.getPackageManager().getPackageInfo("net.sourceforge.subsonic.androidapp", 0);
        return new Version(packageInfo.versionName);
    }

    @Override
    public Version getLatestVersion(Context context, ProgressListener progressListener) throws Exception {
        Reader reader = openURL(VERSION_URL);
        addReader(reader);
        try {
            return versionParser.parse(reader, progressListener);
        } finally {
            closeReader(reader);
        }
    }

    @Override
    public Bitmap getCoverArt(Context context, String id, int size, ProgressListener progressListener) throws Exception {
        String url = Util.getRestUrl(context, "getCoverArt") + "&id=" + id + "&size=" + size;

        HttpGet method = new HttpGet(url);
        HttpResponse response = httpClient.execute(method);
        InputStream in = response.getEntity().getContent();

        try {
            // If content type is XML, an error occured.  Get it.
            String contentType = Util.getContentType(response);
            if (contentType != null && contentType.startsWith("text/xml")) {
                new ErrorParser().parse(new InputStreamReader(in, Constants.UTF_8));
                return null; // Never reached.
            }

            byte[] bytes = Util.toByteArray(in);
            return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);

        } finally {
            Util.close(in);
        }
    }

    @Override
    public synchronized void cancel(Context context, ProgressListener progressListener) {
        while (!readers.isEmpty()) {
            Reader reader = readers.get(readers.size() - 1);
            closeReader(reader);
        }
    }

    private synchronized void addReader(Reader reader) {
        readers.add(reader);
    }

    private synchronized void closeReader(Reader reader) {
        try {
            reader.close();
        } catch (IOException x) {
            x.printStackTrace();
        }
        readers.remove(reader);
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

        StringBuilder url = new StringBuilder();
        url.append(Util.getRestUrl(context, method));

        if (parameterNames != null) {
            for (int i = 0; i < parameterNames.size(); i++) {
                url.append("&");
                url.append(parameterNames.get(i)).append("=");
                url.append(parameterValues.get(i));
            }
        }

        if (progressListener != null) {
            progressListener.updateProgress("Contacting server.");
        }

        Log.i(TAG, "Using URL " + url);
        return openURL(url.toString());
    }

    private Reader openURL(String url) throws IOException {
        HttpGet method = new HttpGet(url);

        HttpResponse response = httpClient.execute(method);
        InputStream in = response.getEntity().getContent();

        return new InputStreamReader(in, Constants.UTF_8);
    }

}