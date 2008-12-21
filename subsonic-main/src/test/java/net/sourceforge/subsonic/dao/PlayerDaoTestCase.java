package net.sourceforge.subsonic.dao;

import net.sourceforge.subsonic.domain.CoverArtScheme;
import net.sourceforge.subsonic.domain.Player;
import net.sourceforge.subsonic.domain.Playlist;
import net.sourceforge.subsonic.domain.TranscodeScheme;
import net.sourceforge.subsonic.domain.PlayerTechnology;

import java.util.Date;

/**
 * Unit test of {@link PlayerDao}.
 *
 * @author Sindre Mehus
 */
public class PlayerDaoTestCase extends DaoTestCaseBase {

    protected void setUp() throws Exception {
        getJdbcTemplate().execute("delete from player");
    }

    public void testCreatePlayer() {
        Player player = new Player();
        player.setName("name");
        player.setType("type");
        player.setUsername("username");
        player.setIpAddress("ipaddress");
        player.setDynamicIp(false);
        player.setAutoControlEnabled(false);
        player.setClientSidePlaylist(true);
        player.setTechnology(PlayerTechnology.EXTERNAL_WITH_PLAYLIST);
        player.setLastSeen(new Date());
        player.setCoverArtScheme(CoverArtScheme.LARGE);
        player.setTranscodeScheme(TranscodeScheme.MAX_160);

        playerDao.createPlayer(player);
        Player newPlayer = playerDao.getAllPlayers()[0];
        assertPlayerEquals(player, newPlayer);
    }

    public void testDefaultValues() {
        playerDao.createPlayer(new Player());
        Player player = playerDao.getAllPlayers()[0];

        assertTrue("Player should have dynamic IP by default.", player.isDynamicIp());
        assertTrue("Player should be auto-controlled by default.", player.isAutoControlEnabled());
        assertFalse("Player should have server-side playlist by default.", player.isClientSidePlaylist());
    }

    public void testIdentity() {
        Player player = new Player();

        playerDao.createPlayer(player);
        assertEquals("Wrong ID", "1", player.getId());
        assertEquals("Wrong number of players.", 1, playerDao.getAllPlayers().length);

        playerDao.createPlayer(player);
        assertEquals("Wrong ID", "2", player.getId());
        assertEquals("Wrong number of players.", 2, playerDao.getAllPlayers().length);

        playerDao.createPlayer(player);
        assertEquals("Wrong ID", "3", player.getId());
        assertEquals("Wrong number of players.", 3, playerDao.getAllPlayers().length);

        playerDao.deletePlayer("3");
        playerDao.createPlayer(player);
        assertEquals("Wrong ID", "3", player.getId());
        assertEquals("Wrong number of players.", 3, playerDao.getAllPlayers().length);

        playerDao.deletePlayer("2");
        playerDao.createPlayer(player);
        assertEquals("Wrong ID", "4", player.getId());
        assertEquals("Wrong number of players.", 3, playerDao.getAllPlayers().length);
    }

    public void testPlaylist() {
        Player player = new Player();
        playerDao.createPlayer(player);
        Playlist playlist = player.getPlaylist();
        assertNotNull("Missing playlist.", playlist);

        playerDao.deletePlayer(player.getId());
        playerDao.createPlayer(player);
        assertNotSame("Wrong playlist.", playlist, player.getPlaylist());
    }

    public void testUpdatePlayer() {
        Player player = new Player();
        playerDao.createPlayer(player);
        assertPlayerEquals(player, playerDao.getAllPlayers()[0]);

        player.setName("name");
        player.setType("Winamp");
        player.setTechnology(PlayerTechnology.WEB);
        player.setUsername("username");
        player.setIpAddress("ipaddress");
        player.setDynamicIp(true);
        player.setAutoControlEnabled(false);
        player.setClientSidePlaylist(true);
        player.setLastSeen(new Date());
        player.setCoverArtScheme(CoverArtScheme.LARGE);
        player.setTranscodeScheme(TranscodeScheme.MAX_160);

        playerDao.updatePlayer(player);
        Player newPlayer = playerDao.getAllPlayers()[0];
        assertPlayerEquals(player, newPlayer);
    }

    public void testDeletePlayer() {
        assertEquals("Wrong number of players.", 0, playerDao.getAllPlayers().length);

        playerDao.createPlayer(new Player());
        assertEquals("Wrong number of players.", 1, playerDao.getAllPlayers().length);

        playerDao.createPlayer(new Player());
        assertEquals("Wrong number of players.", 2, playerDao.getAllPlayers().length);

        playerDao.deletePlayer("1");
        assertEquals("Wrong number of players.", 1, playerDao.getAllPlayers().length);

        playerDao.deletePlayer("2");
        assertEquals("Wrong number of players.", 0, playerDao.getAllPlayers().length);
    }

    private void assertPlayerEquals(Player expected, Player actual) {
        assertEquals("Wrong ID.", expected.getId(), actual.getId());
        assertEquals("Wrong name.", expected.getName(), actual.getName());
        assertEquals("Wrong technology.", expected.getTechnology(), actual.getTechnology());
        assertEquals("Wrong type.", expected.getType(), actual.getType());
        assertEquals("Wrong username.", expected.getUsername(), actual.getUsername());
        assertEquals("Wrong IP address.", expected.getIpAddress(), actual.getIpAddress());
        assertEquals("Wrong dynamic IP.", expected.isDynamicIp(), actual.isDynamicIp());
        assertEquals("Wrong auto control enabled.", expected.isAutoControlEnabled(), actual.isAutoControlEnabled());
        assertEquals("Wrong client-side playlist.", expected.isClientSidePlaylist(), actual.isClientSidePlaylist());
        assertEquals("Wrong last seen.", expected.getLastSeen(), actual.getLastSeen());
        assertEquals("Wrong cover art scheme.", expected.getCoverArtScheme(), actual.getCoverArtScheme());
        assertEquals("Wrong transcode scheme.", expected.getTranscodeScheme(), actual.getTranscodeScheme());
    }
}