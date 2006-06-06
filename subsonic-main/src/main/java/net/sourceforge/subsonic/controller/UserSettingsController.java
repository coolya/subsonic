package net.sourceforge.subsonic.controller;

import net.sourceforge.subsonic.service.*;
import net.sourceforge.subsonic.domain.*;
import net.sourceforge.subsonic.command.*;
import org.springframework.web.servlet.mvc.*;
import org.springframework.web.bind.*;

import javax.servlet.http.*;

/**
 * Controller for the page used to administrate users.
 *
 * @author Sindre Mehus
 */
public class UserSettingsController extends SimpleFormController {

    private SecurityService securityService;

    protected Object formBackingObject(HttpServletRequest request) throws Exception {
        UserSettingsCommand command = new UserSettingsCommand();

        User user = getUser(request);
        if (user != null) {
            command.setUser(user);
            command.setAdmin(User.USERNAME_ADMIN.equals(user.getUsername()));
        } else {
            command.setNew(true);
        }

        command.setUsers(securityService.getAllUsers());

        return command;
    }

    private User getUser(HttpServletRequest request) throws ServletRequestBindingException {
        Integer userIndex = ServletRequestUtils.getIntParameter(request, "userIndex");
        if (userIndex != null) {
            User[] allUsers = securityService.getAllUsers();
            if (userIndex >= 0 && userIndex < allUsers.length) {
                return allUsers[userIndex];
            }
        }
        return null;
    }

    protected void doSubmitAction(Object comm) throws Exception {
        UserSettingsCommand command = (UserSettingsCommand) comm;

        if (command.isDelete()) {
            deleteUser(command);
        } else if (command.isNew()) {
            createUser(command);
        } else {
            updateUser(command);
        }
        resetCommand(command);
    }

    private void deleteUser(UserSettingsCommand command) {
        securityService.deleteUser(command.getUsername());
    }

    private void createUser(UserSettingsCommand command) {
        User user = new User(command.getUsername(), command.getPassword());
        securityService.createUser(user);
        updateUser(command);
    }

    private void updateUser(UserSettingsCommand command) {
        User user = securityService.getUserByName(command.getUsername());
        user.setAdminRole(command.isAdminRole());
        user.setDownloadRole(command.isDownloadRole());
        user.setUploadRole(command.isUploadRole());
        user.setPlaylistRole(command.isPlaylistRole());
        user.setCoverArtRole(command.isCoverArtRole());
        user.setCommentRole(command.isCommentRole());

        if (command.isPasswordChange()) {
            user.setPassword(command.getPassword());
        }

        securityService.updateUser(user);
    }

    private void resetCommand(UserSettingsCommand command) {
        command.setUser(null);
        command.setUsers(securityService.getAllUsers());
        command.setDelete(false);
        command.setPasswordChange(false);
        command.setNew(true);
        command.setPassword(null);
        command.setConfirmPassword(null);
    }

    public void setSecurityService(SecurityService securityService) {
        this.securityService = securityService;
    }
}
