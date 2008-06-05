/*
 * (c) Copyright WesternGeco. Unpublished work, created 2008. All rights
 * reserved under copyright laws. This information is confidential and is
 * the trade property of WesternGeco. Do not use, disclose, or reproduce
 * without the prior written permission of the owner.
 */
package net.sourceforge.subsonic.jmeplayer;

import net.sourceforge.subsonic.jmeplayer.domain.MusicDirectory;
import net.sourceforge.subsonic.jmeplayer.screens.MonitoredInputStream;

import javax.microedition.io.Connector;
import javax.microedition.media.Manager;
import javax.microedition.media.Player;
import javax.microedition.media.PlayerListener;
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

    private final PlayerControllerListener listener;
    private int index;
    private MusicDirectory.Entry[] entries = {};
    private Player player;
    private MonitoredInputStream input;
    private int state = STOPPED;
    private boolean busy;

    public PlayerController(PlayerControllerListener listener) {
        this.listener = listener;
        listener.busy(false);
        notifySongChanged();
        listener.stateChanged(state);
    }

    /* TODO: Send events when:
        o State changes.
        o Song changes.
        o Busy state changes.
        o An error occurs?
        */

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
        if (isBusy()) {
            System.out.println("Can't play() when busy.");
            return;
        }

        execute(new Runnable() {
            public void run() {
                try {
                    setState(CONNECTING);
                    createPlayer();

                    setState(BUFFERING);
                    player.start();

                } catch (Exception x) {
                    handleException(x);
                    setState(STOPPED);
                }
            }
        });
    }

    public synchronized void pause() {
        if (state != PLAYING) {
            System.out.println("Can't pause() in state " + state);
            return;
        }
        if (isBusy()) {
            System.out.println("Can't pause() when busy.");
            return;
        }

        execute(new Runnable() {
            public void run() {
                try {
                    player.stop();
                    setState(PAUSED);

                } catch (Exception x) {
                    handleException(x);
                }
            }
        });
    }

    public synchronized void resume() {
        if (state != PAUSED) {
            System.out.println("Can't resume() in state " + state);
            return;
        }
        if (isBusy()) {
            System.out.println("Can't resume() when busy.");
            return;
        }

        execute(new Runnable() {
            public void run() {
                try {
                    player.start();
                } catch (Exception x) {
                    handleException(x);
                }
            }
        });
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

        setState(STOPPED);
    }

    public synchronized void next() {
        if (isBusy()) {
            System.out.println("Can't next() when busy.");
            return;
        }

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
        if (isBusy()) {
            System.out.println("Can't previous() when busy.");
            return;
        }

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

    public long getBytesRead() {
        return input == null ? 0L : input.getBytesRead();
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
        setBusy(true);
        new Thread(new Runnable() {
            public void run() {
                try {
                    runnable.run();
                } finally {
                    setBusy(false);
                }
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

        if (PlayerListener.STARTED.equals(event)) {
            setState(PLAYING);
        } else if (PlayerListener.END_OF_MEDIA.equals(event)) {
            next();
        } else if (PlayerListener.ERROR.equals(event)) {
            listener.error(new Exception(eventData == null ? null : eventData.toString()));
            next();
        } else {
            System.out.println("Got event: " + event);
        }
    }
}
