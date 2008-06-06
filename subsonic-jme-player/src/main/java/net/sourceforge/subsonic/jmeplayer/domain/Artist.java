package net.sourceforge.subsonic.jmeplayer.domain;

/**
 * @author Sindre Mehus
 */
public class Artist {

    private final String name;
    private final String path;

    public Artist(String name, String path) {
        this.name = name;
        this.path = path;
    }

    public String getName() {
        return name;
    }

    public String getPath() {
        return path;
    }
}
