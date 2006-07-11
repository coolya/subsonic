package net.sourceforge.subsonic.dao;

import net.sourceforge.subsonic.*;
import net.sourceforge.subsonic.domain.*;
import org.springframework.jdbc.core.*;

import java.sql.*;

/**
 * Provides database services for internet radio.
 *
 * @author Sindre Mehus
 */
public class InternetRadioDao extends AbstractDao {

    private static final Logger LOG = Logger.getLogger(InternetRadioDao.class);
    private static final String COLUMNS = "id, name, stream_url, homepage_url, enabled";
    private InternetRadioRowMapper rowMapper = new InternetRadioRowMapper();

    /**
    * Returns all internet radio stations.
    * @return Possibly empty array of all internet radio stations.
    */
    public InternetRadio[] getAllInternetRadios() {
        String sql = "select " + COLUMNS + " from internet_radio";
        return (InternetRadio[]) getJdbcTemplate().query(sql, rowMapper).toArray(new InternetRadio[0]);
    }

    /**
     * Creates a new internet radio station.
     * @param radio The internet radio station to create.
     */
    public void createInternetRadio(InternetRadio radio) {
        String sql = "insert into internet_radio (" + COLUMNS + ") values (null, ?, ?, ?, ?)";
        getJdbcTemplate().update(sql, new Object[] {radio.getName(), radio.getStreamUrl(), radio.getHomepageUrl(), radio.isEnabled()});
        LOG.info("Created internet radio station " + radio.getName());
    }

    /**
     * Deletes the internet radio station with the given ID.
     * @param id The internet radio station ID.
     */
    public void deleteInternetRadio(Integer id) {
        String sql = "delete from internet_radio where id=?";
        getJdbcTemplate().update(sql, new Object[] {id});
        LOG.info("Deleted internet radio station with ID " + id);
    }

    /**
     * Updates the given internet radio station.
     * @param radio The internet radio station to update.
     */
    public void updateInternetRadio(InternetRadio radio) {
        String sql = "update internet_radio set name=?, stream_url=?, homepage_url=?, enabled=? where id=?";
        getJdbcTemplate().update(sql, new Object[] {radio.getName(), radio.getStreamUrl(), radio.getHomepageUrl(),
                                                              radio.isEnabled(), radio.getId()});
    }

    private static class InternetRadioRowMapper implements RowMapper {
        public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
            return new InternetRadio(rs.getInt(1), rs.getString(2), rs.getString(3), rs.getString(4), rs.getBoolean(5));
        }
    }

}
