package net.sourceforge.subsonic.service;

import net.sourceforge.subsonic.domain.*;
import net.sourceforge.subsonic.*;

import java.util.*;
import java.util.concurrent.*;

/**
 * Provides services for "audioscrobbling", which is the process of
 * registering what songs are played at www.last.fm.
 *
 * @author Sindre Mehus
 */
public class AudioScrobblerService {
    private static final Logger LOG = Logger.getLogger(AudioScrobblerService.class);

    private RegistrationThread thread;

    private LinkedBlockingQueue<RegistrationData> queue = new LinkedBlockingQueue<RegistrationData>();
    private static final int MAX_PENDING_REGISTRATION = 5000;

    /**
     * Registers the given music file at www.last.fm. This method returns immediately, the actual registration is done
     * by a separate thread.
     *
     * @param musicFile The music file to register.
     * @param user The user which played the music file.
     */
    public synchronized void register(MusicFile musicFile, User user) {

        if (thread == null) {
            thread = new RegistrationThread();
            thread.start();
        }

        if (queue.size() >= MAX_PENDING_REGISTRATION) {
            LOG.warn("AudioScrobbler queue is full. Ignoring " + musicFile);
            return;
        }

        try {
            queue.put(createRegistrationData(musicFile, user));
        } catch (InterruptedException x) {
            LOG.warn("Interrupted while queuing AudioScrobbler registration.", x);
        }

    }

    private RegistrationData createRegistrationData(MusicFile musicFile, User user) {
        return null;
    }

    private void scrobble(RegistrationData registrationData) {

    }

    private class RegistrationThread extends Thread {
        private RegistrationThread() {
            super("AudioScrobbler Registration");
        }

        public void run() {
            while (true) {
                try {
                    scrobble(queue.take());
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

        }

    }



    private static class RegistrationData {
        private String username;
        private String password;
        private String artist;
        private String album;
        private String title;
        private Date time;
    }
}