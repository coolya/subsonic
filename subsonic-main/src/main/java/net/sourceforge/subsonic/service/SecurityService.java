package net.sourceforge.subsonic.service;

import net.sourceforge.subsonic.*;
import net.sourceforge.subsonic.dao.*;
import net.sourceforge.subsonic.domain.*;
import net.sourceforge.subsonic.domain.User;
import org.acegisecurity.*;
import org.acegisecurity.providers.dao.*;
import org.acegisecurity.userdetails.*;
import org.acegisecurity.wrapper.*;
import org.springframework.dao.*;

import javax.servlet.http.*;
import java.io.*;

/**
 * Provides security-related services for authentication and authorization.
 *
 * @author Sindre Mehus
 */
public class SecurityService implements UserDetailsService {

    private static final Logger LOG = Logger.getLogger(SecurityService.class);

    private UserDao userDao;
    private SettingsService settingsService;

    /**
    * Locates the user based on the username.
    *
    * @param username The username presented to the {@link DaoAuthenticationProvider}
    * @return A fully populated user record (never <code>null</code>)
    * @throws UsernameNotFoundException
    *          if the user could not be found or the user has no GrantedAuthority
    * @throws DataAccessException
    *          if user could not be found for a repository-specific reason
    */
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException, DataAccessException {
        User user = getUserByName(username);
        if (user == null) {
            throw new UsernameNotFoundException("User \"" + username + "\" was not found.");
        }

        String[] roles = userDao.getRolesForUser(username);
        GrantedAuthority[] authorities = new GrantedAuthority[roles.length];
        for (int i = 0; i < roles.length; i++) {
            authorities[i] = new GrantedAuthorityImpl("ROLE_" + roles[i].toUpperCase());
        }

        return new org.acegisecurity.userdetails.User(username, user.getPassword(), true, true, true, true, authorities);
    }

    /**
     * Returns the currently logged-in user for the given HTTP request.
     * @param request The HTTP request.
     * @return The logged-in user, or <code>null</code>.
     */
    public User getCurrentUser(HttpServletRequest request) {
        String username = getCurrentUsername(request);
        return username == null ? null : userDao.getUserByName(username);
    }

    /**
     * Returns the name of the currently logged-in user.
     * @param request The HTTP request.
     * @return The name of the logged-in user, or <code>null</code>.
     */
    public String getCurrentUsername(HttpServletRequest request) {
        return new SecurityContextHolderAwareRequestWrapper(request).getRemoteUser();
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
    }

    /**
    * Returns whether the given file may be read.
    * @return Whether the given file may be read.
    */
    public boolean isReadAllowed(File file) {
        // Allowed to read from both music folder and playlist folder.
        // TODO: Allow reading from podcast folder.
        return isInMusicFolder(file) || isInPlaylistFolder(file);
    }

    /**
     * Returns whether the given file may be written, created or deleted.
     * @return Whether the given file may be written, created or deleted.
     */
    public boolean isWriteAllowed(File file) {
        // Only allowed to write playlists, podcasts or cover art.
        boolean isPlaylist = isInPlaylistFolder(file);
        boolean isPodcast = isInPodcastFolder(file);
        boolean isCoverArt = isInMusicFolder(file) && file.getName().startsWith("folder.");

        return isPlaylist || isPodcast || isCoverArt;
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
        MusicFolder[] folders =  settingsService.getAllMusicFolders();
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
        String playlistFolder = settingsService.getPlaylistFolder();
        return isFileInFolder(file.getPath(), playlistFolder);
    }

    /**
     * Returns whether the given file is located in the Podcast folder (or any of its sub-folders).
     * @param file The file in question.
     * @return Whether the given file is located in the Podcast folder.
     */
    private boolean isInPodcastFolder(File file) {
        String podcastFolder = settingsService.getPodcastFolder();
        return isFileInFolder(file.getPath(), podcastFolder);
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

    public void setSettingsService(SettingsService settingsService) {
        this.settingsService = settingsService;
    }

    public void setUserDao(UserDao userDao) {
        this.userDao = userDao;
    }
}