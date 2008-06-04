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

    private PlayerControllerListener listener;
    private MusicDirectory.Entry[] entries;
    private Player player;
    private MonitoredInputStream input;
    private int state;
    private boolean busy;


    public void setListener(PlayerControllerListener listener) {

        /* TODO: Send events when:
        o State changes.
        o Song changes.
        o Busy state changes.
        o An error occurs?
        */
        this.listener = listener;
    }

    public void setEntries(MusicDirectory.Entry[] entries) {
        this.entries = entries;
    }

    public void clear() {
    }

    public MusicDirectory.Entry getCurrent() {
        return null;
    }

    public void play() {
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

    public void pause() {
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

    public void resume() {
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

//    public void stop() {
//    }

    //

    public void next() {
    }

    public void previous() {
    }

    public int getState() {
        return 0;
    }

    private void setState(int state) {
        this.state = state;
        // TODO: Notify listener
    }

    public long getBytesRead() {
        return 0L;
    }

    private synchronized void setBusy(boolean busy) {
        this.busy = busy;
    }

    public synchronized boolean isBusy() {
        return busy;
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
        // TODO
        x.printStackTrace();
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
    }

    public void playerUpdate(Player player, String event, Object eventData) {

        if (PlayerListener.STARTED.equals(event)) {
            setState(PLAYING);
        } else if (PlayerListener.END_OF_MEDIA.equals(event)) {
            // TODO
        } else {
            System.out.println("Got event: " + event);
        }
    }
}
