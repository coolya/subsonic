package net.sourceforge.subsonic.ajax;

/**
 * Contains info about cover art images for an album.
 *
 * @author Sindre Mehus
 */
public class CoverArtInfo {

    private final String imagePreviewUrl;
    private final String imageDownloadUrl;

    public CoverArtInfo(String imagePreviewUrl, String imageDownloadUrl) {
        this.imagePreviewUrl = imagePreviewUrl;
        this.imageDownloadUrl = imageDownloadUrl;
    }

    public String getImagePreviewUrl() {
        return imagePreviewUrl;
    }

    public String getImageDownloadUrl() {
        return imageDownloadUrl;
    }
}