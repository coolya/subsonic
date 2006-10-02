package net.sourceforge.subsonic.dao.schema;

import net.sourceforge.subsonic.*;
import org.springframework.jdbc.core.*;

/**
 * Used for creating and evolving the database schema.
 * This class implementes the database schema for Subsonic version 2.9.
 *
 * @author Sindre Mehus
 */
public class Schema29 extends Schema {
    private static final Logger LOG = Logger.getLogger(Schema29.class);

    public void execute(JdbcTemplate template) {

        if (template.queryForInt("select count(*) from version where version = 5") == 0) {
            LOG.info("Updating database schema to version 5.");
            template.execute("insert into version values (5)");
        }

        if (!tableExists(template, "user_rating")) {
            LOG.info("Database table 'user_rating' not found.  Creating it.");
            template.execute("create table user_rating (" +
                             "username varchar not null," +
                             "path varchar not null," +
                             "rating double not null," +
                             "primary key (username, path)," +
                             "foreign key (username) references user(username) on delete cascade)");
            LOG.info("Database table 'user_rating' was created successfully.");

            template.execute("insert into user_rating select 'admin', path, rating from music_file_info " +
                             "where rating is not null and rating > 0");
            LOG.info("Migrated data from 'music_file_info' to 'user_rating'.");
        }
    }
}
