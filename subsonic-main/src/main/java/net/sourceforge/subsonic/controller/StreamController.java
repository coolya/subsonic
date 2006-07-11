package net.sourceforge.subsonic.controller;

import net.sourceforge.subsonic.*;
import net.sourceforge.subsonic.domain.*;
import net.sourceforge.subsonic.service.*;
import net.sourceforge.subsonic.util.*;
import org.springframework.web.servlet.*;
import org.springframework.web.servlet.mvc.*;

import javax.servlet.http.*;
import java.io.*;
import java.util.*;

/**
 * A controller which streams the content of a {@link Playlist} to a remote
 * {@link Player}.
 *
 * @author Sindre Mehus
 */
public class StreamController implements Controller {

    private static final Logger LOG = Logger.getLogger(StreamController.class);

    private StatusService statusService;
    private PlayerService playerService;
    private PlaylistService playlistService;
    private SecurityService securityService;
    private MusicInfoService musicInfoService;
    private SettingsService settingsService;
    private TranscodingService transcodingService;

    public ModelAndView handleRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {

        TransferStatus status = null;
        StreamController.PlaylistInputStream in = null;
        String streamEndpoint = null;
        Player player = playerService.getPlayer(request, response, false, true);
        try {

            // If "playlist" request parameter is set, this is a Podcast request. In that case, create a separate
            // playlist (in order to support multiple parallell Podcast streams).
            String playlistName = request.getParameter("playlist");
            boolean isPodcast = playlistName != null;
            if (isPodcast) {
                Playlist playlist = new Playlist();
                playlistService.loadPlaylist(playlist, playlistName);
                player.setPlaylist(playlist);
                response.setContentLength((int) playlist.length());
                LOG.info("Incoming Podcast request for playlist " + playlistName);
            }

            Playlist playlist = player.getPlaylist();

            String userAgent = request.getHeader("user-agent");
            if (userAgent == null) {
                userAgent = "unknown user-agent";
            }
            streamEndpoint = player.getUsername() + '@' + request.getRemoteHost() + ':' + request.getRemotePort() + " (" + userAgent + ')';

            // Terminate any other streams to this player.
            if (!isPodcast) {
                TransferStatus[] currentStatuses = statusService.getStreamStatusesForPlayer(player);
                for (TransferStatus streamStatus : currentStatuses) {
                    streamStatus.terminate();
                }
            }

            LOG.info("Starting stream " + streamEndpoint);

            status = new TransferStatus();
            status.setPlayer(player);
            statusService.addStreamStatus(status);

            String contentType = StringUtil.getMimeType(request.getParameter("suffix"));
            response.setContentType(contentType);

            in = new StreamController.PlaylistInputStream(player, status);
            OutputStream out = response.getOutputStream();

            // Enabled SHOUTcast, if requested.
            boolean isShoutCastRequested = "1".equals(request.getHeader("icy-metadata"));
            if (isShoutCastRequested) {
                LOG.info("Enabling SHOUTcast.");
                response.setHeader("icy-metaint", "" + StreamController.ShoutCastOutputStream.META_DATA_INTERVAL);
                response.setHeader("icy-notice1", "This stream is served using Subsonic");
                response.setHeader("icy-notice2", "Subsonic - Free media streamer - subsonic.sourceforge.net");
                response.setHeader("icy-name", "Subsonic");
                response.setHeader("icy-genre", "Mixed");
                response.setHeader("icy-url", "http://subsonic.sourceforge.net/");
                out = new StreamController.ShoutCastOutputStream(out, playlist, settingsService);
            }


            final int BUFFER_SIZE = 2048;
            byte[] buf = new byte[BUFFER_SIZE];

            while (true) {

                // Check if stream has been terminated.
                if (status.isTerminated()) {
                    LOG.info("Killing stream " + streamEndpoint);
                    return null;
                }

                if (playlist.getStatus() == Playlist.Status.STOPPED) {
                    if (isPodcast) {
                        break;
                    } else {
                        sendDummy(buf, out);
                    }
                } else {

                    int n = in.read(buf);
                    if (n == -1) {
                        sendDummy(buf, out);

                    } else {
                        out.write(buf, 0, n);
                    }
                }
            }

        } finally {
            if (status != null) {
                statusService.removeStreamStatus(status);
                User user = securityService.getUserByName(player.getUsername());
                if (user != null) {
                    user.setBytesStreamed(user.getBytesStreamed() + status.getBytesTransfered());
                    securityService.updateUser(user);
                }
            }
            if (in != null) {
                in.close();
            }
            LOG.info("Stopping stream " + streamEndpoint);
        }
        return null;
    }

    /**
     * Feed the other end with some dummy data to keep it from reconnecting.
     */
    private void sendDummy(byte[] buf, OutputStream out) throws IOException {
        try {
            Thread.sleep(2000);
        } catch (InterruptedException x) {
            LOG.warn("Interrupted in sleep.", x);
        }
        Arrays.fill(buf, (byte) 0xFF);
        out.write(buf);
        out.flush();
    }

    public void setStatusService(StatusService statusService) {
        this.statusService = statusService;
    }

    public void setPlayerService(PlayerService playerService) {
        this.playerService = playerService;
    }

    public void setPlaylistService(PlaylistService playlistService) {
        this.playlistService = playlistService;
    }

    public void setSecurityService(SecurityService securityService) {
        this.securityService = securityService;
    }

    public void setMusicInfoService(MusicInfoService musicInfoService) {
        this.musicInfoService = musicInfoService;
    }

    public void setSettingsService(SettingsService settingsService) {
        this.settingsService = settingsService;
    }

    public void setTranscodingService(TranscodingService transcodingService) {
        this.transcodingService = transcodingService;
    }

    /**
     * Implementation of {@link InputStream} which reads from a {@link Playlist}.
     */
    private class PlaylistInputStream extends InputStream {
        private Player player;
        private MusicFile currentFile;
        private InputStream currentInputStream;
        private TransferStatus status;

        PlaylistInputStream(Player player, TransferStatus status) {
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

    /**
     * Implements SHOUTcast support by decorating an existing output stream.
     * Based on protocol description found on
     * <em>http://www.smackfu.com/stuff/programming/shoutcast.html</em>
     */
    private static class ShoutCastOutputStream extends OutputStream  {

        /** Number of bytes between each SHOUTcast metadata block. */
        public static final int META_DATA_INTERVAL = 20480;

        /** The underlying output stream to decorate. */
        private OutputStream out;

        /** What to write in the SHOUTcast metadata is fetched from the playlist. */
        private Playlist playlist;

        /** Keeps track of the number of bytes written (excluding meta-data).  Between 0 and {@link META_DATA_INTERVAL}. */
        private int byteCount;

        /** The last stream title sent. */
        private String previousStreamTitle;

        private SettingsService settingsService;

        /**
         * Creates a new SHOUTcast-decorated stream for the given output stream.
         * @param out The output stream to decorate.
         * @param playlist Meta-data is fetched from this playlist.
         */
        ShoutCastOutputStream(OutputStream out, Playlist playlist, SettingsService settingsService) {
            this.out = out;
            this.playlist = playlist;
            this.settingsService = settingsService;
        }

        /**
         * Writes the given byte array to the underlying stream, adding SHOUTcast meta-data as necessary.
         */
        public void write(byte[] b, int off, int len) throws IOException {

            int bytesWritten = 0;
            while (bytesWritten < len) {

                // 'n' is the number of bytes to write before the next potential meta-data block.
                int n = Math.min(len - bytesWritten, StreamController.ShoutCastOutputStream.META_DATA_INTERVAL - byteCount);

                out.write(b, off + bytesWritten, n);
                bytesWritten += n;
                byteCount += n;

                // Reached meta-data block?
                if (byteCount % StreamController.ShoutCastOutputStream.META_DATA_INTERVAL == 0) {
                    writeMetaData();
                    byteCount = 0;
                }
            }
        }

        /**
         * Writes the given byte array to the underlying stream, adding SHOUTcast meta-data as necessary.
         */
        public void write(byte[] b) throws IOException {
            write(b, 0, b.length);
        }

        /**
         * Writes the given byte to the underlying stream, adding SHOUTcast meta-data as necessary.
         */
        public void write(int b) throws IOException {
            byte[] buf = new byte[] {(byte) b};
            write(buf);
        }

        /**
         * Flushes the underlying stream.
         */
        public void flush() throws IOException {
            out.flush();
        }

        /**
         * Closes the underlying stream.
         */
        public void close() throws IOException {
            out.close();
        }

        private void writeMetaData() throws IOException {
            String streamTitle = settingsService.getWelcomeMessage();

            MusicFile musicFile = playlist.getCurrentFile();
            if (musicFile != null) {
                streamTitle = musicFile.getMetaData().getArtist() + " - " + musicFile.getMetaData().getTitle();
            }

            byte[] bytes;

            if (streamTitle.equals(previousStreamTitle)) {
                bytes = new byte[0];
            } else {
                try {
                    previousStreamTitle = streamTitle;
                    bytes = createStreamTitle(streamTitle);
                } catch (UnsupportedEncodingException x) {
                    LOG.warn("Failed to create SHOUTcast meta-data.  Ignoring.", x);
                    bytes = new byte[0];
                }
            }

            // Length in groups of 16 bytes.
            int length = bytes.length / 16;
            if (bytes.length % 16 > 0) {
                length++;
            }

            // Write the length as a single byte.
            out.write(length);

            // Write the message.
            out.write(bytes);

            // Write padding zero bytes.
            int padding = length * 16 - bytes.length;
            for (int i = 0; i < padding; i++) {
                out.write(0);
            }
        }

        private byte[] createStreamTitle(String title) throws UnsupportedEncodingException {
            // Remove any quotes from the title.
            title = title.replaceAll("'", "");

            // Convert non-ascii characters to similar ascii characters.
            for (char[] aCHAR_MAP : StreamController.ShoutCastOutputStream.CHAR_MAP) {
                title = title.replace(aCHAR_MAP[0], aCHAR_MAP[1]);
            }

            title = "StreamTitle='" + title + "';";
            return title.getBytes("US-ASCII");
        }

        /** Maps from miscellaneous accented characters to similar-looking ASCII characters. */
        private static final char[][] CHAR_MAP = {
            {'\u00C0', 'A'}, {'\u00C1', 'A'}, {'\u00C2', 'A'}, {'\u00C3', 'A'}, {'\u00C4', 'A'}, {'\u00C5', 'A'}, {'\u00C6', 'A'},
            {'\u00C8', 'E'}, {'\u00C9', 'E'}, {'\u00CA', 'E'}, {'\u00CB', 'E'}, {'\u00CC', 'I'}, {'\u00CD', 'I'}, {'\u00CE', 'I'},
            {'\u00CF', 'I'}, {'\u00D2', 'O'}, {'\u00D3', 'O'}, {'\u00D4', 'O'}, {'\u00D5', 'O'}, {'\u00D6', 'O'}, {'\u00D9', 'U'},
            {'\u00DA', 'U'}, {'\u00DB', 'U'}, {'\u00DC', 'U'}, {'\u00DF', 'B'}, {'\u00E0', 'a'}, {'\u00E1', 'a'}, {'\u00E2', 'a'},
            {'\u00E3', 'a'}, {'\u00E4', 'a'}, {'\u00E5', 'a'}, {'\u00E6', 'a'}, {'\u00E7', 'c'}, {'\u00E8', 'e'}, {'\u00E9', 'e'},
            {'\u00EA', 'e'}, {'\u00EB', 'e'}, {'\u00EC', 'i'}, {'\u00ED', 'i'}, {'\u00EE', 'i'}, {'\u00EF', 'i'}, {'\u00F1', 'n'},
            {'\u00F2', 'o'}, {'\u00F3', 'o'}, {'\u00F4', 'o'}, {'\u00F5', 'o'}, {'\u00F6', 'o'}, {'\u00F8', 'o'}, {'\u00F9', 'u'},
            {'\u00FA', 'u'}, {'\u00FB', 'u'}, {'\u00FC', 'u'},
        };
    }

}
