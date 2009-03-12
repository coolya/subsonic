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

import net.sourceforge.subsonic.Logger;
import net.sourceforge.subsonic.domain.Player;
import net.sourceforge.subsonic.domain.Playlist;
import net.sourceforge.subsonic.domain.TransferStatus;
import net.sourceforge.subsonic.domain.User;
import net.sourceforge.subsonic.domain.MusicFile;
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
    private SearchService searchService;
    private AudioScrobblerService audioScrobblerService;

    private MusicFile lastMusicFileWithError;

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

        lastMusicFileWithError = null;
        stop();
        if (player.getPlaylist().getStatus() == Playlist.Status.PLAYING) {
            LOG.info("Starting jukebox player on behalf of " + player.getUsername());
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

    /**
     * Invoked from JukeboxThread if an exception occurs during playback.
     */
    private synchronized void onError(Player player, Throwable error) {
        LOG.warn("An error occurred in the jukebox player.", error);

        // Restart song, but only once (to avoid endless loops).
        MusicFile currentFile = player.getPlaylist().getCurrentFile();
        if (currentFile != null && lastMusicFileWithError != currentFile) {
            LOG.info("Restarting jukebox with song " + currentFile);
            lastMusicFileWithError = currentFile;
            thread = new JuxeboxThread(player);
            thread.start();
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

    private class JuxeboxThread extends Thread {
        private javazoom.jl.player.Player jlPlayer;
        private final Player subsonicPlayer;
        private final TransferStatus status;
        private final PlaylistInputStream in;

        public JuxeboxThread(Player subsonicPlayer) {
            this.subsonicPlayer = subsonicPlayer;
            status = statusService.createStreamStatus(subsonicPlayer);
            in = new PlaylistInputStream(subsonicPlayer, status, transcodingService, musicInfoService, audioScrobblerService, searchService);
            try {
                jlPlayer = new javazoom.jl.player.Player(in);
            } catch (Throwable x) {
                statusService.removeStreamStatus(status);
                LOG.error("Failed to create jukebox player.", x);
            }
        }

        @Override
        public void run() {
            Throwable error = null;
            try {
                jlPlayer.play();
            } catch (Throwable x) {
                error = x;
            }

            statusService.removeStreamStatus(status);
            User user = securityService.getUserByName(subsonicPlayer.getUsername());
            securityService.updateUserByteCounts(user, status.getBytesTransfered(), 0L, 0L);
            IOUtils.closeQuietly(in);

            if (error != null) {
                onError(subsonicPlayer, error);
            }
        }

        public void cancel() {
            if (jlPlayer != null) {
                jlPlayer.close();
            }
        }

    }
}
