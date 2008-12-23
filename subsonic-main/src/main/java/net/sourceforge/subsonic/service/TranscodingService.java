package net.sourceforge.subsonic.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.filefilter.PrefixFileFilter;
import org.apache.commons.lang.StringUtils;

import net.sourceforge.subsonic.Logger;
import net.sourceforge.subsonic.dao.TranscodingDao;
import net.sourceforge.subsonic.domain.MusicFile;
import net.sourceforge.subsonic.domain.Player;
import net.sourceforge.subsonic.domain.TranscodeScheme;
import net.sourceforge.subsonic.domain.Transcoding;
import net.sourceforge.subsonic.domain.UserSettings;
import net.sourceforge.subsonic.io.InputStreamReaderThread;
import net.sourceforge.subsonic.io.TranscodeInputStream;
import net.sourceforge.subsonic.util.StringUtil;

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
            boolean downsample = transcodeScheme != TranscodeScheme.OFF;
            Integer bitRate = musicFile.getMetaData().getBitRate();

            if (downsample && bitRate != null && bitRate > transcodeScheme.getMaxBitRate()) {
                return getDownsampledInputStream(musicFile, transcodeScheme);
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
        TranscodeInputStream in = new TranscodeInputStream(createCommand(transcoding.getStep1(), transcodeScheme, musicFile), null);

        if (transcoding.getStep2() != null) {
            in = new TranscodeInputStream(createCommand(transcoding.getStep2(), transcodeScheme, null), in);
        }

        if (transcoding.getStep3() != null) {
            in = new TranscodeInputStream(createCommand(transcoding.getStep3(), transcodeScheme, null), in);
        }

        return in;
    }

    /**
     * Prepares the given command line string. This includes the following:
     * <ul>
     * <li>Splitting the command line string to an array.</li>
     * <li>Replacing occurrences of "%s" with the path of the given music file.</li>
     * <li>Replacing occurrcences of "%b" with the max bitrate from the transcode scheme.</li>
     * <li>Prepending the path of the transcoder directory if the transcoder is found there.</li>
     * </ul>
     *
     * @param command         The command line string.
     * @param transcodeScheme The transcoding (resampling) scheme. May be <code>null</code>.
     * @param musicFile       The music file to use when replacing "%s".  May be <code>null</code>.
     * @return The prepared command array.
     */
    private String[] createCommand(String command, TranscodeScheme transcodeScheme, MusicFile musicFile) {
        if (musicFile != null) {
            command = command.replace("%s", '"' + musicFile.getFile().getAbsolutePath() + '"');
        }

        // If no transcoding scheme is specified, use 128 Kbps.
        if (transcodeScheme == null || transcodeScheme == TranscodeScheme.OFF) {
            transcodeScheme = TranscodeScheme.MAX_128;
        }
        command = command.replace("%b", String.valueOf(transcodeScheme.getMaxBitRate()));

        String[] result = StringUtil.split(command);

        if (isTranscoderInstalled(result[0])) {
            result[0] = getTranscodeDirectory().getPath() + File.separatorChar + result[0];
        }

        return result;
    }

    /**
     * Returns an applicable transcoding for the given file and player, or <code>null</code> if no
     * transcoding should be done.
     */
    public Transcoding getTranscoding(MusicFile musicFile, Player player) {
        for (Transcoding transcoding : getTranscodingsForPlayer(player)) {
            if (transcoding.getSourceFormat().equalsIgnoreCase(musicFile.getSuffix())) {
                return transcoding;
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
        return new TranscodeInputStream(createCommand(command, transcodeScheme, musicFile), null);
    }

    /**
     * Returns whether downsampling is supported (i.e., whether LAME is installed or not.)
     *
     * @return Whether downsampling is supported.
     */
    public boolean isDownsamplingSupported() {
        String commandLine = settingsService.getDownsamplingCommand();
        String firstWord = StringUtils.substringBefore(commandLine, " ");
        if (StringUtils.isEmpty(firstWord)) {
            firstWord = commandLine;
        }

        String[] command = createCommand(firstWord, null, null);

        try {
            Process process = Runtime.getRuntime().exec(command);

            // Must read stdout and stderr from the process, otherwise it may block.
            new InputStreamReaderThread(process.getInputStream(), "lame stdout", false).start();
            new InputStreamReaderThread(process.getErrorStream(), "lame stderr", false).start();

            return true;

        } catch (IOException e) {
            return false;
        }
    }

    /**
     * Returns whether the given transcoder is installed in SUBSONIC_HOME/transcode.
     *
     * @return Whether the transcoder is installed.
     */
    private boolean isTranscoderInstalled(String transcoder) {
        PrefixFileFilter filter = new PrefixFileFilter(transcoder);
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
