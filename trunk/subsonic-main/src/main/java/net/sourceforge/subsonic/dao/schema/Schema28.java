package net.sourceforge.subsonic.dao.schema;

import net.sourceforge.subsonic.*;
import org.springframework.jdbc.core.*;

/**
 * Used for creating and evolving the database schema.
 * This class implementes the database schema for Subsonic version 2.8.
 *
 * @author Sindre Mehus
 */
public class Schema28 extends Schema {
    private static final Logger LOG = Logger.getLogger(Schema28.class);

    public void execute(JdbcTemplate template) {

        if (template.queryForInt("select count(*) from version where version = 4") == 0) {
            LOG.info("Updating database schema to version 4.");
            template.execute("insert into version values (4)");
        }

        if (!tableExists(template, "user_settings")) {
            LOG.info("Database table 'user_settings' not found.  Creating it.");
            template.execute("create table user_settings (" +
                             "username varchar not null," +
                             "locale varchar," +
                             "theme_id varchar," +
                             "final_version_notification boolean default true not null," +
                             "beta_version_notification boolean default false not null," +
                             "main_caption_cutoff int default 35 not null," +
                             "main_track_number boolean default true not null," +
                             "main_artist boolean default true not null," +
                             "main_album boolean default false not null," +
                             "main_genre boolean default false not null," +
                             "main_year boolean default false not null," +
                             "main_bit_rate boolean default false not null," +
                             "main_duration boolean default true not null," +
                             "main_format boolean default false not null," +
                             "main_file_size boolean default false not null," +
                             "playlist_caption_cutoff int default 35 not null," +
                             "playlist_track_number boolean default false not null," +
                             "playlist_artist boolean default true not null," +
                             "playlist_album boolean default true not null," +
                             "playlist_genre boolean default false not null," +
                             "playlist_year boolean default true not null," +
                             "playlist_bit_rate boolean default false not null," +
                             "playlist_duration boolean default true not null," +
                             "playlist_format boolean default true not null," +
                             "playlist_file_size boolean default true not null," +
                             "primary key (username)," +
                             "foreign key (username) references user(username) on delete cascade)");
            LOG.info("Database table 'user_settings' was created successfully.");
        }

        if (!tableExists(template, "transcoding")) {
            LOG.info("Database table 'transcoding' not found.  Creating it.");
            template.execute("create table transcoding (" +
                             "id identity," +
                             "name varchar not null," +
                             "source_format varchar not null," +
                             "target_format varchar not null," +
                             "step1 varchar not null," +
                             "step2 varchar," +
                             "step3 varchar," +
                             "enabled boolean not null)");
            template.execute("insert into transcoding values(null,'wav > mp3','wav','mp3','lame -b %b -S %s -',null,null,false)");
            template.execute("insert into transcoding values(null,'ogg > mp3','ogg','mp3','oggdec %s -o','lame -b %b - -',null,false)");
            template.execute("insert into transcoding values(null,'wma > mp3','wma','mp3','wmadec %s','lame -b %b -x - -',null,false)");
            template.execute("insert into transcoding values(null,'flac > mp3','flac','mp3','flac -c -s -d %s','lame -b %b - -',null,false)");
            template.execute("insert into transcoding values(null,'ape > mp3','ape','mp3','mac %s - -d','lame -b %b - -',null,false)");
            template.execute("insert into transcoding values(null,'m4a > mp3','m4a','mp3','faad -w %s','lame -b %b -x - -',null,false)");
            template.execute("insert into transcoding values(null,'mpc > mp3','mpc','mp3','mppdec --wav --silent %s -','lame -b %b - -',null,false)");
            template.execute("insert into transcoding values(null,'ofr > mp3','ofr','mp3','ofr --decode --silent %s --output -','lame -b %b - -',null,false)");
            template.execute("insert into transcoding values(null,'wv > mp3','wv','mp3','wvunpack -q %s -','lame -b %b - -',null,false)");
            template.execute("insert into transcoding values(null,'shn > mp3','shn','mp3','shorten -x %s -','lame -b %b - -',null,false)");
            LOG.info("Database table 'transcoding' was created successfully.");
        }

        if (!tableExists(template, "player_transcoding")) {
            LOG.info("Database table 'player_transcoding' not found.  Creating it.");
            template.execute("create table player_transcoding (" +
                             "player_id int not null," +
                             "transcoding_id int not null," +
                             "primary key (player_id, transcoding_id)," +
                             "foreign key (player_id) references player(id) on delete cascade," +
                             "foreign key (transcoding_id) references transcoding(id) on delete cascade)");
            LOG.info("Database table 'player_transcoding' was created successfully.");
        }
    }
}
