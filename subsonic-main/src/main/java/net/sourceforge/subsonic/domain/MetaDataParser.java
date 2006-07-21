package net.sourceforge.subsonic.domain;

import net.sourceforge.subsonic.*;

import java.io.*;


/**
 * Parses meta data from media files.
 *
 * @author Sindre Mehus
 */
public abstract class MetaDataParser {
    private static final Logger LOG = Logger.getLogger(MetaDataParser.class);

    /**
     * Parses meta data for the given music file.
     * @param file The music file to parse.
     * @return Meta data for the file.
     */
    public abstract MusicFile.MetaData getMetaData(MusicFile file);

    /**
     * Returns whether this parser is applicable to the given file.
     * @param file The music file in question.
     * @return Whether this parser is applicable to the given file.
     */
    public abstract boolean isApplicable(MusicFile file);

    /**
     * Guesses the artist for the given music file.
     */
    public String guessArtist(MusicFile file) {
        try {
            MusicFile parent = file.getParent();
            if (parent.isRoot()) {
                return "";
            }
            MusicFile grandParent = parent.getParent();
            return grandParent.isRoot() ? "" : grandParent.getName();
        } catch (IOException x) {
            LOG.warn("Error in guessArtist()", x);
            return null;
        }
    }

    /**
     * Returns meta-data containg file size and format.
     * @param file The music file.
     * @return Meta-data containg file size and format.
     */
    protected MusicFile.MetaData getBasicMetaData(MusicFile file) {
        MusicFile.MetaData metaData = new MusicFile.MetaData();
        metaData.setFileSize(file.length());
        metaData.setFormat(file.getSuffix());
        return metaData;
    }

    /**
    * Guesses the album for the given music file.
    */
    public String guessAlbum(MusicFile file) {
        try {
            MusicFile parent = file.getParent();
            return parent.isRoot() ? "" : parent.getName();
        } catch (IOException x) {
            LOG.warn("Error in guessAlbum()", x);
            return null;
        }
    }

    /**
     * Guesses the title for the given music file.
     */
    public String guessTitle(MusicFile file) {
        return removeTrackNumberFromTitle(file.getNameWithoutSuffix());
    }

    /**
     * Removes any prefixed track number from the given title string.
     * @param title The title with or without a prefixed track number, e.g., "02 - Back In Black".
     * @return The title with the track number removed, e.g., "Back In Black".
     */
    protected String removeTrackNumberFromTitle(String title) {
        title = title.trim();
        String result = title.replaceFirst("^\\d{2}[\\.\\- ]+", "");
        return result.length() == 0 ? title : result;
    }

    /**
     * Factory for creating meta-data parsers.
     */
    public static class Factory {

        private static final Factory INSTANCE = new Factory();
        private MetaDataParser[] parsers;

        private Factory() {
            parsers = new MetaDataParser[] {new Mp3Parser(), new DefaultMetaDataParser()};
        }

        /**
         * Returns the singleton instance of the factory.
         * @return The singleton instance of the factory.
         */
        public static Factory getInstance() {
            return INSTANCE;
        }

        /**
         * Returns a meta-data parser for the given music file.
         * @param file The file in question.
         * @return An applicable parser, or <code>null</code> if no parser is found.
         */
        public MetaDataParser getParser(MusicFile file) {
            for (MetaDataParser parser : parsers) {
                if (parser.isApplicable(file)) {
                    return parser;
                }
            }
            return null;
        }
    }

}