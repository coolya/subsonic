package net.sourceforge.subsonic.dao;

import net.sourceforge.subsonic.*;
import net.sourceforge.subsonic.domain.*;
import org.springframework.jdbc.core.*;

import java.sql.*;
import java.io.*;

/**
 * Provides database services for music folders.
 *
 * @author Sindre Mehus
 */
public class MusicFolderDao extends AbstractDao {

    private static final Logger LOG = Logger.getLogger(MusicFolderDao.class);
    private static final String COLUMNS = "id, path, name, enabled";
    private MusicFolderRowMapper rowMapper = new MusicFolderRowMapper();

    /**
     * Returns all music folders.
     * @return Possibly empty array of all music folders.
     */
    public MusicFolder[] getAllMusicFolders() {
        String sql = "select " + COLUMNS + " from music_folder";
        return (MusicFolder[]) getJdbcTemplate().query(sql, rowMapper).toArray(new MusicFolder[0]);
    }

    /**
     * Creates a new music folder.
     * @param musicFolder The music folder to create.
     */
    public void createMusicFolder(MusicFolder musicFolder) {
        String sql = "insert into music_folder (" + COLUMNS + ") values (null, ?, ?, ?)";
        getJdbcTemplate().update(sql, new Object[] {musicFolder.getPath(), musicFolder.getName(), musicFolder.isEnabled()});
        LOG.info("Created music folder " + musicFolder.getPath());
    }

    /**
     * Deletes the music folder with the given ID.
     * @param id The music folder ID.
     */
    public void deleteMusicFolder(Integer id) {
        String sql = "delete from music_folder where id=?";
        getJdbcTemplate().update(sql, new Object[] {id});
        LOG.info("Deleted music folder with ID " + id);
    }

    /**
     * Updates the given music folder.
     * @param musicFolder The music folder to update.
     */
    public void updateMusicFolder(MusicFolder musicFolder) {
        String sql = "update music_folder set path=?, name=?, enabled=? where id=?";
        getJdbcTemplate().update(sql, new Object[] {musicFolder.getPath().getPath(), musicFolder.getName(),
                                                    musicFolder.isEnabled(), musicFolder.getId()});
    }

    private static class MusicFolderRowMapper implements RowMapper {
        public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
            return new MusicFolder(rs.getInt(1), new File(rs.getString(2)), rs.getString(3), rs.getBoolean(4));
        }
    }

}
