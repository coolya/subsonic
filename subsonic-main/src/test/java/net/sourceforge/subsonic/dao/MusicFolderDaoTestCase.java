package net.sourceforge.subsonic.dao;

import net.sourceforge.subsonic.domain.*;
import org.springframework.jdbc.core.*;

import java.io.*;

/**
 * Unit test of {@link MusicFolderDao}.
 * @author Sindre Mehus
 */
public class MusicFolderDaoTestCase extends DaoTestCaseBase {

    private MusicFolderDao musicFolderDao;

    protected void setUp() throws Exception {
        musicFolderDao = new MusicFolderDao();
        JdbcTemplate template = new DaoHelper().getJdbcTemplate();
        template.execute("delete from music_folder");
    }

    public void testCreateMusicFolder() {
        MusicFolder musicFolder = new MusicFolder(new File("path"), "name", true);
        musicFolderDao.createMusicFolder(musicFolder);

        MusicFolder newMusicFolder = musicFolderDao.getAllMusicFolders()[0];
        assertMusicFolderEquals(musicFolder, newMusicFolder);
    }

    public void testUpdateMusicFolder() {
        MusicFolder musicFolder = new MusicFolder(new File("path"), "name", true);
        musicFolderDao.createMusicFolder(musicFolder);
        musicFolder = musicFolderDao.getAllMusicFolders()[0];

        musicFolder.setPath(new File("newPath"));
        musicFolder.setName("newName");
        musicFolder.setEnabled(false);
        musicFolderDao.updateMusicFolder(musicFolder);

        MusicFolder newRadio = musicFolderDao.getAllMusicFolders()[0];
        assertMusicFolderEquals(musicFolder, newRadio);
    }

    public void testDeleteMusicFolder() {
        assertEquals("Wrong number of music folders.", 0, musicFolderDao.getAllMusicFolders().length);

        musicFolderDao.createMusicFolder(new MusicFolder(new File("path"), "name", true));
        assertEquals("Wrong number of music folders.", 1, musicFolderDao.getAllMusicFolders().length);

        musicFolderDao.createMusicFolder(new MusicFolder(new File("path"), "name", true));
        assertEquals("Wrong number of music folders.", 2, musicFolderDao.getAllMusicFolders().length);

        musicFolderDao.deleteMusicFolder(musicFolderDao.getAllMusicFolders()[0].getId());
        assertEquals("Wrong number of music folders.", 1, musicFolderDao.getAllMusicFolders().length);

        musicFolderDao.deleteMusicFolder(musicFolderDao.getAllMusicFolders()[0].getId());
        assertEquals("Wrong number of music folders.", 0, musicFolderDao.getAllMusicFolders().length);
    }

    private void assertMusicFolderEquals(MusicFolder expected, MusicFolder actual) {
        assertEquals("Wrong name.", expected.getName(), actual.getName());
        assertEquals("Wrong path.", expected.getPath(), actual.getPath());
        assertEquals("Wrong enabled state.", expected.isEnabled(), actual.isEnabled());
    }


}