package net.sourceforge.subsonic.ajax;

/**
 * Contains status for a file upload. 
 *
 * @author Sindre Mehus
 */
public class UploadInfo {

    private long bytesUploaded;
    private long bytesTotal;

    public UploadInfo(long bytesUploaded, long bytesTotal) {
        this.bytesUploaded = bytesUploaded;
        this.bytesTotal = bytesTotal;
    }

    /**
     * Returns the number of bytes uploaded.
     * @return The number of bytes uploaded.
     */
    public long getBytesUploaded() {
        return bytesUploaded;
    }

    /**
    * Returns the total number of bytes.
    * @return The total number of bytes.
    */
    public long getBytesTotal() {
        return bytesTotal;
    }

}
