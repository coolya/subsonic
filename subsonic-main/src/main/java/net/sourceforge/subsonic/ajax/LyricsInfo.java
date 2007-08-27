package net.sourceforge.subsonic.ajax;

/**
 * Contains lyrics info for a song.
 *
 * @author Sindre Mehus
 */
public class LyricsInfo {

    private final String lyrics;
    private final String header;

    public LyricsInfo() {
        this(null, null);
    }

    public LyricsInfo(String lyrics, String header) {
        this.lyrics = lyrics;
        this.header = header;
    }

    public String getLyrics() {
        return lyrics;
    }

    public String getHeader() {
        return header;
    }
}
