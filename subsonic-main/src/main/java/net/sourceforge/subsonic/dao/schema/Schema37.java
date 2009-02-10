/*
 This file is part of Subsonic.

 Subsonic is free software: you can redistribute it and/or modify
 it under the terms of the GNU General Public License as published by
 the Free Software Foundation, either version 3 of the License, or
 (at your option) any later version.

 Subsonic is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with Subsonic.  If not, see <http://www.gnu.org/licenses/>.

 Copyright 2009 (C) Sindre Mehus
 */
package net.sourceforge.subsonic.dao.schema;

import net.sourceforge.subsonic.Logger;
import org.springframework.jdbc.core.JdbcTemplate;

/**
 * Used for creating and evolving the database schema.
 * This class implementes the database schema for Subsonic version 3.7.
 *
 * @author Sindre Mehus
 */
public class Schema37 extends Schema {

    private static final Logger LOG = Logger.getLogger(Schema37.class);

    @Override
    public void execute(JdbcTemplate template) {

        if (template.queryForInt("select count(*) from version where version = 13") == 0) {
            LOG.info("Updating database schema to version 13.");
            template.execute("insert into version values (13)");
        }

        if (template.queryForInt("select count(*) from role where id = 9") == 0) {
            LOG.info("Role 'settings' not found in database. Creating it.");
            template.execute("insert into role values (9, 'settings')");
            template.execute("insert into user_role select distinct u.username, 9 from user u");
            LOG.info("Role 'settings' was created successfully.");
        }
    }
}