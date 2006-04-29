package net.sourceforge.subsonic.validator;

import net.sourceforge.subsonic.command.*;
import net.sourceforge.subsonic.controller.*;
import org.springframework.validation.*;

/**
 * Validator for {@link UserSettingsController}.
 *
 * @author Sindre Mehus
 */
public class UserSettingsValidator implements Validator {

    public boolean supports(Class clazz) {
        return clazz.equals(UserSettingsCommand.class);
    }

    public void validate(Object obj, Errors errors) {
        UserSettingsCommand command = (UserSettingsCommand) obj;

        if (command.isNew()) {
            if (command.getUsername() == null || command.getUsername().length() == 0) {
                errors.rejectValue("username", "usersettings.nousername");
            }
        }

        if (command.isNew() || command.isPasswordChange()) {
            if (command.getPassword() == null || command.getPassword().length() == 0) {
                errors.rejectValue("password", "usersettings.nopassword");
            } else if (!command.getPassword().equals(command.getConfirmPassword())) {
                errors.rejectValue("password", "usersettings.wrongpassword");
            }
        }
    }
}