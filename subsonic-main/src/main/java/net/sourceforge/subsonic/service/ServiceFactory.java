package net.sourceforge.subsonic.service;

/**
 * A factory for retrieving services.
 *
 * @deprecated Use Spring-injected beans instead.
 * @author Sindre Mehus
 */
public class ServiceFactory {

    private static final SettingsService SETTINGS_SERVICE = new SettingsService();
    private static final SecurityService SECURITY_SERVICE = new SecurityService();

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
}
