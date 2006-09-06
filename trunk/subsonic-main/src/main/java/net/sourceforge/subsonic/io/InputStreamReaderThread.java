package net.sourceforge.subsonic.io;

import net.sourceforge.subsonic.*;
import org.apache.commons.io.*;

import java.io.*;

/**
 * Utility class which reads everything from an input stream and optionally logs it.
 *
 * @see TranscodeInputStream
 * @author Sindre Mehus
 */
public class InputStreamReaderThread extends Thread {

    private static final Logger LOG = Logger.getLogger(InputStreamReaderThread.class);

    private InputStream input;
    private String name;
    private boolean log;

    public InputStreamReaderThread(InputStream input, String name, boolean log) {
        super(name + " InputStreamLogger");
        this.input = input;
        this.name = name;
        this.log = log;
    }

    public void run() {
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new InputStreamReader(input));
            for (String line = reader.readLine(); line != null; line = reader.readLine()) {
                if (log) {
                    LOG.debug('(' + name + ") " + line);
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
