package net.sourceforge.subsonic.service;

import net.sourceforge.subsonic.domain.*;
import net.sourceforge.subsonic.*;
import net.sourceforge.subsonic.util.*;
import net.sourceforge.subsonic.dao.*;

import java.util.*;
import java.io.*;

/**
 * Provides persistent storage of application settings and preferences.
 *
 * @author Sindre Mehus
 */
public class SettingsService {

    // Subsonic home directory.
    private static final File SUBSONIC_HOME_WINDOWS = new File("c:/subsonic");
    private static final File SUBSONIC_HOME_OTHER   = new File("/var/subsonic");

    // Global settings.
    private static final String          KEY_INDEX_STRING            = "IndexString";
    private static final String          KEY_IGNORED_ARTICLES        = "IgnoredArticles";
    private static final String          KEY_SHORTCUTS               = "Shortcuts";
    private static final String          KEY_PLAYLIST_FOLDER         = "PlaylistFolder";
    private static final String          KEY_MUSIC_MASK              = "MusicMask";
    private static final String          KEY_COVER_ART_MASK          = "CoverArtMask";
    private static final String          KEY_COVER_ART_LIMIT         = "CoverArtLimit";
    private static final String          KEY_WELCOME_MESSAGE         = "WelcomeMessage";
    private static final String          KEY_LOCALE_LANGUAGE         = "LocaleLanguage";
    private static final String          KEY_LOCALE_COUNTRY          = "LocaleCountry";
    private static final String          KEY_LOCALE_VARIANT          = "LocaleVariant";
    private static final String          KEY_THEME_ID                   = "Theme";
    private static final String          KEY_INDEX_CREATION_INTERVAL = "IndexCreationInterval";
    private static final String          KEY_INDEX_CREATION_HOUR     = "IndexCreationHour";
    private static final String          KEY_DOWNLOAD_BITRATE_LIMIT  = "DownloadBitrateLimit";
    private static final String          KEY_UPLOAD_BITRATE_LIMIT    = "UploadBitrateLimit";
    private static final String          KEY_STREAM_PORT             = "StreamPort";

    // Default values.
    private static final String          DEFAULT_INDEX_STRING            = "A B C D E F G H I J K L M N O P Q R S T U V W X-Z(XYZ)";
    private static final String          DEFAULT_IGNORED_ARTICLES        = "The El La Los Las Le Les";
    private static final String          DEFAULT_SHORTCUTS               = "New Incoming";
    private static final String          DEFAULT_PLAYLIST_FOLDER         = "c:/playlists";
    private static final String          DEFAULT_MUSIC_MASK              = ".mp3 .ogg .aac .wav .wma";
    private static final String          DEFAULT_COVER_ART_MASK          = "folder.jpg cover.jpg .jpg .jpeg .gif .png";
    private static final int             DEFAULT_COVER_ART_LIMIT         = 30;
    private static final String          DEFAULT_WELCOME_MESSAGE         = "Welcome to Subsonic!";
    private static final String          DEFAULT_LOCALE_LANGUAGE         = "en";
    private static final String          DEFAULT_LOCALE_COUNTRY          = "";
    private static final String          DEFAULT_LOCALE_VARIANT          = "";
    private static final String          DEFAULT_THEME_ID                   = "default";
    private static final int             DEFAULT_INDEX_CREATION_INTERVAL = 1;
    private static final int             DEFAULT_INDEX_CREATION_HOUR     = 3;
    private static final long            DEFAULT_DOWNLOAD_BITRATE_LIMIT  = 0;
    private static final long            DEFAULT_UPLOAD_BITRATE_LIMIT    = 0;
    private static final long            DEFAULT_STREAM_PORT             = 0;

    // Array of all keys.  Used to clean property file.
    private static final String[] KEYS = {KEY_INDEX_STRING, KEY_IGNORED_ARTICLES, KEY_SHORTCUTS, KEY_PLAYLIST_FOLDER, KEY_MUSIC_MASK,
                                          KEY_COVER_ART_MASK, KEY_COVER_ART_LIMIT, KEY_WELCOME_MESSAGE, KEY_LOCALE_LANGUAGE,
                                          KEY_LOCALE_COUNTRY, KEY_LOCALE_VARIANT, KEY_THEME_ID, KEY_INDEX_CREATION_INTERVAL, KEY_INDEX_CREATION_HOUR,
                                          KEY_DOWNLOAD_BITRATE_LIMIT, KEY_UPLOAD_BITRATE_LIMIT, KEY_STREAM_PORT};

    private static final String LOCALES_FILE = "/net/sourceforge/subsonic/i18n/locales.txt";
    private static final String THEMES_FILE = "/net/sourceforge/subsonic/theme/themes.txt";

    private static final Logger LOG = Logger.getLogger(SettingsService.class);

    private Properties properties = new Properties();
    private List<Theme> themes;
    private List<Locale> locales;
    private InternetRadioDao internetRadioDao;
    private MusicFolderDao musicFolderDao;
    private UserDao userDao;

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
                try {
                    if (in != null) {
                        in.close();
                    }
                } catch (Exception x) {
                    LOG.error("Failed to close property file", x);
                }
            }

            // Removed obsolete properties.
            Set<String> allowedKeys = new HashSet<String>();
            for (String key : KEYS) {
                allowedKeys.add(key);
            }

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
        FileOutputStream out = null;

        try {
            out = new FileOutputStream(getPropertyFile());
            properties.store(out, "Subsonic preferences.  NOTE: This file is automatically generated.");
        } catch (Exception x) {
            LOG.error("Unable to write to property file.", x);
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
            } catch (Exception x) {
                LOG.error("Failed to close property file", x);
            }
        }
    }

    private File getPropertyFile() {
        return new File(getSubsonicHome(), "subsonic.properties");
    }

    /**
     * Returns the Subsonic home directory.
     * @return The Subsonic home directory, if it exists.
     * @throws RuntimeException If directory doesn't exist.
     */
    public static File getSubsonicHome() {
        File home;

        String overrideHome = System.getProperty("subsonic.home");
        if (overrideHome != null) {
            home = new File(overrideHome);
        } else {
            boolean isWindows = System.getProperty("os.name", "Windows").toLowerCase().startsWith("windows");
            home = isWindows ? SUBSONIC_HOME_WINDOWS : SUBSONIC_HOME_OTHER;
        }

        // Attempt to create home directory if it doesn't exist.
        if (!home.exists()) {
            boolean success = home.mkdirs();
            if (success) {
            } else {
                String message = "The directory " + home + " does not exist. Please create it and make it writable. " +
                                 "(You can override the directory location by specifying -Dsubsonic.home=... when " +
                                 "starting the servlet container.)";
                throw new RuntimeException(message);
            }
        }

        return home;
    }

    public String getIndexString() {
        return properties.getProperty(KEY_INDEX_STRING, DEFAULT_INDEX_STRING);
    }

    public void setIndexString(String indexString) {
        properties.setProperty(KEY_INDEX_STRING, indexString);
    }

    public String getIgnoredArticles() {
        return properties.getProperty(KEY_IGNORED_ARTICLES, DEFAULT_IGNORED_ARTICLES);
    }

    public String[] getIgnoredArticlesAsArray() {
        return getIgnoredArticles().split("\\s+");
    }

    public void setIgnoredArticles(String ignoredArticles) {
        properties.setProperty(KEY_IGNORED_ARTICLES, ignoredArticles);
    }

    public String getShortcuts() {
        return properties.getProperty(KEY_SHORTCUTS, DEFAULT_SHORTCUTS);
    }

    public String[] getShortcutsAsArray() {
        return StringUtil.split(getShortcuts());
    }

    public void setShortcuts(String shortcuts) {
        properties.setProperty(KEY_SHORTCUTS, shortcuts);
    }

    public String getPlaylistFolder() {
        return properties.getProperty(KEY_PLAYLIST_FOLDER, DEFAULT_PLAYLIST_FOLDER);
    }

    public void setPlaylistFolder(String playlistFolder) {
        properties.setProperty(KEY_PLAYLIST_FOLDER, playlistFolder);
    }

    public String getMusicMask() {
        return properties.getProperty(KEY_MUSIC_MASK, DEFAULT_MUSIC_MASK);
    }

    public void setMusicMask(String mask) {
        properties.setProperty(KEY_MUSIC_MASK,  mask);
    }

    public String[] getMusicMaskAsArray() {
        return toStringArray(getMusicMask());
    }

    public String getCoverArtMask() {
        return properties.getProperty(KEY_COVER_ART_MASK, DEFAULT_COVER_ART_MASK);
    }

    public void setCoverArtMask(String mask) {
        properties.setProperty(KEY_COVER_ART_MASK,  mask);
    }

    public String[] getCoverArtMaskAsArray() {
        return toStringArray(getCoverArtMask());
    }

    public int getCoverArtLimit() {
        return Integer.parseInt(properties.getProperty(KEY_COVER_ART_LIMIT, "" + DEFAULT_COVER_ART_LIMIT));
    }

    public void setCoverArtLimit(int limit) {
        properties.setProperty(KEY_COVER_ART_LIMIT,  "" + limit);
    }

    public String getWelcomeMessage() {
        return properties.getProperty(KEY_WELCOME_MESSAGE, DEFAULT_WELCOME_MESSAGE);
    }

    public void setWelcomeMessage(String message) {
        properties.setProperty(KEY_WELCOME_MESSAGE,  message);
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
        properties.setProperty(KEY_INDEX_CREATION_INTERVAL, "" + days);
    }

    /**
     * Returns the hour of day (0 - 23) when automatic index creation should run.
     */
    public int getIndexCreationHour() {
        return Integer.parseInt(properties.getProperty(KEY_INDEX_CREATION_HOUR, "" + DEFAULT_INDEX_CREATION_HOUR));
    }

    /**
     * Sets the hour of day (0 - 23) when automatic index creation should run. 
     */
    public void setIndexCreationHour(int hour) {
        properties.setProperty(KEY_INDEX_CREATION_HOUR, "" + hour);
    }

    /**
     * @return The download bitrate limit in Kbit/s. Zero if unlimited.
     */
    public long getDownloadBitrateLimit() {
        return Long.parseLong(properties.getProperty(KEY_DOWNLOAD_BITRATE_LIMIT, "" + DEFAULT_DOWNLOAD_BITRATE_LIMIT));
    }

    /**
     * @param limit The download bitrate limit in Kbit/s. Zero if unlimited.
     */
    public void setDownloadBitrateLimit(long limit) {
        properties.setProperty(KEY_DOWNLOAD_BITRATE_LIMIT,  "" + limit);
    }

    /**
     * @return The upload bitrate limit in Kbit/s. Zero if unlimited.
     */
    public long getUploadBitrateLimit() {
        return Long.parseLong(properties.getProperty(KEY_UPLOAD_BITRATE_LIMIT, "" + DEFAULT_UPLOAD_BITRATE_LIMIT));
    }

    /**
     * @param limit The upload bitrate limit in Kbit/s. Zero if unlimited.
     */
    public void setUploadBitrateLimit(long limit) {
        properties.setProperty(KEY_UPLOAD_BITRATE_LIMIT,  "" + limit);
    }

    /**
     * @return The non-SSL stream port. Zero if disabled.
     */
    public int getStreamPort() {
        return Integer.parseInt(properties.getProperty(KEY_STREAM_PORT, "" + DEFAULT_STREAM_PORT));
    }

    /**
     * @param port The non-SSL stream port. Zero if disabled.
     */
    public void setStreamPort(int port) {
        properties.setProperty(KEY_STREAM_PORT,  "" + port);
    }

    /**
     * Returns the locale (for language, date format etc).
     * @return The locale.
     */
    public Locale getLocale() {
        String language = properties.getProperty(KEY_LOCALE_LANGUAGE, DEFAULT_LOCALE_LANGUAGE);
        String country  = properties.getProperty(KEY_LOCALE_COUNTRY, DEFAULT_LOCALE_COUNTRY);
        String variant  = properties.getProperty(KEY_LOCALE_VARIANT, DEFAULT_LOCALE_VARIANT);

        return new Locale(language, country, variant);
    }

    /**
     * Sets the locale (for language, date format etc.)
     * @param locale The locale.
     */
    public void setLocale(Locale locale) {
        properties.setProperty(KEY_LOCALE_LANGUAGE, locale.getLanguage());
        properties.setProperty(KEY_LOCALE_COUNTRY, locale.getCountry());
        properties.setProperty(KEY_LOCALE_VARIANT, locale.getVariant());
    }

    /**
     * Returns the ID of the theme to use.
     * @return The theme ID.
     */
    public String getThemeId() {
        return properties.getProperty(KEY_THEME_ID, DEFAULT_THEME_ID);
    }

    /**
     * Sets the ID of the theme to use.
     * @param themeId The theme ID
     */
    public void setThemeId(String themeId) {
        properties.setProperty(KEY_THEME_ID, themeId);
    }

    /**
    * Returns a list of available themes.
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
        return themes.toArray(new Theme[0]);
    }

    /**
     * Returns a list of available locales.
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
        return locales.toArray(new Locale[0]);
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
     * @return Possibly empty array of all music folders.
     */
    public MusicFolder[] getAllMusicFolders() {
        return getAllMusicFolders(false);
    }

    /**
     * Returns all music folders.
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
        return result.toArray(new MusicFolder[0]);
    }

    /**
    * Creates a new music folder.
    * @param musicFolder The music folder to create.
    */
    public void createMusicFolder(MusicFolder musicFolder) {
        musicFolderDao.createMusicFolder(musicFolder);
    }

    /**
     * Deletes the music folder with the given ID.
     * @param id The ID of the music folder to delete.
     */
    public void deleteMusicFolder(Integer id) {
        musicFolderDao.deleteMusicFolder(id);
    }

    /**
     * Updates the given music folder.
     * @param musicFolder The music folder to update.
     */
    public void updateMusicFolder(MusicFolder musicFolder) {
        musicFolderDao.updateMusicFolder(musicFolder);
    }

    /**
     * Returns all internet radio stations. Disabled stations are not returned.
     * @return Possibly empty array of all internet radio stations.
     */
    public InternetRadio[] getAllInternetRadios() {
        return getAllInternetRadios(false);
    }

    /**
     * Returns the internet radio station with the given ID.
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
        return result.toArray(new InternetRadio[0]);
    }

    /**
     * Creates a new internet radio station.
     * @param radio The internet radio station to create.
     */
    public void createInternetRadio(InternetRadio radio) {
        internetRadioDao.createInternetRadio(radio);
    }

    /**
     * Deletes the internet radio station with the given ID.
     * @param id The internet radio station ID.
     */
    public void deleteInternetRadio(Integer id) {
        internetRadioDao.deleteInternetRadio(id);
    }

    /**
     * Updates the given internet radio station.
     * @param radio The internet radio station to update.
     */
    public void updateInternetRadio(InternetRadio radio) {
        internetRadioDao.updateInternetRadio(radio);
    }

    /**
     * Returns settings for the given user.
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
     * @param settings The user-specific settings.
     */
    public void updateUserSettings(UserSettings settings) {
        userDao.updateUserSettings(settings);
    }

    private String[] toStringArray(String s) {
        List<String> result = new ArrayList<String>();
        StringTokenizer tokenizer = new StringTokenizer(s, " ");
        while (tokenizer.hasMoreTokens()) {
            result.add(tokenizer.nextToken());
        }

        return result.toArray(new String[0]);
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
}
