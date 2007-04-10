package net.sourceforge.subsonic.service;

import net.sourceforge.subsonic.Logger;
import net.sourceforge.subsonic.domain.MusicFile;
import net.sourceforge.subsonic.domain.UserSettings;
import net.sourceforge.subsonic.util.StringUtil;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.lang.StringUtils;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Provides services for "audioscrobbling", which is the process of
 * registering what songs are played at www.last.fm.
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

    /** Returns registration details, or <code>null</code> if not eligible for registration. */
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
     * Scrobbles the given song data at last.fm, using the protocol defined at http://www.audioscrobbler.net/wiki/Protocol1.1.merged.
     *
     * @param registrationData Registration data for the song.
     * @return The number of seconds last.fm instructs us to sleep before the next registration.
     */
    private int scrobble(RegistrationData registrationData) throws Exception {
        if (registrationData == null) {
            return 0;
        }

        String clientId = "sub";
        String clientVersion = "0.1";
        String[] lines = executeGetRequest("http://post.audioscrobbler.com/?hs=true&p=1.1&c=" + clientId + "&v=" +
                                           clientVersion + "&u=" + registrationData.username);

        if (lines[0].startsWith("BADUSER")) {
            LOG.warn("Failed to scrobble song '" + registrationData.title + "' at Last.fm. Wrong username: " + registrationData.username);
            return parseSleepInterval(lines);
        }

        if (lines[0].startsWith("FAILED")) {
            LOG.warn("Failed to scrobble song '" + registrationData.title + "' at Last.fm: " + lines[0]);
            return parseSleepInterval(lines);
        }

        if (!lines[0].startsWith("UPDATE") && !lines[0].startsWith("UPTODATE")) {
            LOG.warn("Failed to scrobble song '" + registrationData.title + "' at Last.fm.  Unknown response: " + lines[0]);
            return 1;
        }

        String md5Challenge = lines[1];
        String url = lines[2];
        String md5Response = calculateMD5Response(md5Challenge, registrationData.password);

        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        String time = dateFormat.format(registrationData.time);

        Map<String, String> params = new HashMap<String, String>();
        params.put("u", registrationData.username);
        params.put("s", md5Response);
        params.put("a[0]", registrationData.artist);
        params.put("t[0]", registrationData.title);
        params.put("b[0]", registrationData.album);
        params.put("m[0]", "");
        params.put("l[0]", String.valueOf(registrationData.duration));
        params.put("i[0]", time);

        lines = executePostRequest(url, params);

        if (lines[0].startsWith("FAILED")) {
            LOG.warn("Failed to scrobble song '" + registrationData.title + "' at Last.fm: " + lines[0]);
        } else if (lines[0].startsWith("BADAUTH")) {
            LOG.warn("Failed to scrobble song '" + registrationData.title + "' at Last.fm.  Wrong password.");
        } else if (lines[0].startsWith("OK")) {
            LOG.debug("Successfully scrobbled song '" + registrationData.title + "' for user " + registrationData.username + " at Last.fm.");
        }

        return parseSleepInterval(lines);
    }


    /** Parses a string containing the sleep interval, e.g., "INTERVAL 10". */
    private int parseSleepInterval(String[] lines) {
        if (lines.length == 0) {
            return 0;
        }

        String lastLine = StringUtils.trimToEmpty(lines[lines.length - 1]);
        if (lastLine.startsWith("INTERVAL ")) {
            return Integer.valueOf(lastLine.substring(9));
        }
        return 0;
    }

    private String calculateMD5Response(String md5Challenge, String password) {
        return DigestUtils.md5Hex(DigestUtils.md5Hex(password) + md5Challenge);
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

        public void run() {
            while (true) {
                RegistrationData registrationData = null;
                try {
                    registrationData = queue.take();
                    int sleepInterval = scrobble(registrationData);
                    if (sleepInterval > 0) {
                        sleep(sleepInterval * 1000);
                    }
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
}