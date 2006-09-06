package net.sourceforge.subsonic.domain;

/**
 * Enumeration of cover art schemes. Each value contains a size, which indicates how big the
 * scaled covert art images should be.
 *
 * @author Sindre Mehus
 * @version $Revision: 1.3 $ $Date: 2005/06/15 18:10:40 $
 */
public enum CoverArtScheme {

    OFF(0),
    SMALL(70),
    MEDIUM(100),
    LARGE(150);

    private int size;

    CoverArtScheme(int size) {
        this.size = size;
    }

    /**
     * Returns the covert art size for this scheme.
     * @return the covert art size for this scheme.
     */
    public int getSize() {
        return size;
    }
}
