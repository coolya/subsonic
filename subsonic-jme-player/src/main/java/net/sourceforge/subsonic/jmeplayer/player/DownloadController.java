package net.sourceforge.subsonic.jmeplayer.player;

import net.sourceforge.subsonic.jmeplayer.Log;
import net.sourceforge.subsonic.jmeplayer.LogFactory;
import net.sourceforge.subsonic.jmeplayer.SettingsController;
import net.sourceforge.subsonic.jmeplayer.domain.MusicDirectory;

import javax.microedition.io.Connector;
import javax.microedition.io.file.FileConnection;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * @author Sindre Mehus
 */
public class DownloadController {

    public static final int IDLE = 0;
    public static final int DOWNLOADING = 1;

    private static final Log LOG = LogFactory.create("DownloadController");
    private static final int DEFAULT_BUFFER_SIZE = 1024 * 4;

    private DownloadControllerListener listener;
    private SettingsController settingsController;
    private MonitoredInputStream input;
    private OutputStream output;
    private MusicDirectory.Entry entry;
    private int state = IDLE;
    private FileConnection connection;

    public void setListener(DownloadControllerListener listener) {
        this.listener = listener;
        listener.stateChanged(state);
        listener.bytesRead(0L);
    }

    public void download(MusicDirectory.Entry entry) {
        close();
        this.entry = entry;
        setState(DOWNLOADING);

        new Thread() {
            public void run() {
                try {
                    createMarkerFile();
                    createOutputStream();
                    createInputStream();
                    copy();
                    removeMarkerFile();
                } catch (Exception x) {
                    handleException(x);
                } finally {
                    close();
                }
            }
        }.start();
    }

    public void close() {
        LOG.debug("Closing entry " + (entry == null ? null : entry.getName()));

        if (input != null) {
            try {
                input.close();
            } catch (Exception x) {
                handleException(x);
            }
        }

        if (output != null) {
            try {
                output.close();
            } catch (Exception x) {
                handleException(x);
            }
        }

        if (connection != null) {
            try {
                connection.close();
            } catch (Exception x) {
                handleException(x);
            }
        }

        entry = null;
        input = null;
        output = null;
        listener.bytesRead(0L);
        setState(IDLE);
    }

    private void createInputStream() throws IOException {
        InputStream in;
        String url = entry.getUrl();
        LOG.info("Opening URL " + url);
        if (url.startsWith("resource:")) {
            in = getClass().getResourceAsStream(url.substring(9));
        } else {
            int player = settingsController.getPlayer();
            if (player > 0 && !settingsController.isMock()) {
                url += "&player=" + player;
            }
            in = Connector.openInputStream(url);
        }
        LOG.info("Created input stream from " + url);
        input = new MonitoredInputStream(in);
    }

    private void createOutputStream() throws IOException {
        String fileUrl = createFileURL();
        connection = (FileConnection) Connector.open(fileUrl);
        if (!connection.exists()) {
            connection.create();
            LOG.info("Created output file: " + fileUrl);
        } else {
            LOG.info("Output file already exists: " + fileUrl);
        }
        output = connection.openOutputStream(connection.fileSize());
    }

    private void createMarkerFile() throws IOException {
        String fileUrl = createFileURL() + ".tmp";
        FileConnection markerConnection = (FileConnection) Connector.open(fileUrl);
        try {
            if (!markerConnection.exists()) {
                markerConnection.create();
                LOG.info("Created marker file: " + fileUrl);
            } else {
                LOG.info("Marker file already exists: " + fileUrl);
            }
        } finally {
            if (markerConnection != null) {
                try {
                    markerConnection.close();
                } catch (Exception x) {
                    handleException(x);
                }
            }
        }
    }

    private void removeMarkerFile() throws IOException {
        String fileUrl = createFileURL() + ".tmp";
        FileConnection markerConnection = (FileConnection) Connector.open(fileUrl);
        try {
            if (markerConnection.exists()) {
                markerConnection.delete();
                LOG.info("Deleted marker file: " + fileUrl);
            }
        } finally {
            if (markerConnection != null) {
                try {
                    markerConnection.close();
                } catch (Exception x) {
                    handleException(x);
                }
            }
        }
    }

    private int copy() throws IOException {
        byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];
        int count = 0;
        int n;
        while ((n = input.read(buffer)) != -1) {
            output.write(buffer, 0, n);
            count += n;
        }
        output.flush();

        LOG.info("Downloaded " + count + " bytes.");
        return count;
    }

    private String createFileURL() {
        return "file:///root1/newfile.txt";
    }


    public synchronized int getState() {
        return state;
    }

    private synchronized void setState(int state) {
        LOG.debug("setState(" + state + ")");
        if (this.state != state) {
            this.state = state;
            listener.stateChanged(state);
        }
    }

    private void handleException(Exception x) {
        LOG.error("Got exception.", x);
//        listener.error(x);
    }

    public void setSettingsController(SettingsController settingsController) {
        this.settingsController = settingsController;
    }

    /**
     * @author Sindre Mehus
     */
    public class MonitoredInputStream extends InputStream {

        private final InputStream in;
        private long bytesRead;

        public MonitoredInputStream(InputStream in) {
            this.in = in;
            listener.bytesRead(0L);
        }

        public int read() throws IOException {
            int n = in.read();
            if (n != -1) {
                bytesRead++;
                listener.bytesRead(bytesRead);
            } else {
                LOG.debug("End of stream reached.");
            }
            return n;
        }

        public int read(byte[] bytes) throws IOException {
            int n = in.read(bytes);
            if (n != -1) {
                bytesRead += n;
                listener.bytesRead(bytesRead);
            } else {
                LOG.debug("End of stream reached.");
            }
            return n;
        }

        public int read(byte[] bytes, int off, int len) throws IOException {
            int n = in.read(bytes, off, len);
            if (n != -1) {
                bytesRead += n;
                listener.bytesRead(bytesRead);
            } else {
                LOG.debug("End of stream reached.");
            }
            return n;
        }

        public long skip(long l) throws IOException {
            LOG.debug("Stream skipped.");
            return in.skip(l);
        }

        public int available() throws IOException {
            return in.available();
        }

        public void close() throws IOException {
            LOG.debug("Stream closed.");
            in.close();
        }

        public void mark(int i) {
            LOG.debug("Stream marked.");
            in.mark(i);
        }

        public void reset() throws IOException {
            LOG.debug("Stream reset.");
            in.reset();
        }

        public boolean markSupported() {
            return in.markSupported();
        }
    }
}