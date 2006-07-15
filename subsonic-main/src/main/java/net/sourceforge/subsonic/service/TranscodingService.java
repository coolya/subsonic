package net.sourceforge.subsonic.service;

import net.sourceforge.subsonic.*;
import net.sourceforge.subsonic.dao.*;
import net.sourceforge.subsonic.domain.*;
import net.sourceforge.subsonic.io.*;
import org.apache.commons.io.filefilter.*;

import java.io.*;
import java.util.*;

/**
 * Provides services for transcoding media. Transcoding is the process of
 * converting an audio stream to a different format and/or bit rate. The latter is
 * also called downsampling.
 *
 * @see TranscodeInputStream
 * @author Sindre Mehus
 */
public class TranscodingService {

    private static final Logger LOG = Logger.getLogger(TranscodingService.class);

    private TranscodingDao transcodingDao;

    /**
     * Returns all transcodings. Disabled transcodings are not included.
     * @return Possibly empty array of all transcodings.
     */
    public Transcoding[] getAllTranscodings() {
        return getAllTranscodings(false);
    }

    /**
     * Returns all transcodings.
     * @param includeAll Whether disabled transcodings should be included.
     * @return Possibly empty array of all transcodings.
     */
    public Transcoding[] getAllTranscodings(boolean includeAll) {
        Transcoding[] all = transcodingDao.getAllTranscodings();
        List<Transcoding> result = new ArrayList<Transcoding>(all.length);
        for (Transcoding transcoding : all) {
            if (includeAll || transcoding.isEnabled()) {
                result.add(transcoding);
            }
        }
        return result.toArray(new Transcoding[0]);
    }

    /**
     * Returns all active transcodings for the given player. Only enabled transcodings are returned.
     * @param player The player.
     * @return All active transcodings for the player.
     */
    public Transcoding[] getTranscodingsForPlayer(Player player) {
        Transcoding[] all = transcodingDao.getTranscodingsForPlayer(player.getId());
        List<Transcoding> result = new ArrayList<Transcoding>(all.length);
        for (Transcoding transcoding : all) {
            if (transcoding.isEnabled()) {
                result.add(transcoding);
            }
        }
        return result.toArray(new Transcoding[0]);
    }

    /**
     * Sets the list of active transcodings for the given player.
     * @param player The player.
     * @param transcodingIds ID's of the active transcodings.
     */
    public void setTranscodingsForPlayer(Player player, int[] transcodingIds) {
        transcodingDao.setTranscodingsForPlayer(player.getId(), transcodingIds);
    }

    /**
     * Creates a new transcoding.
     * @param transcoding The transcoding to create.
     */
    public void createTranscoding(Transcoding transcoding) {
        transcodingDao.createTranscoding(transcoding);
    }

    /**
     * Deletes the transcoding with the given ID.
     * @param id The transcoding ID.
     */
    public void deleteTranscoding(Integer id) {
        transcodingDao.deleteTranscoding(id);
    }

    /**
     * Updates the given transcoding.
     * @param transcoding The transcoding to update.
     */
    public void updateTranscoding(Transcoding transcoding) {
        transcodingDao.updateTranscoding(transcoding);
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
     * @param player The player.
     * @return A possible transcoded or downsampled input stream.
     * @throws IOException If an I/O error occurs.
     */
    public InputStream getTranscodedInputStream(MusicFile musicFile, Player player) throws IOException {
        try {
            Transcoding transcoding = getTranscoding(musicFile, player);
            if (transcoding != null) {
                return getTranscodedInputStream(musicFile, transcoding);
            }

            TranscodeScheme transcodeScheme = player.getTranscodeScheme();
            boolean downsample = transcodeScheme != TranscodeScheme.OFF;
            int maxBitRate = transcodeScheme.getMaxBitRate();

            if (downsample && musicFile.getBitRate() > maxBitRate) {
                return getDownsampledInputStream(musicFile, maxBitRate);
            }
        } catch (Exception x) {
            LOG.warn("Failed to transcode " + musicFile + ". Using original.", x);
        }

        return new FileInputStream(musicFile.getFile());
    }

    /**
     * Returns an input stream by applying the given transcoding to the given music file.
     * @param musicFile The music file.
     * @param transcoding The transcoding to apply.
     * @return The transcoded input stream
     * @throws IOException If an I/O error occurs.
     */
    private InputStream getTranscodedInputStream(MusicFile musicFile, Transcoding transcoding) throws IOException {
        String step1 = transcoding.getStep1();
        step1 = step1.replace("%s", '"' + musicFile.getPath() + '"');
        String prefix = getTranscodeDirectory().getPath() + File.separatorChar;

        TranscodeInputStream in = new TranscodeInputStream(prefix + step1, null);

        if (transcoding.getStep2() != null) {
            in = new TranscodeInputStream(prefix + transcoding.getStep2(), in);
        }

        if (transcoding.getStep3() != null) {
            in = new TranscodeInputStream(prefix + transcoding.getStep3(), in);
        }

        return in;
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
     * @param bitRate The bitrate to downsample to.
     * @return An input stream to the downsampled music file.
     * @exception IOException If an I/O error occurs.
     */
    private InputStream getDownsampledInputStream(MusicFile musicFile, int bitRate) throws IOException {
        File lame = new File(getTranscodeDirectory(), "lame");
        String command = '"' + lame.getPath() + "\" -S -h -b " + String.valueOf(bitRate) +
                         " \"" + musicFile.getFile().getAbsolutePath() +"\" -";
        return new TranscodeInputStream(command, null);
    }

    /**
     * Returns whether downsampling is supported (i.e., whether LAME is installed or not.)
     * @return Whether downsampling is supported.
     */
    public boolean isDownsamplingSupported() {
        PrefixFileFilter filter = new PrefixFileFilter("lame");
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

}
