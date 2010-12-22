/*
 This file is part of Subsonic.

 Subsonic is free software: you can redistribute it and/or modify
 it under the terms of the GNU General Public License as published by
 the Free Software Foundation, either version 3 of the License, or
 (at your option) any later version.

 Subsonic is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with Subsonic.  If not, see <http://www.gnu.org/licenses/>.

 Copyright 2009 (C) Sindre Mehus
 */
package net.sourceforge.subsonic.service;

import javazoom.jlgui.basicplayer.BasicController;
import javazoom.jlgui.basicplayer.BasicPlayer;
import javazoom.jlgui.basicplayer.BasicPlayerEvent;
import javazoom.jlgui.basicplayer.BasicPlayerException;
import javazoom.jlgui.basicplayer.BasicPlayerListener;
import net.sourceforge.subsonic.Logger;
import net.sourceforge.subsonic.domain.Player;
import net.sourceforge.subsonic.domain.Playlist;
import net.sourceforge.subsonic.domain.TransferStatus;
import net.sourceforge.subsonic.domain.User;
import net.sourceforge.subsonic.io.PlaylistInputStream;
import org.apache.commons.io.IOUtils;

import javax.sound.sampled.UnsupportedAudioFileException;
import javax.sound.sampled.AudioSystem;
import java.io.InputStream;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.util.Map;

/**
 * Plays music on the local audio device.
 *
 * @author Sindre Mehus
 */
public class JukeboxService {

    private static final Logger LOG = Logger.getLogger(JukeboxService.class);

    private JuxeboxPlayer jukeboxPlayer;
    private float gain = 0.85F; // Between 0.0 and 1.0

    private SecurityService securityService;
    private StatusService statusService;
    private TranscodingService transcodingService;
    private MusicInfoService musicInfoService;
    private SearchService searchService;
    private AudioScrobblerService audioScrobblerService;

    /**
     * Start playing the playlist of the given player on the local audio device.
     *
     * @param player The player in question.
     */
    public synchronized void play(Player player) {
        User user = securityService.getUserByName(player.getUsername());
        if (!user.isJukeboxRole()) {
            LOG.warn(user.getUsername() + " is not authorized for jukebox playback.");
            return;
        }

        stop();
        if (player.getPlaylist().getStatus() == Playlist.Status.PLAYING) {
            LOG.info("Starting jukebox player on behalf of " + player.getUsername());
            jukeboxPlayer = new JuxeboxPlayer(player);
            jukeboxPlayer.play();
        }
    }

    /**
     * Stop playing audio on the local device.
     */
    public synchronized void stop() {
        if (jukeboxPlayer != null) {
            jukeboxPlayer.stop();
            jukeboxPlayer = null;
        }
    }

    public synchronized float getGain() {
        return gain;
    }

    public synchronized void setGain(float gain) {
        this.gain = gain;
        if (jukeboxPlayer != null) {
            jukeboxPlayer.setGain(gain);
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

    public void setSearchService(SearchService searchService) {
        this.searchService = searchService;
    }

    public void setAudioScrobblerService(AudioScrobblerService audioScrobblerService) {
        this.audioScrobblerService = audioScrobblerService;
    }


    private class JuxeboxPlayer implements BasicPlayerListener {

        private final BasicPlayer basicPlayer;
        private final Player subsonicPlayer;
        private final TransferStatus status;
        private final InputStream in;

        public JuxeboxPlayer(Player subsonicPlayer) {
            this.subsonicPlayer = subsonicPlayer;
            status = statusService.createStreamStatus(subsonicPlayer);
            in = new BufferedInputStream(new PlaylistInputStream(subsonicPlayer, status, null, null, transcodingService, musicInfoService, audioScrobblerService, searchService));

            basicPlayer = new BasicPlayer() {
                /**
                 * Work-around for a bug occuring when playing WAV.
                 */
                @Override
                protected void initAudioInputStream(InputStream inputStream) throws UnsupportedAudioFileException, IOException {
                    m_audioFileFormat = AudioSystem.getAudioFileFormat(inputStream);
                    m_audioInputStream = AudioSystem.getAudioInputStream(inputStream);
                }
            };
            basicPlayer.addBasicPlayerListener(this);
        }

        public void play() {
            try {
                basicPlayer.open(in);
                basicPlayer.play();
                basicPlayer.setGain(gain);
            } catch (Throwable x) {
                LOG.warn("Error in BasicPlayer.play()", x);
                close();
            }
        }

        public void stop() {
            try {
                basicPlayer.stop();
            } catch (Throwable x) {
                LOG.warn("Error in BasicPlayer.stop()", x);
            } finally {
                close();
            }
        }

        public void setGain(float gain) {
            try {
                basicPlayer.setGain(gain);
            } catch (BasicPlayerException x) {
                LOG.error("Error in BasicPlayer.setGain()", x);
            }
        }

        private void close() {
            User user = securityService.getUserByName(subsonicPlayer.getUsername());
            securityService.updateUserByteCounts(user, status.getBytesTransfered(), 0L, 0L);
            statusService.removeStreamStatus(status);
            IOUtils.closeQuietly(in);
        }

        public void stateUpdated(BasicPlayerEvent event) {
            LOG.debug("stateUpdated : " + event.toString());
            if (event.getCode() == BasicPlayerEvent.STOPPED) {
                close();
            }
        }

        public void opened(Object stream, Map properties) {
            LOG.debug("opened : " + properties);
        }

        public void progress(int bytesread, long microseconds, byte[] pcmdata, Map properties) {
//            LOG.debug("progress : " + properties.toString());
        }

        public void setController(BasicController controller) {
        }
    }
}
