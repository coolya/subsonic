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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.ParameterizableViewController;

import net.sourceforge.subsonic.dao.ShareDao;
import net.sourceforge.subsonic.domain.MusicFile;
import net.sourceforge.subsonic.domain.Share;
import net.sourceforge.subsonic.domain.User;
import net.sourceforge.subsonic.service.MusicFileService;
import net.sourceforge.subsonic.service.SecurityService;

/**
 * Controller for the page used to administrate the set of shared media.
 *
 * @author Sindre Mehus
 */
public class ShareSettingsController extends ParameterizableViewController {

    private ShareManagementController shareManagementController;
    private ShareDao shareDao;
    private MusicFileService musicFileService;
    private SecurityService securityService;

    @Override
    protected ModelAndView handleRequestInternal(HttpServletRequest request, HttpServletResponse response) throws Exception {

        Map<String, Object> map = new HashMap<String, Object>();

        if (isFormSubmission(request)) {
            String error = handleParameters(request);
            map.put("error", error);
        }

        ModelAndView result = super.handleRequestInternal(request, response);
        map.put("shareBaseUrl", shareManagementController.getShareBaseUrl());
        map.put("shareInfos", getShareInfos(request));

        result.addObject("model", map);
        return result;
    }

    /**
     * Determine if the given request represents a form submission.
     * @param request current HTTP request
     * @return if the request represents a form submission
     */
    private boolean isFormSubmission(HttpServletRequest request) {
        return "POST".equals(request.getMethod());
    }

    private String handleParameters(HttpServletRequest request) {

        for (Share share : getShares(request)) {
            Integer id = share.getId();

            String description = getParameter(request, "description", id);
            boolean delete = getParameter(request, "delete", id) != null;

            if (delete) {
                shareDao.deleteShare(id);
            } else {
                share.setDescription(description);
                shareDao.updateShare(share);
            }
        }

        return null;
    }

    
    private List<ShareInfo> getShareInfos(HttpServletRequest request) {
        ArrayList<ShareInfo> result = new ArrayList<ShareInfo>();
        
        for (Share share : getShares(request)) {
            MusicFile dir = null;
            List<String> paths = shareDao.getSharedFiles(share.getId());
            try {
                if (!paths.isEmpty()) {
                    MusicFile file = musicFileService.getMusicFile(paths.get(0));
                    dir = file.isDirectory() ? file : file.getParent();
                }
            } catch (Exception x) {
                // Ignored
            }
            result.add(new ShareInfo(share, dir));

        }
        return result;
    }

    private List<Share> getShares(HttpServletRequest request) {
        User user = securityService.getCurrentUser(request);

        List<Share> result = new ArrayList<Share>();
        for (Share share : shareDao.getAllShares()) {
            if (user.isAdminRole() || ObjectUtils.equals(user.getUsername(), share.getUsername())) {
                result.add(share);
            }

        }
        return result;
    }

    private String getParameter(HttpServletRequest request, String name, Integer id) {
        return StringUtils.trimToNull(request.getParameter(name + "[" + id + "]"));
    }

    public void setShareManagementController(ShareManagementController shareManagementController) {
        this.shareManagementController = shareManagementController;
    }

    public void setShareDao(ShareDao shareDao) {
        this.shareDao = shareDao;
    }

    public void setMusicFileService(MusicFileService musicFileService) {
        this.musicFileService = musicFileService;
    }

    public void setSecurityService(SecurityService securityService) {
        this.securityService = securityService;
    }

    public static class ShareInfo {
        private final Share share;
        private final MusicFile dir;

        public ShareInfo(Share share, MusicFile dir) {
            this.share = share;
            this.dir = dir;
        }

        public Share getShare() {
            return share;
        }

        public MusicFile getDir() {
            return dir;
        }
    }
}