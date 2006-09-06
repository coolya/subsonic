package net.sourceforge.subsonic.validator;

import net.sourceforge.subsonic.command.*;
import net.sourceforge.subsonic.controller.*;
import net.sourceforge.subsonic.service.*;
import org.springframework.validation.*;

import java.io.*;

/**
 * Validator for {@link SavePlaylistController}.
 *
 * @author Sindre Mehus
 */
public class SavePlaylistValidator implements Validator {
    private PlaylistService playlistService;

    public boolean supports(Class clazz) {
        return clazz.equals(SavePlaylistCommand.class);
    }

    public void validate(Object obj, Errors errors) {
        File playlistDirectory = playlistService.getPlaylistDirectory();
        if (!playlistDirectory.exists()) {
            errors.rejectValue("name", "playlist.save.missing_folder", new Object[] {playlistDirectory.getPath()}, null);
        }

        String name = ((SavePlaylistCommand) obj).getName();
        if (name == null || name.trim().length() == 0) {
            errors.rejectValue("name", "playlist.save.noname");
        }
    }

    public void setPlaylistService(PlaylistService playlistService) {
        this.playlistService = playlistService;
    }
}
