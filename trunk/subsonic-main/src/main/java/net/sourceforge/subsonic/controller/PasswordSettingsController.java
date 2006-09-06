package net.sourceforge.subsonic.controller;

import org.springframework.web.servlet.mvc.*;
import net.sourceforge.subsonic.service.*;
import net.sourceforge.subsonic.command.*;
import net.sourceforge.subsonic.domain.*;

import javax.servlet.http.*;

/**
 * Controller for the page used to change password.
 *
 * @author Sindre Mehus
 */
public class PasswordSettingsController extends SimpleFormController {

    private SecurityService securityService;

    protected Object formBackingObject(HttpServletRequest request) throws Exception {
        PasswordSettingsCommand command = new PasswordSettingsCommand();
        User user = securityService.getCurrentUser(request);
        command.setUsername(user.getUsername());
        return command;
    }

    protected void doSubmitAction(Object comm) throws Exception {
        PasswordSettingsCommand command = (PasswordSettingsCommand) comm;
        User user = securityService.getUserByName(command.getUsername());
        user.setPassword(command.getPassword());
        securityService.updateUser(user);

        command.setPassword(null);
        command.setConfirmPassword(null);
    }

    public void setSecurityService(SecurityService securityService) {
        this.securityService = securityService;
    }
}
