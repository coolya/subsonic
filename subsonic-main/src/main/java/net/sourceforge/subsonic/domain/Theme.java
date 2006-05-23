package net.sourceforge.subsonic.domain;

/**
 * Contains the ID and name for a theme.
 *
 * @author Sindre Mehus
 */
public class Theme {
    private String id;
    private String name;

    public Theme(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
