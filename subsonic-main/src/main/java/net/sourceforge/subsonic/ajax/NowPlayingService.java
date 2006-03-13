package net.sourceforge.subsonic.ajax;

import net.sourceforge.subsonic.domain.*;
import net.sourceforge.subsonic.service.*;
import uk.ltd.getahead.dwr.*;

import java.io.*;

/**
 * Provides AJAX-enabled services for retrieving the currently playing file and directory.
 * This class is used by the DWR framework (http://getahead.ltd.uk/dwr/).
 *
 * @author Sindre Mehus
 * @version $Revision: 1.1 $ $Date: 2006/02/26 21:46:28 $
 */
public class NowPlayingService {

    /**
     * Returns the path of the currently playing file.
     * @return The path of the currently playing file, or the string <code>"nil"</code> if no file is playing.
     */
    public String getFile() {
        MusicFile current = getCurrentMusicFile();
        return current == null ? "nil" : current.getPath();
    }

    /**
     * Returns the path of the directory of the currently playing file.
     * @return The path of the directory of the currently playing file, or the string <code>"nil"</code> if no file is playing.
     */
    public String getDirectory() throws IOException {
        MusicFile current = getCurrentMusicFile();
        return current == null || current.getParent() == null ? "nil" : current.getParent().getPath();
    }

    private MusicFile getCurrentMusicFile() {
        WebContext webContext = WebContextFactory.get();
        Player player = ServiceFactory.getPlayerService().getPlayer(webContext.getHttpServletRequest(), webContext.getHttpServletResponse());
        return player.getPlaylist().getCurrentFile();
    }

}
