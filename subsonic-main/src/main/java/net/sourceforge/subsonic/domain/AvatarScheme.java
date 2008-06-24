package net.sourceforge.subsonic.domain;

/**
 * Enumeration of avatar schemes.
 *
 * @author Sindre Mehus
 */
public enum AvatarScheme {

    /**
     * No avatar should be displayed.
     */
    NONE(-1),

    /**
     * One of the system avatars should be displayed.
     */
    SYSTEM(0),

    /**
     * The custom avatar should be displayed.
     */
    CUSTOM(-2);

    private final int code;

    AvatarScheme(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}