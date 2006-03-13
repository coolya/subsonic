package net.sourceforge.subsonic.dao;

/**
 * Unit test of {@link net.sourceforge.subsonic.dao.UserDao}.
 * @author Sindre Mehus
 * @version $Revision: 1.2 $ $Date: 2006/02/25 16:11:14 $
 */

import net.sourceforge.subsonic.domain.*;
import org.springframework.jdbc.core.*;

public class UserDaoTestCase extends DaoTestCaseBase {

    private UserDao userDao;

    protected void setUp() throws Exception {
        userDao = new UserDao();
        JdbcTemplate template = userDao.getJdbcTemplate();
        template.execute("delete from user_role");
        template.execute("delete from user");
    }

    public void testCreateUser() {
        User user = new User("sindre", "secret");
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
        user.setAdminRole(false);
        user.setCommentRole(false);
        user.setCoverArtRole(false);
        user.setDownloadRole(true);
        user.setPlaylistRole(false);
        user.setUploadRole(true);
        userDao.updateUser(user);

        User newUser = userDao.getAllUsers()[0];
        assertUserEquals(user, newUser);
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

    public void testAuthenticate() {
        userDao.createUser(new User("sindre", "secret"));
        assertTrue("Error in authentication.", userDao.authenticate("sindre", "secret"));
        assertFalse("Error in authentication.", userDao.authenticate("sindre", "wrong"));
        assertFalse("Error in authentication.", userDao.authenticate("sindre", ""));
        assertFalse("Error in authentication.", userDao.authenticate("sindre", null));
        assertFalse("Error in authentication.", userDao.authenticate("wrong", "secret"));
        assertFalse("Error in authentication.", userDao.authenticate("", "secret"));
        assertFalse("Error in authentication.", userDao.authenticate(null, "secret"));
        assertFalse("Error in authentication.", userDao.authenticate(null, null));
        assertFalse("Error in authentication.", userDao.authenticate("", ""));
    }

    public void testAuthorize() {
        User user = new User("sindre", "secret");
        userDao.createUser(user);

        assertEquals("Error in authorization.", user.isAdminRole(),    userDao.authorize("sindre", "admin"));
        assertEquals("Error in authorization.", user.isCommentRole(),  userDao.authorize("sindre", "comment"));
        assertEquals("Error in authorization.", user.isCoverArtRole(), userDao.authorize("sindre", "coverart"));
        assertEquals("Error in authorization.", user.isDownloadRole(), userDao.authorize("sindre", "download"));
        assertEquals("Error in authorization.", user.isPlaylistRole(), userDao.authorize("sindre", "playlist"));
        assertEquals("Error in authorization.", user.isUploadRole(),   userDao.authorize("sindre", "upload"));

        assertEquals("Error in authorization.", true, userDao.authorize("sindre", "user"));
        assertEquals("Error in authorization.", true, userDao.authorize("anyone", "user"));
    }

    private void assertUserEquals(User expected, User actual) {
        assertEquals("Wrong name.", expected.getUsername(), actual.getUsername());
        assertEquals("Wrong password.", expected.getPassword(), actual.getPassword());
        assertEquals("Wrong admin role.", expected.isAdminRole(), actual.isAdminRole());
        assertEquals("Wrong comment role.", expected.isCommentRole(), actual.isCommentRole());
        assertEquals("Wrong cover art role.", expected.isCoverArtRole(), actual.isCoverArtRole());
        assertEquals("Wrong download role.", expected.isDownloadRole(), actual.isDownloadRole());
        assertEquals("Wrong playlist role.", expected.isPlaylistRole(), actual.isPlaylistRole());
        assertEquals("Wrong upload role.", expected.isUploadRole(), actual.isUploadRole());
    }
}