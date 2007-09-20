package net.sourceforge.subsonic.domain;

import entagged.audioformats.AudioFile;
import entagged.audioformats.AudioFileIO;
import entagged.audioformats.Tag;
import entagged.audioformats.mp3.util.id3frames.ApicId3Frame;
import entagged.audioformats.generic.TagField;
import net.sourceforge.subsonic.Logger;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Parses meta data from audio files using the Entagged library
 * (http://entagged.sourceforge.net/)
 *
 * @author Sindre Mehus
 */
public class EntaggedParser extends MetaDataParser {

    /** Tags supported by id3v1. */
    public static final String[] DEFAULT_GENRES = Tag.DEFAULT_GENRES;

    private static final Logger LOG = Logger.getLogger(EntaggedParser.class);
    private static final Pattern GENRE_PATTERN = Pattern.compile("\\((\\d+)\\)");

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
            AudioFile audioFile = AudioFileIO.read(file.getFile());
            Tag tag = audioFile.getTag();
            metaData.setArtist(StringUtils.trimToNull(tag.getFirstArtist()));
            metaData.setAlbum(StringUtils.trimToNull(tag.getFirstAlbum()));
            metaData.setTitle(StringUtils.trimToNull(tag.getFirstTitle()));
            metaData.setYear(StringUtils.trimToNull(tag.getFirstYear()));
            metaData.setGenre(mapGenre(StringUtils.trimToNull(tag.getFirstGenre())));

            try {
                int track = Integer.parseInt(StringUtils.trimToNull(tag.getFirstTrack()));
                if (track > 0) {
                    metaData.setTrackNumber(track);
                }
            } catch (NumberFormatException x) { /* Ignored */ }

            metaData.setVariableBitRate(audioFile.isVbr());
            metaData.setBitRate(audioFile.getBitrate());
            metaData.setDuration(audioFile.getLength());

        } catch (Throwable x) {
            LOG.warn("Error when parsing tags in " + file, x);
        }

        return metaData;
    }

    /**
     * Sometimes the genre is returned as "(17)", instead of "Rock".  This method
     * maps the genre ID to the corresponding text.
     */
    private String mapGenre(String genre) {
        if (genre == null) {
            return null;
        }
        Matcher matcher = GENRE_PATTERN.matcher(genre);
        if (matcher.matches()) {
            int genreId = Integer.parseInt(matcher.group(1));
            if (genreId >= 0 && genreId < DEFAULT_GENRES.length) {
                return DEFAULT_GENRES[genreId];
            }
        }

        return genre;
    }

    /**
     * Updates the given file with the given meta data.
     *
     * @param file     The music file to update.
     * @param metaData The new meta data.
     */
    @Override
    public void setMetaData(MusicFile file, MusicFile.MetaData metaData) {

        try {
            AudioFile audioFile = AudioFileIO.read(file.getFile());
            Tag tag = audioFile.getTag();

            tag.setArtist(StringUtils.trimToEmpty(metaData.getArtist()));
            tag.setAlbum(StringUtils.trimToEmpty(metaData.getAlbum()));
            tag.setTitle(StringUtils.trimToEmpty(metaData.getTitle()));
            tag.setYear(StringUtils.trimToEmpty(metaData.getYear()));
            tag.setGenre(StringUtils.trimToEmpty(metaData.getGenre()));
            Integer track = metaData.getTrackNumber();
            tag.setTrack(track == null ? "" : String.valueOf(track));

            audioFile.commit();

        } catch (Throwable x) {
            LOG.warn("Failed to update tags for file " + file, x);
            throw new RuntimeException("Failed to update tags for file " + file + ". " + x.getMessage(), x);
        }
    }

    /**
     * Returns whether this parser supports tag editing (using the {@link #setMetaData} method).
     *
     * @return Always true.
     */
    @Override
    public boolean isEditingSupported() {
        return true;
    }

    /**
     * Returns whether this parser is applicable to the given file.
     *
     * @param file The music file in question.
     * @return Whether this parser is applicable to the given file.
     */
    @Override
    public boolean isApplicable(MusicFile file) {
        if (!file.isFile()) {
            return false;
        }

        String extension = FilenameUtils.getExtension(file.getName()).toLowerCase();

        return extension.equals("mp3") ||
               extension.equals("ogg") ||
               extension.equals("flac") ||
               extension.equals("wav") ||
               extension.equals("mpc") ||
               extension.equals("mp+") ||
               extension.equals("ape") ||
               extension.equals("wma");
    }

    /**
     * Returns whether cover art image data is available in the given file.
     *
     * @param file The music file.
     * @return Whether cover art image data is available.
     */
    public boolean isImageAvailable(MusicFile file) {
        try {
            return getAPICFrame(file) != null;
        } catch (Throwable x) {
            LOG.warn("Failed to parse APIC frame for " + file, x);
            return false;
        }
    }

    /**
     * Returns the cover art image data embedded in the given file.
     *
     * @param file The music file.
     * @return The embedded cover art image data, or <code>null</code> if not available.
     */
    public byte[] getImageData(MusicFile file) {
        try {
            ApicId3Frame apic = getAPICFrame(file);
            return apic.getData();
        } catch (Throwable x) {
            LOG.warn("Failed to parse APIC frame for " + file, x);
            return null;
        }
    }

    @SuppressWarnings({"unchecked"})
    private ApicId3Frame getAPICFrame(MusicFile file) throws Exception {
        AudioFile audioFile = AudioFileIO.read(file.getFile());
        List<TagField> list = audioFile.getTag().get("APIC");

        if (list.isEmpty()) {
            return null;
        }

        TagField field = list.get(0);
        return field instanceof ApicId3Frame ? (ApicId3Frame) field : null;
    }
}
