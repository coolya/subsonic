package net.sourceforge.subsonic.service;

import net.sourceforge.subsonic.*;
import net.sourceforge.subsonic.dao.*;
import net.sourceforge.subsonic.domain.*;

import javax.servlet.http.*;
import java.io.*;

/**
 * Provides security-related services for authentication and authorization.
 *
 * @author Sindre Mehus
 */
public class SecurityService {

    private static final Logger LOG = Logger.getLogger(SecurityService.class);

    private UserDao userDao = new UserDao();

    /**
     * Authenticates a user.
     * @param username The username.
     * @param password The plain text password, as entered by the user.
     * @return Whether the user is authenticated.
     */
    public boolean authenticate(String username, String password) {
        return userDao.authenticate(username, password);
    }

    /**
     * Authorizes a user by checking if it is member of the given role.
     * @param username The username.
     * @param role The role to test for membership.
     * @return Whether the user is authorized for the given role.
     */
    public boolean authorize(String username, String role) {
        return userDao.authorize(username, role);
    }

    /**
     * Returns the currently logged-in user for the given HTTP request.
     * @param request The HTTP request.
     * @return The logged-in user, or <code>null</code>.
     */
    public User getCurrentUser(HttpServletRequest request) {
        String username = request.getRemoteUser();
        return username == null ? null : userDao.getUserByName(username);
    }

    /**
     * Returns the user with the given username.
     * @param username The username used when logging in.
     * @return The user, or <code>null</code> if not found.
     */
    public User getUserByName(String username) {
        return userDao.getUserByName(username);
    }

    /**
     * Returns all users.
     * @return Possibly empty array of all users.
     */
    public User[] getAllUsers() {
        return userDao.getAllUsers();
    }

    /**
     * Creates a new user.
     * @param user The user to create.
     */
    public void createUser(User user) {
        userDao.createUser(user);
        LOG.info("Created user " + user.getUsername());
    }

    /**
     * Deletes the user with the given username.
     * @param username The username.
     */
    public void deleteUser(String username) {
        userDao.deleteUser(username);
        LOG.info("Deleted user " + username);
    }

    /**
     * Updates the password and admin status for the given user.
     * @param user The user to update.
     */
    public void updateUser(User user) {
        userDao.updateUser(user);
        LOG.info("Updated user " + user.getUsername());
    }

    /**
    * Returns whether the given file may be read.
    * @return Whether the given file may be read.
    */
    public boolean isReadAllowed(File file) {
        // Allowed to read from both music folder and playlist folder.
        return isInMusicFolder(file) || isInPlaylistFolder(file);
    }

    /**
     * Returns whether the given file may be written, created or deleted.
     * @return Whether the given file may be written, created or deleted.
     */
    public boolean isWriteAllowed(File file) {
        // Only allowed to write playlists or cover art.
        boolean isPlaylist = isInPlaylistFolder(file);
        boolean isCoverArt = isInMusicFolder(file) && file.getName().startsWith("folder.");

        return isPlaylist || isCoverArt;
    }

    /**
     * Returns whether the given file may be uploaded.
     * @return Whether the given file may be uploaded.
     */
    public boolean isUploadAllowed(File file) {
        return isInMusicFolder(file) && !file.exists();
    }

    /**
     * Returns whether the given file is located in one of the music folders (or any of their sub-folders).
     * @param file The file in question.
     * @return Whether the given file is located in one of the music folders.
     */
    private boolean isInMusicFolder(File file) {
        MusicFolder[] folders =  ServiceFactory.getSettingsService().getAllMusicFolders();
        String path = file.getPath();
        for (MusicFolder folder : folders) {
            if (isFileInFolder(path, folder.getPath().getPath())) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns whether the given file is located in the playlist folder (or any of its sub-folders).
     * @param file The file in question.
     * @return Whether the given file is located in the playlist folder.
     */
    private boolean isInPlaylistFolder(File file) {
        String playlistFolder = ServiceFactory.getSettingsService().getPlaylistFolder();
        return isFileInFolder(file.getPath(), playlistFolder);
    }

    /**
     * Returns whether the given file is located in the given folder (or any of its sub-folders).
     * If the given file contains the expression ".." (indicating a reference to the parent directory),
     * this method will return <code>false</code>.
     * @param file The file in question.
     * @param folder The folder in question.
     * @return Whether the given file is located in the given folder.
     */
    protected boolean isFileInFolder(String file, String folder) {
        // Deny access if file contains ".." surrounded by slashes (or end of line).
        if (file.matches(".*(/|\\\\)\\.\\.(/|\\\\|$).*")) {
            return false;
        }

        // Convert slashes.
        file = file.replace('\\', '/');
        folder = folder.replace('\\', '/');

        return file.toUpperCase().startsWith(folder.toUpperCase());
    }
}