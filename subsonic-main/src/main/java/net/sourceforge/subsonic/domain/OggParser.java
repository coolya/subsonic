package net.sourceforge.subsonic.domain;

import adamb.vorbis.VorbisCommentHeader;
import adamb.vorbis.VorbisIO;
import adamb.vorbis.CommentField;
import net.sourceforge.subsonic.Logger;
import org.apache.commons.lang.StringUtils;

import java.util.Iterator;

/**
 * Parses meta data from OGG Vorbis files.
 *
 * @author Sindre Mehus
 */
public class OggParser extends MetaDataParser {

    private static final Logger LOG = Logger.getLogger(OggParser.class);

    /**
     * Parses meta data for the given music file. No guessing or reformatting is done.
     *
     * @param file The music file to parse.
     * @return Meta data for the file.
     */
    public MusicFile.MetaData getRawMetaData(MusicFile file) {
        MusicFile.MetaData metaData = getBasicMetaData(file);
        try {
            VorbisCommentHeader header = VorbisIO.readComments(file.getFile());
            for (CommentField field : header.fields) {
                if (StringUtils.equalsIgnoreCase("ARTIST", field.name)) {
                    metaData.setArtist(StringUtils.trimToNull(field.value));
                }
                else if (StringUtils.equalsIgnoreCase("ALBUM", field.name)) {
                    metaData.setAlbum(StringUtils.trimToNull(field.value));
                }
                else if (StringUtils.equalsIgnoreCase("TITLE", field.name)) {
                    metaData.setTitle(StringUtils.trimToNull(field.value));
                }
                else if (StringUtils.equalsIgnoreCase("GENRE", field.name)) {
                    metaData.setGenre(StringUtils.trimToNull(field.value));
                }
                else if (StringUtils.equalsIgnoreCase("DATE", field.name)) {
                    metaData.setYear(StringUtils.trimToNull(field.value));
                }
                else if (StringUtils.equalsIgnoreCase("TRACKNUMBER", field.name)) {
                    try {
                        metaData.setTrackNumber(Integer.parseInt(StringUtils.trimToNull(field.value)));
                    } catch (Exception x) {
                        LOG.warn("Failed to parse track number for " + file, x);
                    }
                }
            }

        } catch (Throwable x) {
            LOG.warn("Error when parsing OGG tags in " + file, x);
        }
        return metaData;
    }

    /**
     * Updates the given file with the given meta data.
     *
     * @param file     The music file to update.
     * @param metaData The new meta data.
     */
    public void setMetaData(MusicFile file, MusicFile.MetaData metaData) {
        try {
            VorbisCommentHeader header = VorbisIO.readComments(file.getFile());
            setField(header, "ARTIST", metaData.getArtist());
            setField(header, "ALBUM", metaData.getAlbum());
            setField(header, "TITLE", metaData.getTitle());
            setField(header, "DATE", metaData.getYear());

            String track = metaData.getTrackNumber() == null ? null : String.valueOf(metaData.getTrackNumber());
            setField(header, "TRACKNUMBER", track);
            VorbisIO.writeComments(file.getFile(), header);

        } catch (Throwable x) {
            LOG.warn("Failed to update OGG Vorbis tags for file " + file, x);
            throw new RuntimeException("Failed to update OGG Vorbis tags for file " + file + ". " + x.getMessage(), x);
        }
    }

    private void setField(VorbisCommentHeader header, String name, String value) {
        for (Iterator<CommentField> iterator = header.fields.iterator(); iterator.hasNext();) {
            CommentField field = iterator.next();
            if (StringUtils.equalsIgnoreCase(name, field.name)) {
                if (value == null) {
                    iterator.remove();
                } else {
                    field.value = value;
                }
                return;
            }
        }

        if (value != null) {
            header.fields.add(new CommentField(name, value));
        }
    }

    /**
     * Returns whether this parser supports tag editing (using the {@link #setMetaData} method).
     *
     * @return Always true.
     */
    public boolean isEditingSupported() {
        return true;
    }

    /**
     * Returns whether this parser is applicable to the given file.
     *
     * @param file The music file in question.
     * @return Whether this parser is applicable to the given file.
     */
    public boolean isApplicable(MusicFile file) {
        return file.isFile() && file.getName().toUpperCase().endsWith(".OGG");
    }
}
