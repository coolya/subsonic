package net.sourceforge.subsonic.upload;

/**
 * Extension of Commons FileUpload for monitoring the upload progress.
 *
 * @author Pierre-Alexandre Losson -- http://www.telio.be/blog -- plosson@users.sourceforge.net
 */
public interface UploadListener {
    void start(String fileName);
    void bytesRead(long bytesRead);
}
