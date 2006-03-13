package net.sourceforge.subsonic.domain;

import java.io.*;

/**
 * Represents a top level directory in which music or other media is stored.
 *
 * @author Sindre Mehus
 * @version $Revision: 1.1 $ $Date: 2005/11/27 14:32:05 $
 */
public class MusicFolder {

    private Integer id;
    private File path;
    private String name;
    private boolean isEnabled;

    /**
     * Creates a new music folder.
     * @param id The system-generated ID.
     * @param path The path of the music folder.
     * @param name The user-defined name.
     * @param enabled Whether the folder is enabled.
     */
    public MusicFolder(Integer id, File path, String name, boolean enabled) {
        this.id = id;
        this.path = path;
        this.name = name;
        isEnabled = enabled;
    }

    /**
     * Creates a new music folder.
     * @param path The path of the music folder.
     * @param name The user-defined name.
     * @param enabled Whether the folder is enabled.
     */
    public MusicFolder(File path, String name, boolean enabled) {
        this(null, path, name, enabled);
    }

    /**
     * Returns the system-generated ID.
     * @return The system-generated ID.
     */
    public Integer getId() {
        return id;
    }

    /**
     * Returns the path of the music folder.
     * @return The path of the music folder.
     */
    public File getPath() {
        return path;
    }

    /**
     * Sets the path of the music folder.
     * @param path The path of the music folder.
     */
    public void setPath(File path) {
        this.path = path;
    }

    /**
     * Returns the user-defined name.
     * @return The user-defined name.
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the user-defined name.
     * @param name The user-defined name.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Returns whether the folder is enabled.
     * @return Whether the folder is enabled.
     */
    public boolean isEnabled() {
        return isEnabled;
    }

    /**
     * Sets whether the folder is enabled.
     * @param enabled Whether the folder is enabled.
     */
    public void setEnabled(boolean enabled) {
        isEnabled = enabled;
    }

}