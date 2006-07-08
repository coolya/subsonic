package net.sourceforge.subsonic.service;

import net.sourceforge.subsonic.*;
import net.sourceforge.subsonic.dao.*;
import net.sourceforge.subsonic.domain.*;
import net.sourceforge.subsonic.io.*;
import org.apache.commons.io.filefilter.*;

import java.io.*;

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
     * Returns all transcodings.
     * @return Possibly empty array of all transcodings.
     */
    public Transcoding[] getAllTranscodings() {
        return transcodingDao.getAllTranscodings();
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
    * Returns a possibly downsampled input stream to the music file.
    * @param downsample Whether downsampling should be performed if necessary.
    * @param maxBitRate If downsampling is enabled, and the bitrate of the original file
    * is higher than this value, a downsampled input stream is returned. The bitrate of the
    * returned input stream will be at most <code>maxBitRate</code>.
    * @return An input stream to the possibly downsampled music file.
    * @exception IOException If an I/O error occurs.
    */
    public InputStream getDownsampledInputStream(MusicFile musicFile, boolean downsample, int maxBitRate) throws IOException {
        if (downsample && musicFile.getBitRate() > maxBitRate) {
            try {
                File lame = new File(getTranscodeDirectory(), "lame");

                String command = lame.getPath() + " -S -h -b " + maxBitRate + " \"" + musicFile.getFile().getAbsolutePath() + "\" -";
                return new TranscodeInputStream(command, null);
            } catch (Exception x) {
                LOG.warn("Failed to downsample " + musicFile + ". Using original.");
            }
        }

        return new FileInputStream(musicFile.getFile());
    }

    /**
     * Returns whether downsampling is supported (i.e., whether LAME is installed or not.)
     * @return Whether downsampling is supported.
     */
    public boolean isDownsamplingSupported() {
        SuffixFileFilter filter = new SuffixFileFilter("lame");
        String[] matches = getTranscodeDirectory().list(filter);
        return matches != null && matches.length > 0;
    }

    /**
     * Returns the directory in which all transcoders are installed.
     */
    private File getTranscodeDirectory() {
        return new File(SettingsService.getSubsonicHome(), "transcode");
    }

    public void setTranscodingDao(TranscodingDao transcodingDao) {
        this.transcodingDao = transcodingDao;
    }
}
