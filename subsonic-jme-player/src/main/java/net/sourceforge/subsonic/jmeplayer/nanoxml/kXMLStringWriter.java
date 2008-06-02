package net.sourceforge.subsonic.jmeplayer.nanoxml;

import java.io.Writer;

public class kXMLStringWriter extends Writer {
    private StringBuffer buf;

    /**
     */

    public kXMLStringWriter() {
        buf = new StringBuffer();
    }

    /**
     */

    public void close() {
    }

    /**
     */

    public void flush() {
    }

    /**
     */

    public void write(int c) {
        buf.append((char) c);
    }

    /**
     */

    public void write(char b[], int off, int len) {
        buf.append(b, off, len);
    }

    /**
     */

    public void write(String str) {
        buf.append(str);
    }

    /**
     */

    public String toString() {
        return buf.toString();
    }
}
