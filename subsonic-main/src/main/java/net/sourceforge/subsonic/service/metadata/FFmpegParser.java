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
package net.sourceforge.subsonic.service.metadata;

import java.io.File;
import java.io.InputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FilenameUtils;

import net.sourceforge.subsonic.Logger;
import net.sourceforge.subsonic.domain.MusicFile;
import net.sourceforge.subsonic.io.InputStreamReaderThread;
import net.sourceforge.subsonic.service.TranscodingService;
import net.sourceforge.subsonic.util.StringUtil;

/**
 * Parses meta data from video files using FFmpeg (http://ffmpeg.org/).
 * <p/>
 * Currently, only duration and bitrate is supported.
 *
 * @author Sindre Mehus
 */
public class FFmpegParser extends MetaDataParser {

    private static final Logger LOG = Logger.getLogger(FFmpegParser.class);
    private static final Pattern METADATA_PATTERN = Pattern.compile("Duration: (.*), .*, bitrate: (.*) kb/s.*");
    private static final Pattern DURATION_PATTERN = Pattern.compile("(\\d+):(\\d+):(\\d+).(\\d+)");

    private TranscodingService transcodingService;

    /**
     * Parses meta data for the given music file. No guessing or reformatting is done.
     *
     * @param file The music file to parse.
     * @return Meta data for the file.
     */
    @Override
    public MusicFile.MetaData getRawMetaData(MusicFile file) {

        MusicFile.MetaData metaData = getBasicMetaData(file);

        try {

            File ffmpeg = new File(transcodingService.getTranscodeDirectory(), "ffmpeg");

            String[] command = new String[]{ffmpeg.getAbsolutePath(), "-i", file.getFile().getAbsolutePath()};
            Process process = Runtime.getRuntime().exec(command);
            InputStream stdout = process.getInputStream();
            InputStream stderr = process.getErrorStream();

            // Consume stdout, we're not interested in that.
            new InputStreamReaderThread(stdout, "ffmpeg", true).start();

            // Read everything from stderr.  It will contain text similar to:
            // Input #0, avi, from 'foo.avi':
            //   Duration: 00:00:33.90, start: 0.000000, bitrate: 2225 kb/s
            //     Stream #0.0: Video: mpeg4, yuv420p, 352x240 [PAR 1:1 DAR 22:15], 29.97 fps, 29.97 tbr, 29.97 tbn, 30k tbc
            //     Stream #0.1: Audio: pcm_s16le, 44100 Hz, 2 channels, s16, 1411 kb/s
            String[] lines = StringUtil.readLines(stderr);

            for (String line : lines) {
                Matcher matcher = METADATA_PATTERN.matcher(line);
                if (matcher.matches()) {
                    String duration = matcher.group(1);
                    String bitrate = matcher.group(2);

                    metaData.setDuration(parseDuration(duration));
                    metaData.setBitRate(Integer.valueOf(bitrate));
                }
            }
        } catch (Throwable x) {
            LOG.warn("Error when parsing metadata in " + file, x);
        }

        return metaData;
    }

    private Integer parseDuration(String duration) {
        Matcher matcher = DURATION_PATTERN.matcher(duration);
        if (matcher.matches()) {
            int hours = Integer.parseInt(matcher.group(1));
            int minutes = Integer.parseInt(matcher.group(2));
            int seconds = Integer.parseInt(matcher.group(3));

            return hours * 3600 + minutes * 60 + seconds;
        }
        return null;
    }

    /**
     * Not supported.
     */
    @Override
    public void setMetaData(MusicFile file, MusicFile.MetaData metaData) {
        throw new RuntimeException("setMetaData() not supported in " + getClass().getSimpleName());
    }

    /**
     * Returns whether this parser supports tag editing (using the {@link #setMetaData} method).
     *
     * @return Always false.
     */
    @Override
    public boolean isEditingSupported() {
        return false;
    }

    /**
     * Returns whether this parser is applicable to the given file.
     *
     * @param file The file in question.
     * @return Whether this parser is applicable to the given file.
     */
    @Override
    public boolean isApplicable(MusicFile file) {
        if (!file.isFile()) {
            return false;
        }

        String extension = FilenameUtils.getExtension(file.getName()).toLowerCase();
        return extension.equals("avi") ||
                extension.equals("mpg") ||
                extension.equals("mpeg") ||
                extension.equals("flv") ||
                extension.equals("mp4") ||
                extension.equals("m4v") ||
                extension.equals("mkv") ||
                extension.equals("mov") ||
                extension.equals("wmv");
    }

    public void setTranscodingService(TranscodingService transcodingService) {
        this.transcodingService = transcodingService;
    }
}