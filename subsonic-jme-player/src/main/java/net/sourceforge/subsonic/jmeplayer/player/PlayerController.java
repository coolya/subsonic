package net.sourceforge.subsonic.jmeplayer.player;

import net.sourceforge.subsonic.jmeplayer.Log;
import net.sourceforge.subsonic.jmeplayer.LogFactory;
import net.sourceforge.subsonic.jmeplayer.SettingsController;
import net.sourceforge.subsonic.jmeplayer.domain.MusicDirectory;

import javax.microedition.io.Connector;
import javax.microedition.media.Manager;
import javax.microedition.media.Player;
import javax.microedition.media.PlayerListener;
import java.io.IOException;
import java.io.InputStream;

/**
 * @author Sindre Mehus
 */
public class PlayerController implements PlayerListener {

    public static final int STOPPED = 0;
    public static final int CONNECTING = 1;
    public static final int BUFFERING = 2;
    public static final int PLAYING = 3;
    public static final int PAUSED = 4;

    private static final Log LOG = LogFactory.create("PlayerController");

    private PlayerControllerListener listener;
    private SettingsController settingsController;
    private int index;
    private MusicDirectory.Entry[] entries = {};
    private Player player;
    private MonitoredInputStream input;
    private int state = STOPPED;

    public void setListener(PlayerControllerListener listener) {
        this.listener = listener;
        notifySongChanged();
        listener.stateChanged(state);
        listener.bytesRead(0L);
    }

    public synchronized void setEntries(MusicDirectory.Entry[] entries) {
        clear();
        this.entries = entries;
        notifySongChanged();
    }

    public synchronized void clear() {
        stop();
        entries = new MusicDirectory.Entry[0];
        index = 0;
        notifySongChanged();
    }

    public synchronized int size() {
        return entries.length;
    }

    public synchronized int getCurrentIndex() {
        return index;
    }

    public synchronized MusicDirectory.Entry getCurrent() {
        if (index < 0 || index >= entries.length) {
            return null;
        }

        return entries[index];
    }

    public synchronized void play() {
        if (state != STOPPED) {
            LOG.warn("Can't play() in state " + state);
            return;
        }
        setState(CONNECTING);

        new Thread() {
            public void run() {
                try {
                    createPlayer();
                    setState(BUFFERING);
                    player.start();
                } catch (Exception x) {
                    stop();
                    handleException(x);
                }
            }
        }.start();
    }

    public synchronized void pause() {
        if (state != PLAYING) {
            LOG.warn("Can't pause() in state " + state);
            return;
        }

        try {
            player.stop();
            setState(PAUSED);
        } catch (Exception x) {
            stop();
            handleException(x);
        }
    }

    public synchronized void resume() {
        if (state != PAUSED) {
            LOG.warn("Can't resume() in state " + state);
            return;
        }

        try {
            player.start();
        } catch (Exception x) {
            stop();
            handleException(x);
        }
    }

    public synchronized void stop() {
        if (state == STOPPED) {
            return;
        }

        if (player != null) {
            try {
                player.close();
            } catch (Exception x) {
                handleException(x);
            }
        }
        player = null;
        input = null;
        listener.bytesRead(0L);

        setState(STOPPED);
    }

    public synchronized void next() {
        int previousState = state;
        stop();
        if (index < entries.length - 1) {
            index++;
            notifySongChanged();
            if (previousState != STOPPED && previousState != PAUSED) {
                play();
            }
        }
    }

    public synchronized void previous() {
        int previousState = state;
        stop();
        if (index > 0) {
            index--;
            notifySongChanged();
            if (previousState != STOPPED && previousState != PAUSED) {
                play();
            }
        }
    }

    public synchronized int getState() {
        return state;
    }

    private synchronized void setState(int state) {
        if (this.state != state) {
            this.state = state;
            listener.stateChanged(state);
        }
    }

    private void notifySongChanged() {
        listener.songChanged(getCurrent());
    }

    private void handleException(Exception x) {
        LOG.error("Got exception.", x);
        listener.error(x);
    }

    private void createPlayer() throws Exception {
        MusicDirectory.Entry entry = getCurrent();
        String url = entry.getUrl();
        InputStream in;
        if (url.startsWith("resource:")) {
            in = getClass().getResourceAsStream(url.substring(9));
        } else {
            int player = settingsController.getPlayer();
            if (player > 0 && !settingsController.isMock()) {
                url += "&player=" + player;
            }
            in = Connector.openInputStream(url);
        }
        LOG.info("Opening URL " + url);
        input = new MonitoredInputStream(in);

        LOG.info("Creating player for URL " + url);
        player = Manager.createPlayer(input, entry.getContentType());
        LOG.info("Player created for URL " + url + ": " + player);

        player.addPlayerListener(this);
        notifySongChanged();
    }

    public void playerUpdate(Player player, String event, Object eventData) {

        LOG.debug("Got event '" + event + "' from player " + player);

        if (player != this.player && this.player != null) {
            LOG.warn("Got event '" + event + "' from unknown player.");
            return;
        }

        if (PlayerListener.STARTED.equals(event)) {
            setState(PLAYING);
        } else if (PlayerListener.END_OF_MEDIA.equals(event)) {
            next();
        } else if (PlayerListener.ERROR.equals(event)) {
            listener.error(new Exception("Error: " + eventData));
            next();
        }
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

        public synchronized void mark(int i) {
            LOG.debug("Stream marked.");
            in.mark(i);
        }

        public synchronized void reset() throws IOException {
            LOG.debug("Stream reset.");
            in.reset();
        }

        public boolean markSupported() {
            return in.markSupported();
        }
    }
}
