package net.sourceforge.subsonic.service;

import net.sourceforge.subsonic.*;
import net.sourceforge.subsonic.domain.*;
import org.apache.commons.io.*;

import java.io.*;
import java.net.*;
import java.text.*;
import java.util.*;
import java.util.regex.*;

/**
 * Provides version-related services, including functionality for determining whether a newer
 * version of Subsonic is available.
 *
 * @author Sindre Mehus
 */
public class VersionService {

    private static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyyMMdd");
    private static final Logger LOG = Logger.getLogger(VersionService.class);

    private Version localVersion;
    private Version latestVersion;
    private Date localBuildDate;
    private String localBuildNumber;

    /** Time when latest version was fetched (in milliseconds). */
    private long lastVersionFetched;

    /** Only fetch last version this often (in milliseconds.).*/
    private static final long LAST_VERSION_FETCH_INTERVAL = 7 * 24 * 3600 * 1000; // One week

    /**
     * Returns the version number for the locally installed Subsonic version.
     * @return The version number for the locally installed Subsonic version.
     */
    public synchronized Version getLocalVersion() {
        if (localVersion == null) {
            try {
                localVersion = new Version(readLineFromResource("/version.txt"));
            } catch (Exception x) {
                LOG.warn("Failed to resolve local Subsonic version.", x);
            }
        }
        return localVersion;
    }

    /**
     * Returns the version number for the latest available Subsonic version.
     * @return The version number for the latest available Subsonic version, or <code>null</code>
     * if the version number can't be resolved.
     */
    public synchronized Version getLatestVersion() {
        long now = System.currentTimeMillis();
        if (latestVersion == null || now - lastVersionFetched > LAST_VERSION_FETCH_INTERVAL) {
            try {
                latestVersion = readLatestVersion();
                lastVersionFetched = now;
                LOG.info("Resolved latest Subsonic version to: " + latestVersion);
            } catch (Exception x) {
                LOG.warn("Failed to resolve latest Subsonic version.", x);
            }
        }
        return latestVersion;
    }

    /**
     * Returns the build date for the locally installed Subsonic version.
     * @return The build date for the locally installed Subsonic version, or <code>null</code>
     * if the build date can't be resolved.
     */
    public synchronized Date getLocalBuildDate() {
        if (localBuildDate == null) {
            try {
                String date = readLineFromResource("/build_date.txt");
                localBuildDate = DATE_FORMAT.parse(date);
            } catch (Exception x) {
                LOG.warn("Failed to resolve local Subsonic build date.", x);
            }
        }
        return localBuildDate;
    }

    /**
     * Returns the build number for the locally installed Subsonic version.
     * @return The build number for the locally installed Subsonic version, or <code>null</code>
     * if the build number can't be resolved.
     */
    public synchronized String getLocalBuildNumber() {
        if (localBuildNumber == null) {
            try {
                localBuildNumber = readLineFromResource("/build_number.txt");
            } catch (Exception x) {
                LOG.warn("Failed to resolve local Subsonic build number.", x);
            }
        }
        return localBuildNumber;
    }

    /**
     * Returns whether a new version of Subsonic is available.
     * @return Whether a new version of Subsonic is available.
     */
    public boolean isNewVersionAvailable() {
        Version latest = getLatestVersion();
        Version local = getLocalVersion();

        if (latest == null || local == null) {
            return false;
        }

        return local.compareTo(latest) < 0;
    }

    /**
     * Reads the first line from the resource with the given name.
     * @param resourceName The resource name.
     * @return The first line of the resource.
     */
    private String readLineFromResource(String resourceName) {
        InputStream in = VersionService.class.getResourceAsStream(resourceName);
        if (in == null) {
            return null;
        }
        BufferedReader reader = null;
        try {

            reader = new BufferedReader(new InputStreamReader(in));
            return reader.readLine();

        } catch (IOException x) {
            return null;
        } finally {
            IOUtils.closeQuietly(reader);
            IOUtils.closeQuietly(in);
        }
    }

    /**
     * Returns the latest available Subsonic version by screen-scraping a web page.
     * @return The latest available Subsonic version.
     * @throws IOException If an I/O error occurs.
     */
    private Version readLatestVersion() throws IOException {
        URL url = new URL("http://subsonic.sourceforge.net/version.html");
        BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));
        Pattern pattern = Pattern.compile("SUBSONIC_FULL_VERSION_BEGIN(.*)SUBSONIC_FULL_VERSION_END");

        try {

            String line = reader.readLine();
            while (line != null) {
                Matcher matcher = pattern.matcher(line);
                if (matcher.find()) {
                    return new Version(matcher.group(1));
                }
                line = reader.readLine();
            }

        } finally {
            reader.close();
        }
        return null;
    }
}