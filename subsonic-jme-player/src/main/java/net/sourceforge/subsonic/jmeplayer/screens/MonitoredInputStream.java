/*
 * (c) Copyright WesternGeco. Unpublished work, created 2008. All rights
 * reserved under copyright laws. This information is confidential and is
 * the trade property of WesternGeco. Do not use, disclose, or reproduce
 * without the prior written permission of the owner.
 */
package net.sourceforge.subsonic.jmeplayer.screens;

import java.io.IOException;
import java.io.InputStream;

/**
 * @author Sindre Mehus
 */
public class MonitoredInputStream extends InputStream {

    private final InputStream in;
    private long bytesRead;

    public MonitoredInputStream(InputStream in) {
        this.in = in;
    }

    public long getBytesRead() {
        return bytesRead;
    }

    public int read() throws IOException {
        int n = in.read();
        if (n != -1) {
            bytesRead++;
        }
        return n;
    }

    public int read(byte[] bytes) throws IOException {
        int n = in.read(bytes);
        if (n != -1) {
            bytesRead += n;
        }
        return n;
    }

    public int read(byte[] bytes, int off, int len) throws IOException {
        int n = in.read(bytes, off, len);
        if (n != -1) {
            bytesRead += n;
        }
        return n;
    }

    public long skip(long l) throws IOException {
        return in.skip(l);
    }

    public int available() throws IOException {
        return in.available();
    }

    public void close() throws IOException {
        in.close();
    }

    public synchronized void mark(int i) {
        in.mark(i);
    }

    public synchronized void reset() throws IOException {
        in.reset();
    }

    public boolean markSupported() {
        return in.markSupported();
    }
}
