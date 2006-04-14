package net.sourceforge.subsonic.i18n;

import net.sourceforge.subsonic.service.*;
import org.springframework.web.servlet.*;

import javax.servlet.http.*;
import java.util.*;

/**
 * Locale resolver implementation which returns the proper locale for the
 * current user.
 *
 * @author Sindre Mehus
 */
public class SubsonicLocaleResolver implements LocaleResolver {

    private SettingsService settingsService;

    public Locale resolveLocale(HttpServletRequest request) {
        return settingsService.getLocale();
    }

    public void setLocale(HttpServletRequest request, HttpServletResponse response, Locale locale) {
        throw new UnsupportedOperationException("Cannot change locale - use a different locale resolution strategy");
    }

    public void setSettingsService(SettingsService settingsService) {
        this.settingsService = settingsService;
    }
}
