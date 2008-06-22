package net.sourceforge.subsonic.controller;

import org.springframework.web.servlet.mvc.*;
import org.apache.commons.lang.StringUtils;
import net.sourceforge.subsonic.service.*;
import net.sourceforge.subsonic.command.*;
import net.sourceforge.subsonic.domain.*;

import javax.servlet.http.*;
import java.util.*;

/**
 * Controller for the page used to administrate per-user settings.
 *
 * @author Sindre Mehus
 */
public class PersonalSettingsController extends SimpleFormController {

    private SettingsService settingsService;
    private SecurityService securityService;

    @Override
    protected Object formBackingObject(HttpServletRequest request) throws Exception {
        PersonalSettingsCommand command = new PersonalSettingsCommand();

        User user = securityService.getCurrentUser(request);
        UserSettings userSettings = settingsService.getUserSettings(user.getUsername());

        command.setUser(user);
        command.setLocaleIndex("-1");
        command.setThemeIndex("-1");
        command.setAvatars(settingsService.getAllSystemAvatars());
        command.setPartyModeEnabled(userSettings.isPartyModeEnabled());
        command.setShowNowPlayingEnabled(userSettings.isShowNowPlayingEnabled());
        command.setNowPlayingAllowed(userSettings.isNowPlayingAllowed());
        command.setWebPlayerDefault(userSettings.isWebPlayerDefault());
        command.setMainVisibility(userSettings.getMainVisibility());
        command.setPlaylistVisibility(userSettings.getPlaylistVisibility());
        command.setFinalVersionNotificationEnabled(userSettings.isFinalVersionNotificationEnabled());
        command.setBetaVersionNotificationEnabled(userSettings.isBetaVersionNotificationEnabled());
        command.setLastFmEnabled(userSettings.isLastFmEnabled());
        command.setLastFmUsername(userSettings.getLastFmUsername());
        command.setLastFmPassword(userSettings.getLastFmPassword());

        Locale currentLocale = userSettings.getLocale();
        Locale[] locales = settingsService.getAvailableLocales();
        String[] localeStrings = new String[locales.length];
        for (int i = 0; i < locales.length; i++) {
            localeStrings[i] = locales[i].getDisplayLanguage(Locale.ENGLISH) +
                               " (" + locales[i].getDisplayLanguage(locales[i]) + ')';

            if (locales[i].equals(currentLocale)) {
                command.setLocaleIndex(String.valueOf(i));
            }
        }
        command.setLocales(localeStrings);

        String currentThemeId = userSettings.getThemeId();
        Theme[] themes = settingsService.getAvailableThemes();
        command.setThemes(themes);
        for (int i = 0; i < themes.length; i++) {
            if (themes[i].getId().equals(currentThemeId)) {
                command.setThemeIndex(String.valueOf(i));
                break;
            }
        }

        return command;
    }

    @Override
    protected void doSubmitAction(Object comm) throws Exception {
        PersonalSettingsCommand command = (PersonalSettingsCommand) comm;

        int localeIndex = Integer.parseInt(command.getLocaleIndex());
        Locale locale = null;
        if (localeIndex != -1) {
            locale = settingsService.getAvailableLocales()[localeIndex];
        }

        int themeIndex = Integer.parseInt(command.getThemeIndex());
        String themeId = null;
        if (themeIndex != -1) {
            themeId = settingsService.getAvailableThemes()[themeIndex].getId();
        }

        String username = command.getUser().getUsername();
        UserSettings settings = settingsService.getUserSettings(username);

        settings.setLocale(locale);
        settings.setThemeId(themeId);
        settings.setPartyModeEnabled(command.isPartyModeEnabled());
        settings.setShowNowPlayingEnabled(command.isShowNowPlayingEnabled());
        settings.setNowPlayingAllowed(command.isNowPlayingAllowed());
        settings.setWebPlayerDefault(command.isWebPlayerDefault());
        settings.setMainVisibility(command.getMainVisibility());
        settings.setPlaylistVisibility(command.getPlaylistVisibility());
        settings.setFinalVersionNotificationEnabled(command.isFinalVersionNotificationEnabled());
        settings.setBetaVersionNotificationEnabled(command.isBetaVersionNotificationEnabled());
        settings.setLastFmEnabled(command.isLastFmEnabled());
        settings.setLastFmUsername(command.getLastFmUsername());

        if (StringUtils.isNotBlank(command.getLastFmPassword())) {
            settings.setLastFmPassword(command.getLastFmPassword());
        }

        settingsService.updateUserSettings(settings);

        command.setReloadNeeded(true);
    }

    public void setSettingsService(SettingsService settingsService) {
        this.settingsService = settingsService;
    }

    public void setSecurityService(SecurityService securityService) {
        this.securityService = securityService;
    }
}
