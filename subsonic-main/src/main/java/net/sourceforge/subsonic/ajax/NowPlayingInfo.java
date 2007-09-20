package net.sourceforge.subsonic.ajax;

/**
 * Details about what a user is currently listening to.
 *
 * @author Sindre Mehus
 */
public class NowPlayingInfo {

    private final String username;
    private final String artist;
    private final String title;
    private final String tooltip;
    private final String albumUrl;
    private final String lyricsUrl;
    private final String coverArtUrl;

    public NowPlayingInfo(String user, String artist, String title, String tooltip, String albumUrl, String lyricsUrl, String coverArtUrl) {
        this.username = user;
        this.artist = artist;
        this.title = title;
        this.tooltip = tooltip;
        this.albumUrl = albumUrl;
        this.lyricsUrl = lyricsUrl;
        this.coverArtUrl = coverArtUrl;
    }

    public String getUsername() {
        return username;
    }

    public String getArtist() {
        return artist;
    }

    public String getTitle() {
        return title;
    }

    public String getTooltip() {
        return tooltip;
    }

    public String getAlbumUrl() {
        return albumUrl;
    }

    public String getLyricsUrl() {
        return lyricsUrl;
    }

    public String getCoverArtUrl() {
        return coverArtUrl;
    }
}
