package net.sourceforge.subsonic.domain;

import net.sourceforge.subsonic.service.SearchService;

/**
 * Defines criteria used when generating random playlists.
 *
 * @author Sindre Mehus
 * @see SearchService#getRandomSongs
 */
public class RandomSearchCriteria {
    private final int count;
    private final String genre;
    private final Integer fromYear;
    private final Integer toYear;
    private final Integer musicFolderId;

    /**
     * Creates a new instance.
     *
     * @param count         Maximum number of songs to return.
     * @param genre         Only return songs of the given genre. May be <code>null</code>.
     * @param fromYear      Only return songs released after (or in) this year. May be <code>null</code>.
     * @param toYear        Only return songs released before (or in) this year. May be <code>null</code>.
     * @param musicFolderId Only return songs from this music folder. May be <code>null</code>.
     */
    public RandomSearchCriteria(int count, String genre, Integer fromYear, Integer toYear, Integer musicFolderId) {
        this.count = count;
        this.genre = genre;
        this.fromYear = fromYear;
        this.toYear = toYear;
        this.musicFolderId = musicFolderId;
    }

    public int getCount() {
        return count;
    }

    public String getGenre() {
        return genre;
    }

    public Integer getFromYear() {
        return fromYear;
    }

    public Integer getToYear() {
        return toYear;
    }

    public Integer getMusicFolderId() {
        return musicFolderId;
    }
}
