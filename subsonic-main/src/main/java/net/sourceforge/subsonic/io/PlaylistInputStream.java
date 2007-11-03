package net.sourceforge.subsonic.io;

import net.sourceforge.subsonic.Logger;
import net.sourceforge.subsonic.domain.MusicFile;
import net.sourceforge.subsonic.domain.Player;
import net.sourceforge.subsonic.domain.Playlist;
import net.sourceforge.subsonic.domain.TransferStatus;
import net.sourceforge.subsonic.service.AudioScrobblerService;
import net.sourceforge.subsonic.service.MusicInfoService;
import net.sourceforge.subsonic.service.TranscodingService;

import java.io.IOException;
import java.io.InputStream;

/**
 * Implementation of {@link InputStream} which reads from a {@link Playlist}.
 *
 * @author Sindre Mehus
 */
public class PlaylistInputStream extends InputStream {

    private static final Logger LOG = Logger.getLogger(PlaylistInputStream.class);

    private final Player player;
    private final TransferStatus status;
    private final TranscodingService transcodingService;
    private final MusicInfoService musicInfoService;
    private final AudioScrobblerService audioScrobblerService;

    private MusicFile currentFile;
    private InputStream currentInputStream;

    public PlaylistInputStream(Player player, TransferStatus status, TranscodingService transcodingService,
                               MusicInfoService musicInfoService, AudioScrobblerService audioScrobblerService) {
        this.transcodingService = transcodingService;
        this.musicInfoService = musicInfoService;
        this.audioScrobblerService = audioScrobblerService;
        this.player = player;
        this.status = status;
    }

    public int read(byte[] b) throws IOException {
        prepare();
        if (currentInputStream == null) {
            return -1;
        }

        int n = currentInputStream.read(b);

        if (n == -1) {
            player.getPlaylist().next();
            close();
        } else {
            status.addBytesTransfered(n);
        }
        return n;
    }

    private void prepare() throws IOException {
        MusicFile file = player.getPlaylist().getCurrentFile();
        if (file == null) {
            close();
        } else if (!file.equals(currentFile)) {
            close();
            LOG.info("Opening new song " + file);
            updateStatistics(file);

            currentInputStream = transcodingService.getTranscodedInputStream(file, player);
            currentFile = file;
            status.setFile(currentFile.getFile());
        }
    }

    private void updateStatistics(MusicFile file) {
        try {
            MusicFile folder = file.getParent();
            if (!folder.isRoot()) {
                musicInfoService.incrementPlayCount(folder);
            }
            audioScrobblerService.register(file, player.getUsername());
        } catch (Exception x) {
            LOG.warn("Failed to update statistics for " + file, x);
        }
    }

    public void close() throws IOException {
        try {
            if (currentInputStream != null) {
                currentInputStream.close();
            }
        } finally {
            currentInputStream = null;
            currentFile = null;
        }
    }

    public int read() throws IOException {
        byte[] b = new byte[1];
        int n = read(b);
        return n == -1 ? -1 : b[0];
    }
}
