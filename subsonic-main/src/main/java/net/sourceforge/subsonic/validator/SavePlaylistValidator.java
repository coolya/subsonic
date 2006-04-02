package net.sourceforge.subsonic.validator;

import org.springframework.validation.*;
import net.sourceforge.subsonic.util.*;
import net.sourceforge.subsonic.controller.*;
import net.sourceforge.subsonic.service.*;
import net.sourceforge.subsonic.domain.*;

import java.io.*;

/**
 * Validator for {@link SavePlaylistController}.
 *
 * @author Sindre Mehus
 */
public class SavePlaylistValidator implements Validator {
    private PlaylistService playlistService;

    public boolean supports(Class clazz) {
        return clazz.equals(Playlist.class);
    }

    public void validate(Object obj, Errors errors) {
        Playlist playlist = (Playlist) obj;

        File playlistDirectory = playlistService.getPlaylistDirectory();
        if (!playlistDirectory.exists()) {
            errors.rejectValue("name", "playlist.save.missing_folder", new Object[] {playlistDirectory.getPath()}, null);
        }

        String name = playlist.getName();
        if (name == null || StringUtil.removeSuffix(name.trim()).length() == 0) {
            errors.rejectValue("name", "playlist.save.noname");
        }
    }

    public void setPlaylistService(PlaylistService playlistService) {
        this.playlistService = playlistService;
    }
}
