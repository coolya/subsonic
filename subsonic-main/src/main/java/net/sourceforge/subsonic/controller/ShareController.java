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

import net.sourceforge.subsonic.Logger;
import net.sourceforge.subsonic.domain.MusicFile;
import net.sourceforge.subsonic.service.MusicFileService;
import net.sourceforge.subsonic.service.SettingsService;
import net.sourceforge.subsonic.service.UrlShortenerService;
import net.sourceforge.subsonic.util.StringUtil;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.ParameterizableViewController;
import org.springframework.web.bind.ServletRequestUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.Arrays;
import java.util.ArrayList;
import java.io.IOException;

/**
 * Controller for sharing music on Twitter, Facebook etc.
 *
 * @author Sindre Mehus
 */
public class ShareController extends ParameterizableViewController {

    private static final Logger LOG = Logger.getLogger(ShareController.class);

    private final UrlShortenerService urlShortenerService = new UrlShortenerService();
    private MusicFileService musicFileService;
    private SettingsService settingsService;

    protected ModelAndView handleRequestInternal(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String dir = request.getParameter("dir");

        StringBuilder builder = new StringBuilder();
        builder.append("http://").append(settingsService.getUrlRedirectFrom()).append(".subsonic.org/externalPlayer.view?");

        List<MusicFile> files = getMusicFiles(request);
        for (MusicFile file : files) {
            builder.append("pathUtf8Hex=").append(StringUtil.utf8HexEncode(file.getPath())).append("&");
        }

        String playUrl = urlShortenerService.shorten(builder.toString());

        Map<String, Object> map = new HashMap<String, Object>();
        map.put("urlRedirectionEnabled", settingsService.isUrlRedirectionEnabled());
        map.put("dir", musicFileService.getMusicFile(dir));
        map.put("playUrl", playUrl);

        ModelAndView result = super.handleRequestInternal(request, response);
        result.addObject("model", map);
        return result;
    }

    private List<MusicFile> getMusicFiles(HttpServletRequest request) throws IOException {
        MusicFile dir = musicFileService.getMusicFile(request.getParameter("dir"));
        int[] songIndexes = ServletRequestUtils.getIntParameters(request, "i");
        if (songIndexes.length == 0) {
            return Arrays.asList(dir);
        }

        List<MusicFile> children = dir.getChildren(true, true, true);
        List<MusicFile> result = new ArrayList<MusicFile>();
        for (int songIndex : songIndexes) {
            result.add(children.get(songIndex));
        }

        return result;
    }

    public void setMusicFileService(MusicFileService musicFileService) {
        this.musicFileService = musicFileService;
    }

    public void setSettingsService(SettingsService settingsService) {
        this.settingsService = settingsService;
    }
}