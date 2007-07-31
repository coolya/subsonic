package net.sourceforge.subsonic.dao;

import net.sourceforge.subsonic.*;
import net.sourceforge.subsonic.domain.*;
import net.sourceforge.subsonic.util.*;
import org.springframework.jdbc.core.*;

import java.sql.*;
import java.util.*;

/**
 * Provides user-related database services.
 *
 * @author Sindre Mehus
 */
@SuppressWarnings({"unchecked"})
public class UserDao extends AbstractDao {

    private static final Logger LOG = Logger.getLogger(UserDao.class);
    private static final String USER_COLUMNS = "username, password, bytes_streamed, bytes_downloaded, bytes_uploaded";
    private static final String USER_SETTINGS_COLUMNS = "username, locale, theme_id, final_version_notification, beta_version_notification, " +
                                                        "main_caption_cutoff, main_track_number, main_artist, main_album, main_genre, " +
                                                        "main_year, main_bit_rate, main_duration, main_format, main_file_size, " +
                                                        "playlist_caption_cutoff, playlist_track_number, playlist_artist, playlist_album, playlist_genre, " +
                                                        "playlist_year, playlist_bit_rate, playlist_duration, playlist_format, playlist_file_size, " +
                                                        "last_fm_enabled, last_fm_username, last_fm_password, transcode_scheme, show_now_playing";

    private static final Integer ROLE_ID_ADMIN      = 1;
    private static final Integer ROLE_ID_DOWNLOAD   = 2;
    private static final Integer ROLE_ID_UPLOAD     = 3;
    private static final Integer ROLE_ID_PLAYLIST   = 4;
    private static final Integer ROLE_ID_COVER_ART  = 5;
    private static final Integer ROLE_ID_COMMENT    = 6;
    private static final Integer ROLE_ID_PODCAST    = 7;

    private UserRowMapper userRowMapper = new UserRowMapper();
    private UserSettingsRowMapper userSettingsRowMapper = new UserSettingsRowMapper();

    /**
     * Returns the user with the given username.
     * @param username The username used when logging in.
     * @return The user, or <code>null</code> if not found.
     */
    public User getUserByName(String username) {
        String sql = "select " + USER_COLUMNS + " from user where username=?";
        User[] users = (User[]) getJdbcTemplate().query(sql, new Object[] {username}, userRowMapper).toArray(new User[0]);
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
        String sql = "select " + USER_COLUMNS + " from user";
        User[] users = (User[]) getJdbcTemplate().query(sql, userRowMapper).toArray(new User[0]);
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
        String sql = "insert into user (" + USER_COLUMNS + ") values (" + questionMarks(USER_COLUMNS) + ')';
        getJdbcTemplate().update(sql, new Object[]{user.getUsername(), user.getPassword(), user.getBytesStreamed(),
                                                   user.getBytesDownloaded(), user.getBytesUploaded()});
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
        String sql = "update user set password=?, bytes_streamed=?, bytes_downloaded=?, bytes_uploaded=? " +
                     "where username=?";
        getJdbcTemplate().update(sql, new Object[] {user.getPassword(), user.getBytesStreamed(),
                                                    user.getBytesDownloaded(), user.getBytesUploaded(),
                                                    user.getUsername()});
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
        List<?> roles = getJdbcTemplate().queryForList(sql, new Object[] {username}, String.class);
        String[] result = new String[roles.size()];
        for (int i = 0; i < result.length; i++) {
            result[i] = (String) roles.get(i);
        }
        return result;
    }

    /**
     * Returns settings for the given user.
     * @param username The username.
     * @return User-specific settings, or <code>null</code> if no such settings exist.
     */
    public UserSettings getUserSettings(String username) {
        String sql = "select " + USER_SETTINGS_COLUMNS + " from user_settings where username=?";
        UserSettings[] result = (UserSettings[]) getJdbcTemplate().query(sql, new Object[]{username}, userSettingsRowMapper).toArray(new UserSettings[0]);

        return result.length == 0 ? null : result[0];
    }

    /**
     * Updates settings for the given username, creating it if necessary.
     * @param settings The user-specific settings.
     */
    public void updateUserSettings(UserSettings settings) {
        getJdbcTemplate().update("delete from user_settings where username=?", new Object[]{settings.getUsername()});

        String sql = "insert into user_settings (" + USER_SETTINGS_COLUMNS + ") values (" + questionMarks(USER_SETTINGS_COLUMNS) + ')';
        String locale = settings.getLocale() == null ? null : settings.getLocale().toString();
        UserSettings.Visibility main = settings.getMainVisibility();
        UserSettings.Visibility playlist = settings.getPlaylistVisibility();
        getJdbcTemplate().update(sql, new Object[]{settings.getUsername(), locale, settings.getThemeId(),
                                                   settings.isFinalVersionNotificationEnabled(), settings.isBetaVersionNotificationEnabled(),
                                                   main.getCaptionCutoff(), main.isTrackNumberVisible(), main.isArtistVisible(), main.isAlbumVisible(),
                                                   main.isGenreVisible(), main.isYearVisible(), main.isBitRateVisible(), main.isDurationVisible(),
                                                   main.isFormatVisible(), main.isFileSizeVisible(),
                                                   playlist.getCaptionCutoff(), playlist.isTrackNumberVisible(), playlist.isArtistVisible(), playlist.isAlbumVisible(),
                                                   playlist.isGenreVisible(), playlist.isYearVisible(), playlist.isBitRateVisible(), playlist.isDurationVisible(),
                                                   playlist.isFormatVisible(), playlist.isFileSizeVisible(),
                                                   settings.isLastFmEnabled(), settings.getLastFmUsername(), settings.getLastFmPassword(),
                                                   settings.getTranscodeScheme().name(),
                                                   settings.isShowNowPlayingEnabled()});
    }

    private void readRoles(User user) {
        String sql = "select role_id from user_role where username=?";
        List<?> roles = getJdbcTemplate().queryForList(sql, new Object[] {user.getUsername()}, Integer.class);
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
            } else if (ROLE_ID_PODCAST.equals(role)) {
                user.setPodcastRole(true);
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
        if (user.isPodcastRole()) {
            getJdbcTemplate().update(sql, new Object[] {user.getUsername(), ROLE_ID_PODCAST});
        }
    }

    private static class UserRowMapper implements RowMapper {
        public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
            return new User(rs.getString(1), rs.getString(2), rs.getLong(3), rs.getLong(4), rs.getLong(5));
        }
    }

    private static class UserSettingsRowMapper implements RowMapper {
        public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
            int col = 1;
            UserSettings settings = new UserSettings(rs.getString(col++));
            settings.setLocale(StringUtil.parseLocale(rs.getString(col++)));
            settings.setThemeId(rs.getString(col++));
            settings.setFinalVersionNotificationEnabled(rs.getBoolean(col++));
            settings.setBetaVersionNotificationEnabled(rs.getBoolean(col++));

            settings.getMainVisibility().setCaptionCutoff(rs.getInt(col++));
            settings.getMainVisibility().setTrackNumberVisible(rs.getBoolean(col++));
            settings.getMainVisibility().setArtistVisible(rs.getBoolean(col++));
            settings.getMainVisibility().setAlbumVisible(rs.getBoolean(col++));
            settings.getMainVisibility().setGenreVisible(rs.getBoolean(col++));
            settings.getMainVisibility().setYearVisible(rs.getBoolean(col++));
            settings.getMainVisibility().setBitRateVisible(rs.getBoolean(col++));
            settings.getMainVisibility().setDurationVisible(rs.getBoolean(col++));
            settings.getMainVisibility().setFormatVisible(rs.getBoolean(col++));
            settings.getMainVisibility().setFileSizeVisible(rs.getBoolean(col++));

            settings.getPlaylistVisibility().setCaptionCutoff(rs.getInt(col++));
            settings.getPlaylistVisibility().setTrackNumberVisible(rs.getBoolean(col++));
            settings.getPlaylistVisibility().setArtistVisible(rs.getBoolean(col++));
            settings.getPlaylistVisibility().setAlbumVisible(rs.getBoolean(col++));
            settings.getPlaylistVisibility().setGenreVisible(rs.getBoolean(col++));
            settings.getPlaylistVisibility().setYearVisible(rs.getBoolean(col++));
            settings.getPlaylistVisibility().setBitRateVisible(rs.getBoolean(col++));
            settings.getPlaylistVisibility().setDurationVisible(rs.getBoolean(col++));
            settings.getPlaylistVisibility().setFormatVisible(rs.getBoolean(col++));
            settings.getPlaylistVisibility().setFileSizeVisible(rs.getBoolean(col++));

            settings.setLastFmEnabled(rs.getBoolean(col++));
            settings.setLastFmUsername(rs.getString(col++));
            settings.setLastFmPassword(rs.getString(col++));

            settings.setTranscodeScheme(TranscodeScheme.valueOf(rs.getString(col++)));
            settings.setShowNowPlayingEnabled(rs.getBoolean(col++));

            return settings;
        }
    }
}
