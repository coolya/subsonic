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

import net.sourceforge.subsonic.Logger;
import net.sourceforge.subsonic.io.InputStreamReaderThread;
import net.sourceforge.subsonic.dao.ProcessedVideoDao;
import net.sourceforge.subsonic.domain.ProcessedVideo;
import net.sourceforge.subsonic.service.TranscodingService;
import net.sourceforge.subsonic.util.FileUtil;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileFilter;
import java.io.InputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

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
    private TranscodingService transcodingService;

//    private final BlockingQueue<Integer> processingQueue = new LinkedBlockingQueue<Integer>();

    /**
    * Returns all processed videos for the given path.
    *
    * @param sourcePath The path of the source video.
    * @return List of processed videos.
    */
    public List<ProcessedVideo> getProcessedVideos(String sourcePath) {
        return processedVideoDao.getProcessedVideos(sourcePath);
    }

    /**
     * Returns all available video qualities. Each quality is represented as a
     * processing script in SUBSONIC_HOME/transcode/video
     *
     * @return Available video qualities.
     */
    public List<String> getVideoQualities() {
        File dir = new File(transcodingService.getTranscodeDirectory(), "video");

        if (!dir.exists() || !dir.isDirectory()) {
            LOG.warn("Video transcoding directory not found: " + dir);
            return Collections.emptyList();
        }

        FileFilter filter = new FileFilter() {
            public boolean accept(File file) {
                return file.isFile() && !file.isHidden() && file.canRead();
            }
        };
        File[] files = FileUtil.listFiles(dir, filter);
        List<String> result = new ArrayList<String>();
        for (File file : files) {
            result.add(FilenameUtils.getBaseName(file.getName()));
        }
        return result;
    }

    /**
     * Request processing of the given video in the given quality.
     *
     * @param sourcePath Path of the source video file.
     * @param quality    The requested video quality.
     */
    public void processVideo(String sourcePath, String quality) {
        File sourceFile = new File(sourcePath);

        String logFileName = "." + FilenameUtils.getBaseName(sourcePath) + "." + quality + ".log";
        File logFile = new File(sourceFile.getParentFile(), logFileName);

        String processedFileName = "." + FilenameUtils.getBaseName(sourcePath) + "." + quality + ".mp4";
        File processedFile = new File(sourceFile.getParentFile(), processedFileName);

        ProcessedVideo video = new ProcessedVideo();
        video.setPath(processedFile.getPath());
        video.setSourcePath(sourcePath);
        video.setQuality(quality);
        video.setLogPath(logFile.getPath());
        video.setStatus(ProcessedVideo.Status.QUEUED);

        processedVideoDao.createProcessedVideo(video);
        // TODO: Trigger processing.
    }

    /**
     * Cancels video processing.
     *
     * @param id The video ID.
     */
    public void cancelVideoProcessing(int id) {
        ProcessedVideo video = processedVideoDao.getProcessedVideo(id);
        if (video == null) {
            LOG.warn("Video " + id + " not found.");
            return;
        }

        // TODO: Stop process

        // TODO: Delete log and tmp files.

        video.setStatus(ProcessedVideo.Status.FAILED);
        processedVideoDao.updateProcessedVideo(video);
    }

    /**
     * Deletes the processed video with the given ID.
     *
     * @param id The video ID.
     */
    public void deleteProcessedVideo(int id) {
        cancelVideoProcessing(id);
        processedVideoDao.deleteProcessedVideo(id);
    }

    /**
     * Returns the log for a given video processing.
     * @param id The video ID.
     * @return The log, or <code>null</code>.
     */
    public String getVideoProcessingLog(int id) {
        ProcessedVideo video = processedVideoDao.getProcessedVideo(id);
        if (video == null) {
            LOG.warn("Video " + id + " not found.");
            return null;
        }

        File logFile = new File(video.getLogPath());
        if (!logFile.exists()) {
            return null;
        }

        // TODO: Don't read entire file it it's big.
        InputStream in = null;
        try {
            in = new FileInputStream(logFile);
            return IOUtils.toString(in);
        } catch (IOException x) {
            LOG.warn("Failed to read log file " + logFile, x);
            return null;
        } finally {
            IOUtils.closeQuietly(in);
        }
    }

    private void processAndWait(ProcessedVideo video) {
        video.setStatus(ProcessedVideo.Status.PROCESSING);
        processedVideoDao.updateProcessedVideo(video);

        Process process = Runtime.getRuntime().exec(command);

        // Consume stdout and stderr from the process, otherwise it may block.
        new InputStreamReaderThread(process.getErrorStream(), video.getSourcePath(), true).start();
        new InputStreamReaderThread(process.getInputStream(), video.getSourcePath(), true).start();

        process.waitFor();
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

    public void setTranscodingService(TranscodingService transcodingService) {
        this.transcodingService = transcodingService;
    }
}
