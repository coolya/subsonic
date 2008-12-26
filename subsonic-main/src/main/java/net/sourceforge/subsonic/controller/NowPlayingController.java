/*
 This file is part of Subsonic.

 Subsonic is free software: you can redistribute it and/or modify
 it under the terms of the GNU General Public License as published by
 the Free Software Foundation, either version 3 of the License, or
 (at your option) any later version.

 Subsonic is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with Subsonic.  If not, see <http://www.gnu.org/licenses/>.

 Copyright 2009 (C) Sindre Mehus
 */
package net.sourceforge.subsonic.controller;

import net.sourceforge.subsonic.domain.*;
import net.sourceforge.subsonic.service.*;
import net.sourceforge.subsonic.util.StringUtil;
import net.sourceforge.subsonic.filter.ParameterDecodingFilter;
import org.springframework.web.servlet.*;
import org.springframework.web.servlet.mvc.*;
import org.springframework.web.servlet.view.*;

import javax.servlet.http.*;

/**
 * Controller for showing what's currently playing.
 *
 * @author Sindre Mehus
 */
public class NowPlayingController extends AbstractController {

    private PlayerService playerService;

    protected ModelAndView handleRequestInternal(HttpServletRequest request, HttpServletResponse response) throws Exception {

        Player player = playerService.getPlayer(request, response);
        Playlist playlist = player.getPlaylist();

        MusicFile current = playlist.getCurrentFile();
        String url;
        if (current != null && !current.getParent().isRoot()) {
            url = "main.view?path" + ParameterDecodingFilter.PARAM_SUFFIX  + "=" +
                  StringUtil.utf8HexEncode(current.getParent().getPath()) + "&updateNowPlaying=true";
        } else {
            url = "home.view";
        }

        return new ModelAndView(new RedirectView(url));
    }

    public void setPlayerService(PlayerService playerService) {
        this.playerService = playerService;
    }
}
