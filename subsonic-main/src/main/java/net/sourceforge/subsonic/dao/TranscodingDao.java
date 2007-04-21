package net.sourceforge.subsonic.dao;

import net.sourceforge.subsonic.Logger;
import net.sourceforge.subsonic.domain.Transcoding;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Provides database services for transcoding configurations.
 *
 * @author Sindre Mehus
 */
@SuppressWarnings({"unchecked"})
public class TranscodingDao extends AbstractDao {

    private static final Logger LOG = Logger.getLogger(TranscodingDao.class);
    private static final String COLUMNS = "id, name, source_format, target_format, step1, step2, step3, enabled, default_active";
    private TranscodingRowMapper rowMapper = new TranscodingRowMapper();

    /**
     * Returns all transcodings.
     *
     * @return Possibly empty array of all transcodings.
     */
    public Transcoding[] getAllTranscodings() {
        String sql = "select " + COLUMNS + " from transcoding";
        return (Transcoding[]) getJdbcTemplate().query(sql, rowMapper).toArray(new Transcoding[0]);
    }

    /**
     * Returns all active transcodings for the given player.
     *
     * @param playerId The player ID.
     * @return All active transcodings for the player.
     */
    public Transcoding[] getTranscodingsForPlayer(String playerId) {
        String sql = "select " + COLUMNS + " from transcoding, player_transcoding " +
                     "where player_transcoding.player_id = ? " +
                     "and   player_transcoding.transcoding_id = transcoding.id";
        return (Transcoding[]) getJdbcTemplate().query(sql, new Object[]{playerId}, rowMapper).toArray(new Transcoding[0]);
    }

    /**
     * Sets the list of active transcodings for the given player.
     *
     * @param playerId       The player ID.
     * @param transcodingIds ID's of the active transcodings.
     */
    public void setTranscodingsForPlayer(String playerId, int[] transcodingIds) {
        getJdbcTemplate().update("delete from player_transcoding where player_id = ?", new Object[]{playerId});
        String sql = "insert into player_transcoding(player_id, transcoding_id) values (?, ?)";
        for (int transcodingId : transcodingIds) {
            getJdbcTemplate().update(sql, new Object[]{playerId, transcodingId});
        }
    }

    /**
     * Creates a new transcoding.
     *
     * @param transcoding The transcoding to create.
     */
    public void createTranscoding(Transcoding transcoding) {
        String sql = "insert into transcoding (" + COLUMNS + ") values (null, ?, ?, ?, ?, ?, ?, ?, ?)";
        getJdbcTemplate().update(sql, new Object[] {transcoding.getName(), transcoding.getSourceFormat(),
                                                              transcoding.getTargetFormat(), transcoding.getStep1(),
                                                              transcoding.getStep2(), transcoding.getStep3(),
                                                              transcoding.isEnabled(), transcoding.isDefaultActive()});
        TranscodingDao.LOG.info("Created transcoding " + transcoding.getName());
    }

    /**
     * Deletes the transcoding with the given ID.
     *
     * @param id The transcoding ID.
     */
    public void deleteTranscoding(Integer id) {
        String sql = "delete from transcoding where id=?";
        getJdbcTemplate().update(sql, new Object[]{id});
        TranscodingDao.LOG.info("Deleted transcoding with ID " + id);
    }

    /**
     * Updates the given transcoding.
     *
     * @param transcoding The transcoding to update.
     */
    public void updateTranscoding(Transcoding transcoding) {
        String sql = "update transcoding set name=?, source_format=?, target_format=?, " +
                     "step1=?, step2=?, step3=?, enabled=?, default_active=? where id=?";
        getJdbcTemplate().update(sql, new Object[]{transcoding.getName(), transcoding.getSourceFormat(),
                                                   transcoding.getTargetFormat(), transcoding.getStep1(), transcoding.getStep2(),
                                                   transcoding.getStep3(), transcoding.isEnabled(), transcoding.isDefaultActive(),
                                                   transcoding.getId()});
    }

    private static class TranscodingRowMapper implements RowMapper {
        public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
            return new Transcoding(rs.getInt(1), rs.getString(2), rs.getString(3), rs.getString(4), rs.getString(5),
                                   rs.getString(6), rs.getString(7), rs.getBoolean(8), rs.getBoolean(9));
        }
    }
}
