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
package net.sourceforge.subsonic.service;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;

import net.sourceforge.subsonic.Logger;
import net.sourceforge.subsonic.domain.MusicFile;
import net.sourceforge.subsonic.domain.UserSettings;
import net.sourceforge.subsonic.util.StringUtil;

/**
 * Provides services for "audioscrobbling", which is the process of
 * registering what songs are played at www.last.fm.
 * <p/>
 * See http://www.last.fm/api/submissions
 *
 * @author Sindre Mehus
 */
public class AudioScrobblerService {

    private static final Logger LOG = Logger.getLogger(AudioScrobblerService.class);
    private static final int MAX_PENDING_REGISTRATION = 2000;
    private static final long MIN_REGISTRATION_INTERVAL = 30000L;

    private RegistrationThread thread;
    private final Map<String, Long> lastRegistrationTimes = new HashMap<String, Long>();
    private final LinkedBlockingQueue<RegistrationData> queue = new LinkedBlockingQueue<RegistrationData>();

    private SettingsService settingsService;

    /**
     * Registers the given music file at www.last.fm. This method returns immediately, the actual registration is done
     * by a separate thread.
     *
     * @param musicFile The music file to register.
     * @param username  The user which played the music file.
     */
    public synchronized void register(MusicFile musicFile, String username) {

        if (thread == null) {
            thread = new RegistrationThread();
            thread.start();
        }

        if (queue.size() >= MAX_PENDING_REGISTRATION) {
            LOG.warn("Last.fm scrobbler queue is full. Ignoring " + musicFile);
            return;
        }

        RegistrationData registrationData = createRegistrationData(musicFile, username);
        if (registrationData == null) {
            return;
        }

        try {
            queue.put(registrationData);
        } catch (InterruptedException x) {
            LOG.warn("Interrupted while queuing Last.fm scrobble.", x);
        }
    }

    /**
     * Returns registration details, or <code>null</code> if not eligible for registration.
     */
    private RegistrationData createRegistrationData(MusicFile musicFile, String username) {

        MusicFile.MetaData metaData = musicFile.getMetaData();
        if (metaData == null) {
            return null;
        }

        UserSettings userSettings = settingsService.getUserSettings(username);
        if (!userSettings.isLastFmEnabled() || userSettings.getLastFmUsername() == null || userSettings.getLastFmUsername() == null) {
            return null;
        }

        // Don't register more often than every 30 seconds.
        Long lastRegistrationTime = lastRegistrationTimes.get(username);
        long now = System.currentTimeMillis();
        if (lastRegistrationTime != null && now - lastRegistrationTime < MIN_REGISTRATION_INTERVAL) {
            return null;
        }
        lastRegistrationTimes.put(username, now);

        RegistrationData reg = new RegistrationData();
        reg.username = userSettings.getLastFmUsername();
        reg.password = userSettings.getLastFmPassword();
        reg.artist = metaData.getArtist();
        reg.album = metaData.getAlbum();
        reg.title = metaData.getTitle();
        reg.duration = metaData.getDuration() == null ? 0 : metaData.getDuration();
        reg.time = new Date(now);

        return reg;
    }

    /**
     * Scrobbles the given song data at last.fm, using the protocol defined at http://www.last.fm/api/submissions.
     *
     * @param registrationData Registration data for the song.
     */
    private void scrobble(RegistrationData registrationData) throws Exception {
        if (registrationData == null) {
            return;
        }

        String clientId = "sub";
        String clientVersion = "0.1";
        long timestamp = System.currentTimeMillis() / 1000L;
        String authToken = calculateAuthenticationToken(registrationData.password, timestamp);
        String[] lines = executeGetRequest("http://post.audioscrobbler.com/?hs=true&p=1.2.1&c=" + clientId + "&v=" +
                clientVersion + "&u=" + registrationData.username + "&t=" + timestamp + "&a=" + authToken);

        if (lines[0].startsWith("BANNED")) {
            LOG.warn("Failed to scrobble song '" + registrationData.title + "' at Last.fm. Client version is banned.");
            return;
        }

        if (lines[0].startsWith("BADAUTH")) {
            LOG.warn("Failed to scrobble song '" + registrationData.title + "' at Last.fm. Wrong username or password.");
            return;
        }

        if (lines[0].startsWith("BADTIME")) {
            LOG.warn("Failed to scrobble song '" + registrationData.title + "' at Last.fm. Bad timestamp, please check local clock.");
            return;
        }

        if (lines[0].startsWith("FAILED")) {
            LOG.warn("Failed to scrobble song '" + registrationData.title + "' at Last.fm: " + lines[0]);
            return;
        }

        if (!lines[0].startsWith("OK")) {
            LOG.warn("Failed to scrobble song '" + registrationData.title + "' at Last.fm.  Unknown response: " + lines[0]);
            return;
        }

        String sessionId = lines[1];
        String nowPlayingUrl = lines[2];
        String submissionUrl = lines[3];

        Map<String, String> params = new HashMap<String, String>();
        params.put("s", sessionId);
        params.put("a[0]", registrationData.artist);
        params.put("t[0]", registrationData.title);
        params.put("i[0]", String.valueOf(registrationData.time.getTime() / 1000L));
        params.put("o[0]", "P");
        params.put("r[0]", "");
        params.put("l[0]", String.valueOf(registrationData.duration));
        params.put("b[0]", registrationData.album);
        params.put("n[0]", "");
        params.put("m[0]", "");

        lines = executePostRequest(submissionUrl, params);

        if (lines[0].startsWith("FAILED")) {
            LOG.warn("Failed to scrobble song '" + registrationData.title + "' at Last.fm: " + lines[0]);
        } else if (lines[0].startsWith("BADSESSION")) {
            LOG.warn("Failed to scrobble song '" + registrationData.title + "' at Last.fm.  Invalid session.");
        } else if (lines[0].startsWith("OK")) {
            LOG.debug("Successfully scrobbled song '" + registrationData.title + "' for user " + registrationData.username + " at Last.fm.");
        }
    }

    private String calculateAuthenticationToken(String password, long timestamp) {
        return DigestUtils.md5Hex(DigestUtils.md5Hex(password) + timestamp);
    }

    private String[] executeGetRequest(String url) throws IOException {
        return executeRequest(new GetMethod(url));
    }

    private String[] executePostRequest(String url, Map<String, String> parameters) throws IOException {
        PostMethod method = new PostMethod(url);

        for (Map.Entry<String, String> entry : parameters.entrySet()) {
            method.addParameter(entry.getKey(), entry.getValue());
        }
        return executeRequest(method);
    }

    private String[] executeRequest(HttpMethod method) throws IOException {
        HttpClient client = new HttpClient();
        client.getParams().setContentCharset(StringUtil.ENCODING_UTF8);

        try {
            int statusCode = client.executeMethod(method);

            if (statusCode != HttpStatus.SC_OK) {
                throw new IOException("Method failed: " + method.getStatusLine());
            }

            String response = method.getResponseBodyAsString();
            return response.split("\\n");

        } finally {
            method.releaseConnection();
        }

    }

    public void setSettingsService(SettingsService settingsService) {
        this.settingsService = settingsService;
    }

    private class RegistrationThread extends Thread {
        private RegistrationThread() {
            super("AudioScrobbler Registration");
        }

        @Override
        public void run() {
            while (true) {
                RegistrationData registrationData = null;
                try {
                    registrationData = queue.take();
                    scrobble(registrationData);
                } catch (IOException x) {
                    handleNetworkError(registrationData, x);
                } catch (Exception x) {
                    LOG.warn("Error in Last.fm registration.", x);
                }
            }
        }

        private void handleNetworkError(RegistrationData registrationData, IOException x) {
            try {
                queue.put(registrationData);
                LOG.info("Last.fm registration for " + registrationData.title +
                        " encountered network error.  Will try again later. In queue: " + queue.size(), x);
            } catch (InterruptedException e) {
                LOG.error("Failed to reschedule Last.fm registration for " + registrationData.title, e);
            }
            try {
                sleep(15L * 60L * 1000L);  // Wait 15 minutes.
            } catch (InterruptedException e) {
                LOG.error("Failed to sleep after Last.fm registration failure for " + registrationData.title, e);
            }
        }
    }

    private static class RegistrationData {
        private String username;
        private String password;
        private String artist;
        private String album;
        private String title;
        private int duration;
        private Date time;
    }

    // TODO: REMOVE
    public static void main(String[] args) throws Exception {
        AudioScrobblerService service = new AudioScrobblerService();

        RegistrationData regData = new RegistrationData();
        regData.username = "sindre_mehus";
        regData.password = "harmo9sk";
        regData.artist = "Sex Pistols";
        regData.album = "Never Mind The Bollocks";
        regData.title = "Problems";
        regData.duration = 179;
        regData.time = new Date();

        service.scrobble(regData);
    }

}