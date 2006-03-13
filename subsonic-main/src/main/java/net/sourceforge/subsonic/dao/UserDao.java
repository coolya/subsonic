package net.sourceforge.subsonic.dao;

import net.sourceforge.subsonic.*;
import net.sourceforge.subsonic.domain.*;
import org.springframework.jdbc.core.*;

import java.sql.*;
import java.util.*;

/**
 * Provides user-related database services, including authorization and authentication.
 *
 * @author Sindre Mehus
 * @version $Revision: 1.5 $ $Date: 2006/02/25 16:11:14 $
 */
public class UserDao extends AbstractDao {

    private static final Logger LOG = Logger.getLogger(UserDao.class);
    private static final String COLUMNS = "username, password";

    private static final String ROLE_USER = "user";
    private static final Integer ROLE_ID_ADMIN      = 1;
    private static final Integer ROLE_ID_DOWNLOAD   = 2;
    private static final Integer ROLE_ID_UPLOAD     = 3;
    private static final Integer ROLE_ID_PLAYLIST   = 4;
    private static final Integer ROLE_ID_COVER_ART  = 5;
    private static final Integer ROLE_ID_COMMENT    = 6;

    private UserRowMapper rowMapper = new UserRowMapper();

    /**
    * Authenticates a user.
    * @param username The username.
    * @param password The plain text password, as entered by the user.
    * @return Whether the user is authenticated.
    */
    public boolean authenticate(String username, String password) {
        String sql = "select count(*) from user where username=? and password=?";
        return getJdbcTemplate().queryForInt(sql, new Object[]{username, password}) > 0;
    }

    /**
     * Authorizes a user by checking if it is member of the given role.
     * @param username The username.
     * @param role The role to test for membership.
     * @return Whether the user is authorized for the given role.
     */
    public boolean authorize(String username, String role) {
        if (ROLE_USER.equals(role)) {
            return true;
        }

        String sql = "select count(*) from user u, role r, user_role ur " +
                     "where u.username = ? and r.name = ? and ur.username = u.username and ur.role_id = r.id";
        return getJdbcTemplate().queryForInt(sql, new Object[]{username, role}) > 0;
    }

    /**
     * Returns the user with the given username.
     * @param username The username used when logging in.
     * @return The user, or <code>null</code> if not found.
     */
    public User getUserByName(String username) {
        String sql = "select " + COLUMNS + " from user where username=?";
        User[] users = (User[]) getJdbcTemplate().query(sql, new Object[] {username}, rowMapper).toArray(new User[0]);
        if (users.length == 0) {
            return null;
        }

        readRoles(users[0]);
        return users[0];
    }

    /**
     * Returns all users.
     * @return Possibly empty array of all users.
     */
    public User[] getAllUsers() {
        String sql = "select " + COLUMNS + " from user";
        User[] users = (User[]) getJdbcTemplate().query(sql, rowMapper).toArray(new User[0]);
        for (User user : users) {
            readRoles(user);
        }
        return users;
    }

    /**
     * Creates a new user.
     * @param user The user to create.
     */
    public void createUser(User user) {
        String sql = "insert into user (" + COLUMNS + ") values (?, ?)";
        getJdbcTemplate().update(sql, new Object[] {user.getUsername(), user.getPassword()});
        writeRoles(user);
    }

    /**
     * Deletes the user with the given username.
     * @param username The username.
     */
    public void deleteUser(String username) {
        if (User.USERNAME_ADMIN.equals(username)) {
            throw new IllegalArgumentException("Can't delete admin user.");
        }

        String sql = "delete from user_role where username=?";
        getJdbcTemplate().update(sql, new Object[] {username});

        sql = "delete from user where username=?";
        getJdbcTemplate().update(sql, new Object[] {username});
    }

    /**
     * Updates the password and admin status for the given user.
     * @param user The user to update.
     */
    public void updateUser(User user) {
        String sql = "update user set password=? where username=?";
        getJdbcTemplate().update(sql, new Object[] {user.getPassword(), user.getUsername()});
        writeRoles(user);
    }

    private void readRoles(User user) {
        String sql = "select role_id from user_role where username=?";
        List roles = getJdbcTemplate().queryForList(sql, new Object[] {user.getUsername()}, Integer.class);
        for (Object role : roles) {
            if (ROLE_ID_ADMIN.equals(role)) {
                user.setAdminRole(true);
            } else if (ROLE_ID_DOWNLOAD.equals(role)) {
                user.setDownloadRole(true);
            } else if (ROLE_ID_UPLOAD.equals(role)) {
                user.setUploadRole(true);
            } else if (ROLE_ID_PLAYLIST.equals(role)) {
                user.setPlaylistRole(true);
            } else if (ROLE_ID_COVER_ART.equals(role)) {
                user.setCoverArtRole(true);
            } else if (ROLE_ID_COMMENT.equals(role)) {
                user.setCommentRole(true);
            } else {
                LOG.warn("Unknown role: '" + role + '\'');
            }

        }
    }

    private void writeRoles(User user) {
        String sql = "delete from user_role where username=?";
        getJdbcTemplate().update(sql, new Object[] {user.getUsername()});
        sql = "insert into user_role (username, role_id) values(?, ?)";
        if (user.isAdminRole()) {
            getJdbcTemplate().update(sql, new Object[] {user.getUsername(), ROLE_ID_ADMIN});
        }
        if (user.isDownloadRole()) {
            getJdbcTemplate().update(sql, new Object[] {user.getUsername(), ROLE_ID_DOWNLOAD});
        }
        if (user.isUploadRole()) {
            getJdbcTemplate().update(sql, new Object[] {user.getUsername(), ROLE_ID_UPLOAD});
        }
        if (user.isPlaylistRole()) {
            getJdbcTemplate().update(sql, new Object[] {user.getUsername(), ROLE_ID_PLAYLIST});
        }
        if (user.isCoverArtRole()) {
            getJdbcTemplate().update(sql, new Object[] {user.getUsername(), ROLE_ID_COVER_ART});
        }
        if (user.isCommentRole()) {
            getJdbcTemplate().update(sql, new Object[] {user.getUsername(), ROLE_ID_COMMENT});
        }
    }

    private static class UserRowMapper implements RowMapper {
        public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
            return new User(rs.getString(1), rs.getString(2));
        }
    }
}
