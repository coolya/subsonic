package net.sourceforge.subsonic.dao;

import net.sourceforge.subsonic.domain.*;
import org.springframework.jdbc.core.*;
import org.springframework.dao.*;

import java.util.*;

/**
 * Unit test of {@link UserDao}.
 * @author Sindre Mehus
 */
public class UserDaoTestCase extends DaoTestCaseBase {

    protected void setUp() throws Exception {
        JdbcTemplate template = getJdbcTemplate();
        template.execute("delete from user_role");
        template.execute("delete from user");
    }

    public void testCreateUser() {
        User user = new User("sindre", "secret", 1000L, 2000L, 3000L);
        user.setAdminRole(true);
        user.setCommentRole(true);
        user.setCoverArtRole(true);
        user.setDownloadRole(false);
        user.setPlaylistRole(true);
        user.setUploadRole(false);
        userDao.createUser(user);

        User newUser = userDao.getAllUsers()[0];
        assertUserEquals(user, newUser);
    }

    public void testUpdateUser() {
        User user = new User("sindre", "secret");
        user.setAdminRole(true);
        user.setCommentRole(true);
        user.setCoverArtRole(true);
        user.setDownloadRole(false);
        user.setPlaylistRole(true);
        user.setUploadRole(false);
        userDao.createUser(user);

        user.setPassword("foo");
        user.setBytesStreamed(1);
        user.setBytesDownloaded(2);
        user.setBytesUploaded(3);
        user.setAdminRole(false);
        user.setCommentRole(false);
        user.setCoverArtRole(false);
        user.setDownloadRole(true);
        user.setPlaylistRole(false);
        user.setUploadRole(true);

        userDao.updateUser(user);

        User newUser = userDao.getAllUsers()[0];
        assertUserEquals(user, newUser);
        assertEquals("Wrong bytes streamed.", 1, newUser.getBytesStreamed());
        assertEquals("Wrong bytes downloaded.", 2, newUser.getBytesDownloaded());
        assertEquals("Wrong bytes uploaded.", 3, newUser.getBytesUploaded());
    }

    public void testGetUserByName() {
        User user = new User("sindre", "secret");
        userDao.createUser(user);

        User newUser = userDao.getUserByName("sindre");
        assertNotNull("Error in getUserByName().", newUser);
        assertUserEquals(user, newUser);

        assertNull("Error in getUserByName().", userDao.getUserByName("sindre2"));
        assertNull("Error in getUserByName().", userDao.getUserByName("sindre "));
        assertNull("Error in getUserByName().", userDao.getUserByName("bente"));
        assertNull("Error in getUserByName().", userDao.getUserByName(""));
        assertNull("Error in getUserByName().", userDao.getUserByName(null));
    }

    public void testDeleteUser() {
        assertEquals("Wrong number of users.", 0, userDao.getAllUsers().length);

        userDao.createUser(new User("sindre", "secret"));
        assertEquals("Wrong number of users.", 1, userDao.getAllUsers().length);

        userDao.createUser(new User("bente", "secret"));
        assertEquals("Wrong number of users.", 2, userDao.getAllUsers().length);

        userDao.deleteUser("sindre");
        assertEquals("Wrong number of users.", 1, userDao.getAllUsers().length);

        userDao.deleteUser("bente");
        assertEquals("Wrong number of users.", 0, userDao.getAllUsers().length);
    }

    public void testGetRolesForUser() {
        User user = new User("sindre", "secret");
        user.setAdminRole(true);
        user.setCommentRole(true);
        userDao.createUser(user);

        String[] roles = userDao.getRolesForUser("sindre");
        assertEquals("Wrong number of roles.", 2, roles.length);
        assertEquals("Wrong role.", "admin", roles[0]);
        assertEquals("Wrong role.", "comment", roles[1]);
    }

    public void testUserSettings() {
        assertNull("Error in getUserSettings.", userDao.getUserSettings("sindre"));

        try {
            userDao.updateUserSettings(new UserSettings("sindre", null, null));
            fail("Expected DataIntegrityViolationException.");
        } catch (DataIntegrityViolationException x) {}

        userDao.createUser(new User("sindre", "secret"));
        assertNull("Error in getUserSettings.", userDao.getUserSettings("sindre"));

        userDao.updateUserSettings(new UserSettings("sindre", null, null));
        UserSettings userSettings = userDao.getUserSettings("sindre");
        assertNotNull("Error in getUserSettings().", userSettings);
        assertNull("Error in getUserSettings().", userSettings.getLocale());
        assertNull("Error in getUserSettings().", userSettings.getThemeId());

        userDao.updateUserSettings(new UserSettings("sindre", Locale.SIMPLIFIED_CHINESE, "midnight"));
        userSettings = userDao.getUserSettings("sindre");
        assertNotNull("Error in getUserSettings().", userSettings);
        assertEquals("Error in getUserSettings().", Locale.SIMPLIFIED_CHINESE, userSettings.getLocale());
        assertEquals("Error in getUserSettings().", "midnight", userSettings.getThemeId());

        userDao.deleteUser("sindre");
        assertNull("Error in cascading delete.", userDao.getUserSettings("sindre"));
    }

    private void assertUserEquals(User expected, User actual) {
        assertEquals("Wrong name.", expected.getUsername(), actual.getUsername());
        assertEquals("Wrong password.", expected.getPassword(), actual.getPassword());
        assertEquals("Wrong bytes streamed.", expected.getBytesStreamed(), actual.getBytesStreamed());
        assertEquals("Wrong bytes downloaded.", expected.getBytesDownloaded(), actual.getBytesDownloaded());
        assertEquals("Wrong bytes uploaded.", expected.getBytesUploaded(), actual.getBytesUploaded());
        assertEquals("Wrong admin role.", expected.isAdminRole(), actual.isAdminRole());
        assertEquals("Wrong comment role.", expected.isCommentRole(), actual.isCommentRole());
        assertEquals("Wrong cover art role.", expected.isCoverArtRole(), actual.isCoverArtRole());
        assertEquals("Wrong download role.", expected.isDownloadRole(), actual.isDownloadRole());
        assertEquals("Wrong playlist role.", expected.isPlaylistRole(), actual.isPlaylistRole());
        assertEquals("Wrong upload role.", expected.isUploadRole(), actual.isUploadRole());
    }
}