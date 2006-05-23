package net.sourceforge.subsonic.theme;

import net.sourceforge.subsonic.service.*;
import org.springframework.web.servlet.*;

import javax.servlet.http.*;

/**
 * Theme resolver implementation which returns the theme selected in the settings.
 *
 * @author Sindre Mehus
 */
public class SubsonicThemeResolver implements ThemeResolver {

    private SettingsService settingsService;

    /**
     * Resolve the current theme name via the given request.
     *
     * @param request Request to be used for resolution
     * @return The current theme name
     */
    public String resolveThemeName(HttpServletRequest request) {
        return settingsService.getThemeId();
    }

    /**
     * Set the current theme name to the given one. This method is not supported.
     *
     * @param request   Request to be used for theme name modification
     * @param response  Response to be used for theme name modification
     * @param themeName The new theme name
     * @throws UnsupportedOperationException If the ThemeResolver implementation
     *                                       does not support dynamic changing of the theme
     */
    public void setThemeName(HttpServletRequest request, HttpServletResponse response, String themeName) {

        throw new UnsupportedOperationException("Cannot change theme - use a different theme resolution strategy");
    }

    public void setSettingsService(SettingsService settingsService) {
        this.settingsService = settingsService;
    }
}
