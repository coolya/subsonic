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
package net.sourceforge.subsonic.ajax;

import java.util.List;

import net.sourceforge.subsonic.Logger;
import net.sourceforge.subsonic.dao.ProcessedVideoDao;
import net.sourceforge.subsonic.domain.ProcessedVideo;

/**
 * Provides AJAX-enabled services for video processing.
 * <p/>
 * This class is used by the DWR framework (http://getahead.ltd.uk/dwr/).
 *
 * @author Sindre Mehus
 */
public class VideoService {

    private static final Logger LOG = Logger.getLogger(VideoService.class);
    private ProcessedVideoDao processedVideoDao;

    public List<ProcessedVideo> getProcessedVideos(String path) {
        return processedVideoDao.getProcessedVideos(path);
    }

    public List<String> getVideoQualities() {
        // TODO
        return null;
    }

    public void processVideo(String path, String quality) {
        ProcessedVideo video = new ProcessedVideo();
        // TODO

        processedVideoDao.createProcessedVideo(video);
    }

    public void cancelVideoProcessing(int id) {
        ProcessedVideo video = processedVideoDao.getProcessedVideo(id);
        // TODO

        processedVideoDao.updateProcessedVideo(video);
    }

    public void deleteProcessedVideo(int id) {
        // TODO
        processedVideoDao.deleteProcessedVideo(id);
    }

    public String getVideoProcessingLog(int id) {
        // TODO
        return null;
    }

    /**
     * Invoked by Spring container on startup.
     */
    public void init() {
        // TODO
    }

    public void setProcessedVideoDao(ProcessedVideoDao processedVideoDao) {
        this.processedVideoDao = processedVideoDao;
    }
}
