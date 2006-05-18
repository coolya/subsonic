package net.sourceforge.subsonic.dao.schema;

import net.sourceforge.subsonic.*;
import org.springframework.jdbc.core.*;

/**
 * Used for creating and evolving the database schema.
 * This class implementes the database schema for Subsonic version 2.7.
 *
 * @author Sindre Mehus
 * @version $Revision: 1.2 $ $Date: 2006/02/25 17:39:39 $
 */
public class Schema27 implements Schema{
    private static final Logger LOG = Logger.getLogger(Schema27.class);

    public void execute(JdbcTemplate template) {

        if (template.queryForInt("select count(*) from version where version = 3") == 0) {
            LOG.info("Updating database schema to version 3.");
            template.execute("insert into version values (3)");

            LOG.info("Converting database column 'music_file_info.path' to varchar_ignorecase.");
            template.execute("drop index idx_music_file_info_path");
            template.execute("alter table music_file_info alter column path varchar_ignorecase not null");
            template.execute("create index idx_music_file_info_path on music_file_info(path)");
            LOG.info("Database column 'music_file_info.path' was converted successfully.");
        }

        try {
            template.execute("select bytes_streamed from user");
        } catch (Exception x) {
            LOG.info("Database columns 'user.bytes_streamed/downloaded/uploaded' not found.  Creating them.");
            template.execute("alter table user add bytes_streamed bigint default 0 not null");
            template.execute("alter table user add bytes_downloaded bigint default 0 not null");
            template.execute("alter table user add bytes_uploaded bigint default 0 not null");
            LOG.info("Database columns 'user.bytes_streamed/downloaded/uploaded' were added successfully.");
        }
    }
}
