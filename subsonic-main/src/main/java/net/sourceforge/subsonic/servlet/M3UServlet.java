package net.sourceforge.subsonic.servlet;

import net.sourceforge.subsonic.domain.*;
import net.sourceforge.subsonic.service.*;

import javax.servlet.http.*;
import java.io.*;

/**
 * A servlet which generates the M3U playlist.
 *
 * @author Sindre Mehus
 * @version $Revision: 1.2 $ $Date: 2006/03/06 20:20:28 $
 */
public class M3UServlet extends HttpServlet {

    /**
     * Handles the given HTTP request.
     * @param request The HTTP request.
     * @param response The HTTP response.
     * @throws IOException If an I/O error occurs.
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("audio/x-mpegurl");
        Player player = ServiceFactory.getPlayerService().getPlayer(request, response);
        Playlist playlist = player.getPlaylist();

        String s = "stream?player=" + player.getId() + '&';

        // Get suffix of current file, e.g., ".mp3".
        String suffix = playlist.getSuffix();
        if (suffix != null) {
            s += "suffix=" + suffix;
        }

        String url = request.getRequestURL().toString();
        url = url.replaceFirst("play.m3u.*", s);

        response.getOutputStream().println("#EXTM3U");
        response.getOutputStream().println("#EXTINF:-1,subsonic");
        response.getOutputStream().println(url);
    }
}
