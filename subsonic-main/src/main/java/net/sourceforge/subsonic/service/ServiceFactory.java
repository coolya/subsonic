package net.sourceforge.subsonic.service;

/**
 * A factory for retrieving services.
 *
 * @author Sindre Mehus
 * @version $Revision: 1.7 $ $Date: 2005/12/04 15:04:21 $
 */
public class ServiceFactory {

    private static final SettingsService             SETTINGS_SERVICE             = new SettingsService();
    private static final PlayerService               PLAYER_SERVICE               = new PlayerService();
    private static final PlaylistService             PLAYLIST_SERVICE             = new PlaylistService();
    private static final SearchService               SEARCH_SERVICE               = new SearchService();
    private static final SecurityService             SECURITY_SERVICE             = new SecurityService();
    private static final StatusService               STATUS_SERVICE               = new StatusService();
    private static final VersionService              VERSION_SERVICE              = new VersionService();
    private static final AmazonSearchService         AMAZON_SEARCH_SERVICE        = new AmazonSearchService();
    private static final InternationalizationService INTERNATIONALIZATION_SERVICE = new InternationalizationService();
    private static final MusicInfoService            MUSIC_INFO_SERVICE           = new MusicInfoService();

    /**
     * Returns the player service.
     * @return The player service.
     */
    public static PlayerService getPlayerService() {
        return PLAYER_SERVICE;
    }

    /**
     * Returns the playlist service.
     * @return The playlist service.
     */
    public static PlaylistService getPlaylistService() {
        return PLAYLIST_SERVICE;
    }

    /**
     * Returns the search service.
     * @return The search service.
     */
    public static SearchService getSearchService() {
        return SEARCH_SERVICE;
    }

    /**
     * Returns the settings service.
     * @return The settings service.
     */
    public static SettingsService getSettingsService() {
        return SETTINGS_SERVICE;
    }

    /**
     * Returns the security service.
     * @return The security service.
     */
    public static SecurityService getSecurityService() {
        return SECURITY_SERVICE;
    }

    /**
     * Returns the status service.
     * @return The status service.
     */
    public static StatusService getStatusService() {
        return STATUS_SERVICE;
    }

    /**
     * Returns the version service.
     * @return The version service.
     */
    public static VersionService getVersionService() {
        return VERSION_SERVICE;
    }

    /**
     * Returns the Amazon search service.
     * @return The Amazon search service.
     */
    public static AmazonSearchService getAmazonSearchService() {
        return AMAZON_SEARCH_SERVICE;
    }

    /**
     * Returns the internationalization service.
     * @return The internationalization service.
     */
    public static InternationalizationService getInternationalizationService() {
        return INTERNATIONALIZATION_SERVICE;
    }

    /**
     * Returns the music information service.
     * @return The music information service.
     */
    public static MusicInfoService getMusicInfoService() {
        return MUSIC_INFO_SERVICE;
    }
}
