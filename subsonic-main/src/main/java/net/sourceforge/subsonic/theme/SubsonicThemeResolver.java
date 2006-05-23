package net.sourceforge.subsonic.theme;

import net.sourceforge.subsonic.service.*;
import net.sourceforge.subsonic.domain.*;
import org.springframework.web.servlet.*;

import javax.servlet.http.*;
import java.util.*;

/**
 * Theme resolver implementation which returns the theme selected in the settings.
 *
 * @author Sindre Mehus
 */
public class SubsonicThemeResolver implements ThemeResolver {

    private SettingsService settingsService;
    private Set<String> themeIds;

    /**
    * Resolve the current theme name via the given request.
    *
    * @param request Request to be used for resolution
    * @return The current theme name
    */
    public String resolveThemeName(HttpServletRequest request) {
        String themeId = settingsService.getThemeId();

        // Verify that theme exists.
        return themeExists(themeId) ? themeId : "default";
    }

    /**
     * Returns whether the theme with the given ID exists.
     * @param themeId The theme ID.
     * @return Whether the theme with the given ID exists.
     */
    private synchronized boolean themeExists(String themeId) {
        // Lazily create set of theme IDs.
        if (themeIds == null) {
            themeIds = new HashSet<String>();
            Theme[] themes = settingsService.getAvailableThemes();
            for (Theme theme : themes) {
                themeIds.add(theme.getId());
            }
        }

        return themeIds.contains(themeId);
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
