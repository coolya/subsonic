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
import net.sourceforge.subsonic.dao.ProcessedVideoDao;
import net.sourceforge.subsonic.domain.ProcessedVideo;
import static net.sourceforge.subsonic.domain.ProcessedVideo.Status.*;
import net.sourceforge.subsonic.io.InputStreamReaderThread;
import net.sourceforge.subsonic.service.TranscodingService;
import net.sourceforge.subsonic.util.FileUtil;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.filefilter.PrefixFileFilter;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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
    private ProcessingThread processingThread;

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
        File dir = getVideoScriptDir();

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

    private File getVideoScriptDir() {
        return new File(transcodingService.getTranscodeDirectory(), "video");
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
        video.setStatus(QUEUED);

        processedVideoDao.createProcessedVideo(video);
        triggerProcessing();
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

        // Delete from database.
        processedVideoDao.deleteProcessedVideo(id);

        // Stop process.
        if (processingThread != null && processingThread.getVideo().getId() == id) {
            processingThread.cancel();
        }

        // Delete files.
        new File(video.getLogPath()).delete();
        new File(video.getPath()).delete();
    }

    /**
     * Returns the log for a given video processing.
     *
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

    private synchronized void triggerProcessing() {
        List<ProcessedVideo> videos = processedVideoDao.getProcessedVideos(QUEUED);
        LOG.info(videos.size() + " video(s) in processing queue.");

        if (!videos.isEmpty() && (processingThread == null || !processingThread.isAlive())) {
            processingThread = new ProcessingThread(videos.get(0));
            processingThread.start();
        }
    }

    /**
     * Invoked by Spring container on startup.
     */
    public void init() {
        triggerProcessing();
    }

    public void setProcessedVideoDao(ProcessedVideoDao processedVideoDao) {
        this.processedVideoDao = processedVideoDao;
    }

    public void setTranscodingService(TranscodingService transcodingService) {
        this.transcodingService = transcodingService;
    }

    private class ProcessingThread extends Thread {
        private final ProcessedVideo video;
        private Process process;
        private boolean cancelled;

        private ProcessingThread(ProcessedVideo video) {
            super("ProcessingThread");
            this.video = video;
        }

        public ProcessedVideo getVideo() {
            return video;
        }

        @Override
        public void run() {
            try {
                LOG.info("Starting video processing for " + video.getSourcePath());
                video.setStatus(PROCESSING);
                processedVideoDao.updateProcessedVideo(video);

                process = Runtime.getRuntime().exec(createCommand());

                // Consume stdout and stderr from the process, otherwise it may block.
                new InputStreamReaderThread(process.getErrorStream(), video.getSourcePath(), true).start();
                new InputStreamReaderThread(process.getInputStream(), video.getSourcePath(), true).start();

                process.waitFor();
                if (!cancelled) {
                    video.setStatus(FINISHED);
                    processedVideoDao.updateProcessedVideo(video);
                    LOG.info("Finished video processing for " + video.getSourcePath());
                }
            } catch (Throwable x) {
                LOG.error("Error while processing " + video.getSourcePath(), x);
            } finally {
                triggerProcessing();
            }
        }

        public void cancel() {
            cancelled = true;
            LOG.info("Cancelling video processing for " + video.getSourcePath());
            if (process != null) {
                process.destroy();
            }
        }

        private String[] createCommand() {

            File[] scripts = FileUtil.listFiles(getVideoScriptDir(), new PrefixFileFilter(video.getQuality()));

            if (scripts.length == 0) {
                throw new IllegalArgumentException("Video processing script for quality '" + video.getQuality() + "' not found.");
            }

            if (scripts.length > 1) {
                LOG.warn("Multiple video processing scripts for quality '" + video.getQuality() + "' found.");
            }

            return new String[]{
                    scripts[0].getPath(),
                    video.getSourcePath(),
                    video.getPath(),
                    video.getLogPath(),
                    transcodingService.getTranscodeDirectory().getPath()
            };

        }
    }
}
