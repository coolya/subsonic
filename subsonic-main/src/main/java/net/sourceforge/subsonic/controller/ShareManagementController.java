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
import net.sourceforge.subsonic.dao.ShareDao;
import net.sourceforge.subsonic.domain.MusicFile;
import net.sourceforge.subsonic.domain.Share;
import net.sourceforge.subsonic.service.MusicFileService;
import net.sourceforge.subsonic.service.SecurityService;
import net.sourceforge.subsonic.service.SettingsService;

import org.apache.commons.lang.RandomStringUtils;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.util.Date;
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
public class ShareManagementController extends MultiActionController {

    private static final Logger LOG = Logger.getLogger(ShareManagementController.class);

    private MusicFileService musicFileService;
    private SettingsService settingsService;
    private ShareDao shareDao;
    private SecurityService securityService;

    public ModelAndView createShare(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String dir = request.getParameter("dir");

        List<MusicFile> files = getMusicFiles(request);

        Map<String, Object> map = new HashMap<String, Object>();
        map.put("urlRedirectionEnabled", settingsService.isUrlRedirectionEnabled());
        map.put("dir", musicFileService.getMusicFile(dir));
        map.put("playUrl", getShareUrl(request, files));

        return new ModelAndView("createShare", "model", map);
    }

    public String getShareUrl(HttpServletRequest request, List<MusicFile> files) throws Exception {

        Share share = new Share();
        share.setName(RandomStringUtils.random(5, "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ"));
        share.setCreated(new Date());
        share.setUsername(securityService.getCurrentUsername(request));

        int shareId = shareDao.createShare(share);
        for (MusicFile file : files) {
            shareDao.createSharedFiles(shareId, file.getPath());
        }
        LOG.info("Created share '" + share.getName() + "' with " + files.size() + " file(s).");

        return getShareBaseUrl() + share.getName();
    }

    public String getShareBaseUrl() {
        return "http://" + settingsService.getUrlRedirectFrom() + ".subsonic.org/share/";
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

    public void setShareDao(ShareDao shareDao) {
        this.shareDao = shareDao;
    }

    public void setSecurityService(SecurityService securityService) {
        this.securityService = securityService;
    }
}