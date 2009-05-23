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
package net.sourceforge.subsonic.domain;

import junit.framework.*;
import net.sourceforge.subsonic.domain.Playlist.*;

import java.io.IOException;

/**
 * Unit test of {@link Playlist}.
 *
 * @author Sindre Mehus
 */
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

        playlist.addFiles(true, new TestMusicFile());
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

        playlist.addFiles(true, new TestMusicFile("D"));
        assertPlaylistEquals(playlist, 1, "A", "B", "C", "D");

        playlist.addFiles(false, new TestMusicFile("E"));
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

        playlist.addFiles(true, new TestMusicFile());
        playlist.undo();
        assertPlaylistEquals(playlist, 0, "A", "B", "C");

        playlist.moveDown(1);
        playlist.undo();
        assertPlaylistEquals(playlist, 0, "A", "B", "C");

        playlist.moveUp(1);
        playlist.undo();
        assertPlaylistEquals(playlist, 0, "A", "B", "C");
    }

    public void testOrder() throws IOException {
        Playlist playlist = new Playlist();
        playlist.addFiles(true, new TestMusicFile(2, "Artist A", "Album B"));
        playlist.addFiles(true, new TestMusicFile(1, "Artist C", "Album C"));
        playlist.addFiles(true, new TestMusicFile(3, "Artist B", "Album A"));
        playlist.addFiles(true, new TestMusicFile(null, "Artist D", "Album D"));
        playlist.setIndex(2);
        assertEquals("Error in sort.", new Integer(3), playlist.getCurrentFile().getMetaData().getTrackNumber());

        // Order by track.
        playlist.sort(SortOrder.TRACK);
        assertEquals("Error in sort().", null, playlist.getFile(0).getMetaData().getTrackNumber());
        assertEquals("Error in sort().", new Integer(1), playlist.getFile(1).getMetaData().getTrackNumber());
        assertEquals("Error in sort().", new Integer(2), playlist.getFile(2).getMetaData().getTrackNumber());
        assertEquals("Error in sort().", new Integer(3), playlist.getFile(3).getMetaData().getTrackNumber());
        assertEquals("Error in sort().", new Integer(3), playlist.getCurrentFile().getMetaData().getTrackNumber());

        // Order by artist.
        playlist.sort(SortOrder.ARTIST);
        assertEquals("Error in sort().", "Artist A", playlist.getFile(0).getMetaData().getArtist());
        assertEquals("Error in sort().", "Artist B", playlist.getFile(1).getMetaData().getArtist());
        assertEquals("Error in sort().", "Artist C", playlist.getFile(2).getMetaData().getArtist());
        assertEquals("Error in sort().", "Artist D", playlist.getFile(3).getMetaData().getArtist());
        assertEquals("Error in sort().", new Integer(3), playlist.getCurrentFile().getMetaData().getTrackNumber());

        // Order by album.
        playlist.sort(SortOrder.ALBUM);
        assertEquals("Error in sort().", "Album A", playlist.getFile(0).getMetaData().getAlbum());
        assertEquals("Error in sort().", "Album B", playlist.getFile(1).getMetaData().getAlbum());
        assertEquals("Error in sort().", "Album C", playlist.getFile(2).getMetaData().getAlbum());
        assertEquals("Error in sort().", "Album D", playlist.getFile(3).getMetaData().getAlbum());
        assertEquals("Error in sort().", new Integer(3), playlist.getCurrentFile().getMetaData().getTrackNumber());
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
            playlist.addFiles(true, new TestMusicFile(song));
        }
        playlist.setIndex(index);
        return playlist;
    }

    private static class TestMusicFile extends MusicFile {

        private String name;
        private MetaData metaData;

        TestMusicFile() {}

        TestMusicFile(String name) {
            this.name = name;
        }

        TestMusicFile(Integer track, String artist, String album) {
            metaData = new MetaData();
            if (track != null) {
                metaData.setTrackNumber(track);
            }
            metaData.setArtist(artist);
            metaData.setAlbum(album);
        }

        @Override
        public String getName() {
            return name;
        }

        @Override
        public boolean exists() {
            return true;
        }

        @Override
        public boolean isFile() {
            return true;
        }

        @Override
        public boolean isDirectory() {
            return false;
        }

        @Override
        public MetaData getMetaData() {
            return metaData;
        }

        @Override
        public boolean equals(Object o) {
            return this == o;
        }
    }
}