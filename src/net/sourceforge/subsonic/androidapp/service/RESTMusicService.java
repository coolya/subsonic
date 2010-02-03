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

import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.Header;
import org.apache.http.message.BasicHeader;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.conn.params.ConnPerRouteBean;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
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
import net.sourceforge.subsonic.androidapp.service.parser.ErrorParser;
import net.sourceforge.subsonic.androidapp.service.parser.IndexesParser;
import net.sourceforge.subsonic.androidapp.service.parser.LicenseParser;
import net.sourceforge.subsonic.androidapp.service.parser.MusicDirectoryParser;
import net.sourceforge.subsonic.androidapp.service.parser.PlaylistParser;
import net.sourceforge.subsonic.androidapp.service.parser.PlaylistsParser;
import net.sourceforge.subsonic.androidapp.service.parser.SearchResultParser;
import net.sourceforge.subsonic.androidapp.service.parser.VersionParser;
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
    private static final int SOCKET_READ_TIMEOUT = 20 * 1000;
    private static final int SOCKET_CONNECT_TIMEOUT_DOWNLOAD = 120 * 1000;
    private static final int SOCKET_READ_TIMEOUT_DOWNLOAD = 180 * 1000;

    /**
     * URL from which to fetch latest versions.
     */
    private static final String VERSION_URL = "http://subsonic.org/backend/version.view";

    private static final int HTTP_REQUEST_MAX_ATTEMPTS = 5;
    private static final long REDIRECTION_CHECK_INTERVAL_MILLIS = 60L * 60L * 1000L;
    private static final String FILENAME_INDEXES_SER = "indexes.ser";

    private final List<Reader> readers = new ArrayList<Reader>(10);
    private final DefaultHttpClient httpClient;
    private Pair<String, Indexes> cachedIndexesPair;

    private long redirectionLastChecked;
    private String redirectFrom;
    private String redirectTo;

    public RESTMusicService() {

        // Create and initialize HTTP parameters
        HttpParams params = new BasicHttpParams();
        ConnManagerParams.setMaxTotalConnections(params, 10);
        ConnManagerParams.setMaxConnectionsPerRoute(params, new ConnPerRouteBean(5));
        HttpConnectionParams.setConnectionTimeout(params, SOCKET_CONNECT_TIMEOUT);
        HttpConnectionParams.setSoTimeout(params, SOCKET_READ_TIMEOUT);

        // Create and initialize scheme registry
        SchemeRegistry schemeRegistry = new SchemeRegistry();
        schemeRegistry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));

        // Create an HttpClient with the ThreadSafeClientConnManager.
        // This connection manager must be used if more than one thread will
        // be using the HttpClient.
        ClientConnectionManager cm = new ThreadSafeClientConnManager(params, schemeRegistry);
        httpClient = new DefaultHttpClient(cm, params);
    }

    @Override
    public void ping(Context context, ProgressListener progressListener) throws Exception {
        Reader reader = getReader(context, progressListener, "ping");
        addReader(reader);
        try {
            new ErrorParser(context).parse(reader);
        } finally {
            closeReader(reader);
        }
    }

    @Override
    public boolean isLicenseValid(Context context, ProgressListener progressListener) throws Exception {
        Reader reader = getReader(context, progressListener, "getLicense");
        addReader(reader);
        try {
            return new LicenseParser(context).parse(reader, progressListener);
        } finally {
            closeReader(reader);
        }
    }

    @Override
    public Indexes getIndexes(Context context, ProgressListener progressListener) throws Exception {
        Indexes cachedIndexes = readCachedIndexes(context);
        long lastModified = cachedIndexes == null ? 0L : cachedIndexes.getLastModified();

        Reader reader = getReader(context, progressListener, "getIndexes", "ifModifiedSince", lastModified);
        addReader(reader);
        try {
            Indexes indexes = new IndexesParser(context).parse(reader, progressListener);
            if (indexes != null) {
                writeCachedIndexes(context, indexes);
                return indexes;
            }
            return cachedIndexes;
        } finally {
            closeReader(reader);
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
        addReader(reader);
        try {
            return new MusicDirectoryParser(context).parse(reader, progressListener);
        } finally {
            closeReader(reader);
        }
    }

    @Override
    public MusicDirectory search(String query, Context context, ProgressListener progressListener) throws Exception {
        Reader reader = getReader(context, progressListener, "search", "any", query);
        addReader(reader);
        try {
            return new SearchResultParser(context).parse(reader, progressListener);
        } finally {
            closeReader(reader);
        }
    }

    @Override
    public MusicDirectory getPlaylist(String id, Context context, ProgressListener progressListener) throws Exception {
        Reader reader = getReader(context, progressListener, "getPlaylist", "id", id);
        addReader(reader);
        try {
            return new PlaylistParser(context).parse(reader, progressListener);
        } finally {
            closeReader(reader);
        }
    }

    @Override
    public List<Pair<String, String>> getPlaylists(Context context, ProgressListener progressListener) throws Exception {
        Reader reader = getReader(context, progressListener, "getPlaylists");
        addReader(reader);
        try {
            return new PlaylistsParser(context).parse(reader, progressListener);
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
        Reader reader = getReaderForURL(context, VERSION_URL, progressListener);
        addReader(reader);
        try {
            return new VersionParser().parse(reader, progressListener);
        } finally {
            closeReader(reader);
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
    public HttpResponse getDownloadInputStream(Context context, MusicDirectory.Entry song, long offset) throws Exception {

        String url = Util.getRestUrl(context, "stream") + "&id=" + song.getId();

        // Use longer timeouts for download.
        HttpParams params = new BasicHttpParams();
        HttpConnectionParams.setConnectionTimeout(params, SOCKET_CONNECT_TIMEOUT_DOWNLOAD);
        HttpConnectionParams.setSoTimeout(params, SOCKET_READ_TIMEOUT_DOWNLOAD);

        // Add "Range" header if offset is given.
        List<Header> headers = new ArrayList<Header>();
        if (offset > 0) {
            headers.add(new BasicHeader("Range", "bytes=" + offset + "-"));
        }
        HttpResponse response = getResponseForURL(context, url, params, headers, null);

        // If content type is XML, an error occured.  Get it.
        String contentType = Util.getContentType(response.getEntity());
        if (contentType != null && contentType.startsWith("text/xml")) {
            InputStream in = response.getEntity().getContent();
            new ErrorParser(context).parse(new InputStreamReader(in, Constants.UTF_8));
            return null; // Never reached.
        }

        return response;
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
        return getResponseForURL(context, url, null, null, progressListener).getEntity();
    }

    private HttpResponse getResponseForURL(Context context, String url, HttpParams requestParams,
                                           List<Header> headers, ProgressListener progressListener) throws Exception {
        url = rewriteUrlWithRedirect(url);
        return executeWithRetry(context, url, requestParams, headers, progressListener);
    }

    private HttpResponse executeWithRetry(Context context, String url, HttpParams requestParams,
                                          List<Header> headers, ProgressListener progressListener) throws IOException {
        Log.i(TAG, "Using URL " + url);

        int attempts = 0;
        while (true) {
            attempts++;
            HttpContext httpContext = new BasicHttpContext();
            HttpGet request = new HttpGet(url);

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
                if (attempts >= HTTP_REQUEST_MAX_ATTEMPTS) {
                    throw x;
                }
                if (progressListener != null) {
                    String msg = context.getResources().getString(R.string.music_service_retry, attempts, HTTP_REQUEST_MAX_ATTEMPTS - 1);
                    progressListener.updateProgress(msg);
                }
                Log.e(TAG, "Got IOException (" + attempts + "), will retry", x);
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

        String path = StringUtils.substringAfter(originalUrl, ".subsonic.org");
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