package net.sourceforge.subsonic.io;

import net.sourceforge.subsonic.*;
import org.apache.commons.io.*;

import java.io.*;

/**
 * Subclass of {@link InputStream} which provides on-the-fly transcoding.
 * Instances of <code>TranscodeInputStream</code> can be chained together, for instance to convert
 * from OGG to WAV to MP3.
 *
 * @author Sindre Mehus
 */
public class TranscodeInputStream extends InputStream {

    private static final Logger LOG = Logger.getLogger(TranscodeInputStream.class);

    private InputStream processInputStream;
    private OutputStream processOutputStream;

    /**
     * Creates a transcoded input stream by executing the given command. If <code>in</code> is not null,
     * data from it is copied to the command.
     * @param command The command to execute.
     * @param in Data to feed to the command.  May be <code>null</code>.
     * @throws IOException If an I/O error occurs.
     */
    public TranscodeInputStream(String[] command, final InputStream in) throws IOException {

        StringBuffer buf = new StringBuffer("Starting transcoder: ");
        for (String s : command) {
            buf.append('[').append(s).append("] ");
        }
        LOG.debug("Starting transcoder: " + buf);

        Process process = Runtime.getRuntime().exec(command);
        processOutputStream = process.getOutputStream();
        processInputStream = process.getInputStream();

        // Must read stderr from the process, otherwise it may block.
        final String name = command[0];
        new InputStreamReaderThread(process.getErrorStream(), name, true).start();

        // Copy data in a separate thread
        if (in != null) {
            new Thread(name + " TranscodedInputStream copy thread") {
                public void run() {
                    try {
                        IOUtils.copy(in, processOutputStream);
                    } catch (IOException x) {
                        // Intentionally ignored. Will happen if the remote player closes the stream.
                    } finally {
                        IOUtils.closeQuietly(in);
                        IOUtils.closeQuietly(processOutputStream);
                    }
                }
            }.start();
        }
    }

    /**
     * @see InputStream#read()
     */
    public int read() throws IOException {
        return processInputStream.read();
    }

    /**
     * @see InputStream#read(byte[])
     */
    public int read(byte[] b) throws IOException {
        return processInputStream.read(b);
    }

    /**
     * @see InputStream#read(byte[], int, int)
     */
    public int read(byte[] b, int off, int len) throws IOException {
        return processInputStream.read(b, off, len);
    }

    /**
     * @see InputStream#close()
     */
    public void close() throws IOException {
        IOUtils.closeQuietly(processInputStream);
        IOUtils.closeQuietly(processOutputStream);
    }
}
