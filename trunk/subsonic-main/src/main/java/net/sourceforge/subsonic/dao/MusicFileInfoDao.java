package net.sourceforge.subsonic.dao;

import net.sourceforge.subsonic.*;
import net.sourceforge.subsonic.domain.*;
import org.springframework.jdbc.core.*;

import java.sql.*;
import java.util.*;

/**
 * Provides database services for music file info.
 *
 * @author Sindre Mehus
 */
public class MusicFileInfoDao extends AbstractDao {

    private static final Logger LOG = Logger.getLogger(MusicFileInfoDao.class);
    private static final String COLUMNS = "id, path, rating, comment, play_count, last_played";
    private MusicFileInfoRowMapper rowMapper = new MusicFileInfoRowMapper();

    /**
    * Returns music file info for the given path.
    * @return Music file info for the given path, or <code>null</code> if not found.
    */
    public MusicFileInfo getMusicFileInfoForPath(String path) {
        String sql = "select " + COLUMNS + " from music_file_info where path=?";
        List result = getJdbcTemplate().query(sql, new Object[] {path}, rowMapper);
        return (MusicFileInfo) (result.isEmpty() ? null : result.get(0));
    }

    /**
     * Returns info for the highest rated music files.
     * @param offset Number of files to skip.
     * @param count Maximum number of files to return.
     * @return Info for the highest rated music files.
     */
    public MusicFileInfo[] getHighestRated(int offset, int count) {
        if (count < 1) {
            return new MusicFileInfo[0];
        }

        JdbcTemplate template = getJdbcTemplate();
        template.setMaxRows(offset + count);
        String sql = "select " + COLUMNS + " from music_file_info where rating > 0 order by rating desc";
        MusicFileInfo[] tmp = (MusicFileInfo[]) template.query(sql, rowMapper).toArray(new MusicFileInfo[0]);
        return copy(tmp, offset, count);
    }

    /**
    * Returns info for the most frequently played music files.
     * @param offset Number of files to skip.
    * @param count Maximum number of elements to return.
    * @return Info for the most frequently played music files.
    */
    public MusicFileInfo[] getMostFrequentlyPlayed(int offset, int count) {
        if (count < 1) {
            return new MusicFileInfo[0];
        }

        JdbcTemplate template = getJdbcTemplate();
        template.setMaxRows(offset + count);
        String sql = "select " + COLUMNS + " from music_file_info where play_count > 0 order by play_count desc";
        MusicFileInfo[] tmp = (MusicFileInfo[]) template.query(sql, rowMapper).toArray(new MusicFileInfo[0]);
        return copy(tmp, offset, count);
    }

    /**
     * Returns info for the most recently played music files.
     * @param offset Number of files to skip.
     * @param count Maximum number of elements to return.
     * @return Info for the most recently played music files.
     */
    public MusicFileInfo[] getMostRecentlyPlayed(int offset, int count) {
        if (count < 1) {
            return new MusicFileInfo[0];
        }

        JdbcTemplate template = getJdbcTemplate();
        template.setMaxRows(offset + count);
        String sql = "select " + COLUMNS + " from music_file_info where last_played is not null order by last_played desc";
        MusicFileInfo[] tmp = (MusicFileInfo[]) template.query(sql, rowMapper).toArray(new MusicFileInfo[0]);
        return copy(tmp, offset, count);
    }

    /**
    * Creates a new music file info.
    * @param info The music file info to create.
    */
    public void createMusicFileInfo(MusicFileInfo info) {
        String sql = "insert into music_file_info (" + COLUMNS + ") values (null, ?, ?, ?, ?, ?)";
        getJdbcTemplate().update(sql, new Object[] {info.getPath(), info.getRating(), info.getComment(),
                                                    info.getPlayCount(), info.getLastPlayed()});
        LOG.info("Created music file info for " + info.getPath());
    }

    /**
     * Updates the given music file info.
     * @param info The music file info to update.
     */
    public void updateMusicFileInfo(MusicFileInfo info) {
        String sql = "update music_file_info set path=?, rating=?, comment=?, play_count=?, last_played=? where id=?";
        getJdbcTemplate().update(sql, new Object[] {info.getPath(), info.getRating(), info.getComment(),
                                                    info.getPlayCount(), info.getLastPlayed(), info.getId()});
    }

    /**
     * A "lenient" array copy.
     * @param src The array to copy from.
     * @param offset Offset in the original array.
     * @param length Maximum number of elements to copy.
     * @return The sub-array.
     */
    private MusicFileInfo[] copy(MusicFileInfo[] src, int offset, int length) {
        if (src.length < offset) {
            return new MusicFileInfo[0];
        }
        int actualLength = Math.min(length, src.length - offset);
        MusicFileInfo[] result = new MusicFileInfo[actualLength];
        System.arraycopy(src, offset, result, 0, actualLength);
        return result;
    }

    private static class MusicFileInfoRowMapper implements RowMapper {
        public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
            return new MusicFileInfo(rs.getInt(1), rs.getString(2), rs.getInt(3), rs.getString(4), rs.getInt(5), rs.getTimestamp(6));
        }
    }

}
