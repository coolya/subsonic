package net.sourceforge.subsonic.dao;

import net.sourceforge.subsonic.*;
import net.sourceforge.subsonic.domain.*;
import org.springframework.jdbc.core.*;
import org.springframework.dao.*;

import java.sql.*;
import java.util.*;

/**
 * Provides database services for music file info.
 *
 * @author Sindre Mehus
 */
@SuppressWarnings({"unchecked"})
public class MusicFileInfoDao extends AbstractDao {

    private static final Logger LOG = Logger.getLogger(MusicFileInfoDao.class);
    private static final String COLUMNS = "id, path, comment, play_count, last_played";
    private MusicFileInfoRowMapper rowMapper = new MusicFileInfoRowMapper();

    /**
     * Returns music file info for the given path.
     *
     * @return Music file info for the given path, or <code>null</code> if not found.
     */
    public MusicFileInfo getMusicFileInfoForPath(String path) {
        String sql = "select " + COLUMNS + " from music_file_info where path=?";
        List result = getJdbcTemplate().query(sql, new Object[]{path}, rowMapper);
        return (MusicFileInfo) (result.isEmpty() ? null : result.get(0));
    }

    /**
     * Returns paths for the highest rated music files.
     *
     * @param offset Number of files to skip.
     * @param count  Maximum number of files to return.
     * @return Paths for the highest rated music files.
     */
    public List<String> getHighestRated(int offset, int count) {
        if (count < 1) {
            return new ArrayList<String>();
        }
        String sql = "select path from user_rating " +
                     "group by path " +
                     "order by avg(rating) desc " +
                     " limit " + count + " offset " + offset;
        return getJdbcTemplate().queryForList(sql, String.class);
    }

    /**
     * Returns info for the most frequently played music files.
     *
     * @param offset Number of files to skip.
     * @param count  Maximum number of elements to return.
     * @return Info for the most frequently played music files.
     */
    public MusicFileInfo[] getMostFrequentlyPlayed(int offset, int count) {
        if (count < 1) {
            return new MusicFileInfo[0];
        }

        JdbcTemplate template = getJdbcTemplate();
        String sql = "select " + COLUMNS + " from music_file_info where play_count > 0 order by play_count desc limit " + count + " offset " + offset;
        return (MusicFileInfo[]) template.query(sql, rowMapper).toArray(new MusicFileInfo[0]);
    }

    /**
     * Returns info for the most recently played music files.
     *
     * @param offset Number of files to skip.
     * @param count  Maximum number of elements to return.
     * @return Info for the most recently played music files.
     */
    public MusicFileInfo[] getMostRecentlyPlayed(int offset, int count) {
        if (count < 1) {
            return new MusicFileInfo[0];
        }

        JdbcTemplate template = getJdbcTemplate();
        String sql = "select " + COLUMNS + " from music_file_info where last_played is not null order by last_played desc limit " + count + " offset " + offset;
        return (MusicFileInfo[]) template.query(sql, rowMapper).toArray(new MusicFileInfo[0]);
    }

    /**
     * Creates a new music file info.
     *
     * @param info The music file info to create.
     */
    public void createMusicFileInfo(MusicFileInfo info) {
        String sql = "insert into music_file_info (" + COLUMNS + ") values (null, ?, ?, ?, ?)";
        getJdbcTemplate().update(sql, new Object[] {info.getPath(), info.getComment(),
                                                    info.getPlayCount(), info.getLastPlayed()});
        LOG.info("Created music file info for " + info.getPath());
    }

    /**
     * Updates the given music file info.
     *
     * @param info The music file info to update.
     */
    public void updateMusicFileInfo(MusicFileInfo info) {
        String sql = "update music_file_info set path=?, comment=?, play_count=?, last_played=? where id=?";
        getJdbcTemplate().update(sql, new Object[]{info.getPath(), info.getComment(),
                                                   info.getPlayCount(), info.getLastPlayed(), info.getId()});
    }

    /**
     * Sets the rating for a music file and a given user.
     *
     * @param username  The user name.
     * @param musicFile The music file.
     * @param rating    The rating between 1 and 5, or <code>null</code> to remove the rating.
     */
    public void setRatingForUser(String username, MusicFile musicFile, Integer rating) {
        getJdbcTemplate().update("delete from user_rating where username=? and path=?", new Object[]{username, musicFile.getPath()});
        if (rating != null && rating > 0) {
            getJdbcTemplate().update("insert into user_rating values(?, ?, ?)", new Object[]{username, musicFile.getPath(), rating});
        }
    }

    /**
     * Returns the average rating for the given music file.
     *
     * @param musicFile The music file.
     * @return The average rating, or <code>null</code> if no ratings are set.
     */
    public Double getAverageRating(MusicFile musicFile) {
        try {
            return (Double) getJdbcTemplate().queryForObject("select avg(rating) from user_rating where path=?", new Object[]{musicFile.getPath()}, Double.class);
        } catch (EmptyResultDataAccessException x) {
            return null;
        }
    }

    /**
     * Returns the rating for the given user and music file.
     *
     * @param username  The user name.
     * @param musicFile The music file.
     * @return The rating, or <code>null</code> if no rating is set.
     */
    public Integer getRatingForUser(String username, MusicFile musicFile) {
        try {
            return getJdbcTemplate().queryForInt("select rating from user_rating where username=? and path=?", new Object[]{username, musicFile.getPath()});
        } catch (EmptyResultDataAccessException x) {
            return null;
        }
    }


    private static class MusicFileInfoRowMapper implements RowMapper {
        public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
            return new MusicFileInfo(rs.getInt(1), rs.getString(2), rs.getString(3), rs.getInt(4), rs.getTimestamp(5));
        }
    }

}
