package net.sourceforge.subsonic.domain;

/**
 * Contains media libaray statistics, including the number of artists, albums and songs.
 *
 * @author Sindre Mehus
 * @version $Revision: 1.1 $ $Date: 2005/11/17 18:29:03 $
 */
public class MediaLibraryStatistics {

    private int artistCount;
    private int albumCount;
    private int songCount;
    private long totalLengthInBytes;

    public MediaLibraryStatistics(int artistCount, int albumCount, int songCount, long totalLengthInBytes) {
        this.artistCount = artistCount;
        this.albumCount = albumCount;
        this.songCount = songCount;
        this.totalLengthInBytes = totalLengthInBytes;
    }

    public int getArtistCount() {
        return artistCount;
    }

    public int getAlbumCount() {
        return albumCount;
    }

    public int getSongCount() {
        return songCount;
    }

    public long getTotalLengthInBytes() {
        return totalLengthInBytes;
    }
}
