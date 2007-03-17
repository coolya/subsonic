package net.sourceforge.subsonic.service;

import net.sourceforge.subsonic.Logger;
import net.sourceforge.subsonic.domain.MusicFile;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;

import java.io.IOException;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Provides services for generating ads with Google AdSense program.
 *
 * @author Sindre Mehus
 */
public class AdService {

    private static final Logger LOG = Logger.getLogger(AdService.class);
    private static final long GOOGLE_CHECK_DELAY = 60L * 1000L;
    private static final long GOOGLE_CHECK_PERIOD = 10L * 60L * 1000L;
    private static final String GOOGLE_CHECK_URL = "http://pagead2.googlesyndication.com/pagead/show_ads.js";

    private int adInterval;
    private List<String> adReferrers;

    private int pageCount;

    private int referrerIndex;
    private boolean googleOnline = false;

    public AdService() {

        // Start a period task which checks connectivity to Google Adsense server.
        Timer timer = new Timer("Google Adsense poller", true);
        TimerTask task = new TimerTask() {
            public void run() {
                checkGoogleConnectivity();
            }
        };
        timer.schedule(task, GOOGLE_CHECK_DELAY, GOOGLE_CHECK_PERIOD);
    }

    /**
     * Returns an ad referrer for the given file, or <code>null</code> if
     * no ads should be displayed.
     */
    public String getAdReferrer(MusicFile dir) throws IOException {
        if (dir.isAlbum() && isGoogleOnline() && pageCount++ % adInterval == 0) {
            referrerIndex = (referrerIndex + 1) % adReferrers.size();
            return adReferrers.get(referrerIndex);
        }

        return null;
    }

    /**
     * Returns whether the Google ad server is reachable. What really matters is
     * whether the client (browser) can reach the ad server, but we assume that this
     * will correlate strongly with whether the Subsonic server can reach the
     * ad server.
     *
     * @return Whether the Google ad server is reachable.
     */
    private boolean isGoogleOnline() {
        return googleOnline;
    }

    private void checkGoogleConnectivity() {
        GetMethod method = new GetMethod(GOOGLE_CHECK_URL);
        HttpClient client = new HttpClient();

        try {
            client.executeMethod(method);
            if (!googleOnline) {
                googleOnline = true;
                LOG.info("Connection to Google Adsense OK.");
            }
        } catch (Exception x) {
            if (googleOnline) {
                googleOnline = false;
                LOG.info("Connection to Google Adsense not OK.");
            }
        } finally {
            method.releaseConnection();
        }
    }

    /** Set by Spring. */
    public void setAdInterval(int adInterval) {
        this.adInterval = adInterval;
    }

    /** Set by Spring. */
    public void setAdReferrers(List<String> adReferrers) {
        this.adReferrers = adReferrers;
    }
}