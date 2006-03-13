package net.sourceforge.subsonic.domain;

/**
 * Contains album info retrieved from Amazon.
 *
 * @author Sindre Mehus
 * @version $Revision: 1.1 $ $Date: 2005/10/06 19:43:27 $
 */
public class AmazonAlbumInfo implements Comparable<AmazonAlbumInfo>{

    private String[] artists = new String[0];
    private String album;
    private String[] formats = new String[0];
    private String label;
    private String releaseDate;
    private String asin;
    private String detailPageUrl;
    private String imageUrl;
    private String[] editorialReviews = new String[0];

    public String getAlbum() {
        return album;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    public String[] getArtists() {
        return artists;
    }

    public void setArtists(String[] artists) {
        this.artists = (artists == null) ? new String[0] : artists;
    }

    public String getAsin() {
        return asin;
    }

    public void setAsin(String asin) {
        this.asin = asin;
    }

    public String getDetailPageUrl() {
        return detailPageUrl;
    }

    public void setDetailPageUrl(String detailPageUrl) {
        this.detailPageUrl = detailPageUrl;
    }

    public String[] getEditorialReviews() {
        return editorialReviews;
    }

    public void setEditorialReviews(String[] editorialReviews) {
        this.editorialReviews = (editorialReviews == null) ? new String[0] : editorialReviews;
    }

    public String[] getFormats() {
        return formats;
    }

    public void setFormats(String[] formats) {
        this.formats = (formats == null) ? new String[0] : formats;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    private int getReviewLength() {
        int length = 0;
        for (String review : editorialReviews) {
            length += review.length();
        }
        return length;
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AmazonAlbumInfo that = (AmazonAlbumInfo) o;
        return !(asin != null ? !asin.equals(that.asin) : that.asin != null);
    }

    public int hashCode() {
        return (asin != null ? asin.hashCode() : 0);
    }

    /**
     * Compares this object with the specified object for order.  Returns a
     * negative integer, zero, or a positive integer as this object is less
     * than, equal to, or greater than the specified object.
     * <p/>
     * One album info is "less than" another if it is considered to be more relevant.
     * @param other the album info to be compared.
     * @return a negative integer, zero, or a positive integer as this object
     *         is less than, equal to, or greater than the specified object.
     */
    public int compareTo(AmazonAlbumInfo other) {

        if (equals(other)) {
            return 0;
        }

        // Albums with few (or none) formats (e.g., Best Of, Gold, SACD) are considered more relevant.
        int formatDiff = getFormats().length - other.getFormats().length;
        if (formatDiff != 0) {
            return formatDiff;
        }

        // Albums with longer reviews are considered more relevant.
        int reviewDiff = getReviewLength() - other.getReviewLength();
        if (reviewDiff != 0) {
            return -reviewDiff;
        }

        // Albums with image URLs are considered more relevant.
        if (getImageUrl() != null && other.getImageUrl() == null) {
            return -1;
        }
        if (getImageUrl() == null && other.getImageUrl() != null) {
            return 1;
        }

        return 0;
    }
}