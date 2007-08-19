package net.sourceforge.subsonic.ajax;

/**
 * Contains lyrics info for a song.
 *
 * @author Sindre Mehus
 */
public class LyricsInfo {

    private final String lyrics;

    public LyricsInfo(String lyrics) {
        this.lyrics = lyrics;
    }

    public String getLyrics() {
        return lyrics;
    }
}
