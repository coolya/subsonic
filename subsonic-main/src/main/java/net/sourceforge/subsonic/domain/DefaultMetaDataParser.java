package net.sourceforge.subsonic.domain;

/**
 * Parses meta data by guessing artist, album and song title based on the path of the file.
 *
 * @author Sindre Mehus
 * @version $Revision: 1.2 $ $Date: 2005/03/16 23:16:53 $
 */
public class DefaultMetaDataParser extends MetaDataParser {

    /**
     * Parses meta data for the given music file.
     * @param file The music file to parse.
     * @return Meta data for the file.
     */
    public MusicFile.MetaData getMetaData(MusicFile file) {
        return new MusicFile.MetaData(guessArtist(file), guessAlbum(file), guessTitle(file), null);
    }
    
    /**
     * Returns the bit rate of this music file.
     * @return Always 0.
     */
    public int getBitRate(MusicFile file) {
        return 0;
    }
    
    /**
     * Returns whether this parser is applicable to the given file.
     * @param file The music file in question.
     * @return Whether this parser is applicable to the given file.
     */ 
    protected  boolean isApplicable(MusicFile file) {
        return file.isFile();
    }
}