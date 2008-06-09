package net.sourceforge.subsonic.jmeplayer.player;

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

    private PlayerControllerListener listener;
    private int index;
    private MusicDirectory.Entry[] entries = {};
    private Player player;
    private MonitoredInputStream input;
    private int state = STOPPED;
    private boolean busy;

    public void setListener(PlayerControllerListener listener) {
        this.listener = listener;
        listener.busy(false);
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
            System.out.println("Can't play() in state " + state);
            return;
        }
        setState(CONNECTING);

        execute(new Runnable() {
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
        });
    }

    public synchronized void pause() {
        if (state != PLAYING) {
            System.out.println("Can't pause() in state " + state);
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
            System.out.println("Can't resume() in state " + state);
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

        try {
            player.stop();
            player.close();
        } catch (Exception x) {
            handleException(x);
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

    private synchronized void setBusy(boolean busy) {
        this.busy = busy;
        listener.busy(busy);
    }

    public synchronized boolean isBusy() {
        return busy;
    }

    private void notifySongChanged() {
        listener.songChanged(getCurrent());
    }

    private void execute(final Runnable runnable) {
        // TODO: Throw exception if busy?
//        setBusy(true);
        new Thread(new Runnable() {
            public void run() {
//                try {
                runnable.run();
//                } finally {
//                    setBusy(false);
//                }
            }
        }).start();
    }

    private void handleException(Exception x) {
        listener.error(x);
    }

    private void createPlayer() throws Exception {
        MusicDirectory.Entry entry = getCurrent();
        String url = entry.getUrl();
        InputStream in;
        if (url.startsWith("resource:")) {
            in = getClass().getResourceAsStream(url.substring(9));
        } else {
            in = Connector.openInputStream(url);
        }

        input = new MonitoredInputStream(in);
        player = Manager.createPlayer(input, entry.getContentType());
        player.addPlayerListener(this);

        notifySongChanged();
    }

    public void playerUpdate(Player player, String event, Object eventData) {

        if (player != this.player && this.player != null) {
            System.out.println("Got event '" + event + "' from unknown player.");
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
            }
            return n;
        }

        public int read(byte[] bytes) throws IOException {
            int n = in.read(bytes);
            if (n != -1) {
                bytesRead += n;
                listener.bytesRead(bytesRead);
            }
            return n;
        }

        public int read(byte[] bytes, int off, int len) throws IOException {
            int n = in.read(bytes, off, len);
            if (n != -1) {
                bytesRead += n;
                listener.bytesRead(bytesRead);
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
}
