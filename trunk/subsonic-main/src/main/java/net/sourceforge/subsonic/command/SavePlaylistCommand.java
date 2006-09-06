package net.sourceforge.subsonic.command;

import net.sourceforge.subsonic.controller.*;
import net.sourceforge.subsonic.domain.*;
import org.springframework.util.*;

/**
 * Command used in {@link SavePlaylistController}.
 *
 * @author Sindre Mehus
 */
public class SavePlaylistCommand {

    private Playlist playlist;
    private String name;
    private String suffix;
    private String[] formats;

    public SavePlaylistCommand(Playlist playlist) {
        this.playlist = playlist;
        name = StringUtils.stripFilenameExtension(playlist.getName());
        suffix = StringUtils.getFilenameExtension(playlist.getName());
        formats = new String[]{"m3u", "pls", "xspf"};
    }

    public Playlist getPlaylist() {
        return playlist;
    }

    public void setPlaylist(Playlist playlist) {
        this.playlist = playlist;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSuffix() {
        return suffix;
    }

    public void setSuffix(String suffix) {
        this.suffix = suffix;
    }

    public String[] getFormats() {
        return formats;
    }

    public void setFormats(String[] formats) {
        this.formats = formats;
    }
}