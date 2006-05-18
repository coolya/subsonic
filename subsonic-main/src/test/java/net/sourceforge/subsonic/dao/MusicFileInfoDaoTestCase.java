package net.sourceforge.subsonic.dao;

/**
 * Unit test of {@link net.sourceforge.subsonic.dao.MusicFileInfoDao}.
 * @author Sindre Mehus
 * @version $Revision: 1.2 $ $Date: 2006/02/27 21:39:39 $
 */

import net.sourceforge.subsonic.domain.*;
import org.springframework.jdbc.core.*;

import java.util.*;

public class MusicFileInfoDaoTestCase extends DaoTestCaseBase {

    private MusicFileInfoDao musicFileInfoDao;

    protected void setUp() throws Exception {
        musicFileInfoDao = new MusicFileInfoDao();
        JdbcTemplate template = musicFileInfoDao.getJdbcTemplate();
        template.execute("delete from music_file_info");
    }

    public void testCreateMusicFileInfo() {
        MusicFileInfo info = new MusicFileInfo(null, "path", 0, "comment", 123, new Date());
        musicFileInfoDao.createMusicFileInfo(info);

        MusicFileInfo newInfo = musicFileInfoDao.getMusicFileInfoForPath("path");
        assertMusicFileInfoEquals(info, newInfo);
    }

    public void testCaseInsensitivity() {
        MusicFileInfo info = new MusicFileInfo(null, "aBcDeFgH", 0, "comment", 123, new Date());
        musicFileInfoDao.createMusicFileInfo(info);

        MusicFileInfo newInfo = musicFileInfoDao.getMusicFileInfoForPath("AbcdefGH");
        assertMusicFileInfoEquals(info, newInfo);
    }

    public void testUpdateMusicFileInfo() {
        MusicFileInfo info = new MusicFileInfo(null, "path", 0, "comment", 123, new Date());
        musicFileInfoDao.createMusicFileInfo(info);
        info = musicFileInfoDao.getMusicFileInfoForPath("path");

        info.setPath("newPath");
        info.setPlayCount(5);
        info.setRating(3);
        info.setLastPlayed(new Date());
        info.setComment("newComment");
        musicFileInfoDao.updateMusicFileInfo(info);

        MusicFileInfo newInfo = musicFileInfoDao.getMusicFileInfoForPath("newPath");
        assertMusicFileInfoEquals(info, newInfo);
    }

    public void testGetHighestRated() {
        musicFileInfoDao.createMusicFileInfo(new MusicFileInfo(null, "f", 5, null, 0, null));
        musicFileInfoDao.createMusicFileInfo(new MusicFileInfo(null, "b", 4, null, 0, null));
        musicFileInfoDao.createMusicFileInfo(new MusicFileInfo(null, "d", 3, null, 0, null));
        musicFileInfoDao.createMusicFileInfo(new MusicFileInfo(null, "a", 2, null, 0, null));
        musicFileInfoDao.createMusicFileInfo(new MusicFileInfo(null, "e", 1, null, 0, null));
        musicFileInfoDao.createMusicFileInfo(new MusicFileInfo(null, "c", 0, null, 0, null)); // Not inclued in query.

        MusicFileInfo[] highestRated = musicFileInfoDao.getHighestRated(0, 0);
        assertEquals("Error in getHighestRated().", 0, highestRated.length);

        highestRated = musicFileInfoDao.getHighestRated(0, 1);
        assertEquals("Error in getHighestRated().", 1, highestRated.length);
        assertEquals("Error in getHighestRated().", "f", highestRated[0].getPath());

        highestRated = musicFileInfoDao.getHighestRated(0, 2);
        assertEquals("Error in getHighestRated().", 2, highestRated.length);
        assertEquals("Error in getHighestRated().", "f", highestRated[0].getPath());
        assertEquals("Error in getHighestRated().", "b", highestRated[1].getPath());

        highestRated = musicFileInfoDao.getHighestRated(0, 5);
        assertEquals("Error in getHighestRated().", 5, highestRated.length);
        assertEquals("Error in getHighestRated().", "f", highestRated[0].getPath());
        assertEquals("Error in getHighestRated().", "b", highestRated[1].getPath());
        assertEquals("Error in getHighestRated().", "d", highestRated[2].getPath());
        assertEquals("Error in getHighestRated().", "a", highestRated[3].getPath());
        assertEquals("Error in getHighestRated().", "e", highestRated[4].getPath());

        highestRated = musicFileInfoDao.getHighestRated(0, 6);
        assertEquals("Error in getHighestRated().", 5, highestRated.length);

        highestRated = musicFileInfoDao.getHighestRated(1, 0);
        assertEquals("Error in getHighestRated().", 0, highestRated.length);

        highestRated = musicFileInfoDao.getHighestRated(1, 1);
        assertEquals("Error in getHighestRated().", 1, highestRated.length);
        assertEquals("Error in getHighestRated().", "b", highestRated[0].getPath());

        highestRated = musicFileInfoDao.getHighestRated(3, 2);
        assertEquals("Error in getHighestRated().", 2, highestRated.length);
        assertEquals("Error in getHighestRated().", "a", highestRated[0].getPath());
        assertEquals("Error in getHighestRated().", "e", highestRated[1].getPath());

        highestRated = musicFileInfoDao.getHighestRated(4, 10);
        assertEquals("Error in getHighestRated().", 1, highestRated.length);
        assertEquals("Error in getHighestRated().", "e", highestRated[0].getPath());

        highestRated = musicFileInfoDao.getHighestRated(5, 10);
        assertEquals("Error in getHighestRated().", 0, highestRated.length);

        highestRated = musicFileInfoDao.getHighestRated(6, 10);
        assertEquals("Error in getHighestRated().", 0, highestRated.length);
    }

    public void testGetMostFrequentlyPlayed() {
        musicFileInfoDao.createMusicFileInfo(new MusicFileInfo(null, "f", 0, null, 5, null));
        musicFileInfoDao.createMusicFileInfo(new MusicFileInfo(null, "b", 0, null, 4, null));
        musicFileInfoDao.createMusicFileInfo(new MusicFileInfo(null, "d", 0, null, 3, null));
        musicFileInfoDao.createMusicFileInfo(new MusicFileInfo(null, "a", 0, null, 2, null));
        musicFileInfoDao.createMusicFileInfo(new MusicFileInfo(null, "e", 0, null, 1, null));
        musicFileInfoDao.createMusicFileInfo(new MusicFileInfo(null, "c", 0, null, 0, null));  // Not included in query.

        MusicFileInfo[] mostFrequent = musicFileInfoDao.getMostFrequentlyPlayed(0, 0);
        assertEquals("Error in getMostFrequentlyPlayed().", 0, mostFrequent.length);

        mostFrequent = musicFileInfoDao.getMostFrequentlyPlayed(0, 1);
        assertEquals("Error in getMostFrequentlyPlayed().", 1, mostFrequent.length);
        assertEquals("Error in getMostFrequentlyPlayed().", "f", mostFrequent[0].getPath());

        mostFrequent = musicFileInfoDao.getMostFrequentlyPlayed(0, 2);
        assertEquals("Error in getMostFrequentlyPlayed().", 2, mostFrequent.length);
        assertEquals("Error in getMostFrequentlyPlayed().", "f", mostFrequent[0].getPath());
        assertEquals("Error in getMostFrequentlyPlayed().", "b", mostFrequent[1].getPath());

        mostFrequent = musicFileInfoDao.getMostFrequentlyPlayed(0, 5);
        assertEquals("Error in getMostFrequentlyPlayed().", 5, mostFrequent.length);
        assertEquals("Error in getMostFrequentlyPlayed().", "f", mostFrequent[0].getPath());
        assertEquals("Error in getMostFrequentlyPlayed().", "b", mostFrequent[1].getPath());
        assertEquals("Error in getMostFrequentlyPlayed().", "d", mostFrequent[2].getPath());
        assertEquals("Error in getMostFrequentlyPlayed().", "a", mostFrequent[3].getPath());
        assertEquals("Error in getMostFrequentlyPlayed().", "e", mostFrequent[4].getPath());

        mostFrequent = musicFileInfoDao.getMostFrequentlyPlayed(0, 6);
        assertEquals("Error in getMostFrequentlyPlayed().", 5, mostFrequent.length);

        mostFrequent = musicFileInfoDao.getMostFrequentlyPlayed(1, 0);
        assertEquals("Error in getMostFrequentlyPlayed().", 0, mostFrequent.length);

        mostFrequent = musicFileInfoDao.getMostFrequentlyPlayed(1, 1);
        assertEquals("Error in getMostFrequentlyPlayed().", 1, mostFrequent.length);
        assertEquals("Error in getMostFrequentlyPlayed().", "b", mostFrequent[0].getPath());

        mostFrequent = musicFileInfoDao.getMostFrequentlyPlayed(3, 2);
        assertEquals("Error in getMostFrequentlyPlayed().", 2, mostFrequent.length);
        assertEquals("Error in getMostFrequentlyPlayed().", "a", mostFrequent[0].getPath());
        assertEquals("Error in getMostFrequentlyPlayed().", "e", mostFrequent[1].getPath());

        mostFrequent = musicFileInfoDao.getMostFrequentlyPlayed(4, 10);
        assertEquals("Error in getMostFrequentlyPlayed().", 1, mostFrequent.length);
        assertEquals("Error in getMostFrequentlyPlayed().", "e", mostFrequent[0].getPath());

        mostFrequent = musicFileInfoDao.getMostFrequentlyPlayed(5, 10);
        assertEquals("Error in getMostFrequentlyPlayed().", 0, mostFrequent.length);

        mostFrequent = musicFileInfoDao.getMostFrequentlyPlayed(6, 10);
        assertEquals("Error in getMostFrequentlyPlayed().", 0, mostFrequent.length);
    }

    public void testGetMostRecentlyPlayed() {
        musicFileInfoDao.createMusicFileInfo(new MusicFileInfo(null, "f", 0, null, 0, new Date(5)));
        musicFileInfoDao.createMusicFileInfo(new MusicFileInfo(null, "b", 0, null, 0, new Date(4)));
        musicFileInfoDao.createMusicFileInfo(new MusicFileInfo(null, "d", 0, null, 0, new Date(3)));
        musicFileInfoDao.createMusicFileInfo(new MusicFileInfo(null, "a", 0, null, 0, new Date(2)));
        musicFileInfoDao.createMusicFileInfo(new MusicFileInfo(null, "e", 0, null, 0, new Date(1)));
        musicFileInfoDao.createMusicFileInfo(new MusicFileInfo(null, "c", 0, null, 0, null));  // Not included in query.

        MusicFileInfo[] mostRecent = musicFileInfoDao.getMostRecentlyPlayed(0, 0);
        assertEquals("Error in getMostRecentlyPlayed().", 0, mostRecent.length);

        mostRecent = musicFileInfoDao.getMostRecentlyPlayed(0, 1);
        assertEquals("Error in getMostRecentlyPlayed().", 1, mostRecent.length);
        assertEquals("Error in getMostRecentlyPlayed().", "f", mostRecent[0].getPath());

        mostRecent = musicFileInfoDao.getMostRecentlyPlayed(0, 2);
        assertEquals("Error in getMostRecentlyPlayed().", 2, mostRecent.length);
        assertEquals("Error in getMostRecentlyPlayed().", "f", mostRecent[0].getPath());
        assertEquals("Error in getMostRecentlyPlayed().", "b", mostRecent[1].getPath());

        mostRecent = musicFileInfoDao.getMostRecentlyPlayed(0, 5);
        assertEquals("Error in getMostRecentlyPlayed().", 5, mostRecent.length);
        assertEquals("Error in getMostRecentlyPlayed().", "f", mostRecent[0].getPath());
        assertEquals("Error in getMostRecentlyPlayed().", "b", mostRecent[1].getPath());
        assertEquals("Error in getMostRecentlyPlayed().", "d", mostRecent[2].getPath());
        assertEquals("Error in getMostRecentlyPlayed().", "a", mostRecent[3].getPath());
        assertEquals("Error in getMostRecentlyPlayed().", "e", mostRecent[4].getPath());

        mostRecent = musicFileInfoDao.getMostRecentlyPlayed(0, 6);
        assertEquals("Error in getMostRecentlyPlayed().", 5, mostRecent.length);

        mostRecent = musicFileInfoDao.getMostRecentlyPlayed(1, 0);
        assertEquals("Error in getMostRecentlyPlayed().", 0, mostRecent.length);

        mostRecent = musicFileInfoDao.getMostRecentlyPlayed(1, 1);
        assertEquals("Error in getMostRecentlyPlayed().", 1, mostRecent.length);
        assertEquals("Error in getMostRecentlyPlayed().", "b", mostRecent[0].getPath());

        mostRecent = musicFileInfoDao.getMostRecentlyPlayed(3, 2);
        assertEquals("Error in getMostRecentlyPlayed().", 2, mostRecent.length);
        assertEquals("Error in getMostRecentlyPlayed().", "a", mostRecent[0].getPath());
        assertEquals("Error in getMostRecentlyPlayed().", "e", mostRecent[1].getPath());

        mostRecent = musicFileInfoDao.getMostRecentlyPlayed(4, 10);
        assertEquals("Error in getMostRecentlyPlayed().", 1, mostRecent.length);
        assertEquals("Error in getMostRecentlyPlayed().", "e", mostRecent[0].getPath());

        mostRecent = musicFileInfoDao.getMostRecentlyPlayed(5, 10);
        assertEquals("Error in getMostRecentlyPlayed().", 0, mostRecent.length);

        mostRecent = musicFileInfoDao.getMostRecentlyPlayed(6, 10);
        assertEquals("Error in getMostRecentlyPlayed().", 0, mostRecent.length);
    }


    private void assertMusicFileInfoEquals(MusicFileInfo expected, MusicFileInfo actual) {
        assertEquals("Wrong path.", expected.getPath(), actual.getPath());
        assertEquals("Wrong comment.", expected.getComment(), actual.getComment());
        assertEquals("Wrong last played date.", expected.getLastPlayed(), actual.getLastPlayed());
        assertEquals("Wrong play count.", expected.getPlayCount(), actual.getPlayCount());
        assertEquals("Wrong rating.", expected.getRating(), actual.getRating());
    }


}