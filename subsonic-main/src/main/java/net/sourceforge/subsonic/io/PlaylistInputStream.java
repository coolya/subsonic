package net.sourceforge.subsonic.io;

import net.sourceforge.subsonic.Logger;
import net.sourceforge.subsonic.domain.MusicFile;
import net.sourceforge.subsonic.domain.Player;
import net.sourceforge.subsonic.domain.Playlist;
import net.sourceforge.subsonic.domain.TransferStatus;
import net.sourceforge.subsonic.service.AudioScrobblerService;
import net.sourceforge.subsonic.service.MusicInfoService;
import net.sourceforge.subsonic.service.TranscodingService;
import net.sourceforge.subsonic.service.SearchService;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * Implementation of {@link InputStream} which reads from a {@link Playlist}.
 *
 * @author Sindre Mehus
 */
public class PlaylistInputStream extends InputStream {

    private static final Logger LOG = Logger.getLogger(PlaylistInputStream.class);

    private final Player player;
    private final TransferStatus status;
    private final SearchService searchService;
    private final TranscodingService transcodingService;
    private final MusicInfoService musicInfoService;
    private final AudioScrobblerService audioScrobblerService;

    private MusicFile currentFile;
    private InputStream currentInputStream;

    public PlaylistInputStream(Player player, TransferStatus status, TranscodingService transcodingService,
                               MusicInfoService musicInfoService, AudioScrobblerService audioScrobblerService,
                               SearchService searchService) {
        this.transcodingService = transcodingService;
        this.musicInfoService = musicInfoService;
        this.audioScrobblerService = audioScrobblerService;
        this.player = player;
        this.status = status;
        this.searchService = searchService;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int read() throws IOException {
        byte[] b = new byte[1];
        int n = read(b);
        return n == -1 ? -1 : b[0];
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int read(byte[] b) throws IOException {
        return read(b, 0, b.length);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        prepare();
        if (currentInputStream == null) {
            return -1;
        }

        int n = currentInputStream.read(b, off, len);

        // If end of song reached, skip to next song and call read() again.
        if (n == -1) {
            player.getPlaylist().next();
            close();
            return read(b, off, len);
        } else {
            status.addBytesTransfered(n);
        }
        return n;
    }

    private void prepare() throws IOException {
        Playlist playlist = player.getPlaylist();

        // If playlist is in auto-random mode, populate it with new random songs.
        if (playlist.getIndex() == -1 && playlist.getRandomSearchCriteria() != null) {
            populateRandomPlaylist(playlist);
        }

        MusicFile file = playlist.getCurrentFile();
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

    private void populateRandomPlaylist(Playlist playlist) throws IOException {
        List<MusicFile> files = searchService.getRandomSongs(playlist.getRandomSearchCriteria());
        playlist.addFiles(false, files);

        LOG.info("Recreated random playlist with " + playlist.size() + " songs.");
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

    /**
     * {@inheritDoc}
     */
    @Override
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
}
