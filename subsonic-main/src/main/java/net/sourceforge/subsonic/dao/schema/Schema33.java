package net.sourceforge.subsonic.dao.schema;

import net.sourceforge.subsonic.Logger;
import org.springframework.jdbc.core.JdbcTemplate;

/**
 * Used for creating and evolving the database schema.
 * This class implementes the database schema for Subsonic version 3.3.
 *
 * @author Sindre Mehus
 */
public class Schema33 extends Schema {

    private static final Logger LOG = Logger.getLogger(Schema33.class);

    public void execute(JdbcTemplate template) {

        if (template.queryForInt("select count(*) from version where version = 9") == 0) {
            LOG.info("Updating database schema to version 9.");
            template.execute("insert into version values (9)");
        }

        if (!columnExists(template, "client_side_playlist", "player")) {
            LOG.info("Database column 'player.client_side_playlist' not found.  Creating it.");
            template.execute("alter table player add client_side_playlist boolean default false not null");
            LOG.info("Database column 'player.client_side_playlist' was added successfully.");
        }
    }
}
