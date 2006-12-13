package net.sourceforge.subsonic.domain;

/**
 * Parses meta data from OGG Vorbis files.
 *
 * @author Sindre Mehus
 */
public class OggParser extends MetaDataParser {

    /**
     * Parses meta data for the given music file.
     *
     * @param file The music file to parse.
     * @return Meta data for the file.
     */
    public MusicFile.MetaData getMetaData(MusicFile file) {
        return null;
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
