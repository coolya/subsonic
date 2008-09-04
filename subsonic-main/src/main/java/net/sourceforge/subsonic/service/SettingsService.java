package net.sourceforge.subsonic.service;

import net.sourceforge.subsonic.Logger;
import net.sourceforge.subsonic.dao.InternetRadioDao;
import net.sourceforge.subsonic.dao.MusicFolderDao;
import net.sourceforge.subsonic.dao.UserDao;
import net.sourceforge.subsonic.dao.AvatarDao;
import net.sourceforge.subsonic.domain.InternetRadio;
import net.sourceforge.subsonic.domain.MusicFolder;
import net.sourceforge.subsonic.domain.Theme;
import net.sourceforge.subsonic.domain.UserSettings;
import net.sourceforge.subsonic.domain.Avatar;
import net.sourceforge.subsonic.util.StringUtil;
import net.sourceforge.subsonic.util.Util;
import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Properties;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.Date;
import java.util.Arrays;

/**
 * Provides persistent storage of application settings and preferences.
 *
 * @author Sindre Mehus
 */
public class SettingsService {

    // Subsonic home directory.
    private static final File SUBSONIC_HOME_WINDOWS = new File("c:/subsonic");
    private static final File SUBSONIC_HOME_OTHER = new File("/var/subsonic");

    // Global settings.
    private static final String KEY_INDEX_STRING = "IndexString";
    private static final String KEY_IGNORED_ARTICLES = "IgnoredArticles";
    private static final String KEY_SHORTCUTS = "Shortcuts";
    private static final String KEY_PLAYLIST_FOLDER = "PlaylistFolder";
    private static final String KEY_MUSIC_MASK = "MusicMask";
    private static final String KEY_COVER_ART_MASK = "CoverArtMask";
    private static final String KEY_COVER_ART_LIMIT = "CoverArtLimit";
    private static final String KEY_WELCOME_MESSAGE = "WelcomeMessage";
    private static final String KEY_LOCALE_LANGUAGE = "LocaleLanguage";
    private static final String KEY_LOCALE_COUNTRY = "LocaleCountry";
    private static final String KEY_LOCALE_VARIANT = "LocaleVariant";
    private static final String KEY_THEME_ID = "Theme";
    private static final String KEY_INDEX_CREATION_INTERVAL = "IndexCreationInterval";
    private static final String KEY_INDEX_CREATION_HOUR = "IndexCreationHour";
    private static final String KEY_PODCAST_UPDATE_INTERVAL = "PodcastUpdateInterval";
    private static final String KEY_PODCAST_FOLDER = "PodcastFolder";
    private static final String KEY_PODCAST_EPISODE_RETENTION_COUNT = "PodcastEpisodeRetentionCount";
    private static final String KEY_PODCAST_EPISODE_DOWNLOAD_COUNT = "PodcastEpisodeDownloadCount";
    private static final String KEY_DOWNLOAD_BITRATE_LIMIT = "DownloadBitrateLimit";
    private static final String KEY_UPLOAD_BITRATE_LIMIT = "UploadBitrateLimit";
    private static final String KEY_STREAM_PORT = "StreamPort";
    private static final String KEY_LICENSE_EMAIL = "LicenseEmail";
    private static final String KEY_LICENSE_CODE = "LicenseCode";
    private static final String KEY_LICENSE_DATE = "LicenseDate";
    private static final String KEY_DOWNSAMPLING_COMMAND = "DownsamplingCommand";
    private static final String KEY_REWRITE_URL = "RewriteUrl";
    private static final String KEY_LDAP_ENABLED = "LdapEnabled";
    private static final String KEY_LDAP_URL = "LdapUrl";
    private static final String KEY_LDAP_MANAGER_DN = "LdapManagerDn";
    private static final String KEY_LDAP_MANAGER_PASSWORD = "LdapManagerPassword";
    private static final String KEY_LDAP_SEARCH_FILTER = "LdapSearchFilter";
    private static final String KEY_LDAP_AUTO_SHADOWING = "LdapAutoShadowing";

    // Default values.
    private static final String DEFAULT_INDEX_STRING = "A B C D E F G H I J K L M N O P Q R S T U V W X-Z(XYZ)";
    private static final String DEFAULT_IGNORED_ARTICLES = "The El La Los Las Le Les";
    private static final String DEFAULT_SHORTCUTS = "New Incoming Podcast";
    private static final String DEFAULT_PLAYLIST_FOLDER = Util.isRipserver() ? "/media/Playlists" : "c:/playlists";
    private static final String DEFAULT_MUSIC_MASK = ".mp3 .ogg .aac .flac .m4a .wav .wma";
    private static final String DEFAULT_COVER_ART_MASK = "folder.jpg cover.jpg .jpg .jpeg .gif .png";
    private static final int DEFAULT_COVER_ART_LIMIT = 30;
    private static final String DEFAULT_WELCOME_MESSAGE = "Welcome to Subsonic!";
    private static final String DEFAULT_LOCALE_LANGUAGE = "en";
    private static final String DEFAULT_LOCALE_COUNTRY = "";
    private static final String DEFAULT_LOCALE_VARIANT = "";
    private static final String DEFAULT_THEME_ID = Util.isRipserver() ? "ripserver" : "default";
    private static final int DEFAULT_INDEX_CREATION_INTERVAL = 1;
    private static final int DEFAULT_INDEX_CREATION_HOUR = 3;
    private static final int DEFAULT_PODCAST_UPDATE_INTERVAL = 24;
    private static final String DEFAULT_PODCAST_FOLDER = Util.isRipserver() ? "/media/Music/Podcast" : "c:/music/Podcast";
    private static final int DEFAULT_PODCAST_EPISODE_RETENTION_COUNT = 10;
    private static final int DEFAULT_PODCAST_EPISODE_DOWNLOAD_COUNT = 1;
    private static final long DEFAULT_DOWNLOAD_BITRATE_LIMIT = 0;
    private static final long DEFAULT_UPLOAD_BITRATE_LIMIT = 0;
    private static final long DEFAULT_STREAM_PORT = 0;
    private static final String DEFAULT_LICENSE_EMAIL = Util.isRipserver() ? "Ripserver End User" : null;
    private static final String DEFAULT_LICENSE_CODE = Util.isRipserver() ? "7ff4a6e00112a3757438278dcfae13c4" : null;
    private static final String DEFAULT_LICENSE_DATE = Util.isRipserver() ? "1220546796952" : null;
    private static final String DEFAULT_DOWNSAMPLING_COMMAND = "lame -S -h -b %b %s -";
    private static final boolean DEFAULT_REWRITE_URL = true;
    private static final boolean DEFAULT_LDAP_ENABLED = false;
    private static final String DEFAULT_LDAP_URL = "ldap://host.domain.com:389/cn=Users,dc=domain,dc=com";
    private static final String DEFAULT_LDAP_MANAGER_DN = null;
    private static final String DEFAULT_LDAP_MANAGER_PASSWORD = null;
    private static final String DEFAULT_LDAP_SEARCH_FILTER = "(sAMAccountName={0})";
    private static final boolean DEFAULT_LDAP_AUTO_SHADOWING = false;

    // Array of all keys.  Used to clean property file.
    private static final String[] KEYS = {KEY_INDEX_STRING, KEY_IGNORED_ARTICLES, KEY_SHORTCUTS, KEY_PLAYLIST_FOLDER, KEY_MUSIC_MASK,
                                          KEY_COVER_ART_MASK, KEY_COVER_ART_LIMIT, KEY_WELCOME_MESSAGE, KEY_LOCALE_LANGUAGE,
                                          KEY_LOCALE_COUNTRY, KEY_LOCALE_VARIANT, KEY_THEME_ID, KEY_INDEX_CREATION_INTERVAL, KEY_INDEX_CREATION_HOUR,
                                          KEY_PODCAST_UPDATE_INTERVAL, KEY_PODCAST_FOLDER, KEY_PODCAST_EPISODE_RETENTION_COUNT,
                                          KEY_PODCAST_EPISODE_DOWNLOAD_COUNT, KEY_DOWNLOAD_BITRATE_LIMIT, KEY_UPLOAD_BITRATE_LIMIT, KEY_STREAM_PORT,
                                          KEY_LICENSE_EMAIL, KEY_LICENSE_CODE, KEY_LICENSE_DATE, KEY_DOWNSAMPLING_COMMAND, KEY_REWRITE_URL,
                                          KEY_LDAP_ENABLED, KEY_LDAP_URL, KEY_LDAP_MANAGER_DN, KEY_LDAP_MANAGER_PASSWORD, KEY_LDAP_SEARCH_FILTER, KEY_LDAP_AUTO_SHADOWING
    };

    private static final String LOCALES_FILE = "/net/sourceforge/subsonic/i18n/locales.txt";
    private static final String THEMES_FILE = "/net/sourceforge/subsonic/theme/themes.txt";

    private static final Logger LOG = Logger.getLogger(SettingsService.class);

    private Properties properties = new Properties();
    private List<Theme> themes;
    private List<Locale> locales;
    private InternetRadioDao internetRadioDao;
    private MusicFolderDao musicFolderDao;
    private UserDao userDao;
    private AvatarDao avatarDao;

    private String[] cachedCoverArtMaskArray;
    private String[] cachedMusicMaskArray;
    private static File subsonicHome;
    private long settingsLastChanged;

    public SettingsService() {
        File propertyFile = getPropertyFile();

        if (propertyFile.exists()) {
            FileInputStream in = null;
            try {
                in = new FileInputStream(propertyFile);
                properties.load(in);
            } catch (Exception x) {
                LOG.error("Unable to read from property file.", x);
            } finally {
                IOUtils.closeQuietly(in);
            }

            // Remove obsolete properties.
            Set<String> allowedKeys = new HashSet<String>(Arrays.asList(KEYS));

            for (Iterator<Object> iterator = properties.keySet().iterator(); iterator.hasNext();) {
                String key = (String) iterator.next();
                if (!allowedKeys.contains(key)) {
                    LOG.debug("Removing obsolete property [" + key + ']');
                    iterator.remove();
                }
            }
        }

        save();
    }

    /**
     * Register in service locator so that non-Spring objects can access me.
     * This method is invoked automatically by Spring.
     */
    public void init() {
        ServiceLocator.setSettingsService(this);
    }

    public void save() {
        OutputStream out = null;

        try {
            out = new FileOutputStream(getPropertyFile());
            properties.store(out, "Subsonic preferences.  NOTE: This file is automatically generated.");
        } catch (Exception x) {
            LOG.error("Unable to write to property file.", x);
        } finally {
            IOUtils.closeQuietly(out);
        }

        settingsChanged();
    }

    private File getPropertyFile() {
        return new File(getSubsonicHome(), "subsonic.properties");
    }

    /**
     * Returns the Subsonic home directory.
     *
     * @return The Subsonic home directory, if it exists.
     * @throws RuntimeException If directory doesn't exist.
     */
    public static synchronized File getSubsonicHome() {

        if (subsonicHome != null) {
            return subsonicHome;
        }

        File home;

        String overrideHome = System.getProperty("subsonic.home");
        if (overrideHome != null) {
            home = new File(overrideHome);
        } else {
            boolean isWindows = System.getProperty("os.name", "Windows").toLowerCase().startsWith("windows");
            home = isWindows ? SUBSONIC_HOME_WINDOWS : SUBSONIC_HOME_OTHER;
        }

        // Attempt to create home directory if it doesn't exist.
        if (!home.exists() || !home.isDirectory()) {
            boolean success = home.mkdirs();
            if (success) {
                subsonicHome = home;
            } else {
                String message = "The directory " + home + " does not exist. Please create it and make it writable. " +
                                 "(You can override the directory location by specifying -Dsubsonic.home=... when " +
                                 "starting the servlet container.)";
                System.err.println("ERROR: " + message);
            }
        } else {
            subsonicHome = home;
        }

        return home;
    }

    public String getIndexString() {
        return properties.getProperty(KEY_INDEX_STRING, DEFAULT_INDEX_STRING);
    }

    public void setIndexString(String indexString) {
        setProperty(KEY_INDEX_STRING, indexString);
    }

    public String getIgnoredArticles() {
        return properties.getProperty(KEY_IGNORED_ARTICLES, DEFAULT_IGNORED_ARTICLES);
    }

    public String[] getIgnoredArticlesAsArray() {
        return getIgnoredArticles().split("\\s+");
    }

    public void setIgnoredArticles(String ignoredArticles) {
        setProperty(KEY_IGNORED_ARTICLES, ignoredArticles);
    }

    public String getShortcuts() {
        return properties.getProperty(KEY_SHORTCUTS, DEFAULT_SHORTCUTS);
    }

    public String[] getShortcutsAsArray() {
        return StringUtil.split(getShortcuts());
    }

    public void setShortcuts(String shortcuts) {
        setProperty(KEY_SHORTCUTS, shortcuts);
    }

    public String getPlaylistFolder() {
        return properties.getProperty(KEY_PLAYLIST_FOLDER, DEFAULT_PLAYLIST_FOLDER);
    }

    public void setPlaylistFolder(String playlistFolder) {
        setProperty(KEY_PLAYLIST_FOLDER, playlistFolder);
    }

    public String getMusicMask() {
        return properties.getProperty(KEY_MUSIC_MASK, DEFAULT_MUSIC_MASK);
    }

    public synchronized void setMusicMask(String mask) {
        setProperty(KEY_MUSIC_MASK, mask);
        cachedMusicMaskArray = null;
    }

    public synchronized String[] getMusicMaskAsArray() {
        if (cachedMusicMaskArray == null) {
            cachedMusicMaskArray = toStringArray(getMusicMask());
        }
        return cachedMusicMaskArray;
    }

    public String getCoverArtMask() {
        return properties.getProperty(KEY_COVER_ART_MASK, DEFAULT_COVER_ART_MASK);
    }

    public synchronized void setCoverArtMask(String mask) {
        setProperty(KEY_COVER_ART_MASK, mask);
        cachedCoverArtMaskArray = null;
    }

    public synchronized String[] getCoverArtMaskAsArray() {
        if (cachedCoverArtMaskArray == null) {
            cachedCoverArtMaskArray = toStringArray(getCoverArtMask());
        }
        return cachedCoverArtMaskArray;
    }

    public int getCoverArtLimit() {
        return Integer.parseInt(properties.getProperty(KEY_COVER_ART_LIMIT, "" + DEFAULT_COVER_ART_LIMIT));
    }

    public void setCoverArtLimit(int limit) {
        setProperty(KEY_COVER_ART_LIMIT, "" + limit);
    }

    public String getWelcomeMessage() {
        return properties.getProperty(KEY_WELCOME_MESSAGE, DEFAULT_WELCOME_MESSAGE);
    }

    public void setWelcomeMessage(String message) {
        setProperty(KEY_WELCOME_MESSAGE, message);
    }

    /**
     * Returns the number of days between automatic index creation, of -1 if automatic index
     * creation is disabled.
     */
    public int getIndexCreationInterval() {
        return Integer.parseInt(properties.getProperty(KEY_INDEX_CREATION_INTERVAL, "" + DEFAULT_INDEX_CREATION_INTERVAL));
    }

    /**
     * Sets the number of days between automatic index creation, of -1 if automatic index
     * creation is disabled.
     */
    public void setIndexCreationInterval(int days) {
        setProperty(KEY_INDEX_CREATION_INTERVAL, String.valueOf(days));
    }

    /** Returns the hour of day (0 - 23) when automatic index creation should run. */
    public int getIndexCreationHour() {
        return Integer.parseInt(properties.getProperty(KEY_INDEX_CREATION_HOUR, String.valueOf(DEFAULT_INDEX_CREATION_HOUR)));
    }

    /** Sets the hour of day (0 - 23) when automatic index creation should run. */
    public void setIndexCreationHour(int hour) {
        setProperty(KEY_INDEX_CREATION_HOUR, String.valueOf(hour));
    }

    /**
     * Returns the number of hours between Podcast updates, of -1 if automatic updates
     * are disabled.
     */
    public int getPodcastUpdateInterval() {
        return Integer.parseInt(properties.getProperty(KEY_PODCAST_UPDATE_INTERVAL, String.valueOf(DEFAULT_PODCAST_UPDATE_INTERVAL)));
    }

    /**
     * Sets the number of hours between Podcast updates, of -1 if automatic updates
     * are disabled.
     */
    public void setPodcastUpdateInterval(int hours) {
        setProperty(KEY_PODCAST_UPDATE_INTERVAL, String.valueOf(hours));
    }

    /** Returns the number of Podcast episodes to keep (-1 to keep all). */
    public int getPodcastEpisodeRetentionCount() {
        return Integer.parseInt(properties.getProperty(KEY_PODCAST_EPISODE_RETENTION_COUNT, String.valueOf(DEFAULT_PODCAST_EPISODE_RETENTION_COUNT)));
    }

    /** Sets the number of Podcast episodes to keep (-1 to keep all). */
    public void setPodcastEpisodeRetentionCount(int count) {
        setProperty(KEY_PODCAST_EPISODE_RETENTION_COUNT, String.valueOf(count));
    }

    /** Returns the number of Podcast episodes to download (-1 to download all). */
    public int getPodcastEpisodeDownloadCount() {
        return Integer.parseInt(properties.getProperty(KEY_PODCAST_EPISODE_DOWNLOAD_COUNT, String.valueOf(DEFAULT_PODCAST_EPISODE_DOWNLOAD_COUNT)));
    }

    /** Sets the number of Podcast episodes to download (-1 to download all). */
    public void setPodcastEpisodeDownloadCount(int count) {
        setProperty(KEY_PODCAST_EPISODE_DOWNLOAD_COUNT, String.valueOf(count));
    }

    /** Returns the Podcast download folder. */
    public String getPodcastFolder() {
        return properties.getProperty(KEY_PODCAST_FOLDER, DEFAULT_PODCAST_FOLDER);
    }

    /** Sets the Podcast download folder. */
    public void setPodcastFolder(String folder) {
        setProperty(KEY_PODCAST_FOLDER, folder);
    }

    /** @return The download bitrate limit in Kbit/s. Zero if unlimited. */
    public long getDownloadBitrateLimit() {
        return Long.parseLong(properties.getProperty(KEY_DOWNLOAD_BITRATE_LIMIT, "" + DEFAULT_DOWNLOAD_BITRATE_LIMIT));
    }

    /** @param limit The download bitrate limit in Kbit/s. Zero if unlimited. */
    public void setDownloadBitrateLimit(long limit) {
        setProperty(KEY_DOWNLOAD_BITRATE_LIMIT, "" + limit);
    }

    /** @return The upload bitrate limit in Kbit/s. Zero if unlimited. */
    public long getUploadBitrateLimit() {
        return Long.parseLong(properties.getProperty(KEY_UPLOAD_BITRATE_LIMIT, "" + DEFAULT_UPLOAD_BITRATE_LIMIT));
    }

    /** @param limit The upload bitrate limit in Kbit/s. Zero if unlimited. */
    public void setUploadBitrateLimit(long limit) {
        setProperty(KEY_UPLOAD_BITRATE_LIMIT, "" + limit);
    }

    /** @return The non-SSL stream port. Zero if disabled. */
    public int getStreamPort() {
        return Integer.parseInt(properties.getProperty(KEY_STREAM_PORT, "" + DEFAULT_STREAM_PORT));
    }

    /** @param port The non-SSL stream port. Zero if disabled. */
    public void setStreamPort(int port) {
        setProperty(KEY_STREAM_PORT, "" + port);
    }

    public String getLicenseEmail() {
        return properties.getProperty(KEY_LICENSE_EMAIL, DEFAULT_LICENSE_EMAIL);
    }

    public void setLicenseEmail(String email) {
        setProperty(KEY_LICENSE_EMAIL, email);
    }

    public String getLicenseCode() {
        return properties.getProperty(KEY_LICENSE_CODE, DEFAULT_LICENSE_CODE);
    }

    public void setLicenseCode(String code) {
        setProperty(KEY_LICENSE_CODE, code);
    }

    public Date getLicenseDate() {
        String value = properties.getProperty(KEY_LICENSE_DATE, DEFAULT_LICENSE_DATE);
        return value == null ? null : new Date(Long.parseLong(value));
    }

    public void setLicenseDate(Date date) {
        String value = (date == null ? null : String.valueOf(date.getTime()));
        setProperty(KEY_LICENSE_DATE, value);
    }

    public boolean isLicenseValid(String email, String license) {

        if (email == null || license == null) {
            return false;
        }

        // Little point in doing anything more fancy, since there are
        // easier ways to disable the nag messages.
        return license.equalsIgnoreCase(StringUtil.md5Hex(email.toLowerCase()));
    }

    public String getDownsamplingCommand() {
        return properties.getProperty(KEY_DOWNSAMPLING_COMMAND, DEFAULT_DOWNSAMPLING_COMMAND);
    }

    public void setDownsamplingCommand(String command) {
        setProperty(KEY_DOWNSAMPLING_COMMAND, command);
    }

    public boolean isRewriteUrlEnabled() {
        return Boolean.valueOf(properties.getProperty(KEY_REWRITE_URL, String.valueOf(DEFAULT_REWRITE_URL)));
    }

    public void setRewriteUrlEnabled(boolean rewriteUrl) {
        properties.setProperty(KEY_REWRITE_URL, String.valueOf(rewriteUrl));
    }

    public boolean isLdapEnabled() {
        return Boolean.valueOf(properties.getProperty(KEY_LDAP_ENABLED, String.valueOf(DEFAULT_LDAP_ENABLED)));
    }

    public void setLdapEnabled(boolean ldapEnabled) {
        properties.setProperty(KEY_LDAP_ENABLED, String.valueOf(ldapEnabled));
    }

    public String getLdapUrl() {
        return properties.getProperty(KEY_LDAP_URL, DEFAULT_LDAP_URL);
    }

    public void setLdapUrl(String ldapUrl) {
        properties.setProperty(KEY_LDAP_URL, ldapUrl);
    }

    public String getLdapSearchFilter() {
        return properties.getProperty(KEY_LDAP_SEARCH_FILTER, DEFAULT_LDAP_SEARCH_FILTER);
    }

    public void setLdapSearchFilter(String ldapSearchFilter) {
        properties.setProperty(KEY_LDAP_SEARCH_FILTER, ldapSearchFilter);
    }

    public String getLdapManagerDn() {
        return properties.getProperty(KEY_LDAP_MANAGER_DN, DEFAULT_LDAP_MANAGER_DN);
    }

    public void setLdapManagerDn(String ldapManagerDn) {
        properties.setProperty(KEY_LDAP_MANAGER_DN, ldapManagerDn);
    }

    public String getLdapManagerPassword() {
        String s = properties.getProperty(KEY_LDAP_MANAGER_PASSWORD, DEFAULT_LDAP_MANAGER_PASSWORD);
        try {
            return StringUtil.utf8HexDecode(s);
        } catch (Exception x) {
            LOG.warn("Failed to decode LDAP manager password.", x);
            return s;
        }
    }

    public void setLdapManagerPassword(String ldapManagerPassword) {
        try {
            ldapManagerPassword = StringUtil.utf8HexEncode(ldapManagerPassword);
        } catch (Exception x) {
            LOG.warn("Failed to encode LDAP manager password.", x);
        }
        properties.setProperty(KEY_LDAP_MANAGER_PASSWORD, ldapManagerPassword);
    }

    public boolean isLdapAutoShadowing() {
        return Boolean.valueOf(properties.getProperty(KEY_LDAP_AUTO_SHADOWING, String.valueOf(DEFAULT_LDAP_AUTO_SHADOWING)));
    }

    public void setLdapAutoShadowing(boolean ldapAutoShadowing) {
        properties.setProperty(KEY_LDAP_AUTO_SHADOWING, String.valueOf(ldapAutoShadowing));
    }

    /**
    * Returns the locale (for language, date format etc).
    *
    * @return The locale.
    */
    public Locale getLocale() {
        String language = properties.getProperty(KEY_LOCALE_LANGUAGE, DEFAULT_LOCALE_LANGUAGE);
        String country = properties.getProperty(KEY_LOCALE_COUNTRY, DEFAULT_LOCALE_COUNTRY);
        String variant = properties.getProperty(KEY_LOCALE_VARIANT, DEFAULT_LOCALE_VARIANT);

        return new Locale(language, country, variant);
    }

    /**
     * Sets the locale (for language, date format etc.)
     *
     * @param locale The locale.
     */
    public void setLocale(Locale locale) {
        setProperty(KEY_LOCALE_LANGUAGE, locale.getLanguage());
        setProperty(KEY_LOCALE_COUNTRY, locale.getCountry());
        setProperty(KEY_LOCALE_VARIANT, locale.getVariant());
    }

    /**
     * Returns the ID of the theme to use.
     *
     * @return The theme ID.
     */
    public String getThemeId() {
        return properties.getProperty(KEY_THEME_ID, DEFAULT_THEME_ID);
    }

    /**
     * Sets the ID of the theme to use.
     *
     * @param themeId The theme ID
     */
    public void setThemeId(String themeId) {
        setProperty(KEY_THEME_ID, themeId);
    }

    /**
     * Returns a list of available themes.
     *
     * @return A list of available themes.
     */
    public synchronized Theme[] getAvailableThemes() {
        if (themes == null) {
            themes = new ArrayList<Theme>();
            try {
                InputStream in = SettingsService.class.getResourceAsStream(THEMES_FILE);
                String[] lines = StringUtil.readLines(in);
                for (String line : lines) {
                    String[] elements = StringUtil.split(line);
                    if (elements.length == 2) {
                        themes.add(new Theme(elements[0], elements[1]));
                    } else {
                        LOG.warn("Failed to parse theme from line: [" + line + "].");
                    }
                }
            } catch (IOException x) {
                LOG.error("Failed to resolve list of themes.", x);
                themes.add(new Theme("default", "Subsonic default"));
            }
        }
        return themes.toArray(new Theme[themes.size()]);
    }

    /**
     * Returns a list of available locales.
     *
     * @return A list of available locales.
     */
    public synchronized Locale[] getAvailableLocales() {
        if (locales == null) {
            locales = new ArrayList<Locale>();
            try {
                InputStream in = SettingsService.class.getResourceAsStream(LOCALES_FILE);
                String[] lines = StringUtil.readLines(in);

                for (String line : lines) {
                    locales.add(parseLocale(line));
                }

            } catch (IOException x) {
                LOG.error("Failed to resolve list of locales.", x);
                locales.add(Locale.ENGLISH);
            }
        }
        return locales.toArray(new Locale[locales.size()]);
    }

    private Locale parseLocale(String line) {
        String[] s = line.split("_");
        String language = s[0];
        String country = "";
        String variant = "";

        if (s.length > 1) {
            country = s[1];
        }
        if (s.length > 2) {
            variant = s[2];
        }
        return new Locale(language, country, variant);
    }


    /**
     * Returns all music folders. Non-existing and disabled folders are not included.
     *
     * @return Possibly empty array of all music folders.
     */
    public MusicFolder[] getAllMusicFolders() {
        return getAllMusicFolders(false);
    }

    /**
     * Returns all music folders.
     *
     * @param includeAll Whether non-existing and disabled folders should be included.
     * @return Possibly empty array of all music folders.
     */
    public MusicFolder[] getAllMusicFolders(boolean includeAll) {
        MusicFolder[] all = musicFolderDao.getAllMusicFolders();
        List<MusicFolder> result = new ArrayList<MusicFolder>(all.length);
        for (MusicFolder folder : all) {
            if (includeAll || folder.isEnabled() && folder.getPath().exists()) {
                result.add(folder);
            }
        }
        return result.toArray(new MusicFolder[result.size()]);
    }

    /**
     * Returns the music folder with the given ID.
     *
     * @param id The ID.
     * @return The music folder with the given ID, or <code>null</code> if not found.
     */
    public MusicFolder getMusicFolderById(Integer id) {
        MusicFolder[] all = getAllMusicFolders();
        for (MusicFolder folder : all) {
            if (id.equals(folder.getId())) {
                return folder;
            }
        }
        return null;
    }

    /**
     * Creates a new music folder.
     *
     * @param musicFolder The music folder to create.
     */
    public void createMusicFolder(MusicFolder musicFolder) {
        musicFolderDao.createMusicFolder(musicFolder);
        settingsChanged();
    }

    /**
     * Deletes the music folder with the given ID.
     *
     * @param id The ID of the music folder to delete.
     */
    public void deleteMusicFolder(Integer id) {
        musicFolderDao.deleteMusicFolder(id);
        settingsChanged();
    }

    /**
     * Updates the given music folder.
     *
     * @param musicFolder The music folder to update.
     */
    public void updateMusicFolder(MusicFolder musicFolder) {
        musicFolderDao.updateMusicFolder(musicFolder);
        settingsChanged();
    }

    /**
     * Returns all internet radio stations. Disabled stations are not returned.
     *
     * @return Possibly empty array of all internet radio stations.
     */
    public InternetRadio[] getAllInternetRadios() {
        return getAllInternetRadios(false);
    }

    /**
     * Returns the internet radio station with the given ID.
     *
     * @param id The ID.
     * @return The internet radio station with the given ID, or <code>null</code> if not found.
     */
    public InternetRadio getInternetRadioById(Integer id) {
        InternetRadio[] all = getAllInternetRadios();
        for (InternetRadio radio : all) {
            if (id.equals(radio.getId())) {
                return radio;
            }
        }
        return null;
    }

    /**
     * Returns all internet radio stations.
     *
     * @param includeAll Whether disabled stations should be included.
     * @return Possibly empty array of all internet radio stations.
     */
    public InternetRadio[] getAllInternetRadios(boolean includeAll) {
        InternetRadio[] all = internetRadioDao.getAllInternetRadios();
        List<InternetRadio> result = new ArrayList<InternetRadio>(all.length);
        for (InternetRadio folder : all) {
            if (includeAll || folder.isEnabled()) {
                result.add(folder);
            }
        }
        return result.toArray(new InternetRadio[result.size()]);
    }

    /**
     * Creates a new internet radio station.
     *
     * @param radio The internet radio station to create.
     */
    public void createInternetRadio(InternetRadio radio) {
        internetRadioDao.createInternetRadio(radio);
        settingsChanged();
    }

    /**
     * Deletes the internet radio station with the given ID.
     *
     * @param id The internet radio station ID.
     */
    public void deleteInternetRadio(Integer id) {
        internetRadioDao.deleteInternetRadio(id);
        settingsChanged();
    }

    /**
     * Updates the given internet radio station.
     *
     * @param radio The internet radio station to update.
     */
    public void updateInternetRadio(InternetRadio radio) {
        internetRadioDao.updateInternetRadio(radio);
        settingsChanged();
    }

    /**
     * Returns settings for the given user.
     *
     * @param username The username.
     * @return User-specific settings. Never <code>null</code>.
     */
    public UserSettings getUserSettings(String username) {
        UserSettings settings = userDao.getUserSettings(username);
        return settings == null ? createDefaultUserSettings(username) : settings;
    }

    private UserSettings createDefaultUserSettings(String username) {
        UserSettings settings = new UserSettings(username);
        settings.setFinalVersionNotificationEnabled(true);
        settings.setBetaVersionNotificationEnabled(false);
        settings.setShowNowPlayingEnabled(true);
        settings.setPartyModeEnabled(false);
        settings.setNowPlayingAllowed(true);
        settings.setWebPlayerDefault(false);
        settings.setLastFmEnabled(false);
        settings.setLastFmUsername(null);
        settings.setLastFmPassword(null);

        UserSettings.Visibility playlist = settings.getPlaylistVisibility();
        playlist.setCaptionCutoff(35);
        playlist.setArtistVisible(true);
        playlist.setAlbumVisible(true);
        playlist.setYearVisible(true);
        playlist.setDurationVisible(true);
        playlist.setBitRateVisible(true);
        playlist.setFormatVisible(true);
        playlist.setFileSizeVisible(true);

        UserSettings.Visibility main = settings.getMainVisibility();
        main.setCaptionCutoff(35);
        main.setTrackNumberVisible(true);
        main.setArtistVisible(true);
        main.setDurationVisible(true);

        return settings;
    }

    /**
     * Updates settings for the given username.
     *
     * @param settings The user-specific settings.
     */
    public void updateUserSettings(UserSettings settings) {
        userDao.updateUserSettings(settings);
        settingsChanged();
    }

    /**
     * Returns all system avatars.
     *
     * @return All system avatars.
     */
    public Avatar[] getAllSystemAvatars() {
        return avatarDao.getAllSystemAvatars();
    }

    /**
     * Returns the system avatar with the given ID.
     *
     * @param id The system avatar ID.
     * @return The avatar or <code>null</code> if not found.
     */
    public Avatar getSystemAvatar(int id) {
        return avatarDao.getSystemAvatar(id);
    }

    /**
     * Returns the custom avatar for the given user.
     *
     * @param username The username.
     * @return The avatar or <code>null</code> if not found.
     */
    public Avatar getCustomAvatar(String username) {
        return avatarDao.getCustomAvatar(username);
    }

    /**
     * Sets the custom avatar for the given user.
     *
     * @param avatar The avatar, or <code>null</code> to remove the avatar.
     * @param username The username.
     */
    public void setCustomAvatar(Avatar avatar, String username) {
        avatarDao.setCustomAvatar(avatar, username);
    }

    private void setProperty(String key, String value) {
        if (value == null) {
            properties.remove(key);
        } else {
            properties.setProperty(key, value);
        }
    }

    private String[] toStringArray(String s) {
        List<String> result = new ArrayList<String>();
        StringTokenizer tokenizer = new StringTokenizer(s, " ");
        while (tokenizer.hasMoreTokens()) {
            result.add(tokenizer.nextToken());
        }

        return result.toArray(new String[result.size()]);
    }

    private void settingsChanged() {
        settingsLastChanged = System.currentTimeMillis();
    }

    public long getSettingsLastChanged() {
        return settingsLastChanged;
    }

    public void setInternetRadioDao(InternetRadioDao internetRadioDao) {
        this.internetRadioDao = internetRadioDao;
    }

    public void setMusicFolderDao(MusicFolderDao musicFolderDao) {
        this.musicFolderDao = musicFolderDao;
    }

    public void setUserDao(UserDao userDao) {
        this.userDao = userDao;
    }

    public void setAvatarDao(AvatarDao avatarDao) {
        this.avatarDao = avatarDao;
    }
}
