package net.sourceforge.subsonic.jmeplayer.domain;

/**
 * @author Sindre Mehus
 */
public class Index {

    private final String name;
    private final Artist[] artists;

    public Index(String name, Artist[] artists) {
        this.name = name;
        this.artists = artists;
    }

    public String getName() {
        return name;
    }

    public Artist[] getArtists() {
        return artists;
    }
}
