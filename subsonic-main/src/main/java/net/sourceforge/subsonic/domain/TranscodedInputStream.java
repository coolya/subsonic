package net.sourceforge.subsonic.domain;

import net.sourceforge.subsonic.*;
import org.apache.commons.io.*;

import java.io.*;

/**
 * Subclass of {@link FileInputStream} which provides on-the-fly MP3 transcoding
 * using LAME.
 *
 * @author Sindre Mehus
 */
public class TranscodedInputStream extends FileInputStream {

    private static final Logger LOG = Logger.getLogger(TranscodedInputStream.class);

    private File file;
    private Process process;

    /** The longest time allowed for attempting to kill the external process. */
    private static final int MAX_KILL_TIME_SECONDS = 10;

    /**
     * Creates an input stream for the given file and bit rate.
     * @param file The file to transcode.
     * @param bitRate The bitrate in kilobits per second.
     * @return An input stream for reading the transcoded MP3 data.
     * @throws IOException If an I/O error occurs.
     */
    public static TranscodedInputStream create(final File file, final int bitRate) throws IOException {

        // Creates a temporary output file.
        final File tmpFile = File.createTempFile("subsonic_", ".mp3");
        tmpFile.deleteOnExit();

        // Start LAME as an external process.
        String[] commandArr = {"lame", "-S", "-h", "-b", String.valueOf(bitRate), file.getAbsolutePath(), tmpFile.getAbsolutePath()};
        StringBuffer command = new StringBuffer();
        for (String s : commandArr) {
            command.append(s).append(' ');
        }
        LOG.info(command.toString());

        Process process = Runtime.getRuntime().exec(commandArr);

        // Must read stdout and stderr from the process, otherwise it may block.
        new InputStreamReaderThread(process.getInputStream(), true).start();
        new InputStreamReaderThread(process.getErrorStream(), true).start();

        return new TranscodedInputStream(tmpFile, process);
    }

    /**
     * Returns whether transcoding is supported (i.e., whether LAME is installed or not.)
     * @return Whether transcoding is supported.
     */
    public static boolean isTranscodingSupported() {
        try {
            Process process = Runtime.getRuntime().exec("lame");

            // Must read stdout and stderr from the process, otherwise it may block.
            new InputStreamReaderThread(process.getInputStream(), false).start();
            new InputStreamReaderThread(process.getErrorStream(), false).start();

            return true;
        } catch (Exception x) {
            return false;
        }
    }

    /**
     * Creates a transcoded input stream.
     * @param file The transcoded file.
     * @param process The LAME process.
     * @throws IOException If an I/O error occurs.
     */
    private TranscodedInputStream(File file, Process process) throws IOException {
        super(file);
        this.file = file;
        this.process = process;
    }

    /**
     * @see InputStream#read()
     */
    public int read() throws IOException {
        byte[] b = new byte[1];
        int result = read(b);
        return result == -1 ? -1 : b[0];
    }

    /**
     * @see InputStream#read(byte[])
     */
    public int read(byte[] b) throws IOException {
        return read(b, 0, b.length);
    }

    /**
     * @see InputStream#read(byte[], int, int)
     */
    public int read(byte[] b, int off, int len) throws IOException {
        int result = super.read(b, off, len);

        // If end-of-file is reached it may be because the LAME process is lagging behind.
        // Don't return end-of-file unless the LAME process has terminated.
        if (result == -1) {
            if (isProcessRunning()) {
                LOG.debug("Waiting for more data.");
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) { /* Intentionally ignored. */ }
                return 0;
            }
        }
        return result;
    }

    /**
     * @see InputStream#close()
     */
    public void close() throws IOException {
        super.close();

        // Kill LAME process and delete temporary file.
        killProcess();
        if (file.exists()) {
            boolean b = file.delete();
            if (b) {
                LOG.debug(file + " was deleted.");
            } else {
                LOG.warn(file + " was NOT deleted.");
            }
        }
    }

    /**
     * Returns whether the LAME process is running.
     * @return Whether the LAME process is running.
     */
    private boolean isProcessRunning() {
        try {
            process.exitValue();
            return false;
        } catch (IllegalThreadStateException x) {
            return true;
        }
    }

    /**
     * Attempts to kill the LAME process.
     */
    private void killProcess() {
        int seconds = 0;
        while (isProcessRunning() && seconds < MAX_KILL_TIME_SECONDS) {
            seconds++;
            LOG.debug("Trying to kill LAME process.");
            process.destroy();
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                continue;
            }
        }

        if (isProcessRunning()) {
            LOG.warn("Failed to kill LAME process.");
        }
    }

    /**
     * Utility class which consumes an input stream.
     */
    private static class InputStreamReaderThread extends Thread {
        private InputStream input;
        private boolean log;

        private InputStreamReaderThread(InputStream input, boolean log) {
            this.input = input;
            this.log = log;
        }

        public void run() {
            BufferedReader reader = null;
            try {
                reader = new BufferedReader(new InputStreamReader(input));
                for (String line = reader.readLine(); line != null; line = reader.readLine()) {
                    if (log) {
                        LOG.debug("(LAME) " + line);
                    }
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