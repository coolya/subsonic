package net.sourceforge.subsonic.dao.schema;

import org.springframework.jdbc.core.*;

/**
 * Used for creating and evolving the database schema.
 *
 * @author Sindre Mehus
 * @version $Revision: 1.1 $ $Date: 2005/12/04 15:05:56 $
 */
public interface Schema {
    void execute(JdbcTemplate template);
}
