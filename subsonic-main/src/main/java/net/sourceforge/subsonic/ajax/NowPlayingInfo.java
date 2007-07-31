package net.sourceforge.subsonic.ajax;

/**
 * Details about what a user is currently listening to.
 *
 * @author Sindre Mehus
 */
public class NowPlayingInfo {

    private String username;
    private String artist;
    private String title;
    private String tooltip;
    private String albumUrl;

    public NowPlayingInfo(String user, String artist, String title, String tooltip,  String albumUrl) {
        this.username = user;
        this.artist = artist;
        this.title = title;
        this.tooltip = tooltip;
        this.albumUrl = albumUrl;
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
}
