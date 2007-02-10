package net.sourceforge.subsonic.command;

/**
 * Holds the name and description of an enum value.
 *
 * @author Sindre Mehus
 */
public class EnumHolder {
    private String name;
    private String description;

    public EnumHolder(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }
}
