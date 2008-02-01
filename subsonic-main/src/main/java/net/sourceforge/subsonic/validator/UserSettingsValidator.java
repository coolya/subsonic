package net.sourceforge.subsonic.validator;

import net.sourceforge.subsonic.command.UserSettingsCommand;
import net.sourceforge.subsonic.controller.UserSettingsController;
import net.sourceforge.subsonic.service.SecurityService;
import net.sourceforge.subsonic.service.SettingsService;
import org.apache.commons.lang.StringUtils;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

/**
 * Validator for {@link UserSettingsController}.
 *
 * @author Sindre Mehus
 */
public class UserSettingsValidator implements Validator {

    private SecurityService securityService;
    private SettingsService settingsService;

    /**
     * {@inheritDoc}
     */
    public boolean supports(Class clazz) {
        return clazz.equals(UserSettingsCommand.class);
    }

    /**
     * {@inheritDoc}
     */
    public void validate(Object obj, Errors errors) {
        UserSettingsCommand command = (UserSettingsCommand) obj;
        String username = command.getUsername();
        String password = StringUtils.trimToNull(command.getPassword());
        String confirmPassword = command.getConfirmPassword();

        if (command.isNew()) {
            if (username == null || username.length() == 0) {
                errors.rejectValue("username", "usersettings.nousername");
            } else if (securityService.getUserByName(username) != null) {
                errors.rejectValue("username", "usersettings.useralreadyexists");
            } else if (command.isLdapAuthenticated() && !settingsService.isLdapEnabled()) {
                errors.rejectValue("password", "usersettings.ldapdisabled");
            } else if (command.isLdapAuthenticated() && password != null) {
                errors.rejectValue("password", "usersettings.passwordnotsupportedforldap");
            }
        }

        if ((command.isNew() || command.isPasswordChange()) && !command.isLdapAuthenticated()) {
            if (password == null) {
                errors.rejectValue("password", "usersettings.nopassword");
            } else if (!password.equals(confirmPassword)) {
                errors.rejectValue("password", "usersettings.wrongpassword");
            }
        }

        if (command.isPasswordChange() && command.isLdapAuthenticated()) {
            errors.rejectValue("password", "usersettings.passwordnotsupportedforldap");
        }

    }

    public void setSecurityService(SecurityService securityService) {
        this.securityService = securityService;
    }

    public void setSettingsService(SettingsService settingsService) {
        this.settingsService = settingsService;
    }
}