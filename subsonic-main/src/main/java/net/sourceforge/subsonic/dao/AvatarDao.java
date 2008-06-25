package net.sourceforge.subsonic.dao;

import net.sourceforge.subsonic.domain.Avatar;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * Provides database services for avatars
 *
 * @author Sindre Mehus
 */
@SuppressWarnings({"unchecked"})
public class AvatarDao extends AbstractDao {

    private static final String COLUMNS = "id, name, created_date, mime_type, width, height, data";
    private AvatarRowMapper rowMapper = new AvatarRowMapper();

    /**
     * Returns all system avatars.
     *
     * @return All system avatars.
     */
    public Avatar[] getAllSystemAvatars() {
        String sql = "select " + COLUMNS + " from system_avatar";
        return (Avatar[]) getJdbcTemplate().query(sql, rowMapper).toArray(new Avatar[0]);
    }

    /**
     * Returns the system avatar with the given ID.
     *
     * @param id The system avatar ID.
     * @return The avatar or <code>null</code> if not found.
     */
    public Avatar getSystemAvatar(int id) {
        String sql = "select " + COLUMNS + " from system_avatar where id=" + id;
        List<?> result = getJdbcTemplate().query(sql, rowMapper);
        return (Avatar) (result.isEmpty() ? null : result.get(0));
    }

    /**
     * Returns the custom avatar for the given user.
     *
     * @param username The username.
     * @return The avatar or <code>null</code> if not found.
     */
    public Avatar getCustomAvatar(String username) {
        String sql = "select " + COLUMNS + " from custom_avatar where username=?";
        List<?> result = getJdbcTemplate().query(sql, new Object[] {username}, rowMapper);
        return (Avatar) (result.isEmpty() ? null : result.get(0));
    }

    /**
     * Sets the custom avatar for the given user.
     *
     * @param avatar The avatar, or <code>null</code> to remove the avatar.
     * @param username The username.
     */
    public void setCustomAvatar(Avatar avatar, String username) {
        String sql = "delete from custom_avatar where username=?";
        getJdbcTemplate().update(sql, new Object[] {username});

        if (avatar != null) {
            getJdbcTemplate().update("insert into custom_avatar(" + COLUMNS + ", username) values(" + questionMarks(COLUMNS) + ", ?)",
                                     new Object[]{null, avatar.getName(), avatar.getCreatedDate(), avatar.getMimeType(),
                                                  avatar.getWidth(), avatar.getHeight(), avatar.getData(), username});
        }
    }

    private static class AvatarRowMapper implements ParameterizedRowMapper<Avatar> {
        public Avatar mapRow(ResultSet rs, int rowNum) throws SQLException {
            return new Avatar(rs.getInt(1), rs.getString(2), rs.getTimestamp(3), rs.getString(4),
                              rs.getInt(5), rs.getInt(6), rs.getBytes(7));
        }
    }

}