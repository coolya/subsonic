package net.sourceforge.subsonic.jmeplayer.domain;

/**
 * @author Sindre Mehus
 */
public class Index {

    private final String index;
    private final Artist[] artists;

    public Index(String index, Artist[] artists) {
        this.index = index;
        this.artists = artists;
    }

    public String getIndex() {
        return index;
    }

    public Artist[] getArtists() {
        return artists;
    }
}
