package net.sourceforge.subsonic.validator;

import net.sourceforge.subsonic.command.*;
import net.sourceforge.subsonic.controller.*;
import net.sourceforge.subsonic.service.*;
import org.springframework.validation.*;

/**
 * Validator for {@link UserSettingsController}.
 *
 * @author Sindre Mehus
 */
public class UserSettingsValidator implements Validator {

    private SecurityService securityService;

    public boolean supports(Class clazz) {
        return clazz.equals(UserSettingsCommand.class);
    }

    public void validate(Object obj, Errors errors) {
        UserSettingsCommand command = (UserSettingsCommand) obj;
        String username = command.getUsername();
        String password = command.getPassword();
        String confirmPassword = command.getConfirmPassword();

        if (command.isNew()) {
            if (username == null || username.length() == 0) {
                errors.rejectValue("username", "usersettings.nousername");
            } else if (securityService.getUserByName(username) != null) {
                errors.rejectValue("username", "usersettings.useralreadyexists");
            }
        }

        if (command.isNew() || command.isPasswordChange()) {
            if (password == null || password.length() == 0) {
                errors.rejectValue("password", "usersettings.nopassword");
            } else {
                if (!password.equals(confirmPassword)) {
                    errors.rejectValue("password", "usersettings.wrongpassword");
                }
            }
        }
    }

    public void setSecurityService(SecurityService securityService) {
        this.securityService = securityService;
    }
}