package net.sourceforge.subsonic.domain;

/**
 * Unit test of {@link Playlist}.
 *
 * @author Sindre Mehus
 * @version $Revision: 1.4 $ $Date: 2005/11/14 21:31:25 $
 */

import junit.framework.*;
import net.sourceforge.subsonic.domain.Playlist.*;

public class PlaylistTestCase extends TestCase {

    public void testEmpty() {
        Playlist playlist = new Playlist();
        assertEquals(0, playlist.size());
        assertTrue(playlist.isEmpty());
        assertEquals(0, playlist.getFiles().length);
        assertNull(playlist.getCurrentFile());
    }

    public void testStatus() throws Exception {
        Playlist playlist = new Playlist();
        assertEquals(Status.PLAYING, playlist.getStatus());

        playlist.setStatus(Status.STOPPED);
        assertEquals(Status.STOPPED, playlist.getStatus());

        playlist.addFile(new TestMusicFile());
        assertEquals(Status.PLAYING, playlist.getStatus());

        playlist.clear();
        assertEquals(Status.PLAYING, playlist.getStatus());
    }

    public void testMoveUp() throws Exception {
        Playlist playlist = createPlaylist(0, "A", "B", "C", "D");
        playlist.moveUp(0);
        assertPlaylistEquals(playlist, 0, "A", "B", "C", "D");

        playlist = createPlaylist(0, "A", "B", "C", "D");
        playlist.moveUp(9999);
        assertPlaylistEquals(playlist, 0, "A", "B", "C", "D");

        playlist = createPlaylist(1, "A", "B", "C", "D");
        playlist.moveUp(1);
        assertPlaylistEquals(playlist, 0, "B", "A", "C", "D");

        playlist = createPlaylist(3, "A", "B", "C", "D");
        playlist.moveUp(3);
        assertPlaylistEquals(playlist, 2, "A", "B", "D", "C");
    }

    public void testMoveDown() throws Exception {
        Playlist playlist = createPlaylist(0, "A", "B", "C", "D");
        playlist.moveDown(0);
        assertPlaylistEquals(playlist, 1, "B", "A", "C", "D");

        playlist = createPlaylist(0, "A", "B", "C", "D");
        playlist.moveDown(9999);
        assertPlaylistEquals(playlist, 0, "A", "B", "C", "D");

        playlist = createPlaylist(1, "A", "B", "C", "D");
        playlist.moveDown(1);
        assertPlaylistEquals(playlist, 2, "A", "C", "B", "D");

        playlist = createPlaylist(3, "A", "B", "C", "D");
        playlist.moveDown(3);
        assertPlaylistEquals(playlist, 3, "A", "B", "C", "D");
    }

    public void testRemove() throws Exception {
        Playlist playlist = createPlaylist(0, "A", "B", "C", "D");
        playlist.removeFileAt(0);
        assertPlaylistEquals(playlist, 0, "B", "C", "D");

        playlist = createPlaylist(1, "A", "B", "C", "D");
        playlist.removeFileAt(0);
        assertPlaylistEquals(playlist, 0, "B", "C", "D");

        playlist = createPlaylist(0, "A", "B", "C", "D");
        playlist.removeFileAt(3);
        assertPlaylistEquals(playlist, 0, "A", "B", "C");

        playlist = createPlaylist(1, "A", "B", "C", "D");
        playlist.removeFileAt(1);
        assertPlaylistEquals(playlist, 1, "A", "C", "D");

        playlist = createPlaylist(3, "A", "B", "C", "D");
        playlist.removeFileAt(3);
        assertPlaylistEquals(playlist, 2, "A", "B", "C");

        playlist = createPlaylist(0, "A");
        playlist.removeFileAt(0);
        assertPlaylistEquals(playlist, -1);
    }

    public void testNext() throws Exception {
        Playlist playlist = createPlaylist(0, "A", "B", "C");
        assertFalse(playlist.isRepeatEnabled());
        playlist.next();
        assertPlaylistEquals(playlist, 1, "A", "B", "C");
        playlist.next();
        assertPlaylistEquals(playlist, 2, "A", "B", "C");
        playlist.next();
        assertPlaylistEquals(playlist, -1, "A", "B", "C");

        playlist = createPlaylist(0, "A", "B", "C");
        playlist.setRepeatEnabled(true);
        assertTrue(playlist.isRepeatEnabled());
        playlist.next();
        assertPlaylistEquals(playlist, 1, "A", "B", "C");
        playlist.next();
        assertPlaylistEquals(playlist, 2, "A", "B", "C");
        playlist.next();
        assertPlaylistEquals(playlist, 0, "A", "B", "C");
    }

    public void testPlayAfterEndReached() throws Exception {
        Playlist playlist = createPlaylist(2, "A", "B", "C");
        playlist.setStatus(Status.PLAYING);
        playlist.next();
        assertNull(playlist.getCurrentFile());
        assertEquals(Status.STOPPED, playlist.getStatus());

        playlist.setStatus(Status.PLAYING);
        assertEquals(Status.PLAYING, playlist.getStatus());
        assertEquals(0, playlist.getIndex());
        assertEquals("A", playlist.getCurrentFile().getName());
    }

    public void testAppend() throws Exception {
        Playlist playlist = createPlaylist(1, "A", "B", "C");

        playlist.addFile(new TestMusicFile("D"), true);
        assertPlaylistEquals(playlist, 1, "A", "B", "C", "D");

        playlist.addFile(new TestMusicFile("E"), false);
        assertPlaylistEquals(playlist, 0, "E");
    }

    public void testUndo() throws Exception {
        Playlist playlist = createPlaylist(0, "A", "B", "C");
        playlist.setIndex(2);
        playlist.undo();
        assertPlaylistEquals(playlist, 0, "A", "B", "C");

        playlist.removeFileAt(2);
        playlist.undo();
        assertPlaylistEquals(playlist, 0, "A", "B", "C");

        playlist.clear();
        playlist.undo();
        assertPlaylistEquals(playlist, 0, "A", "B", "C");

        playlist.addFile(new TestMusicFile());
        playlist.undo();
        assertPlaylistEquals(playlist, 0, "A", "B", "C");

        playlist.moveDown(1);
        playlist.undo();
        assertPlaylistEquals(playlist, 0, "A", "B", "C");

        playlist.moveUp(1);
        playlist.undo();
        assertPlaylistEquals(playlist, 0, "A", "B", "C");
    }

    private void assertPlaylistEquals(Playlist playlist, int index, String... songs) {
        assertEquals(songs.length, playlist.size());
        for (int i = 0; i < songs.length; i++) {
            assertEquals(songs[i], playlist.getFiles()[i].getName());
        }

        if (index == -1) {
            assertNull(playlist.getCurrentFile());
        } else {
            assertEquals(songs[index], playlist.getCurrentFile().getName());
        }
    }

    private Playlist createPlaylist(int index, String... songs) throws Exception {
        Playlist playlist = new Playlist();
        for (String song : songs) {
            playlist.addFile(new TestMusicFile(song));
        }
        playlist.setIndex(index);
        return playlist;
    }

    private static class TestMusicFile extends MusicFile {

        private String name;

        public TestMusicFile() {}

        public TestMusicFile(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

        public MusicFile[] getChildren(boolean recurse) {
            return new MusicFile[] {this};
        }

        public boolean exists() {
            return true;
        }
    }
}