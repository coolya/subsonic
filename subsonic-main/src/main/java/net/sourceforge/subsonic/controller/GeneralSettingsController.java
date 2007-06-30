package net.sourceforge.subsonic.controller;

import org.springframework.web.servlet.mvc.*;
import net.sourceforge.subsonic.service.*;
import net.sourceforge.subsonic.command.*;
import net.sourceforge.subsonic.domain.*;

import javax.servlet.http.*;
import java.util.*;

/**
 * Controller for the page used to administrate general settings.
 *
 * @author Sindre Mehus
 */
public class GeneralSettingsController extends SimpleFormController {

    private SettingsService settingsService;

    protected Object formBackingObject(HttpServletRequest request) throws Exception {
        GeneralSettingsCommand command = new GeneralSettingsCommand();
        command.setCoverArtLimit(String.valueOf(settingsService.getCoverArtLimit()));
        command.setCoverArtMask(settingsService.getCoverArtMask());
        command.setDownsampleCommand(settingsService.getDownsamplingCommand());
        command.setDownloadLimit(String.valueOf(settingsService.getDownloadBitrateLimit()));
        command.setUploadLimit(String.valueOf(settingsService.getUploadBitrateLimit()));
        command.setStreamPort(String.valueOf(settingsService.getStreamPort()));
        command.setIgnoredArticles(settingsService.getIgnoredArticles());
        command.setShortcuts(settingsService.getShortcuts().replaceAll("\"", "&quot;"));
        command.setIndex(settingsService.getIndexString());
        command.setMusicMask(settingsService.getMusicMask());
        command.setPlaylistFolder(settingsService.getPlaylistFolder());
        command.setWelcomeMessage(settingsService.getWelcomeMessage());

        Theme[] themes = settingsService.getAvailableThemes();
        command.setThemes(themes);
        String currentThemeId = settingsService.getThemeId();
        for (int i = 0; i < themes.length; i++) {
            if (currentThemeId.equals(themes[i].getId())) {
                command.setThemeIndex(String.valueOf(i));
                break;
            }
        }

        Locale currentLocale = settingsService.getLocale();
        Locale[] locales = settingsService.getAvailableLocales();
        String[] localeStrings = new String[locales.length];
        for (int i = 0; i < locales.length; i++) {
            localeStrings[i] = locales[i].getDisplayLanguage(Locale.ENGLISH) +
                               " (" + locales[i].getDisplayLanguage(locales[i]) + ')';

            if (currentLocale.equals(locales[i])) {
                command.setLocaleIndex(String.valueOf(i));
            }
        }
        command.setLocales(localeStrings);

        return command;

    }

    protected void doSubmitAction(Object comm) throws Exception {
        GeneralSettingsCommand command = (GeneralSettingsCommand) comm;

        int themeIndex = Integer.parseInt(command.getThemeIndex());
        Theme theme = settingsService.getAvailableThemes()[themeIndex];

        int localeIndex = Integer.parseInt(command.getLocaleIndex());
        Locale locale = settingsService.getAvailableLocales()[localeIndex];

        command.setReloadNeeded(!settingsService.getIndexString().equals(command.getIndex()) ||
                                !settingsService.getIgnoredArticles().equals(command.getIgnoredArticles()) ||
                                !settingsService.getShortcuts().equals(command.getShortcuts()) ||
                                !settingsService.getThemeId().equals(theme.getId()) ||
                                !settingsService.getLocale().equals(locale));

        settingsService.setIndexString(command.getIndex());
        settingsService.setIgnoredArticles(command.getIgnoredArticles());
        settingsService.setShortcuts(command.getShortcuts());
        settingsService.setPlaylistFolder(command.getPlaylistFolder());
        settingsService.setMusicMask(command.getMusicMask());
        settingsService.setCoverArtMask(command.getCoverArtMask());
        settingsService.setDownsamplingCommand(command.getDownsampleCommand());
        settingsService.setWelcomeMessage(command.getWelcomeMessage());
        settingsService.setThemeId(theme.getId());
        settingsService.setLocale(locale);

        try {
            settingsService.setCoverArtLimit(Integer.parseInt(command.getCoverArtLimit()));
        } catch (NumberFormatException x) { /* Intentionally ignored. */ }
        try {
            settingsService.setDownloadBitrateLimit(Long.parseLong(command.getDownloadLimit()));
        } catch (NumberFormatException x) { /* Intentionally ignored. */ }
        try {
            settingsService.setUploadBitrateLimit(Long.parseLong(command.getUploadLimit()));
        } catch (NumberFormatException x) { /* Intentionally ignored. */ }
        try {
            settingsService.setStreamPort(Integer.parseInt(command.getStreamPort()));
        } catch (NumberFormatException x) { /* Intentionally ignored. */ }
        settingsService.save();

        command.setShortcuts(command.getShortcuts().replaceAll("\"", "&quot;"));
    }

    public void setSettingsService(SettingsService settingsService) {
        this.settingsService = settingsService;
    }
}
