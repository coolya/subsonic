package net.sourceforge.subsonic.backend.dao;

import net.sourceforge.subsonic.backend.domain.Redirection;
import org.apache.log4j.Logger;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Provides database services for transcoding configurations.
 *
 * @author Sindre Mehus
 */
public class RedirectionDao extends AbstractDao {

    private static final Logger LOG = Logger.getLogger(RedirectionDao.class);
    private static final String COLUMNS = "id, principal, redirect_from, redirect_to, trial, trial_expires, last_updated, last_read";

    private RedirectionRowMapper rowMapper = new RedirectionRowMapper();

    /**
     * Returns the redirection with the given "redirect from".
     *
     * @param redirectFrom The "redirect from" string.
     * @return The redirection or <code>null</code> if not found.
     */
    public Redirection getRedirection(String redirectFrom) {
        String sql = "select " + COLUMNS + " from redirection where redirect_from=?";
        return queryOne(sql, rowMapper, redirectFrom);
    }

    /**
     * Creates a new redirection.
     *
     * @param redirection The redirection to create.
     */
    public void createRedirection(Redirection redirection) {
        String sql = "insert into redirection (" + COLUMNS + ") values (null, ?, ?, ?, ?, ?, ?, ?)";
        update(sql, redirection.getPrincipal(), redirection.getRedirectFrom(),
               redirection.getRedirectTo(), redirection.isTrial(),
               redirection.getTrialExpires(), redirection.getLastUpdated(),
               redirection.getLastRead());
        LOG.info("Created redirection " + redirection.getRedirectFrom() + " -> " + redirection.getRedirectTo());
    }

    /**
     * Updates the given redirection.
     *
     * @param redirection The redirection to update.
     */
    public void updateRedirection(Redirection redirection) {
        String sql = "update redirection set principal=?, redirect_from=?, redirect_to=?, " +
                     "trial=?, trial_expires=?, last_updated=?, last_read=? where id=?";
        update(sql, redirection.getPrincipal(), redirection.getRedirectFrom(),
               redirection.getRedirectTo(), redirection.isTrial(), redirection.getTrialExpires(),
               redirection.getLastUpdated(), redirection.getLastRead(), redirection.getId());
    }

    private static class RedirectionRowMapper implements ParameterizedRowMapper<Redirection> {
        public Redirection mapRow(ResultSet rs, int rowNum) throws SQLException {
            return new Redirection(rs.getInt(1), rs.getString(2), rs.getString(3), rs.getString(4), rs.getBoolean(5),
                                   rs.getTimestamp(6), rs.getTimestamp(7), rs.getTimestamp(8));
        }
    }
}
