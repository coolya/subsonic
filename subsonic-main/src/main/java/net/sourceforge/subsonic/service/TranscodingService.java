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
package net.sourceforge.subsonic.service;

import net.sourceforge.subsonic.Logger;
import net.sourceforge.subsonic.dao.TranscodingDao;
import net.sourceforge.subsonic.domain.MusicFile;
import net.sourceforge.subsonic.domain.Player;
import net.sourceforge.subsonic.domain.TranscodeScheme;
import net.sourceforge.subsonic.domain.Transcoding;
import net.sourceforge.subsonic.domain.UserSettings;
import net.sourceforge.subsonic.io.TranscodeInputStream;
import net.sourceforge.subsonic.util.StringUtil;
import net.sourceforge.subsonic.util.Util;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.filefilter.PrefixFileFilter;
import org.apache.commons.lang.StringUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * Provides services for transcoding media. Transcoding is the process of
 * converting an audio stream to a different format and/or bit rate. The latter is
 * also called downsampling.
 *
 * @author Sindre Mehus
 * @see TranscodeInputStream
 */
public class TranscodingService {

    private static final Logger LOG = Logger.getLogger(TranscodingService.class);

    private TranscodingDao transcodingDao;
    private SettingsService settingsService;

    /**
     * Returns all transcodings. Disabled transcodings are not included.
     *
     * @return Possibly empty list of all transcodings.
     */
    public List<Transcoding> getAllTranscodings() {
        return getAllTranscodings(false);
    }

    /**
     * Returns all transcodings.
     *
     * @param includeAll Whether disabled transcodings should be included.
     * @return Possibly empty list of all transcodings.
     */
    public List<Transcoding> getAllTranscodings(boolean includeAll) {
        List<Transcoding> all = transcodingDao.getAllTranscodings();
        List<Transcoding> result = new ArrayList<Transcoding>(all.size());
        for (Transcoding transcoding : all) {
            if (includeAll || transcoding.isEnabled()) {
                result.add(transcoding);
            }
        }
        return result;
    }

    /**
     * Returns all active transcodings for the given player. Only enabled transcodings are returned.
     *
     * @param player The player.
     * @return All active transcodings for the player.
     */
    public List<Transcoding> getTranscodingsForPlayer(Player player) {
        List<Transcoding> all = transcodingDao.getTranscodingsForPlayer(player.getId());
        List<Transcoding> result = new ArrayList<Transcoding>(all.size());
        for (Transcoding transcoding : all) {
            if (transcoding.isEnabled()) {
                result.add(transcoding);
            }
        }
        return result;
    }

    /**
     * Sets the list of active transcodings for the given player.
     *
     * @param player         The player.
     * @param transcodingIds ID's of the active transcodings.
     */
    public void setTranscodingsForPlayer(Player player, int[] transcodingIds) {
        transcodingDao.setTranscodingsForPlayer(player.getId(), transcodingIds);
    }

    /**
     * Creates a new transcoding.
     *
     * @param transcoding The transcoding to create.
     */
    public void createTranscoding(Transcoding transcoding) {
        transcodingDao.createTranscoding(transcoding);
    }

    /**
     * Deletes the transcoding with the given ID.
     *
     * @param id The transcoding ID.
     */
    public void deleteTranscoding(Integer id) {
        transcodingDao.deleteTranscoding(id);
    }

    /**
     * Updates the given transcoding.
     *
     * @param transcoding The transcoding to update.
     */
    public void updateTranscoding(Transcoding transcoding) {
        transcodingDao.updateTranscoding(transcoding);
    }

    /**
     * Returns whether transcoding and/or downsampling is required for the given music file and player combination.
     *
     * @param musicFile The music file.
     * @param player    The player.
     * @return Whether transcoding and/or downsampling will be performed if invoking the
     *         {@link #getTranscodedInputStream(MusicFile,Player)} method with the same arguments.
     */
    public boolean isTranscodingRequired(MusicFile musicFile, Player player) {
        if (getTranscoding(musicFile, player) != null) {
            return true;
        }

        TranscodeScheme transcodeScheme = getTranscodeScheme(player);
        boolean downsample = transcodeScheme != TranscodeScheme.OFF;
        Integer bitRate = musicFile.getMetaData().getBitRate();

        return downsample && bitRate != null && bitRate > transcodeScheme.getMaxBitRate();
    }

    /**
     * Returns the suffix for the given player and music file, taking transcodings into account.
     *
     * @param player The player in question.
     * @param file   The music player.
     * @return The file suffix, e.g., "mp3".
     */
    public String getSuffix(Player player, MusicFile file) {
        Transcoding transcoding = getTranscoding(file, player);
        return transcoding != null ? transcoding.getTargetFormat() : file.getSuffix();
    }

    /**
     * Returns a possibly transcoded or downsampled input stream for the given music file and player combination.
     * <p/>
     * A transcoding is applied if it is applicable for the format of the given file, and is activated for the
     * given player.
     * <p/>
     * If no transcoding is applicable, the file may still be downsampled, given that the player is configured
     * with a bit rate limit which is higher than the actual bit rate of the file.
     * <p/>
     * Otherwise, a normal input stream to the original file is returned.
     *
     * @param musicFile The music file.
     * @param player    The player.
     * @return A possible transcoded or downsampled input stream.
     * @throws IOException If an I/O error occurs.
     */
    public InputStream getTranscodedInputStream(MusicFile musicFile, Player player) throws IOException {
        try {
            Transcoding transcoding = getTranscoding(musicFile, player);
            if (transcoding != null) {
                return getTranscodedInputStream(musicFile, transcoding, getTranscodeScheme(player));
            }

            TranscodeScheme transcodeScheme = getTranscodeScheme(player);
            if (transcodeScheme != TranscodeScheme.OFF) {
                boolean supported = isDownsamplingSupported(musicFile);
                Integer bitRate = musicFile.getMetaData().getBitRate();
                if (supported && bitRate != null && bitRate > transcodeScheme.getMaxBitRate()) {
                    return getDownsampledInputStream(musicFile, transcodeScheme);
                }
            }
        } catch (Exception x) {
            LOG.warn("Failed to transcode " + musicFile + ". Using original.", x);
        }

        return new FileInputStream(musicFile.getFile());
    }

    /**
     * Returns the strictest transcoding scheme defined for the player and the user.
     */
    private TranscodeScheme getTranscodeScheme(Player player) {
        String username = player.getUsername();
        if (username != null) {
            UserSettings userSettings = settingsService.getUserSettings(username);
            return player.getTranscodeScheme().strictest(userSettings.getTranscodeScheme());
        }

        return player.getTranscodeScheme();
    }

    /**
     * Returns an input stream by applying the given transcoding to the given music file.
     *
     * @param musicFile       The music file.
     * @param transcoding     The transcoding to apply.
     * @param transcodeScheme The transcoding (resampling) scheme. May be <code>null</code>.
     * @return The transcoded input stream.
     * @throws IOException If an I/O error occurs.
     */
    private InputStream getTranscodedInputStream(MusicFile musicFile, Transcoding transcoding, TranscodeScheme transcodeScheme)
            throws IOException {
        TranscodeInputStream in = createTranscodeInputStream(transcoding.getStep1(), transcodeScheme, musicFile, null);

        if (transcoding.getStep2() != null) {
            in = createTranscodeInputStream(transcoding.getStep2(), transcodeScheme, musicFile, in);
        }

        if (transcoding.getStep3() != null) {
            in = createTranscodeInputStream(transcoding.getStep3(), transcodeScheme, musicFile, in);
        }

        return in;
    }

    /**
     * Creates a transcoded input stream by interpreting the given command line string.
     * This includes the following:
     * <ul>
     * <li>Splitting the command line string to an array.</li>
     * <li>Replacing occurrences of "%s" with the path of the given music file.</li>
     * <li>Replacing occurrences of "%t" with the title of the given music file.</li>
     * <li>Replacing occurrences of "%l" with the album name of the given music file.</li>
     * <li>Replacing occurrences of "%a" with the artist name of the given music file.</li>
     * <li>Replacing occurrcences of "%b" with the max bitrate from the transcode scheme.</li>
     * <li>Prepending the path of the transcoder directory if the transcoder is found there.</li>
     * </ul>
     *
     * @param command         The command line string.
     * @param transcodeScheme The transcoding (resampling) scheme. May be {@code null}.
     * @param musicFile       The music file to use when replacing "%s" etc.
     * @param in              Data to feed to the process.  May be {@code null}.
     * @return The newly created input stream.
     */
    private TranscodeInputStream createTranscodeInputStream(String command, TranscodeScheme transcodeScheme, MusicFile musicFile, InputStream in) throws IOException {
        String path = musicFile.getFile().getAbsolutePath();

        // If no transcoding scheme is specified, use 128 Kbps.
        if (transcodeScheme == null || transcodeScheme == TranscodeScheme.OFF) {
            transcodeScheme = TranscodeScheme.MAX_128;
        }

        String title = musicFile.getMetaData().getTitle();
        String album = musicFile.getMetaData().getAlbum();
        String artist = musicFile.getMetaData().getArtist();

        if (title == null) {
            title = "Unknown Song";
        }
        if (album == null) {
            title = "Unknown Album";
        }
        if (artist == null) {
            title = "Unknown Artist";
        }

        List<String> result = new LinkedList<String>(Arrays.asList(StringUtil.split(command)));
        result.set(0, getTranscodeDirectory().getPath() + File.separatorChar + result.get(0));

        File tmpFile = null;

        for (int i = 1; i < result.size(); i++) {
            String cmd = result.get(i);
            if ("%s".equals(cmd)) {

                // Work-around for filename character encoding problem on Windows.
                // Create temporary file, and feed this to the transcoder.
                if (Util.isWindows() && !StringUtils.isAsciiPrintable(path)) {
                    tmpFile = File.createTempFile("subsonic", "." + FilenameUtils.getExtension(path));
                    tmpFile.deleteOnExit();
                    FileUtils.copyFile(new File(path), tmpFile);
                    LOG.debug("Created tmp file: " + tmpFile);
                    path = tmpFile.getPath();
                }

                result.set(i, path);
            } else if ("%b".equals(cmd)) {
                result.set(i, String.valueOf(transcodeScheme.getMaxBitRate()));
            } else if ("%t".equals(cmd)) {
                result.set(i, title);
            } else if ("%l".equals(cmd)) {
                result.set(i, album);
            } else if ("%a".equals(cmd)) {
                result.set(i, artist);
            }
        }
        return new TranscodeInputStream(new ProcessBuilder(result), in, tmpFile);
    }

    /**
     * Returns an applicable transcoding for the given file and player, or <code>null</code> if no
     * transcoding should be done.
     */
    private Transcoding getTranscoding(MusicFile musicFile, Player player) {
        for (Transcoding transcoding : getTranscodingsForPlayer(player)) {
            if (transcoding.getSourceFormat().equalsIgnoreCase(musicFile.getSuffix())) {
                if (isTranscodingInstalled(transcoding)) {
                    return transcoding;
                }
            }
        }
        return null;
    }

    /**
     * Returns a downsampled input stream to the music file.
     *
     * @param transcodeScheme Contains the bitrate to downsample to.
     * @return An input stream to the downsampled music file.
     * @throws IOException If an I/O error occurs.
     */
    private InputStream getDownsampledInputStream(MusicFile musicFile, TranscodeScheme transcodeScheme) throws IOException {
        String command = settingsService.getDownsamplingCommand();
        return createTranscodeInputStream(command, transcodeScheme, musicFile, null);
    }

    /**
     * Returns whether downsampling is supported (i.e., whether LAME is installed or not.)
     *
     * @return Whether downsampling is supported.
     * @param musicFile If not null, returns whether downsampling is supported for this file.
     */
    public boolean isDownsamplingSupported(MusicFile musicFile) {
        if (musicFile != null) {
            boolean isMp3 = "mp3".equalsIgnoreCase(musicFile.getSuffix());
            if (!isMp3) {
                return false;
            }
        }

        String commandLine = settingsService.getDownsamplingCommand();
        return isTranscodingStepInstalled(commandLine);
    }

    private boolean isTranscodingInstalled(Transcoding transcoding) {
        return isTranscodingStepInstalled(transcoding.getStep1()) &&
               isTranscodingStepInstalled(transcoding.getStep2()) &&
               isTranscodingStepInstalled(transcoding.getStep3());
    }

    private boolean isTranscodingStepInstalled(String step) {
        if (StringUtils.isEmpty(step)) {
            return true;
        }
        String executable = StringUtil.split(step)[0];
        PrefixFileFilter filter = new PrefixFileFilter(executable);
        String[] matches = getTranscodeDirectory().list(filter);
        return matches != null && matches.length > 0;
    }

    /**
     * Returns the directory in which all transcoders are installed.
     */
    public File getTranscodeDirectory() {
        File dir = new File(SettingsService.getSubsonicHome(), "transcode");
        if (!dir.exists()) {
            boolean ok = dir.mkdir();
            if (ok) {
                LOG.info("Created directory " + dir);
            } else {
                LOG.warn("Failed to create directory " + dir);
            }
        }
        return dir;
    }

    public void setTranscodingDao(TranscodingDao transcodingDao) {
        this.transcodingDao = transcodingDao;
    }

    public void setSettingsService(SettingsService settingsService) {
        this.settingsService = settingsService;
    }
}
