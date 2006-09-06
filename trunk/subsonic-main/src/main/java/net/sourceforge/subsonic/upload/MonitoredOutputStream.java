package net.sourceforge.subsonic.upload;

import java.io.OutputStream;
import java.io.IOException;

/**
 * Extension of Commons FileUpload for monitoring the upload progress.
 *
 * @author Pierre-Alexandre Losson -- http://www.telio.be/blog -- plosson@users.sourceforge.net
 */
public class MonitoredOutputStream extends OutputStream {
    private OutputStream target;
    private UploadListener listener;

    public MonitoredOutputStream(OutputStream target, UploadListener listener) {
        this.target = target;
        this.listener = listener;
    }

    public void write(byte[] b, int off, int len) throws IOException {
        target.write(b, off, len);
        listener.bytesRead(len);
    }

    public void write(byte[] b) throws IOException {
        target.write(b);
        listener.bytesRead(b.length);
    }

    public void write(int b) throws IOException {
        target.write(b);
        listener.bytesRead(1);
    }

    public void close() throws IOException {
        target.close();
    }

    public void flush() throws IOException {
        target.flush();
    }
}
