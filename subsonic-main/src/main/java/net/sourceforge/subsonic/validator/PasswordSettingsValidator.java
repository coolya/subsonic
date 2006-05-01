package net.sourceforge.subsonic.validator;

import org.springframework.validation.*;
import net.sourceforge.subsonic.command.*;
import net.sourceforge.subsonic.controller.*;

/**
 * Validator for {@link PasswordSettingsController}.
 *
 * @author Sindre Mehus
 */
public class PasswordSettingsValidator implements Validator {

    public boolean supports(Class clazz) {
        return clazz.equals(PasswordSettingsCommand.class);
    }

    public void validate(Object obj, Errors errors) {
        PasswordSettingsCommand command = (PasswordSettingsCommand) obj;

        if (command.getPassword() == null || command.getPassword().length() == 0) {
            errors.rejectValue("password", "usersettings.nopassword");
        } else if (!command.getPassword().equals(command.getConfirmPassword())) {
            errors.rejectValue("password", "usersettings.wrongpassword");
        }
    }
}
