package net.sourceforge.subsonic.domain;

/**
 * Parses meta data by guessing artist, album and song title based on the path of the file.
 *
 * @author Sindre Mehus
 */
public class DefaultMetaDataParser extends MetaDataParser {

    /**
     * Parses meta data for the given music file.
     * @param file The music file to parse.
     * @return Meta data for the file.
     */
    public MusicFile.MetaData getMetaData(MusicFile file) {
        MusicFile.MetaData metaData = getBasicMetaData(file);
        metaData.setArtist(guessArtist(file));
        metaData.setAlbum(guessAlbum(file));
        metaData.setTitle(guessTitle(file));
        return metaData;
    }

    /**
     * Returns whether this parser is applicable to the given file.
     * @param file The music file in question.
     * @return Whether this parser is applicable to the given file.
     */
    public boolean isApplicable(MusicFile file) {
        return file.isFile();
    }
}