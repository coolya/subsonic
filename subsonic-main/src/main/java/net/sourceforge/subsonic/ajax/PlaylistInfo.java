package net.sourceforge.subsonic.ajax;

import java.util.List;

/**
 * The playlist of a player.
 *
 * @author Sindre Mehus
 */
public class PlaylistInfo {

    private final List<Entry> entries;

    public PlaylistInfo(List<Entry> entries) {
        this.entries = entries;
    }

    public List<Entry> getEntries() {
        return entries;
    }

    public static class Entry {
        private final String artist;
        private final String album;
        private final String title;
        private final String albumUrl;

        public Entry(String artist, String album, String title, String albumUrl) {
            this.artist = artist;
            this.album = album;
            this.title = title;
            this.albumUrl = albumUrl;
        }

        public String getArtist() {
            return artist;
        }

        public String getAlbum() {
            return album;
        }

        public String getTitle() {
            return title;
        }

        public String getAlbumUrl() {
            return albumUrl;
        }
    }

}