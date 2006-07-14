package net.sourceforge.subsonic.io;

import net.sourceforge.subsonic.*;
import net.sourceforge.subsonic.util.*;
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
    public TranscodeInputStream(String command, final InputStream in) throws IOException {
        LOG.debug("Starting transcoder: " + command);

        String[] commandArray = StringUtil.split(command);
        Process process = Runtime.getRuntime().exec(commandArray);
        processOutputStream = process.getOutputStream();
        processInputStream = process.getInputStream();

        // Must read stderr from the process, otherwise it may block.
        final String name = commandArray[0];
        new InputStreamLogger(process.getErrorStream(), name).start();

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

    /**
     * Utility class which logs everything from an input stream.
     */
    public static class InputStreamLogger extends Thread {
        private InputStream input;
        private String name;

        public InputStreamLogger(InputStream input, String name) {
            super(name + " InputStreamLogger");
            this.input = input;
            this.name = name;
        }

        public void run() {
            BufferedReader reader = null;
            try {
                reader = new BufferedReader(new InputStreamReader(input));
                for (String line = reader.readLine(); line != null; line = reader.readLine()) {
                    LOG.debug('(' + name + ") " + line);
                }
            } catch (IOException x) {
                // Intentionally ignored.
            } finally {
                IOUtils.closeQuietly(reader);
                IOUtils.closeQuietly(input);
            }
        }
    }
}
