package net.sourceforge.subsonic.dao.schema;

import net.sourceforge.subsonic.*;
import org.springframework.jdbc.core.*;

/**
 * Used for creating and evolving the database schema.
 * This class implementes the database schema for Subsonic version 2.6.
 *
 * @author Sindre Mehus
 * @version $Revision: 1.2 $ $Date: 2006/02/25 17:39:39 $
 */
public class Schema26 implements Schema{
    private static final Logger LOG = Logger.getLogger(Schema26.class);

    public void execute(JdbcTemplate template) {

        if (template.queryForInt("select count(*) from version where version = 2") == 0) {
            LOG.info("Updating database schema to version 2.");
            template.execute("insert into version values (2)");
        }

        try {
            template.execute("select 1 from music_folder");
        } catch (Exception x) {
            LOG.info("Database table 'music_folder' not found.  Creating it.");
            template.execute("create table music_folder (" +
                             "id identity," +
                             "path varchar not null," +
                             "name varchar not null," +
                             "enabled boolean not null)");
            template.execute("insert into music_folder values (null, 'c:\\music', 'Music', true)");
            LOG.info("Database table 'music_folder' was created successfully.");
        }

        try {
            template.execute("select 1 from music_file_info");
        } catch (Exception x) {
            LOG.info("Database table 'music_file_info' not found.  Creating it.");
            template.execute("create cached table music_file_info (" +
                             "id identity," +
                             "path varchar not null," +
                             "rating int," +
                             "comment varchar," +
                             "play_count int," +
                             "last_played datetime)");
            template.execute("create index idx_music_file_info_path on music_file_info(path)");
            LOG.info("Database table 'music_file_info' was created successfully.");
        }

        try {
            template.execute("select 1 from internet_radio");
        } catch (Exception x) {
            LOG.info("Database table 'internet_radio' not found.  Creating it.");
            template.execute("create table internet_radio (" +
                             "id identity," +
                             "name varchar not null," +
                             "stream_url varchar not null," +
                             "homepage_url varchar," +
                             "enabled boolean not null)");
            LOG.info("Database table 'internet_radio' was created successfully.");
        }

        try {
            template.execute("select 1 from player");
        } catch (Exception x) {
            LOG.info("Database table 'player' not found.  Creating it.");
            template.execute("create table player (" +
                             "id int not null," +
                             "name varchar," +
                             "type varchar," +
                             "username varchar," +
                             "ip_address varchar," +
                             "auto_control_enabled boolean not null," +
                             "last_seen datetime," +
                             "cover_art_scheme varchar not null," +
                             "transcode_scheme varchar not null," +
                             "primary key (id))");
            LOG.info("Database table 'player' was created successfully.");
        }

        // 'dynamic_ip' was added in 2.6.beta2
        try {
            template.execute("select dynamic_ip from player");
        } catch (Exception x) {
            LOG.info("Database column 'player.dynamic_ip' not found.  Creating it.");
            template.execute("alter table player " +
                             "add dynamic_ip boolean default true not null");
            LOG.info("Database column 'player.dynamic_ip' was added successfully.");
        }

        if (template.queryForInt("select count(*) from role where id = 6") == 0) {
            LOG.info("Role 'comment' not found in database. Creating it.");
            template.execute("insert into role values (6, 'comment')");
            template.execute("insert into user_role " +
                             "select distinct u.username, 6 from user u, user_role ur " +
                             "where u.username = ur.username and ur.role_id in (1, 5)");
            LOG.info("Role 'comment' was created successfully.");
        }
    }
}
