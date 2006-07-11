package net.sourceforge.subsonic.dao;

import net.sourceforge.subsonic.*;
import net.sourceforge.subsonic.util.*;
import net.sourceforge.subsonic.domain.*;
import org.springframework.jdbc.core.*;

import java.sql.*;
import java.util.*;

/**
 * Provides user-related database services, including authorization and authentication.
 *
 * @author Sindre Mehus
 */
public class UserDao extends AbstractDao {

    private static final Logger LOG = Logger.getLogger(UserDao.class);
    private static final String COLUMNS = "username, password, bytes_streamed, bytes_downloaded, bytes_uploaded, locale, theme";

    private static final Integer ROLE_ID_ADMIN      = 1;
    private static final Integer ROLE_ID_DOWNLOAD   = 2;
    private static final Integer ROLE_ID_UPLOAD     = 3;
    private static final Integer ROLE_ID_PLAYLIST   = 4;
    private static final Integer ROLE_ID_COVER_ART  = 5;
    private static final Integer ROLE_ID_COMMENT    = 6;

    private UserRowMapper rowMapper = new UserRowMapper();

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
        String sql = "insert into user (" + COLUMNS + ") values (?, ?, ?, ?, ?, ?, ?)";
        String locale = user.getLocale() == null ? null : user.getLocale().toString();
        getJdbcTemplate().update(sql, new Object[] {user.getUsername(), user.getPassword(), user.getBytesStreamed(),
                                                    user.getBytesDownloaded(), user.getBytesUploaded(),
                                                    locale, user.getThemeId()});
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
    * Updates the given user.
    * @param user The user to update.
    */
    public void updateUser(User user) {
        String sql = "update user set password=?, bytes_streamed=?, bytes_downloaded=?, bytes_uploaded=?, " +
                     "locale=?, theme=? where username=?";
        String locale = user.getLocale() == null ? null : user.getLocale().toString();
        getJdbcTemplate().update(sql, new Object[] {user.getPassword(), user.getBytesStreamed(),
                                                    user.getBytesDownloaded(), user.getBytesUploaded(),
                                                    locale, user.getThemeId(), user.getUsername()});
        writeRoles(user);
    }

    /**
     * Returns the name of the roles for the given user.
     * @param username The user name.
     * @return Roles the user is granted.
     */
    public String[] getRolesForUser(String username) {
        String sql = "select r.name from role r, user_role ur " +
                     "where ur.username=? and ur.role_id=r.id";
        List roles = getJdbcTemplate().queryForList(sql, new Object[] {username}, String.class);
        String[] result = new String[roles.size()];
        for (int i = 0; i < result.length; i++) {
            result[i] = (String) roles.get(i);
        }
        return result;
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
            return new User(rs.getString(1), rs.getString(2), rs.getLong(3), rs.getLong(4),
                            rs.getLong(5), StringUtil.parseLocale(rs.getString(6)), rs.getString(7));
        }
    }
}
