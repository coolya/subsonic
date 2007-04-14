package net.sourceforge.subsonic.dao;

import net.sourceforge.subsonic.*;
import net.sourceforge.subsonic.service.*;
import net.sourceforge.subsonic.dao.schema.*;
import org.springframework.jdbc.datasource.*;
import org.springframework.jdbc.core.*;

import javax.sql.*;
import java.io.*;

/**
 * DAO helper class which creates the data source, and updates the database schema.
 *
 * @author Sindre Mehus
 */
public class DaoHelper {

    private static final Logger LOG = Logger.getLogger(DaoHelper.class);

    private Schema[] schemas = {new Schema25(), new Schema26(), new Schema27(), new Schema28(), new Schema29(), 
                                new Schema30(), new Schema31()};
    private DataSource dataSource;

    public DaoHelper() {
        dataSource = createDataSource();
        checkDatabase();
    }

    /**
     * Returns a JDBC template for performing database operations.
     * @return A JDBC template.
     */
    public JdbcTemplate getJdbcTemplate() {
        return new JdbcTemplate(dataSource);
    }

    private DataSource createDataSource() {
        File subsonicHome = SettingsService.getSubsonicHome();
        DriverManagerDataSource ds = new DriverManagerDataSource();
        ds.setDriverClassName("org.hsqldb.jdbcDriver");
        ds.setUrl("jdbc:hsqldb:file:" + subsonicHome.getPath() + "/db/subsonic");
        ds.setUsername("sa");
        ds.setPassword("");

        return ds;
    }

    private void checkDatabase() {
        LOG.info("Checking database schema.");
        try {
            for (Schema schema : schemas) {
                schema.execute(getJdbcTemplate());
            }
            LOG.info("Done checking database schema.");
        } catch (Exception x) {
            LOG.error("Failed to initialize database.", x);
        }
    }
}
