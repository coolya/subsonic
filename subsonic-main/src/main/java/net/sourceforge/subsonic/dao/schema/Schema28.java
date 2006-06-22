package net.sourceforge.subsonic.dao.schema;

import net.sourceforge.subsonic.*;
import org.springframework.jdbc.core.*;

/**
 * Used for creating and evolving the database schema.
 * This class implementes the database schema for Subsonic version 2.8.
 *
 * @author Sindre Mehus
 */
public class Schema28 implements Schema{
    private static final Logger LOG = Logger.getLogger(Schema28.class);

    public void execute(JdbcTemplate template) {

        if (template.queryForInt("select count(*) from version where version = 4") == 0) {
            Schema28.LOG.info("Updating database schema to version 4.");
            template.execute("insert into version values (4)");
        }

        try {
            template.execute("select locale from user");
        } catch (Exception x) {
            LOG.info("Database columns 'user.locale/theme' not found.  Creating them.");
            template.execute("alter table user add locale varchar");
            template.execute("alter table user add theme varchar");
            LOG.info("Database columns 'user.locale/theme' were added successfully.");
        }
    }
}
