package net.sourceforge.subsonic.webservice.dto;

/**
 * @author Sindre Mehus
 */
public class MusicFileDto {

    private final String name;
    private final String path;

    public MusicFileDto(String name, String path) {
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
