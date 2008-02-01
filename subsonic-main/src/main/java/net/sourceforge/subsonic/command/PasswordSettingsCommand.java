package net.sourceforge.subsonic.command;

import net.sourceforge.subsonic.controller.*;

/**
 * Command used in {@link PasswordSettingsController}.
 *
 * @author Sindre Mehus
 */
public class PasswordSettingsCommand {
    private String username;
    private String password;
    private String confirmPassword;
    private boolean ldapAuthenticated;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getConfirmPassword() {
        return confirmPassword;
    }

    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }

    public boolean isLdapAuthenticated() {
        return ldapAuthenticated;
    }

    public void setLdapAuthenticated(boolean ldapAuthenticated) {
        this.ldapAuthenticated = ldapAuthenticated;
    }
}