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

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;
import org.springframework.web.servlet.mvc.ParameterizableViewController;

import net.sourceforge.subsonic.service.MusicFileService;
import net.sourceforge.subsonic.service.SecurityService;
import net.sourceforge.subsonic.service.SettingsService;
import net.sourceforge.subsonic.service.TranscodingService;
import net.sourceforge.subsonic.service.VideoService;
import net.sourceforge.subsonic.domain.ProcessedVideo;
import net.sourceforge.subsonic.Logger;
import net.sourceforge.subsonic.util.StringUtil;
import net.sourceforge.subsonic.filter.ParameterDecodingFilter;

/**
 * Controller for the page used to play videos.
 *
 * @author Sindre Mehus
 */
public class VideoPlayerController extends ParameterizableViewController {

    private static final Logger LOG = Logger.getLogger(StreamController.class);

    private SecurityService securityService;
    private SettingsService settingsService;
    private TranscodingService transcodingService;
    private MusicFileService musicFileService;
    private VideoService videoService;

    @Override
    protected ModelAndView handleRequestInternal(HttpServletRequest request, HttpServletResponse response) throws Exception {
        ModelAndView result = handleCommand(request, response);
        if (result != null) {
            return result;
        }

        Map<String, Object> map = new HashMap<String, Object>();
        String path = request.getParameter("path");
        map.put("video", musicFileService.getMusicFile(path));
        map.put("processedVideos", videoService.getProcessedVideos(path));
        map.put("qualities", videoService.getVideoQualities());

        result = super.handleRequestInternal(request, response);
        result.addObject("model", map);
        return result;
    }

    private ModelAndView handleCommand(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String action = request.getParameter("action");
        if ("create".equals(action)) {
            return handleCreateCommand(request, response);
        }
        if ("delete".equals(action)) {
            return handleDeleteCommand(request, response);
        }
        return null;
    }

    private ModelAndView handleCreateCommand(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String path = ServletRequestUtils.getRequiredStringParameter(request, "path");
        String quality = ServletRequestUtils.getRequiredStringParameter(request, "quality");

        boolean exists = false;
        for (ProcessedVideo processedVideo : videoService.getProcessedVideos(path)) {
            if (processedVideo.getQuality().equals(quality)) {
                exists = true;
                break;
            }
        }

        if (exists) {
            LOG.warn("The video '" + path + "' already exists in quality '" + quality + "'.");
        } else {
            videoService.processVideo(path, quality);
        }

        return createRedirectView(path);
    }

    private ModelAndView handleDeleteCommand(HttpServletRequest request, HttpServletResponse response) throws Exception {
        int id = ServletRequestUtils.getRequiredIntParameter(request, "id");
        ProcessedVideo video = videoService.getProcessedVideo(id);
        if (video == null) {
            return null;
        }
        videoService.cancelVideoProcessing(id);
        return createRedirectView(video.getSourcePath());
    }

    private ModelAndView createRedirectView(String path) {
        return new ModelAndView(new RedirectView("videoPlayer.view?path" + ParameterDecodingFilter.PARAM_SUFFIX + "=" + StringUtil.utf8HexEncode(path)));
    }

    public void setSecurityService(SecurityService securityService) {
        this.securityService = securityService;
    }

    public void setSettingsService(SettingsService settingsService) {
        this.settingsService = settingsService;
    }

    public void setTranscodingService(TranscodingService transcodingService) {
        this.transcodingService = transcodingService;
    }

    public void setMusicFileService(MusicFileService musicFileService) {
        this.musicFileService = musicFileService;
    }

    public void setVideoService(VideoService videoService) {
        this.videoService = videoService;
    }
}
