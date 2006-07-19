package net.sourceforge.subsonic.domain;

import java.util.*;

/**
 * Represent a user's preferences.
 *
 * @author Sindre Mehus
 */
public class UserSettings {

    private String username;
    private Locale locale;
    private String themeId;

    public UserSettings(String username, Locale locale, String themeId) {
        this.username = username;
        this.locale = locale;
        this.themeId = themeId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Locale getLocale() {
        return locale;
    }

    public void setLocale(Locale locale) {
        this.locale = locale;
    }

    public String getThemeId() {
        return themeId;
    }

    public void setThemeId(String themeId) {
        this.themeId = themeId;
    }
}
