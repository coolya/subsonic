package net.sourceforge.subsonic.service;

import net.sourceforge.subsonic.Logger;
import net.sourceforge.subsonic.domain.Player;
import net.sourceforge.subsonic.domain.TransferStatus;
import net.sourceforge.subsonic.domain.User;
import net.sourceforge.subsonic.domain.Playlist;
import net.sourceforge.subsonic.io.PlaylistInputStream;
import org.apache.commons.io.IOUtils;

/**
 * Plays music on the local audio device.
 *
 * @author Sindre Mehus
 */
public class JukeboxService {

    private static final Logger LOG = Logger.getLogger(JukeboxService.class);

    private JuxeboxThread thread;
    private SecurityService securityService;
    private StatusService statusService;
    private TranscodingService transcodingService;
    private MusicInfoService musicInfoService;
    private AudioScrobblerService audioScrobblerService;

    /**
     * Start playing the playlist of the given player on the local audio device.
     *
     * @param player The player in question.
     */
    public synchronized void play(Player player) {
        stop();
        if (player.getPlaylist().getStatus() == Playlist.Status.PLAYING) {
            thread = new JuxeboxThread(player);
            thread.start();
        }
    }

    /**
     * Stop playing audio on the local device.
     */
    public synchronized void stop() {
        if (thread != null) {
            thread.cancel();
            thread = null;
        }
    }

    public void setSecurityService(SecurityService securityService) {
        this.securityService = securityService;
    }

    public void setStatusService(StatusService statusService) {
        this.statusService = statusService;
    }

    public void setTranscodingService(TranscodingService transcodingService) {
        this.transcodingService = transcodingService;
    }

    public void setMusicInfoService(MusicInfoService musicInfoService) {
        this.musicInfoService = musicInfoService;
    }

    public void setAudioScrobblerService(AudioScrobblerService audioScrobblerService) {
        this.audioScrobblerService = audioScrobblerService;
    }

    private class JuxeboxThread extends Thread {
        private Player subsonicPlayer;
        private javazoom.jl.player.Player jlPlayer;

        public JuxeboxThread(Player subsonicPlayer) {
            this.subsonicPlayer = subsonicPlayer;
        }

        @Override
        public void run() {
            User user = securityService.getUserByName(subsonicPlayer.getUsername());
            TransferStatus status = null;
            PlaylistInputStream in = null;
            try {
                LOG.info("Starting jukebox player.");
                status = statusService.createStreamStatus(subsonicPlayer);
                in = new PlaylistInputStream(subsonicPlayer, status, transcodingService, musicInfoService, audioScrobblerService, null);
                jlPlayer = new javazoom.jl.player.Player(in);
                jlPlayer.play(Integer.MAX_VALUE);
            } catch (Throwable x) {
                LOG.error("Failed to start jukebox player.", x);
            } finally {
                LOG.info("Stopping jukebox player.");
                if (status != null) {
                    statusService.removeStreamStatus(status);
                    securityService.updateUserByteCounts(user, status.getBytesTransfered(), 0L, 0L);
                }
                IOUtils.closeQuietly(in);
            }
        }

        public void cancel() {
            if (jlPlayer != null) {
                jlPlayer.close();
            }
        }
    }
}
