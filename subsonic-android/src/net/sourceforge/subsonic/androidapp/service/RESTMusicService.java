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

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.LinkedList;
import java.util.concurrent.atomic.AtomicReference;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.Header;
import org.apache.http.message.BasicHeader;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.conn.params.ConnPerRouteBean;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.ExecutionContext;
import org.apache.http.protocol.HttpContext;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import net.sourceforge.subsonic.androidapp.R;
import net.sourceforge.subsonic.androidapp.domain.Indexes;
import net.sourceforge.subsonic.androidapp.domain.MusicDirectory;
import net.sourceforge.subsonic.androidapp.domain.Version;
import net.sourceforge.subsonic.androidapp.domain.Playlist;
import net.sourceforge.subsonic.androidapp.service.parser.ErrorParser;
import net.sourceforge.subsonic.androidapp.service.parser.IndexesParser;
import net.sourceforge.subsonic.androidapp.service.parser.LicenseParser;
import net.sourceforge.subsonic.androidapp.service.parser.MusicDirectoryParser;
import net.sourceforge.subsonic.androidapp.service.parser.PlaylistParser;
import net.sourceforge.subsonic.androidapp.service.parser.PlaylistsParser;
import net.sourceforge.subsonic.androidapp.service.parser.RandomSongsParser;
import net.sourceforge.subsonic.androidapp.service.parser.SearchResultParser;
import net.sourceforge.subsonic.androidapp.service.parser.VersionParser;
import net.sourceforge.subsonic.androidapp.service.parser.AlbumListParser;
import net.sourceforge.subsonic.androidapp.util.CancellableTask;
import net.sourceforge.subsonic.androidapp.util.Constants;
import net.sourceforge.subsonic.androidapp.util.FileUtil;
import net.sourceforge.subsonic.androidapp.util.Pair;
import net.sourceforge.subsonic.androidapp.util.ProgressListener;
import net.sourceforge.subsonic.androidapp.util.Util;

/**
 * @author Sindre Mehus
 */
public class RESTMusicService implements MusicService {

    private static final String TAG = RESTMusicService.class.getSimpleName();

    private static final int SOCKET_CONNECT_TIMEOUT = 10 * 1000;
    private static final int SOCKET_READ_TIMEOUT = 10 * 1000;
    private static final int SOCKET_CONNECT_TIMEOUT_DOWNLOAD = 10 * 1000;
    private static final int SOCKET_READ_TIMEOUT_DOWNLOAD = 25 * 1000;

    /**
     * URL from which to fetch latest versions.
     */
    private static final String VERSION_URL = "http://subsonic.org/backend/version.view";

    private static final int HTTP_REQUEST_MAX_ATTEMPTS = 5;
    private static final long REDIRECTION_CHECK_INTERVAL_MILLIS = 60L * 60L * 1000L;
    private static final String FILENAME_INDEXES_SER = "indexes.ser";

    private final DefaultHttpClient httpClient;
    private Pair<String, Indexes> cachedIndexesPair;

    private long redirectionLastChecked;
    private String redirectFrom;
    private String redirectTo;
    private final ThreadSafeClientConnManager connManager;

    public RESTMusicService() {

        // Create and initialize HTTP parameters
        HttpParams params = new BasicHttpParams();
        ConnManagerParams.setMaxTotalConnections(params, 20);
        ConnManagerParams.setMaxConnectionsPerRoute(params, new ConnPerRouteBean(20));
        HttpConnectionParams.setConnectionTimeout(params, SOCKET_CONNECT_TIMEOUT);
        HttpConnectionParams.setSoTimeout(params, SOCKET_READ_TIMEOUT);

        // Create and initialize scheme registry
        SchemeRegistry schemeRegistry = new SchemeRegistry();
        schemeRegistry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
        schemeRegistry.register(new Scheme("https", SSLSocketFactory.getSocketFactory(), 443));

        // Create an HttpClient with the ThreadSafeClientConnManager.
        // This connection manager must be used if more than one thread will
        // be using the HttpClient.
        connManager = new ThreadSafeClientConnManager(params, schemeRegistry);
        httpClient = new DefaultHttpClient(connManager, params);
    }

    @Override
    public void ping(Context context, ProgressListener progressListener) throws Exception {
        Reader reader = getReader(context, progressListener, "ping");
        try {
            new ErrorParser(context).parse(reader);
        } finally {
            Util.close(reader);
        }
    }

    @Override
    public boolean isLicenseValid(Context context, ProgressListener progressListener) throws Exception {
        Reader reader = getReader(context, progressListener, "getLicense");
        try {
            return new LicenseParser(context).parse(reader, progressListener);
        } finally {
            Util.close(reader);
        }
    }

    @Override
    public Indexes getIndexes(Context context, ProgressListener progressListener) throws Exception {
        Indexes cachedIndexes = readCachedIndexes(context);
        long lastModified = cachedIndexes == null ? 0L : cachedIndexes.getLastModified();

        Reader reader = getReader(context, progressListener, "getIndexes", "ifModifiedSince", lastModified);
        try {
            Indexes indexes = new IndexesParser(context).parse(reader, progressListener);
            if (indexes != null) {
                writeCachedIndexes(context, indexes);
                return indexes;
            }
            return cachedIndexes;
        } finally {
            Util.close(reader);
        }
    }

    private Indexes readCachedIndexes(Context context) {
        String key = Util.getRestUrl(context, null);
        if (cachedIndexesPair == null) {
            cachedIndexesPair = FileUtil.deserialize(context, FILENAME_INDEXES_SER);
        }

        if (cachedIndexesPair != null && key.equals(cachedIndexesPair.getFirst())) {
            return cachedIndexesPair.getSecond();
        }

        return null;
    }

    private void writeCachedIndexes(Context context, Indexes indexes) {
        String key = Util.getRestUrl(context, null);
        cachedIndexesPair = new Pair<String, Indexes>(key, indexes);
        FileUtil.serialize(context, cachedIndexesPair, FILENAME_INDEXES_SER);
    }

    @Override
    public MusicDirectory getMusicDirectory(String id, Context context, ProgressListener progressListener) throws Exception {
        Reader reader = getReader(context, progressListener, "getMusicDirectory", "id", id);
        try {
            return new MusicDirectoryParser(context).parse(reader, progressListener);
        } finally {
            Util.close(reader);
        }
    }

    @Override
    public MusicDirectory search(String query, Context context, ProgressListener progressListener) throws Exception {
        Reader reader = getReader(context, progressListener, "search", "any", query);
        try {
            return new SearchResultParser(context).parse(reader, progressListener);
        } finally {
            Util.close(reader);
        }
    }

    @Override
    public MusicDirectory getPlaylist(String id, Context context, ProgressListener progressListener) throws Exception {
        Reader reader = getReader(context, progressListener, "getPlaylist", "id", id);
        try {
            return new PlaylistParser(context).parse(reader, progressListener);
        } finally {
            Util.close(reader);
        }
    }

    @Override
    public List<Playlist> getPlaylists(Context context, ProgressListener progressListener) throws Exception {
        Reader reader = getReader(context, progressListener, "getPlaylists");
        try {
            return new PlaylistsParser(context).parse(reader, progressListener);
        } finally {
            Util.close(reader);
        }
    }

    @Override
    public void createPlaylist(String id, String name, List<MusicDirectory.Entry> entries, Context context, ProgressListener progressListener) throws Exception {
        List<String> parameterNames = new LinkedList<String>();
        List<Object> parameterValues = new LinkedList<Object>();

        if (id != null) {
            parameterNames.add("playlistId");
            parameterValues.add(id);
        }
        if (name != null) {
            parameterNames.add("name");
            parameterValues.add(name);
        }
        for (MusicDirectory.Entry entry : entries) {
            parameterNames.add("songId");
            parameterValues.add(entry.getId());
        }

        Reader reader = getReader(context, progressListener, "createPlaylist", parameterNames, parameterValues);
        try {
            new ErrorParser(context).parse(reader);
        } finally {
            Util.close(reader);
        }
    }

    @Override
    public MusicDirectory getAlbumList(String type, int size, int offset, Context context, ProgressListener progressListener) throws Exception {
        Reader reader = getReader(context, progressListener, "getAlbumList",
                Arrays.asList("type", "size", "offset"), Arrays.<Object>asList(type, size, offset));
        try {
            return new AlbumListParser(context).parse(reader, progressListener);
        } finally {
            Util.close(reader);
        }
    }

    @Override
    public MusicDirectory getRandomSongs(int size, Context context, ProgressListener progressListener) throws Exception {
        Reader reader = getReader(context, progressListener, "getRandomSongs", "size", size);
        try {
            return new RandomSongsParser(context).parse(reader, progressListener);
        } finally {
            Util.close(reader);
        }
    }

    @Override
    public Version getLocalVersion(Context context) throws Exception {
        PackageInfo packageInfo = context.getPackageManager().getPackageInfo("net.sourceforge.subsonic.androidapp", 0);
        return new Version(packageInfo.versionName);
    }

    @Override
    public Version getLatestVersion(Context context, ProgressListener progressListener) throws Exception {
        Reader reader = getReaderForURL(context, VERSION_URL, progressListener);
        try {
            return new VersionParser().parse(reader, progressListener);
        } finally {
            Util.close(reader);
        }
    }

    @Override
    public Bitmap getCoverArt(Context context, String id, int size, ProgressListener progressListener) throws Exception {
        String url = Util.getRestUrl(context, "getCoverArt") + "&id=" + id + "&size=" + size;

        InputStream in = null;
        try {
            HttpEntity entity = getEntityForURL(context, url, progressListener);
            in = entity.getContent();

            // If content type is XML, an error occured.  Get it.
            String contentType = Util.getContentType(entity);
            if (contentType != null && contentType.startsWith("text/xml")) {
                new ErrorParser(context).parse(new InputStreamReader(in, Constants.UTF_8));
                return null; // Never reached.
            }

            byte[] bytes = Util.toByteArray(in);
            return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);

        } finally {
            Util.close(in);
        }
    }

    @Override
    public HttpResponse getDownloadInputStream(Context context, MusicDirectory.Entry song, long offset, int maxBitrate, CancellableTask task) throws Exception {

        String url = Util.getRestUrl(context, "stream") + "&id=" + song.getId() + "&maxBitRate=" + maxBitrate;

        // Use longer timeouts for download.
        HttpParams params = new BasicHttpParams();
        HttpConnectionParams.setConnectionTimeout(params, SOCKET_CONNECT_TIMEOUT_DOWNLOAD);
        HttpConnectionParams.setSoTimeout(params, SOCKET_READ_TIMEOUT_DOWNLOAD);

        // Add "Range" header if offset is given.
        List<Header> headers = new ArrayList<Header>();
        if (offset > 0) {
            headers.add(new BasicHeader("Range", "bytes=" + offset + "-"));
        }
        HttpResponse response = getResponseForURL(context, url, params, headers, null, task);

        // If content type is XML, an error occurred.  Get it.
        String contentType = Util.getContentType(response.getEntity());
        if (contentType != null && contentType.startsWith("text/xml")) {
            InputStream in = response.getEntity().getContent();
            try {
                new ErrorParser(context).parse(new InputStreamReader(in, Constants.UTF_8));
            } finally {
                Util.close(in);
            }
        }

        return response;
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
                url.append(URLEncoder.encode(String.valueOf(parameterValues.get(i)), "UTF-8"));
            }
        }

        if (progressListener != null) {
            progressListener.updateProgress(R.string.service_connecting);
        }

        return getReaderForURL(context, url.toString(), progressListener);
    }

    private Reader getReaderForURL(Context context, String url, ProgressListener progressListener) throws Exception {
        HttpEntity entity = getEntityForURL(context, url, progressListener);
        if (entity == null) {
            throw new RuntimeException("No entity received for URL " + url);
        }

        InputStream in = entity.getContent();
        return new InputStreamReader(in, Constants.UTF_8);
    }

    private HttpEntity getEntityForURL(Context context, String url, ProgressListener progressListener) throws Exception {
        return getResponseForURL(context, url, null, null, progressListener, null).getEntity();
    }

    private HttpResponse getResponseForURL(Context context, String url, HttpParams requestParams,
            List<Header> headers, ProgressListener progressListener, CancellableTask task) throws Exception {
        Log.d(TAG, "Connections in pool: " + connManager.getConnectionsInPool());
        url = rewriteUrlWithRedirect(url);
        return executeWithRetry(context, url, requestParams, headers, progressListener, task);
    }

    private HttpResponse executeWithRetry(Context context, String url, HttpParams requestParams,
            List<Header> headers, ProgressListener progressListener, CancellableTask task) throws IOException {
        Log.i(TAG, "Using URL " + url);

        final AtomicReference<Boolean> cancelled = new AtomicReference<Boolean>(false);
        int attempts = 0;
        while (true) {
            attempts++;
            HttpContext httpContext = new BasicHttpContext();
            final HttpGet request = new HttpGet(url);

            if (task != null) {
                // Attempt to abort the HTTP request if the task is cancelled.
                task.setOnCancelListener(new CancellableTask.OnCancelListener() {
                    @Override
                    public void onCancel() {
                        cancelled.set(true);
                        request.abort();
                    }
                });
            }

            if (requestParams != null) {
                request.setParams(requestParams);
            }

            if (headers != null) {
                for (Header header : headers) {
                    request.addHeader(header);
                }
            }

            try {
                HttpResponse response = httpClient.execute(request, httpContext);
                detectRedirect(url, httpContext);
                return response;
            } catch (IOException x) {
                request.abort();
                if (attempts >= HTTP_REQUEST_MAX_ATTEMPTS || cancelled.get()) {
                    throw x;
                }
                if (progressListener != null) {
                    String msg = context.getResources().getString(R.string.music_service_retry, attempts, HTTP_REQUEST_MAX_ATTEMPTS - 1);
                    progressListener.updateProgress(msg);
                }
                Log.w(TAG, "Got IOException (" + attempts + "), will retry", x);
                Util.sleepQuietly(2000L);
            }
        }
    }

    private void detectRedirect(String originalUrl, HttpContext context) {
        if (!originalUrl.contains(".subsonic.org")) {
            return;
        }

        HttpUriRequest request = (HttpUriRequest) context.getAttribute(ExecutionContext.HTTP_REQUEST);
        HttpHost host = (HttpHost) context.getAttribute(ExecutionContext.HTTP_TARGET_HOST);
        String redirectedUrl = host.toURI() + request.getURI();

        String path = Util.substringAfter(originalUrl, ".subsonic.org");
        redirectFrom = originalUrl.replace(path, "");
        redirectTo = redirectedUrl.replace(path, "");

        Log.i(TAG, redirectFrom + " redirects to " + redirectTo);
        redirectionLastChecked = System.currentTimeMillis();
    }

    private String rewriteUrlWithRedirect(String url) throws IOException {

        // Is it a subsonic.org address?
        int index = url.indexOf(".subsonic.org");
        if (index <= 0) {
            return url;
        }

        // Only cache for a certain time.
        if (System.currentTimeMillis() - redirectionLastChecked > REDIRECTION_CHECK_INTERVAL_MILLIS) {
            return url;
        }

        if (redirectFrom == null || redirectTo == null) {
            return url;
        }

        return url.replace(redirectFrom, redirectTo);
    }
}
