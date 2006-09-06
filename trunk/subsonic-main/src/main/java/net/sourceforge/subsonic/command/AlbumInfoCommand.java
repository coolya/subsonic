package net.sourceforge.subsonic.command;

import net.sourceforge.subsonic.controller.*;

import java.util.*;

/**
 * Command used in {@link AlbumInfoController}.
 *
 * @author Sindre Mehus
 */
public class AlbumInfoCommand {
    private String path;
    private String artist;
    private String album;
    private List<Match> matches;

    public AlbumInfoCommand(String path, String artist, String album) {
        this.path = path;
        this.artist = artist;
        this.album = album;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getAlbum() {
        return album;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    public List<Match> getMatches() {
        return matches;
    }

    public void setMatches(List<Match> matches) {
        this.matches = matches;
    }

    public static class Match {
        private String artists;
        private String album;
        private String label;
        private String reviews;
        private String released;
        private String imageUrl;
        private String detailPageUrl;

        public String getArtists() {
            return artists;
        }

        public void setArtists(String artists) {
            this.artists = artists;
        }

        public String getAlbum() {
            return album;
        }

        public void setAlbum(String album) {
            this.album = album;
        }

        public String getLabel() {
            return label;
        }

        public void setLabel(String label) {
            this.label = label;
        }

        public String getReviews() {
            return reviews;
        }

        public void setReviews(String reviews) {
            this.reviews = reviews;
        }

        public String getReleased() {
            return released;
        }

        public void setReleased(String released) {
            this.released = released;
        }

        public String getImageUrl() {
            return imageUrl;
        }

        public void setImageUrl(String imageUrl) {
            this.imageUrl = imageUrl;
        }

        public String getDetailPageUrl() {
            return detailPageUrl;
        }

        public void setDetailPageUrl(String detailPageUrl) {
            this.detailPageUrl = detailPageUrl;
        }
    }
}
