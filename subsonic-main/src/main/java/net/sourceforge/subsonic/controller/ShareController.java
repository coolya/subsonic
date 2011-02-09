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
import net.sourceforge.subsonic.util.*;
import net.sourceforge.subsonic.filter.ParameterDecodingFilter;
import org.springframework.web.servlet.*;
import org.springframework.web.servlet.view.*;
import org.springframework.web.servlet.mvc.*;

import javax.servlet.http.*;
import java.util.Map;
import java.util.HashMap;

/**
 * Controller for sharing music on Twitter, Facebook etc.
 *
 * @author Sindre Mehus
 */
public class ShareController extends ParameterizableViewController {

    private final UrlShortenerService urlShortenerService = new UrlShortenerService();
    private MusicFileService musicFileService;
    private SettingsService settingsService;

    protected ModelAndView handleRequestInternal(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String path = request.getParameter("path");

        String playUrl = "http://" + settingsService.getUrlRedirectFrom() + ".subsonic.org/externalPlayer.view?dirUtf8Hex=" + StringUtil.utf8HexEncode(path);

        try {
//            playUrl = urlShortenerService.shorten(playUrl);
        } catch (Exception e) {
// TODO: Log
        }

        MusicFile file = musicFileService.getMusicFile(path);

        Map<String, Object> map = new HashMap<String, Object>();
        map.put("file", file);
        map.put("playUrl", playUrl);

        ModelAndView result = super.handleRequestInternal(request, response);
        result.addObject("model", map);
        return result;

    }

    public void setMusicFileService(MusicFileService musicFileService) {
        this.musicFileService = musicFileService;
    }

    public void setSettingsService(SettingsService settingsService) {
        this.settingsService = settingsService;
    }
}