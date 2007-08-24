package net.sourceforge.subsonic.domain;

/**
 * Parses meta data by guessing artist, album and song title based on the path of the file.
 *
 * @author Sindre Mehus
 */
public class DefaultMetaDataParser extends MetaDataParser {

    /**
     * Parses meta data for the given music file. No guessing or reformatting is done.
     *
     * @param file The music file to parse.
     * @return Meta data for the file.
     */
    public MusicFile.MetaData getRawMetaData(MusicFile file) {
        MusicFile.MetaData metaData = getBasicMetaData(file);
        metaData.setArtist(guessArtist(file));
        metaData.setAlbum(guessAlbum(file));
        metaData.setTitle(guessTitle(file));
        return metaData;
    }

    /**
     * Updates the given file with the given meta data.
     * This method has no effect.
     *
     * @param file     The music file to update.
     * @param metaData The new meta data.
     */
    public void setMetaData(MusicFile file, MusicFile.MetaData metaData) {
    }

    /**
     * Returns whether this parser supports tag editing (using the {@link #setMetaData} method).
     *
     * @return Always false.
     */
    public boolean isEditingSupported() {
        return false;
    }

    /**
     * Returns whether this parser is applicable to the given file.
     *
     * @param file The music file in question.
     * @return Whether this parser is applicable to the given file.
     */
    public boolean isApplicable(MusicFile file) {
        return file.isFile();
    }
}