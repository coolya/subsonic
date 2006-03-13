package net.sourceforge.subsonic.domain;

import net.sourceforge.subsonic.*;
import org.blinkenlights.jid3.*;
import org.blinkenlights.jid3.v1.*;
import org.blinkenlights.jid3.v2.*;
import org.farng.mp3.MP3File;

import java.io.*;

/**
 * Parses meta data from MP3 files.
 *
 * @author Sindre Mehus
 * @version $Revision: 1.9 $ $Date: 2005/11/27 13:29:33 $
 */
public class Mp3Parser extends MetaDataParser {
    private static final Logger LOG = Logger.getLogger(Mp3Parser.class);

    /**
     * Parses meta data for the given music file.
     * @param file The music file to parse.
     * @return Meta data for the file.
     */
    public MusicFile.MetaData getMetaData(MusicFile file) {

        String artist = null;
        String album  = null;
        String title  = null;
        String year   = null;

        MediaFile mediaFile = new org.blinkenlights.jid3.MP3File(file.getFile());

        try {
            ID3V2Tag tag2 = null;
            try {
                tag2 = mediaFile.getID3V2Tag();
            } catch (Exception x) {
                // Some files have corrupt ID3v2 tags.  In that case (or if no ID3V2 tags are present),
                // proceed with ID3v1.
            }
            if (tag2 != null) {
                artist = trim(tag2.getArtist());
                album = trim(tag2.getAlbum());
                title = trim(tag2.getTitle());
                try {
                    year = String.valueOf(tag2.getYear());
                } catch (Exception x) {
                    // Year is not always present.
                }

            } else {

                ID3V1Tag tag1 = mediaFile.getID3V1Tag();
                if (tag1 != null) {

                    artist = trim(tag1.getArtist());
                    album = trim(tag1.getAlbum());
                    title = trim(tag1.getTitle());
                    try {
                        year = String.valueOf(tag1.getYear());
                    } catch (Exception x) {
                        // Year is not always present.
                    }
                }
            }
        } catch (Exception x) {
            LOG.warn("Error when parsing MP3 tags in " + file, x);
        }

        if (artist == null) {
            artist = guessArtist(file);
        }
        if (album == null) {
            album = guessAlbum(file);
        }
        if (title == null) {
            title = guessTitle(file);
        }

        title = removeTrackNumberFromTitle(title);
        return new MusicFile.MetaData(artist, album, title, year);
    }

    /**
     * Returns the bit rate of this music file.
     * @return The bit rate in kilobits per second, or 0 if the bit rate can't be resolved.
     */
    public int getBitRate(MusicFile file) {
        RandomAccessFile randomAccessFile = null;
        try {
            MP3File mp3File = new MP3File();
            randomAccessFile = new RandomAccessFile(file.getPath(), "r");
            mp3File.seekMP3Frame(randomAccessFile);

            return mp3File.getBitRate();
        } catch (Exception x) {
            LOG.warn("Failed to resolve bit rate for " + file, x);
            return 0;
        } finally {
            try {
                randomAccessFile.close();
            } catch (Exception x) {}
        }
    }

    /**
     * Returns whether this parser is applicable to the given file.
     * @param file The music file in question.
     * @return Whether this parser is applicable to the given file.
     */
    protected  boolean isApplicable(MusicFile file) {
        return file.isFile() && file.getName().toUpperCase().endsWith(".MP3");
    }

    private String trim(String s) {
        if (s == null) {
            return null;
        }
        s = s.trim();
        return s.length() == 0 ? null : s;
    }

}