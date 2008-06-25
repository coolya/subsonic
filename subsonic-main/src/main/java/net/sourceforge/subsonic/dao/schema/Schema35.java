package net.sourceforge.subsonic.dao.schema;

import net.sourceforge.subsonic.Logger;
import org.apache.commons.io.IOUtils;
import org.springframework.jdbc.core.JdbcTemplate;

import java.io.IOException;
import java.io.InputStream;
import java.util.Date;

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

        if (!tableExists(template, "system_avatar")) {
            LOG.info("Database table 'system_avatar' not found.  Creating it.");
            template.execute("create table system_avatar (" +
                             "id identity," +
                             "name varchar," +
                             "created_date datetime not null," +
                             "mime_type varchar not null," +
                             "width int not null," +
                             "height int not null," +
                             "data binary not null)");
            LOG.info("Database table 'system_avatar' was created successfully.");
        }
        for (int i = 1; i <= 40; i++) {
            createAvatar(template, "system-avatar-" + i + ".png", 48, 48);
        }

        if (!columnExists(template, "avatar_scheme", "user_settings")) {
            LOG.info("Database column 'user_settings.avatar_scheme' not found.  Creating it.");
            template.execute("alter table user_settings add avatar_scheme varchar default 'NONE' not null");
            LOG.info("Database column 'user_settings.avatar_scheme' was added successfully.");
        }

        if (!columnExists(template, "system_avatar_id", "user_settings")) {
            LOG.info("Database column 'user_settings.system_avatar_id' not found.  Creating it.");
            template.execute("alter table user_settings add system_avatar_id int");
            template.execute("alter table user_settings add foreign key (system_avatar_id) references system_avatar(id)");
            LOG.info("Database column 'user_settings.system_avatar_id' was added successfully.");
        }
    }

    private void createAvatar(JdbcTemplate template, String avatar, int width, int height) {
        if (template.queryForInt("select count(*) from system_avatar where name = ?", new Object[]{avatar}) == 0) {

            InputStream in = null;
            try {
                in = getClass().getResourceAsStream(avatar);
                byte[] imageData = IOUtils.toByteArray(in);
                template.update("insert into system_avatar values (null, ?, ?, ?, ?, ?, ?)",
                                new Object[]{avatar, new Date(), "image/png", width, height, imageData});
                LOG.info("Created avatar '" + avatar + "'.");
            } catch (IOException x) {
                LOG.error("Failed to create avatar '" + avatar + "'.", x);
            } finally {
                IOUtils.closeQuietly(in);
            }
        }
    }
}