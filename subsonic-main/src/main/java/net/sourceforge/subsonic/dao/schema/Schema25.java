package net.sourceforge.subsonic.dao.schema;

import org.springframework.jdbc.core.*;
import net.sourceforge.subsonic.*;

/**
 * Used for creating and evolving the database schema.
 * This class implementes the database schema for Subsonic version 2.5.
 *
 * @author Sindre Mehus
 * @version $Revision: 1.8 $ $Date: 2006/02/25 16:52:36 $
 */
public class Schema25 implements Schema{
    private static final Logger LOG = Logger.getLogger(Schema25.class);

    public void execute(JdbcTemplate template) {
        try {
            template.execute("select 1 from version");
        } catch (Exception x) {
            LOG.info("Database table 'version' not found.  Creating it.");
            template.execute("create table version (version int not null)");
            template.execute("insert into version values (1)");
            LOG.info("Database table 'version' was created successfully.");
        }

        try {
            template.execute("select 1 from role");
        } catch (Exception x) {
            LOG.info("Database table 'role' not found.  Creating it.");
            template.execute("create table role (" +
                             "id int not null," +
                             "name varchar not null," +
                             "primary key (id))");
            template.execute("insert into role values (1, 'admin')");
            template.execute("insert into role values (2, 'download')");
            template.execute("insert into role values (3, 'upload')");
            template.execute("insert into role values (4, 'playlist')");
            template.execute("insert into role values (5, 'coverart')");
            LOG.info("Database table 'role' was created successfully.");
        }

        try {
            template.execute("select 1 from user");
        } catch (Exception x) {
            LOG.info("Database table 'user' not found.  Creating it.");
            template.execute("create table user (" +
                             "username varchar not null," +
                             "password varchar not null," +
                             "primary key (username))");
            template.execute("insert into user values ('admin', 'admin')");
            LOG.info("Database table 'user' was created successfully.");
        }

        try {
            template.execute("select 1 from user_role");
        } catch (Exception x) {
            LOG.info("Database table 'user_role' not found.  Creating it.");
            template.execute("create table user_role (" +
                             "username varchar not null," +
                             "role_id int not null," +
                             "primary key (username, role_id)," +
                             "foreign key (username) references user(username)," +
                             "foreign key (role_id) references role(id))");
            template.execute("insert into user_role values ('admin', 1)");
            template.execute("insert into user_role values ('admin', 2)");
            template.execute("insert into user_role values ('admin', 3)");
            template.execute("insert into user_role values ('admin', 4)");
            template.execute("insert into user_role values ('admin', 5)");
            LOG.info("Database table 'user_role' was created successfully.");
        }
    }
}
