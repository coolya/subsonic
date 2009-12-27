package net.sourceforge.subsonic.backend.dao;

import net.sourceforge.subsonic.backend.dao.schema.Schema;
import net.sourceforge.subsonic.backend.dao.schema.Schema10;
import org.apache.log4j.Logger;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;
import java.io.File;

/**
 * DAO helper class which creates the data source, and updates the database schema.
 *
 * @author Sindre Mehus
 */
public class DaoHelper {

    private static final Logger LOG = Logger.getLogger(DaoHelper.class);

    private Schema[] schemas = {new Schema10()};
    private DataSource dataSource;
    private static boolean shutdownHookAdded;

    public DaoHelper() {
        dataSource = createDataSource();
        checkDatabase();
        addShutdownHook();
    }

    private void addShutdownHook() {
        if (shutdownHookAdded) {
            return;
        }
        shutdownHookAdded = true;
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                System.err.println("Shutting down database.");
                getJdbcTemplate().execute("shutdown");
                System.err.println("Done.");
            }
        });
    }

    /**
     * Returns a JDBC template for performing database operations.
     *
     * @return A JDBC template.
     */
    public JdbcTemplate getJdbcTemplate() {
        return new JdbcTemplate(dataSource);
    }

    private DataSource createDataSource() {
        File home = getHomeDirectory();
        DriverManagerDataSource ds = new DriverManagerDataSource();
        ds.setDriverClassName("org.hsqldb.jdbcDriver");
        ds.setUrl("jdbc:hsqldb:file:" + home.getPath() + "/db/subsonic-backend");
        ds.setUsername("sa");
        ds.setPassword("");

        return ds;
    }

    private File getHomeDirectory() {
        File home = new File("/var/subsonic-backend");
        if (!home.exists() || !home.isDirectory()) {
            boolean success = home.mkdirs();
            if (!success) {
                String message = "The directory " + home + " does not exist. Please create it and make it writable.";
                LOG.error(message);
                throw new RuntimeException(message);
            }
        }
        return home;
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
