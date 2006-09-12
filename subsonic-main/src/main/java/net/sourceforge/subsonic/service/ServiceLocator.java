package net.sourceforge.subsonic.service;

/**
 * Locates services for objects that are not part of the Spring context.
 *
 * @author Sindre Mehus
 */
public class ServiceLocator {

    private static SettingsService settingsService;
    private static SecurityService securityService;
    private static MusicFileService musicFileService;

    private ServiceLocator() {
    }

    public static SettingsService getSettingsService() {
        return settingsService;
    }

    public static void setSettingsService(SettingsService settingsService) {
        ServiceLocator.settingsService = settingsService;
    }

    public static MusicFileService getMusicFileService() {
        return musicFileService;
    }

    public static void setMusicFileService(MusicFileService musicFileService) {
        ServiceLocator.musicFileService = musicFileService;
    }
}

