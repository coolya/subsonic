package net.sourceforge.subsonic.dao.schema;

import net.sourceforge.subsonic.*;
import org.springframework.jdbc.core.*;

/**
 * Used for creating and evolving the database schema.
 * This class implementes the database schema for Subsonic version 2.8.
 *
 * @author Sindre Mehus
 */
public class Schema28 extends Schema {
    private static final Logger LOG = Logger.getLogger(Schema28.class);

    public void execute(JdbcTemplate template) {

        if (template.queryForInt("select count(*) from version where version = 4") == 0) {
            Schema28.LOG.info("Updating database schema to version 4.");
            template.execute("insert into version values (4)");
        }

        if (!columnExists(template, "locale", "user")) {
            LOG.info("Database columns 'user.locale/theme' not found.  Creating them.");
            template.execute("alter table user add locale varchar");
            template.execute("alter table user add theme varchar");
            LOG.info("Database columns 'user.locale/theme' were added successfully.");
        }


        if (!tableExists(template, "transcoding")) {
            LOG.info("Database table 'transcoding' not found.  Creating it.");
            template.execute("create table transcoding (" +
                             "id identity," +
                             "name varchar not null," +
                             "source_format varchar not null," +
                             "target_format varchar not null," +
                             "step1 varchar not null," +
                             "step2 varchar," +
                             "step3 varchar," +
                             "enabled boolean not null)");
// TODO: Insert default transcodings.
//            template.execute("insert into music_folder values (null, 'c:\\music', 'Music', true)");
            LOG.info("Database table 'transcoding' was created successfully.");
        }
    }
}
