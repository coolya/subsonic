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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
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
import org.apache.http.conn.scheme.SocketFactory;
import org.apache.http.message.BasicHeader;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
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
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import net.sourceforge.subsonic.androidapp.R;
import net.sourceforge.subsonic.androidapp.domain.Indexes;
import net.sourceforge.subsonic.androidapp.domain.MusicDirectory;
import net.sourceforge.subsonic.androidapp.domain.Version;
import net.sourceforge.subsonic.androidapp.domain.Playlist;
import net.sourceforge.subsonic.androidapp.domain.SearchResult;
import net.sourceforge.subsonic.androidapp.domain.SearchCritera;
import net.sourceforge.subsonic.androidapp.domain.ServerInfo;
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
import net.sourceforge.subsonic.androidapp.service.parser.SearchResult2Parser;
import net.sourceforge.subsonic.androidapp.service.ssl.SSLSocketFactory;
import net.sourceforge.subsonic.androidapp.util.CancellableTask;
import net.sourceforge.subsonic.androidapp.util.Constants;
import net.sourceforge.subsonic.androidapp.util.FileUtil;
import net.sourceforge.subsonic.androidapp.util.Pair;
import net.sourceforge.subsonic.androidapp.util.ProgressListener;
import net.sourceforge.subsonic.androidapp.service.ssl.TrustSelfSignedStrategy;
import net.sourceforge.subsonic.androidapp.util.Util;

/**
 * @author Sindre Mehus
 */
public class RESTMusicService implements MusicService {

    private static final String TAG = RESTMusicService.class.getSimpleName();

    private static final int SOCKET_CONNECT_TIMEOUT = 10 * 1000;
    private static final int SOCKET_READ_TIMEOUT_DEFAULT = 10 * 1000;
    private static final int SOCKET_READ_TIMEOUT_DOWNLOAD = 30 * 1000;
    private static final int SOCKET_READ_TIMEOUT_GET_RANDOM_SONGS = 60 * 1000;
    private static final int SOCKET_READ_TIMEOUT_GET_PLAYLIST = 60 * 1000;

    // Allow 20 seconds extra timeout per MB offset.
    private static final double TIMEOUT_MILLIS_PER_OFFSET_BYTE = 20000.0 / 1000000.0;

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
    private int redirectionNetworkType = -1;
    private String redirectFrom;
    private String redirectTo;
    private final ThreadSafeClientConnManager connManager;
    private Version serverRestVersion;

    public RESTMusicService() {

        // Create and initialize default HTTP parameters
        HttpParams params = new BasicHttpParams();
        ConnManagerParams.setMaxTotalConnections(params, 20);
        ConnManagerParams.setMaxConnectionsPerRoute(params, new ConnPerRouteBean(20));
        HttpConnectionParams.setConnectionTimeout(params, SOCKET_CONNECT_TIMEOUT);
        HttpConnectionParams.setSoTimeout(params, SOCKET_READ_TIMEOUT_DEFAULT);

        // Turn off stale checking.  Our connections break all the time anyway,
        // and it's not worth it to pay the penalty of checking every time.
        HttpConnectionParams.setStaleCheckingEnabled(params, false);

        // Create and initialize scheme registry
        SchemeRegistry schemeRegistry = new SchemeRegistry();
        schemeRegistry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
        schemeRegistry.register(new Scheme("https", createSSLSocketFactory(), 443));

        // Create an HttpClient with the ThreadSafeClientConnManager.
        // This connection manager must be used if more than one thread will
        // be using the HttpClient.
        connManager = new ThreadSafeClientConnManager(params, schemeRegistry);
        httpClient = new DefaultHttpClient(connManager, params);
    }

    private SocketFactory createSSLSocketFactory() {
        try {
            return new SSLSocketFactory(new TrustSelfSignedStrategy(), SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
        } catch (Throwable x) {
            Log.e(TAG, "Failed to create custom SSL socket factory, using default.", x);
            return org.apache.http.conn.ssl.SSLSocketFactory.getSocketFactory();
        }
    }

    @Override
    public void ping(Context context, ProgressListener progressListener) throws Exception {
        Reader reader = getReader(context, progressListener, "ping", null);
        try {
            new ErrorParser(context).parse(reader);
        } finally {
            Util.close(reader);
        }
    }

    @Override
    public boolean isLicenseValid(Context context, ProgressListener progressListener) throws Exception {
        Reader reader = getReader(context, progressListener, "getLicense", null);
        try {
            ServerInfo serverInfo = new LicenseParser(context).parse(reader);
            serverRestVersion = serverInfo.getRestVersion();
            return serverInfo.isLicenseValid();
        } finally {
            Util.close(reader);
        }
    }

    @Override
    public Indexes getIndexes(boolean refresh, Context context, ProgressListener progressListener) throws Exception {
        Indexes cachedIndexes = readCachedIndexes(context);
        long lastModified = cachedIndexes == null ? 0L : cachedIndexes.getLastModified();

        Reader reader = getReader(context, progressListener, "getIndexes", null, "ifModifiedSince", lastModified);
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
        Reader reader = getReader(context, progressListener, "getMusicDirectory", null, "id", id);
        try {
            return new MusicDirectoryParser(context).parse(reader, progressListener);
        } finally {
            Util.close(reader);
        }
    }

    @Override
    public SearchResult search(SearchCritera critera, Context context, ProgressListener progressListener) throws Exception {
        // Ensure backward compatibility with REST 1.3.
        if (isServer14()) {
            return searchNew(critera, context, progressListener);
        } else {
            return searchOld(critera, context, progressListener);
        }
    }

    private boolean isServer14() {
        if (serverRestVersion == null) {
            return false;
        }
        Version version14 = new Version("1.4");
        return serverRestVersion.compareTo(version14) >= 0;
    }

    /**
     * Search using the "search" REST method.
     */
    private SearchResult searchOld(SearchCritera critera, Context context, ProgressListener progressListener) throws Exception {
        List<String> parameterNames = Arrays.asList("any", "songCount");
        List<Object> parameterValues = Arrays.<Object>asList(critera.getQuery(), critera.getSongCount());
        Reader reader = getReader(context, progressListener, "search", null, parameterNames, parameterValues);
        try {
            return new SearchResultParser(context).parse(reader, progressListener);
        } finally {
            Util.close(reader);
        }
    }

    /**
     * Search using the "search2" REST method, available in 1.4.0 and later.
     */
    private SearchResult searchNew(SearchCritera critera, Context context, ProgressListener progressListener) throws Exception {
        List<String> parameterNames = Arrays.asList("query", "artistCount", "albumCount", "songCount");
        List<Object> parameterValues = Arrays.<Object>asList(critera.getQuery(), critera.getArtistCount(),
                                                             critera.getAlbumCount(), critera.getSongCount());
        Reader reader = getReader(context, progressListener, "search2", null, parameterNames, parameterValues);
        try {
            return new SearchResult2Parser(context).parse(reader, progressListener);
        } finally {
            Util.close(reader);
        }
    }

    @Override
    public MusicDirectory getPlaylist(String id, Context context, ProgressListener progressListener) throws Exception {
        HttpParams params = new BasicHttpParams();
        HttpConnectionParams.setSoTimeout(params, SOCKET_READ_TIMEOUT_GET_PLAYLIST);

        Reader reader = getReader(context, progressListener, "getPlaylist", params, "id", id);
        try {
            return new PlaylistParser(context).parse(reader, progressListener);
        } finally {
            Util.close(reader);
        }
    }

    @Override
    public List<Playlist> getPlaylists(Context context, ProgressListener progressListener) throws Exception {
        Reader reader = getReader(context, progressListener, "getPlaylists", null);
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

        Reader reader = getReader(context, progressListener, "createPlaylist", null, parameterNames, parameterValues);
        try {
            new ErrorParser(context).parse(reader);
        } finally {
            Util.close(reader);
        }
    }

    @Override
    public MusicDirectory getAlbumList(String type, int size, int offset, Context context, ProgressListener progressListener) throws Exception {
        Reader reader = getReader(context, progressListener, "getAlbumList",
                null, Arrays.asList("type", "size", "offset"), Arrays.<Object>asList(type, size, offset));
        try {
            return new AlbumListParser(context).parse(reader, progressListener);
        } finally {
            Util.close(reader);
        }
    }

    @Override
    public MusicDirectory getRandomSongs(int size, Context context, ProgressListener progressListener) throws Exception {
        HttpParams params = new BasicHttpParams();
        HttpConnectionParams.setSoTimeout(params, SOCKET_READ_TIMEOUT_GET_RANDOM_SONGS);

        Reader reader = getReader(context, progressListener, "getRandomSongs", params, "size", size);
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
        Reader reader = getReaderForURL(context, VERSION_URL, null, progressListener);
        try {
            return new VersionParser().parse(reader);
        } finally {
            Util.close(reader);
        }
    }

    @Override
    public Bitmap getCoverArt(Context context, MusicDirectory.Entry entry, int size, boolean saveToFile, ProgressListener progressListener) throws Exception {

        // Synchronize on the entry so that we don't download concurrently for the same song.
        synchronized (entry) {

            // Use cached file, if existing.
            File albumArtFile = FileUtil.getAlbumArtFile(entry);
            if (albumArtFile.exists()) {

                InputStream in = new FileInputStream(albumArtFile);
                try {
                    byte[] bytes = Util.toByteArray(in);
                    Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                    return Bitmap.createScaledBitmap(bitmap, size, size, true);
                } finally {
                    Util.close(in);
                }
            }

            String url = Util.getRestUrl(context, "getCoverArt") + "&id=" + entry.getCoverArt() + "&size=" + size;

            InputStream in = null;
            try {
                HttpEntity entity = getEntityForURL(context, url, null, progressListener);
                in = entity.getContent();

                // If content type is XML, an error occured.  Get it.
                String contentType = Util.getContentType(entity);
                if (contentType != null && contentType.startsWith("text/xml")) {
                    new ErrorParser(context).parse(new InputStreamReader(in, Constants.UTF_8));
                    return null; // Never reached.
                }

                byte[] bytes = Util.toByteArray(in);

                if (saveToFile) {
                    OutputStream out = null;
                    try {
                        out = new FileOutputStream(albumArtFile);
                        out.write(bytes);
                    } finally {
                        Util.close(out);
                    }
                }

                return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);

            } finally {
                Util.close(in);
            }
        }
    }

    @Override
    public HttpResponse getDownloadInputStream(Context context, MusicDirectory.Entry song, long offset, int maxBitrate, CancellableTask task) throws Exception {

        String url = Util.getRestUrl(context, "stream") + "&id=" + song.getId() + "&maxBitRate=" + maxBitrate;

        // Set socket read timeout. Note: The timeout increases as the offset gets larger. This is
        // to avoid the thrashing effect seen when offset is combined with transcoding/downsampling on the server.
        // In that case, the server uses a long time before sending any data, causing the client to time out.
        HttpParams params = new BasicHttpParams();
        int timeout = (int) (SOCKET_READ_TIMEOUT_DOWNLOAD + offset * TIMEOUT_MILLIS_PER_OFFSET_BYTE);
        HttpConnectionParams.setSoTimeout(params, timeout);

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

    @Override
    public String getVideoUrl(Context context, String id) {
        StringBuilder url = new StringBuilder(Util.getRestUrl(context, "videoPlayer"));
        url.append("&id=").append(id);
        url.append("&maxBitRate=500");
        url.append("&autoplay=false");

        url = rewriteUrlWithRedirect(context, url);

        Log.i("Using video URL: " + url);

        return url.toString();
    }

    private Reader getReader(Context context, ProgressListener progressListener, String method, HttpParams requestParams) throws Exception {
        return getReader(context, progressListener, method, requestParams, Collections.<String>emptyList(), Collections.emptyList());
    }

    private Reader getReader(Context context, ProgressListener progressListener, String method,
            HttpParams requestParams, String parameterName, Object parameterValue) throws Exception {
        return getReader(context, progressListener, method, requestParams, Arrays.asList(parameterName), Arrays.<Object>asList(parameterValue));
    }

    private Reader getReader(Context context, ProgressListener progressListener, String method,
            HttpParams requestParams, List<String> parameterNames, List<Object> parameterValues) throws Exception {

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

        return getReaderForURL(context, url.toString(), requestParams, progressListener);
    }

    private Reader getReaderForURL(Context context, String url, HttpParams requestParams, ProgressListener progressListener) throws Exception {
        HttpEntity entity = getEntityForURL(context, url, requestParams, progressListener);
        if (entity == null) {
            throw new RuntimeException("No entity received for URL " + url);
        }

        InputStream in = entity.getContent();
        return new InputStreamReader(in, Constants.UTF_8);
    }

    private HttpEntity getEntityForURL(Context context, String url, HttpParams requestParams, ProgressListener progressListener) throws Exception {
        return getResponseForURL(context, url, requestParams, null, progressListener, null).getEntity();
    }

    private HttpResponse getResponseForURL(Context context, String url, HttpParams requestParams,
            List<Header> headers, ProgressListener progressListener, CancellableTask task) throws Exception {
        Log.d(TAG, "Connections in pool: " + connManager.getConnectionsInPool());
        url = rewriteUrlWithRedirect(context, url);
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
                Log.d(TAG, "Socket read timeout: " + HttpConnectionParams.getSoTimeout(requestParams) + " ms.");
            }

            if (headers != null) {
                for (Header header : headers) {
                    request.addHeader(header);
                }
            }

            try {
                HttpResponse response = httpClient.execute(request, httpContext);
                detectRedirect(url, context, httpContext);
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
                increaseTimeouts(requestParams);
                Util.sleepQuietly(2000L);
            }
        }
    }

    private void increaseTimeouts(HttpParams requestParams) {
        if (requestParams != null) {
            int connectTimeout = HttpConnectionParams.getConnectionTimeout(requestParams);
            if (connectTimeout != 0) {
                HttpConnectionParams.setConnectionTimeout(requestParams, (int) (connectTimeout * 1.3F));
            }
            int readTimeout = HttpConnectionParams.getSoTimeout(requestParams);
            if (readTimeout != 0) {
                HttpConnectionParams.setSoTimeout(requestParams, (int) (readTimeout * 1.5F));
            }
        }
    }

    private void detectRedirect(String originalUrl, Context context, HttpContext httpContext) {
        if (!originalUrl.contains(".subsonic.org")) {
            return;
        }

        HttpUriRequest request = (HttpUriRequest) httpContext.getAttribute(ExecutionContext.HTTP_REQUEST);
        HttpHost host = (HttpHost) httpContext.getAttribute(ExecutionContext.HTTP_TARGET_HOST);
        String redirectedUrl = host.toURI() + request.getURI();

        String path = Util.substringAfter(originalUrl, ".subsonic.org");
        redirectFrom = originalUrl.replace(path, "");
        redirectTo = redirectedUrl.replace(path, "");

        Log.i(TAG, redirectFrom + " redirects to " + redirectTo);
        redirectionLastChecked = System.currentTimeMillis();
        redirectionNetworkType = getCurrentNetworkType(context);
    }

    private String rewriteUrlWithRedirect(Context context, String url) throws IOException {

        // Is it a subsonic.org address?
        int index = url.indexOf(".subsonic.org");
        if (index <= 0) {
            return url;
        }

        // Only cache for a certain time.
        if (System.currentTimeMillis() - redirectionLastChecked > REDIRECTION_CHECK_INTERVAL_MILLIS) {
            return url;
        }

        // Ignore cache if network type has changed.
        if (redirectionNetworkType != getCurrentNetworkType(context)) {
            return url;
        }

        if (redirectFrom == null || redirectTo == null) {
            return url;
        }

        return url.replace(redirectFrom, redirectTo);
    }
    private int getCurrentNetworkType(Context context) {
        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();
        return networkInfo == null ? -1 : networkInfo.getType();
    }
}
