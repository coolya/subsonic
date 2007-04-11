package net.sourceforge.subsonic.controller;

import net.sourceforge.subsonic.command.DonateCommand;
import net.sourceforge.subsonic.service.SettingsService;
import org.springframework.validation.BindException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.SimpleFormController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;

/**
 * Controller for the donation page.
 *
 * @author Sindre Mehus
 */
public class DonateController extends SimpleFormController {

    private SettingsService settingsService;

    protected Object formBackingObject(HttpServletRequest request) throws Exception {
        DonateCommand command = new DonateCommand();
        command.setPath(request.getParameter("path"));

        String email = settingsService.getLicenseEmail();
        String license = settingsService.getLicenseCode();

        command.setEmailAddress(email);
        command.setLicenseDate(settingsService.getLicenseDate());
        command.setLicenseValid(settingsService.isLicenseValid(email, license));

        return command;
    }

    protected ModelAndView onSubmit(HttpServletRequest request, HttpServletResponse response, Object com, BindException errors)
            throws Exception {
        DonateCommand command = (DonateCommand) com;
        Date now = new Date();

        settingsService.setLicenseCode(command.getLicense());
        settingsService.setLicenseEmail(command.getEmailAddress());
        settingsService.setLicenseDate(now);
        settingsService.save();

        // Reflect changes in view. The validator has already validated the license.
        command.setLicenseValid(true);
        command.setLicenseDate(now);

        return new ModelAndView(getSuccessView(), errors.getModel());
    }

    public void setSettingsService(SettingsService settingsService) {
        this.settingsService = settingsService;
    }
}