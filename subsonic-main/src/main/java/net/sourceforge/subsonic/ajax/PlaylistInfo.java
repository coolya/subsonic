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
        private final Integer trackNumber;
        private final String title;
        private final String artist;
        private final String album;
        private final String genre;
        private final String year;
        private final String bitRate;
        private final String duration;
        private final String format;
        private final String fileSize;
        private final String albumUrl;

        public Entry(Integer trackNumber, String title, String artist, String album, String genre, String year,
                String bitRate, String duration, String format, String fileSize, String albumUrl) {
            this.trackNumber = trackNumber;
            this.title = title;
            this.artist = artist;
            this.album = album;
            this.genre = genre;
            this.year = year;
            this.bitRate = bitRate;
            this.duration = duration;
            this.format = format;
            this.fileSize = fileSize;
            this.albumUrl = albumUrl;
        }

        public Integer getTrackNumber() {
            return trackNumber;
        }

        public String getTitle() {
            return title;
        }

        public String getArtist() {
            return artist;
        }

        public String getAlbum() {
            return album;
        }

        public String getGenre() {
            return genre;
        }

        public String getYear() {
            return year;
        }

        public String getBitRate() {
            return bitRate;
        }

        public String getDuration() {
            return duration;
        }

        public String getFormat() {
            return format;
        }

        public String getFileSize() {
            return fileSize;
        }

        public String getAlbumUrl() {
            return albumUrl;
        }
    }

}