package net.sourceforge.subsonic.dao;

import net.sourceforge.subsonic.*;
import net.sourceforge.subsonic.dao.schema.*;
import net.sourceforge.subsonic.service.*;
import org.springframework.jdbc.core.*;
import org.springframework.jdbc.datasource.*;

import javax.sql.*;
import java.io.*;

/**
 * Abstract superclass for all DAO classes.
 *
 * @author Sindre Mehus
 */
public abstract class AbstractDao {

    private static final Logger LOG = Logger.getLogger(AbstractDao.class);
    private static final Schema[] SCHEMAS = {new Schema25(), new Schema26(), new Schema27(), new Schema28()};

    private static final Object MUTEX = new Object();
    private static DriverManagerDataSource dataSource;

    /**
    * Returns a JDBC template for performing database operations.
    * @return A JDBC template.
    */
    protected JdbcTemplate getJdbcTemplate() {
        return new JdbcTemplate(getDataSource());
    }

    private DataSource getDataSource() {
        synchronized (MUTEX) {
            if (dataSource == null) {
                File subsonicHome = SettingsService.getSubsonicHome();
                dataSource = new DriverManagerDataSource();
                dataSource.setDriverClassName("org.hsqldb.jdbcDriver");
                dataSource.setUrl("jdbc:hsqldb:file:" + subsonicHome.getPath() + "/db/subsonic");
                dataSource.setUsername("sa");
                dataSource.setPassword("");

                try {
                    checkDatabase();
                } catch (Exception x) {
                    LOG.error("Failed to initialize database.", x);
                }
            }
            return dataSource;
        }
    }

    private void checkDatabase() {
        LOG.info("Checking database schema.");
        for (Schema schema : SCHEMAS) {
            schema.execute(getJdbcTemplate());
        }
        LOG.info("Done checking database schema.");
    }
}
