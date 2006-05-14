package net.sourceforge.subsonic.upload;

/**
 * @author Pierre-Alexandre Losson -- http://www.telio.be/blog -- plosson@users.sourceforge.net
 */
public interface UploadListener {
    void start(String fileName);
    void bytesRead(int bytesRead);
}
