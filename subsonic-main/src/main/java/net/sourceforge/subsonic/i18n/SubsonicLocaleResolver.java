package net.sourceforge.subsonic.i18n;

import net.sourceforge.subsonic.service.*;
import net.sourceforge.subsonic.domain.*;
import org.springframework.web.servlet.*;

import javax.servlet.http.*;
import java.util.*;

/**
 * Locale resolver implementation which returns the locale selected in the settings.
 *
 * @author Sindre Mehus
 */
public class SubsonicLocaleResolver implements LocaleResolver {

    private SecurityService securityService;
    private SettingsService settingsService;
    private Set<Locale> locales;

    /**
    * Resolve the current locale via the given request.
    *
    * @param request Request to be used for resolution.
    * @return The current locale.
    */
    public Locale resolveLocale(HttpServletRequest request) {

        // Look for user-specific locale.
        User user = securityService.getCurrentUser(request);
        Locale locale = user.getLocale();
        if (locale != null && localeExists(locale)) {
            return locale;
        }

        // Return system locale.
        locale = settingsService.getLocale();
        return localeExists(locale) ? locale : Locale.ENGLISH;
    }

    /**
     * Returns whether the given locale exists.
     * @param locale The locale.
     * @return Whether the locale exists.
     */
    private synchronized boolean localeExists(Locale locale) {
        // Lazily create set of locales.
        if (locales == null) {
            locales = new HashSet<Locale>(Arrays.asList(settingsService.getAvailableLocales()));
        }

        return locales.contains(locale);
    }

    public void setLocale(HttpServletRequest request, HttpServletResponse response, Locale locale) {
        throw new UnsupportedOperationException("Cannot change locale - use a different locale resolution strategy");
    }

    public void setSecurityService(SecurityService securityService) {
        this.securityService = securityService;
    }

    public void setSettingsService(SettingsService settingsService) {
        this.settingsService = settingsService;
    }
}
