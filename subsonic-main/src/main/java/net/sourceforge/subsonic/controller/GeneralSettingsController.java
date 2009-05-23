/*
 This file is part of Subsonic.

 Subsonic is free software: you can redistribute it and/or modify
 it under the terms of the GNU General Public License as published by
 the Free Software Foundation, either version 3 of the License, or
 (at your option) any later version.

 Subsonic is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with Subsonic.  If not, see <http://www.gnu.org/licenses/>.

 Copyright 2009 (C) Sindre Mehus
 */
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
        command.setCoverArtMask(settingsService.getCoverArtMask());
        command.setIgnoredArticles(settingsService.getIgnoredArticles());
        command.setShortcuts(settingsService.getShortcuts());
        command.setIndex(settingsService.getIndexString());
        command.setMusicMask(settingsService.getMusicMask());
        command.setPlaylistFolder(settingsService.getPlaylistFolder());
        command.setWelcomeTitle(settingsService.getWelcomeTitle());
        command.setWelcomeSubtitle(settingsService.getWelcomeSubtitle());
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
            localeStrings[i] = locales[i].getDisplayName(locales[i]);

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
        settingsService.setWelcomeTitle(command.getWelcomeTitle());
        settingsService.setWelcomeSubtitle(command.getWelcomeSubtitle());
        settingsService.setWelcomeMessage(command.getWelcomeMessage());
        settingsService.setThemeId(theme.getId());
        settingsService.setLocale(locale);
        settingsService.save();
    }

    public void setSettingsService(SettingsService settingsService) {
        this.settingsService = settingsService;
    }
}
