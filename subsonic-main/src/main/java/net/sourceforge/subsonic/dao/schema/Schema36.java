package net.sourceforge.subsonic.dao.schema;

import net.sourceforge.subsonic.Logger;
import org.springframework.jdbc.core.JdbcTemplate;

/**
 * Used for creating and evolving the database schema.
 * This class implementes the database schema for Subsonic version 3.6.
 *
 * @author Sindre Mehus
 */
public class Schema36 extends Schema {

    private static final Logger LOG = Logger.getLogger(Schema35.class);

    @Override
    public void execute(JdbcTemplate template) {

        if (template.queryForInt("select count(*) from version where version = 12") == 0) {
            LOG.info("Updating database schema to version 12.");
            template.execute("insert into version values (12)");
        }

        if (!columnExists(template, "technology", "player")) {
            LOG.info("Database column 'player.technology' not found.  Creating it.");
            template.execute("alter table player add technology varchar default 'WEB' not null");
            LOG.info("Database column 'player.technology' was added successfully.");
        }
    }
}