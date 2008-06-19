package net.sourceforge.subsonic.dao.schema;

import net.sourceforge.subsonic.Logger;
import org.springframework.jdbc.core.JdbcTemplate;

/**
 * Used for creating and evolving the database schema.
 * This class implementes the database schema for Subsonic version 3.5.
 *
 * @author Sindre Mehus
 */
public class Schema35 extends Schema {

    private static final Logger LOG = Logger.getLogger(Schema35.class);

    @Override
    public void execute(JdbcTemplate template) {

        if (template.queryForInt("select count(*) from version where version = 11") == 0) {
            LOG.info("Updating database schema to version 11.");
            template.execute("insert into version values (11)");
        }

        if (!columnExists(template, "now_playing_allowed", "user_settings")) {
            LOG.info("Database column 'user_settings.now_playing_allowed' not found.  Creating it.");
            template.execute("alter table user_settings add now_playing_allowed boolean default true not null");
            LOG.info("Database column 'user_settings.now_playing_allowed' was added successfully.");
        }

        if (!columnExists(template, "web_player_default", "user_settings")) {
            LOG.info("Database column 'user_settings.web_player_default' not found.  Creating it.");
            template.execute("alter table user_settings add web_player_default boolean default false not null");
            LOG.info("Database column 'user_settings.web_player_default' was added successfully.");
         }

        if (template.queryForInt("select count(*) from role where id = 8") == 0) {
            LOG.info("Role 'stream' not found in database. Creating it.");
            template.execute("insert into role values (8, 'stream')");
            template.execute("insert into user_role " +
                             "select distinct u.username, 8 from user u, user_role ur " +
                             "where u.username = ur.username");
            LOG.info("Role 'stream' was created successfully.");
        }
    }
}