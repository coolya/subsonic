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
 * This class implements the database schema for Subsonic version 4.0.
 *
 * @author Sindre Mehus
 */
public class Schema40 extends Schema {

    private static final Logger LOG = Logger.getLogger(Schema40.class);

    @Override
    public void execute(JdbcTemplate template) {

//        if (template.queryForInt("select count(*) from version where version = 15") == 0) {
//            LOG.info("Updating database schema to version 15.");
//            template.execute("insert into version values (15)");
//        }
//
//        if (!tableExists(template, "processed_video")) {
//            LOG.info("Database table 'processed_video' not found.  Creating it.");
//            template.execute("create table processed_video (" +
//                             "id identity," +
//                             "path varchar not null," +
//                             "source_path varchar not null," +
//                             "log_path varchar not null," +
//                             "quality varchar not null," +
//                             "status varchar not null," +
//                             "bit_rate int not null," +
//                             "size bigint not null)");
//
//            template.execute("create index idx_processed_video_source_path on processed_video(source_path)");
//
//            LOG.info("Database table 'processed_video' was created successfully.");
//        }
    }
}