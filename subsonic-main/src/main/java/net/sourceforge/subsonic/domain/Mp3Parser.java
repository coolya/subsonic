package net.sourceforge.subsonic.domain;

import net.sourceforge.subsonic.*;
import org.blinkenlights.jid3.*;
import org.blinkenlights.jid3.MP3File;
import org.blinkenlights.jid3.v1.*;
import org.blinkenlights.jid3.v2.*;
import org.jaudiotagger.audio.mp3.*;
import org.apache.commons.lang.*;

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

        MusicFile.MetaData raw = getRawMetaData(file);
        String artist = raw.getArtist();
        String album  = raw.getAlbum();
        String title  = raw.getTitle();
        String year   = raw.getYear();

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
     * Parses meta data for the given music file. No guessing or reformatting is done.
     * @param file The music file to parse.
     * @return Meta data for the file.
     */
    public MusicFile.MetaData getRawMetaData(MusicFile file) {

        String artist = null;
        String album  = null;
        String title  = null;
        String year   = null;

        MediaFile mediaFile = new MP3File(file.getFile());

        try {
            ID3V2Tag tag2 = null;
            try {
                tag2 = mediaFile.getID3V2Tag();
            } catch (Exception x) {
                // Some files have corrupt ID3v2 tags.  In that case (or if no ID3V2 tags are present),
                // proceed with ID3v1.
            }
            if (tag2 != null) {
                artist = StringUtils.trimToNull(tag2.getArtist());
                album = StringUtils.trimToNull(tag2.getAlbum());
                title = StringUtils.trimToNull(tag2.getTitle());
                try {
                    year = String.valueOf(tag2.getYear());
                } catch (Exception x) {
                    // Year is not always present.
                }

            } else {

                ID3V1Tag tag1 = mediaFile.getID3V1Tag();
                if (tag1 != null) {

                    artist = StringUtils.trimToNull(tag1.getArtist());
                    album = StringUtils.trimToNull(tag1.getAlbum());
                    title = StringUtils.trimToNull(tag1.getTitle());
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

        return new MusicFile.MetaData(artist, album, title, year);
    }

    /**
     * Updates the given file with the given meta data.
     * @param file The music file to update.
     * @param metaData The new meta data.
     */
    public void setMetaData(MusicFile file, MusicFile.MetaData metaData) {

        try {
            MediaFile mediaFile = new MP3File(file.getFile());

            ID3V1Tag tag1 = mediaFile.getID3V1Tag();
            ID3V2Tag tag2 = mediaFile.getID3V2Tag();

            if (tag1 == null) {
                tag1 = new ID3V1_1Tag();
            }
            if (tag2 == null) {
                tag2 = new ID3V2_3_0Tag();
            }

            String artist = metaData.getArtist() == null ? "" : metaData.getArtist();
            String album = metaData.getAlbum() == null ? "" : metaData.getAlbum();
            String title = metaData.getTitle() == null ? "" : metaData.getTitle();
            String year = metaData.getYear() == null ? "" : metaData.getYear();

            int yearInt = 0;
            if (year.length() > 0) {
                try {
                    yearInt = Integer.parseInt(year);
                } catch (NumberFormatException x) {
                    LOG.warn("Failed to parse ID3 year tag: " + year);
                }
            }

            tag1.setArtist(artist);
            tag1.setAlbum(album);
            tag1.setTitle(title);
            tag1.setYear(year);

            tag2.setArtist(artist);
            tag2.setAlbum(album);
            tag2.setTitle(title);
            if (yearInt != 0) {
                tag2.setYear(yearInt);
            } else if (tag2 instanceof ID3V2_3_0Tag) {
                ((ID3V2_3_0Tag) tag2).removeTYERTextInformationFrame();
            }

            mediaFile.setID3Tag(tag1);
            mediaFile.setID3Tag(tag2);
            mediaFile.sync();
        } catch (ID3Exception x) {
            LOG.warn("Failed to update ID3 tags for file " + file, x);
            throw new RuntimeException("Failed to update ID3 tags for file " + file + ". " + x.getMessage(), x);
        }
    }

    /**
     * Returns the bit rate of this music file.
     * @return The bit rate in kilobits per second, or 0 if the bit rate can't be resolved. If variable bitrate (VBR) is used,
     * a negative bitrate is returned.
     */
    public int getBitRate(MusicFile file) {
        try {
            MP3AudioHeader header = new MP3AudioHeader(file.getFile());
            String bitRate = header.getBitRate();
            if (header.isVariableBitRate()) {
                return -1 * Integer.valueOf(bitRate.replace("~", ""));
            }
            return Integer.valueOf(bitRate);
        } catch (Exception x ) {
            LOG.warn("Failed to resolve bit rate for " + file, x);
            return 0;
        }
    }

    /**
     * Returns whether this parser is applicable to the given file.
     * @param file The music file in question.
     * @return Whether this parser is applicable to the given file.
     */
    public boolean isApplicable(MusicFile file) {
        return file.isFile() && file.getName().toUpperCase().endsWith(".MP3");
    }
}