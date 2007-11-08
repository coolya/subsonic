package net.sourceforge.subsonic.dao;

import net.sourceforge.subsonic.Logger;
import net.sourceforge.subsonic.domain.CoverArtScheme;
import net.sourceforge.subsonic.domain.Player;
import net.sourceforge.subsonic.domain.Playlist;
import net.sourceforge.subsonic.domain.TranscodeScheme;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Provides player-related database services.
 *
 * @author Sindre Mehus
 */
@SuppressWarnings({"unchecked"})
public class PlayerDao extends AbstractDao {

    private static final Logger LOG = Logger.getLogger(PlayerDao.class);
    private static final String COLUMNS = "id, name, type, username, ip_address, auto_control_enabled, " +
                                          "last_seen, cover_art_scheme, transcode_scheme, dynamic_ip, client_side_playlist";

    private PlayerRowMapper rowMapper = new PlayerRowMapper();
    private Map<String, Playlist> playlists = Collections.synchronizedMap(new HashMap<String, Playlist>());

    /**
     * Returns all players.
     *
     * @return Possibly empty array of all users.
     */
    public Player[] getAllPlayers() {
        String sql = "select " + COLUMNS + " from player";
        return (Player[]) getJdbcTemplate().query(sql, rowMapper).toArray(new Player[0]);
    }

    /**
     * Creates a new player.
     *
     * @param player The player to create.
     */
    public synchronized void createPlayer(Player player) {
        JdbcTemplate template = getJdbcTemplate();
        int id = template.queryForInt("select max(id) from player") + 1;
        player.setId(String.valueOf(id));
        String sql = "insert into player (" + COLUMNS + ") values (" + questionMarks(COLUMNS) + ")";
        template.update(sql, new Object[]{player.getId(), player.getName(), player.getType(), player.getUsername(),
                                          player.getIpAddress(), player.isAutoControlEnabled(),
                                          player.getLastSeen(), player.getCoverArtScheme().name(),
                                          player.getTranscodeScheme().name(), player.isDynamicIp(), player.isClientSidePlaylist()});
        addPlaylist(player);

        LOG.info("Created player " + id + '.');
    }

    /**
     * Deletes the player with the given ID.
     *
     * @param id The player ID.
     */
    public void deletePlayer(String id) {
        String sql = "delete from player where id=?";
        getJdbcTemplate().update(sql, new Object[]{id});
        playlists.remove(id);
    }

    /**
     * Updates the given player.
     *
     * @param player The player to update.
     */
    public void updatePlayer(Player player) {
        String sql = "update player set " +
                     "name = ?," +
                     "type = ?," +
                     "username = ?," +
                     "ip_address = ?," +
                     "auto_control_enabled = ?," +
                     "last_seen = ?," +
                     "cover_art_scheme = ?," +
                     "transcode_scheme = ?, " +
                     "dynamic_ip = ?, " +
                     "client_side_playlist = ? " +
                     "where id = ?";
        getJdbcTemplate().update(sql, new Object[]{player.getName(), player.getType(), player.getUsername(),
                                                   player.getIpAddress(), player.isAutoControlEnabled(),
                                                   player.getLastSeen(), player.getCoverArtScheme().name(),
                                                   player.getTranscodeScheme().name(), player.isDynamicIp(),
                                                   player.isClientSidePlaylist(), player.getId()});
    }

    private void addPlaylist(Player player) {
        Playlist playlist = playlists.get(player.getId());
        if (playlist == null) {
            playlist = new Playlist();
            playlists.put(player.getId(), playlist);
        }
        player.setPlaylist(playlist);
    }

    private class PlayerRowMapper implements ParameterizedRowMapper<Player> {
        public Player mapRow(ResultSet rs, int rowNum) throws SQLException {
            Player player = new Player();
            int col = 1;
            player.setId(rs.getString(col++));
            player.setName(rs.getString(col++));
            player.setType(rs.getString(col++));
            player.setUsername(rs.getString(col++));
            player.setIpAddress(rs.getString(col++));
            player.setAutoControlEnabled(rs.getBoolean(col++));
            player.setLastSeen(rs.getTimestamp(col++));
            player.setCoverArtScheme(CoverArtScheme.valueOf(rs.getString(col++)));
            player.setTranscodeScheme(TranscodeScheme.valueOf(rs.getString(col++)));
            player.setDynamicIp(rs.getBoolean(col++));
            player.setClientSidePlaylist(rs.getBoolean(col++));

            addPlaylist(player);
            return player;
        }
    }
}
