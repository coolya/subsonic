package net.sourceforge.subsonic.controller;

import net.sourceforge.subsonic.*;
import net.sourceforge.subsonic.domain.*;
import net.sourceforge.subsonic.upload.*;
import net.sourceforge.subsonic.service.*;
import net.sourceforge.subsonic.util.*;
import org.apache.commons.fileupload.*;
import org.apache.commons.fileupload.servlet.*;
import org.apache.tools.zip.*;
import org.springframework.web.servlet.*;
import org.springframework.web.servlet.mvc.*;

import javax.servlet.http.*;
import java.io.*;
import java.util.*;

/**
 * Controller which receives uploaded files.
 *
 * @author Sindre Mehus
 */
public class UploadController extends ParameterizableViewController {

    private static final Logger LOG = Logger.getLogger(UploadController.class);

    private SecurityService securityService;
    private PlayerService playerService;
    private StatusService statusService;
    private SettingsService settingsService;
    public static final String UPLOAD_STATUS = "uploadStatus";

    protected ModelAndView handleRequestInternal(HttpServletRequest request, HttpServletResponse response) throws Exception {

        Map<String, Object> map = new HashMap<String, Object>();
        List<File> uploadedFiles = new ArrayList<File>();
        List<File> unzippedFiles = new ArrayList<File>();
        TransferStatus status = null;

        try {

            status = new TransferStatus();
            status.setPlayer(playerService.getPlayer(request, response, false, false));
            status.setBytesTotal(request.getContentLength());

            statusService.addUploadStatus(status);
            request.getSession().setAttribute(UPLOAD_STATUS, status);

            // Check that we have a file upload request
            if (!ServletFileUpload.isMultipartContent(request)) {
                throw new Exception("Illegal request.");
            }

            File dir = null;
            boolean unzip = false;

            UploadListener listener = new UploadListenerImpl(status);

            FileItemFactory factory = new MonitoredDiskFileItemFactory(listener);
            ServletFileUpload upload = new ServletFileUpload(factory);

            List items = upload.parseRequest(request);

            // First, look for "dir" and "unzip" parameters.
            for (Object o : items) {
                FileItem item = (FileItem) o;

                if (item.isFormField() && "dir".equals(item.getFieldName())) {
                    dir = new File(item.getString());
                } else if (item.isFormField() && "unzip".equals(item.getFieldName())) {
                    unzip = true;
                }
            }

            if (dir == null) {
                throw new Exception("Missing 'dir' parameter.");
            }

            // Look for file items.
            for (Object o : items) {
                FileItem item = (FileItem) o;

                if (!item.isFormField()) {
                    String fileName = item.getName();
                    if (fileName.trim().length() > 0) {

                        File targetFile = new File(dir, new File(fileName).getName());

                        if (!securityService.isUploadAllowed(targetFile)) {
                            throw new Exception("Permission denied: " + StringUtil.toHtml(targetFile.getPath()));
                        }

                        if (!dir.exists()) {
                            dir.mkdirs();
                        }

                        item.write(targetFile);
                        uploadedFiles.add(targetFile);
                        LOG.info("Uploaded " + targetFile);

                        if (unzip && targetFile.getName().toLowerCase().endsWith(".zip")) {
                            unzip(targetFile, unzippedFiles);
                        }
                    }
                }
            }

        } catch (Exception x) {
            LOG.warn("Uploading failed.", x);
            map.put("exception", x);
        } finally {
            if (status != null) {
                statusService.removeUploadStatus(status);
                request.getSession().removeAttribute(UPLOAD_STATUS);
                User user = securityService.getCurrentUser(request);
                if (user != null) {
                    user.setBytesUploaded(user.getBytesUploaded() + status.getBytesTransfered());
                    securityService.updateUser(user);
                }
            }
        }

        map.put("uploadedFiles", uploadedFiles);
        map.put("unzippedFiles", unzippedFiles);

        ModelAndView result = super.handleRequestInternal(request, response);
        result.addObject("model", map);
        return result;
    }

    private void unzip(File file, List<File> unzippedFiles) throws Exception {
        LOG.info("Unzipping " + file);

        ZipFile zipFile = new ZipFile(file);

        try {

            Enumeration entries = zipFile.getEntries();

            while (entries.hasMoreElements()) {
                ZipEntry entry = (ZipEntry) entries.nextElement();
                File entryFile = new File(file.getParentFile(), entry.getName());

                if (!entry.isDirectory()) {

                    if (!securityService.isUploadAllowed(entryFile)) {
                        throw new Exception("Permission denied: " + StringUtil.toHtml(entryFile.getPath()));
                    }

                    entryFile.getParentFile().mkdirs();
                    InputStream inputStream = null;
                    OutputStream outputStream = null;
                    try {
                        inputStream = zipFile.getInputStream(entry);
                        outputStream = new FileOutputStream(entryFile);

                        byte[] buf = new byte[8192];
                        while (true) {
                            int n = inputStream.read(buf);
                            if (n == -1) {
                                break;
                            }
                            outputStream.write(buf, 0, n);
                        }

                        LOG.info("Unzipped " + entryFile);
                        unzippedFiles.add(entryFile);
                    } finally {
                        try {inputStream.close();} catch (Exception x) {}
                        try {outputStream.close();} catch (Exception x) {}
                    }
                }
            }

            zipFile.close();
            file.delete();

        } finally {
            zipFile.close();
        }
    }

    public void setSecurityService(SecurityService securityService) {
        this.securityService = securityService;
    }

    public void setPlayerService(PlayerService playerService) {
        this.playerService = playerService;
    }

    public void setStatusService(StatusService statusService) {
        this.statusService = statusService;
    }

    public void setSettingsService(SettingsService settingsService) {
        this.settingsService = settingsService;
    }

    /**
     * Receives callbacks as the file upload progresses.
     */
    private class UploadListenerImpl implements UploadListener {
        private TransferStatus status;
        private long start;

        private UploadListenerImpl(TransferStatus status) {
            this.status = status;
            this.start = System.currentTimeMillis();
        }

        public void start(String fileName) {
            status.setFile(new File(fileName));
        }

        public void bytesRead(long bytesRead) {

            // Throttle bitrate.

            long byteCount = status.getBytesTransfered() + bytesRead;
            long bitCount = byteCount * 8L;

            float elapsedMillis = Math.max(1, System.currentTimeMillis() - start);
            float elapsedSeconds = elapsedMillis / 1000.0F;
            long maxBitsPerSecond = getBitrateLimit();

            status.setBytesTransfered(byteCount);

            if (maxBitsPerSecond > 0) {
                float sleepMillis = 1000.0F * (bitCount / maxBitsPerSecond - elapsedSeconds);
                if (sleepMillis > 0) {
                    try {
                        Thread.sleep((long) sleepMillis);
                    } catch (InterruptedException x) {
                        LOG.warn("Failed to sleep.", x);
                    }
                }
            }
        }

        private long getBitrateLimit() {
            return 1024L * settingsService.getUploadBitrateLimit() / Math.max(1, statusService.getAllUploadStatuses().length);
        }
    }

}