package net.sourceforge.subsonic.controller;

import net.sourceforge.subsonic.*;
import net.sourceforge.subsonic.domain.*;
import net.sourceforge.subsonic.service.*;
import org.apache.tools.zip.ZipEntry;
import org.apache.tools.zip.ZipOutputStream;
import org.springframework.web.servlet.mvc.*;
import org.springframework.web.servlet.*;

import javax.servlet.http.*;
import java.io.*;
import java.util.zip.*;

/**
 * A controller used for downloading files to a remote client. If the requested path refers to a file, the
 * given file is downloaded.  If the requested path refers to a directory, the entire directory (including
 * sub-directories) are downloaded as an uncompressed zip-file.
 *
 * @author Sindre Mehus
 */
public class DownloadController implements Controller {

    private static final Logger LOG = Logger.getLogger(DownloadController.class);

    private PlayerService playerService;
    private StatusService statusService;
    private SecurityService securityService;
    private PlaylistService playlistService;
    private SettingsService settingsService;

    public ModelAndView handleRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {

        TransferStatus status = null;
        try {
            status = statusService.createDownloadStatus(playerService.getPlayer(request, response, false, false));

            String path = request.getParameter("path");
            String playlistName = request.getParameter("playlist");
            String playerId = request.getParameter("player");

            if (path != null) {
                File file = new File(path);
                if (!securityService.isReadAllowed(file)) {
                    response.sendError(HttpServletResponse.SC_FORBIDDEN);
                    return null;
                }

                if (file.isFile()) {
                    downloadFile(response, status, file);
                } else {
                    downloadDirectory(response, status, file);
                }

            } else if (playlistName != null) {
                Playlist playlist = new Playlist();
                playlistService.loadPlaylist(playlist, playlistName);
                downloadPlaylist(response, status, playlist);

            } else if (playerId != null) {
                Player player = playerService.getPlayerById(playerId);
                Playlist playlist = player.getPlaylist();
                playlist.setName("Playlist");
                downloadPlaylist(response, status, playlist);
            }


        } finally {
            if (status != null) {
                statusService.removeDownloadStatus(status);
                User user = securityService.getCurrentUser(request);
                if (user != null) {
                    user.setBytesDownloaded(user.getBytesDownloaded() + status.getBytesTransfered());
                    securityService.updateUser(user);
                }
            }
        }

        return null;
    }

    /**
     * Downloads a single file.
     * @param response The HTTP response.
     * @param status The download status.
     * @param file The file to download.
     * @throws IOException If an I/O error occurs.
     */
    private void downloadFile(HttpServletResponse response, TransferStatus status, File file) throws IOException {
        DownloadController.LOG.info("Starting to download '" + file + "' to " + status.getPlayer());
        status.setFile(file);

        response.setContentType("application/x-download");
        response.setHeader("Content-Disposition", "attachment; filename=\"" + file.getName() + '\"');
        response.setContentLength((int) file.length());

        copyFileToStream(file, response.getOutputStream(), status);
        DownloadController.LOG.info("Downloaded '" + file + "' to " + status.getPlayer());
    }

    /**
     * Downloads all files in a directory (including sub-directories). The files are packed together in an
     * uncompressed zip-file.
     * @param response The HTTP response.
     * @param status The download status.
     * @param file The file to download.
     * @throws IOException If an I/O error occurs.
     */
    private void downloadDirectory(HttpServletResponse response, TransferStatus status, File file) throws IOException {
        String zipFileName = file.getName() + ".zip";
        DownloadController.LOG.info("Starting to download '" + zipFileName + "' to " + status.getPlayer());
        response.setContentType("application/x-download");
        response.setHeader("Content-Disposition", "attachment; filename=\"" + zipFileName + '"');

        ZipOutputStream out = new ZipOutputStream(response.getOutputStream());
        out.setMethod(ZipOutputStream.STORED);  // No compression.

        zip(out, file.getParentFile(), file, status);
        out.close();
        DownloadController.LOG.info("Downloaded '" + zipFileName + "' to " + status.getPlayer());
    }

    /**
     * Downloads all files in a playlist.  The files are packed together in an
     * uncompressed zip-file.
     * @param response The HTTP response.
     * @param status The download status.
     * @param playlist The playlist to download.
     * @throws IOException If an I/O error occurs.
     */
    private void downloadPlaylist(HttpServletResponse response, TransferStatus status, Playlist playlist) throws IOException {
        String zipFileName = playlist.getName().replaceAll("(\\.m3u)|(\\.pls)", "") + ".zip";
        DownloadController.LOG.info("Starting to download '" + zipFileName + "' to " + status.getPlayer());
        response.setContentType("application/x-download");
        response.setHeader("Content-Disposition", "attachment; filename=\"" + zipFileName + '"');

        ZipOutputStream out = new ZipOutputStream(response.getOutputStream());
        out.setMethod(ZipOutputStream.STORED);  // No compression.

        MusicFile[] musicFiles = playlist.getFiles();
        for (MusicFile musicFile : musicFiles) {
            zip(out, musicFile.getParent().getFile(), musicFile.getFile(), status);
        }

        out.close();
        DownloadController.LOG.info("Downloaded '" + zipFileName + "' to " + status.getPlayer());
    }

    /**
     * Utility method for writing the content of a given file to a given output stream.
     * @param file The file to copy.
     * @param out The output stream to write to.
     * @param status The download status.
     * @throws IOException If an I/O error occurs.
     */
    private void copyFileToStream(File file, OutputStream out, TransferStatus status) throws IOException {
        DownloadController.LOG.info("Downloading " + file + " to " + status.getPlayer());

        final int bufferSize = 16 * 1024; // 16 Kbit
        InputStream in = new BufferedInputStream(new FileInputStream(file), bufferSize);

        try {
            byte[] buf = new byte[bufferSize];
            long bitrateLimit = 0;
            long lastLimitCheck = 0;

            while (true) {
                long before = System.currentTimeMillis();
                int n = in.read(buf);
                if (n == -1) {
                    break;
                }
                out.write(buf, 0, n);
                status.addBytesTransfered(n);
                long after = System.currentTimeMillis();

                // Calculate bitrate limit every 5 seconds.
                if (after - lastLimitCheck > 5000) {
                    bitrateLimit = 1024L * settingsService.getDownloadBitrateLimit() /
                                   Math.max(1, statusService.getAllDownloadStatuses().length);
                    lastLimitCheck = after;
                }

                // Sleep for a while to throttle bitrate.
                if (bitrateLimit != 0) {
                    long sleepTime = 8L * 1000 * bufferSize / bitrateLimit - (after - before);
                    if (sleepTime > 0L) {
                        try {
                            Thread.sleep(sleepTime);
                        } catch (Exception x) {
                            DownloadController.LOG.warn("Failed to sleep.", x);
                        }
                    }
                }
            }
        } finally {
            in.close();
        }
    }

    /**
     * Writes a file or a directory structure to a zip output stream. File entries in the zip file are relative
     * to the given root.
     * @param out The zip output stream.
     * @param root The root of the directory structure.  Used to create path information in the zip file.
     * @param file The file or directory to zip.
     * @param status The download status.
     * @throws IOException If an I/O error occurs.
     */
    private void zip(ZipOutputStream out, File root, File file, TransferStatus status) throws IOException {
        String zipName = file.getCanonicalPath().substring(root.getCanonicalPath().length() + 1);

        if (file.isFile()) {
            status.setFile(file);

            ZipEntry zipEntry = new ZipEntry(zipName);
            zipEntry.setSize(file.length());
            zipEntry.setCompressedSize(file.length());
            zipEntry.setCrc(computeCrc(file));

            out.putNextEntry(zipEntry);
            copyFileToStream(file, out, status);
            out.closeEntry();

        } else {
            ZipEntry zipEntry = new ZipEntry(zipName + '/');
            zipEntry.setSize(0);
            zipEntry.setCompressedSize(0);
            zipEntry.setCrc(0);

            out.putNextEntry(zipEntry);
            out.closeEntry();

            File[] children = file.listFiles();
            for (File child : children) {
                zip(out, root, child, status);
            }
        }
    }

    /**
     * Computes the CRC checksum for the given file.
     * @param file The file to compute checksum for.
     * @return A CRC32 checksum.
     * @throws IOException If an I/O error occurs.
     */
    private long computeCrc(File file) throws IOException {
        CRC32 crc = new CRC32();
        InputStream in = new FileInputStream(file);

        try {

            byte[] buf = new byte[8192];
            int n = in.read(buf);
            while (n != -1) {
                crc.update(buf, 0, n);
                n = in.read(buf);
            }

        } finally {
            in.close();
        }

        return crc.getValue();
    }

    public void setPlayerService(PlayerService playerService) {
        this.playerService = playerService;
    }

    public void setStatusService(StatusService statusService) {
        this.statusService = statusService;
    }

    public void setSecurityService(SecurityService securityService) {
        this.securityService = securityService;
    }

    public void setPlaylistService(PlaylistService playlistService) {
        this.playlistService = playlistService;
    }

    public void setSettingsService(SettingsService settingsService) {
        this.settingsService = settingsService;
    }
}
