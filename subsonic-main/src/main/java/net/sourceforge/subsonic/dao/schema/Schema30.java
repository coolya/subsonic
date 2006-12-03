package net.sourceforge.subsonic.dao.schema;

import net.sourceforge.subsonic.*;
import org.springframework.jdbc.core.*;

/**
 * Used for creating and evolving the database schema.
 * This class implementes the database schema for Subsonic version 3.0.
 *
 * @author Sindre Mehus
 */
public class Schema30 extends Schema {
    private static final Logger LOG = Logger.getLogger(Schema30.class);

    public void execute(JdbcTemplate template) {

        if (template.queryForInt("select count(*) from version where version = 6") == 0) {
            LOG.info("Updating database schema to version 6.");
            template.execute("insert into version values (6)");
        }
        
        if (!columnExists(template, "last_fm_enabled", "user_settings")) {
            LOG.info("Database columns 'user_settings.last_fm_*' not found.  Creating them.");
            template.execute("alter table user_settings add last_fm_enabled boolean default false not null");
            template.execute("alter table user_settings add last_fm_username varchar null");
            template.execute("alter table user_settings add last_fm_password varchar null");
            LOG.info("Database columns 'user_settings.last_fm_*' were added successfully.");
        }

    }
}