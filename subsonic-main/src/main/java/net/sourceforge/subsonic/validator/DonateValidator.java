package net.sourceforge.subsonic.validator;

import org.springframework.validation.Validator;
import org.springframework.validation.Errors;
import net.sourceforge.subsonic.command.PasswordSettingsCommand;
import net.sourceforge.subsonic.command.DonateCommand;
import net.sourceforge.subsonic.controller.DonateController;
import net.sourceforge.subsonic.service.SettingsService;

/**
 * Validator for {@link DonateController}.
 *
 * @author Sindre Mehus
 */
public class DonateValidator implements Validator {
    private SettingsService settingsService;

    public boolean supports(Class clazz) {
        return clazz.equals(DonateCommand.class);
    }

    public void validate(Object obj, Errors errors) {
        DonateCommand command = (DonateCommand) obj;

        if (!settingsService.isLicenseValid(command.getEmailAddress(), command.getLicense())) {
            errors.rejectValue("license", "donate.invalidlicense");
        }
    }

    public void setSettingsService(SettingsService settingsService) {
        this.settingsService = settingsService;
    }
}
