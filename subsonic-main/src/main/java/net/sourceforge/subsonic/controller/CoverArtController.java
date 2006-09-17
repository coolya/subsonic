package net.sourceforge.subsonic.controller;

import net.sourceforge.subsonic.*;
import net.sourceforge.subsonic.service.*;
import org.apache.commons.codec.digest.*;
import org.apache.commons.io.*;
import org.apache.commons.lang.*;
import org.springframework.web.bind.*;
import org.springframework.web.servlet.*;
import org.springframework.web.servlet.mvc.*;

import javax.imageio.*;
import javax.servlet.http.*;
import java.awt.*;
import java.awt.image.*;
import java.io.*;

/**
 * Controller which produces cover art images.
 *
 * @author Sindre Mehus
 */
public class CoverArtController implements Controller, LastModified {

    private SecurityService securityService;

    private static final Logger LOG = Logger.getLogger(CoverArtController.class);

    public long getLastModified(HttpServletRequest request) {
        String path = request.getParameter("path");
        if (StringUtils.trimToNull(path) == null) {
            return 0;
        }

        File file = new File(path);
        if (!file.exists()) {
            return -1;
        }

        return file.lastModified();
    }

    public ModelAndView handleRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String path = request.getParameter("path");
        File file = (path == null || path.length() == 0) ? null : new File(path);
        Integer size = ServletRequestUtils.getIntParameter(request, "size");

        // Check access.
        if (file != null && !securityService.isReadAllowed(file)) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
            return null;
        }

        // Optimize if no scaling is required.
        if (size == null) {
            sendUnscaled(file, response);
            return null;
        }

        // Send default image if no path is given. (No need to cache it, since it will be cached in browser.)
        if (file == null) {
            sendDefault(size, response);
            return null;
        }

        // Send cached image, creating it if necessary.
        File cachedImage = getCachedImage(file, size);
        sendImage(cachedImage, response);

        return null;
    }

    private void sendImage(File file, HttpServletResponse response) throws IOException {
        InputStream in = new FileInputStream(file);
        try {
            IOUtils.copy(in, response.getOutputStream());
        } finally {
            IOUtils.closeQuietly(in);
        }
    }

    private void sendDefault(Integer size, HttpServletResponse response) throws IOException {
        InputStream in = null;
        try {
            in = getClass().getResourceAsStream("default_cover.jpg");
            BufferedImage image = ImageIO.read(in);
            image = scale(image, size, size);
            ImageIO.write(image, "jpeg", response.getOutputStream());
        } finally {
            IOUtils.closeQuietly(in);
        }
    }

    private void sendUnscaled(File file, HttpServletResponse response) throws IOException {
        InputStream in = null;
        try {
            in = new FileInputStream(file);
            IOUtils.copy(in, response.getOutputStream());
        } finally {
            IOUtils.closeQuietly(in);
        }
    }

    private File getCachedImage(File file, int size) throws IOException {
        String md5 = DigestUtils.md5Hex(file.getPath());
        File cachedImage = new File(getImageCacheDirectory(size), md5 + ".jpeg");

        // Is cache missing or obsolete?
        if (!cachedImage.exists() || file.lastModified() > cachedImage.lastModified()) {
            LOG.debug("MISS: " + file + " (" + size + ')');  //TODO: Remove
            InputStream in = null;
            OutputStream out = null;
            try {
                in = new FileInputStream(file);
                out = new FileOutputStream(cachedImage);
                BufferedImage image = ImageIO.read(in);
                image = scale(image, size, size);
                ImageIO.write(image, "jpeg", out);
            } finally {
                IOUtils.closeQuietly(in);
                IOUtils.closeQuietly(out);
            }
        } else {
            LOG.debug("HIT:  " + file + " (" + size + ')');  //TODO: Remove
        }

        return cachedImage;
    }

    private File getImageCacheDirectory(int size) {
        File dir = new File(SettingsService.getSubsonicHome(), "thumbs");
        dir = new File(dir, String.valueOf(size));
        if (!dir.exists()) {
            if (dir.mkdirs()) {
                LOG.info("Created thumbnail cache " + dir);
            } else {
                LOG.error("Failed to create thumbnail cache " + dir);
            }
        }

        return dir;
    }

    public BufferedImage scale(BufferedImage image, int width, int height) {
        int w = image.getWidth();
        int h = image.getHeight();
        BufferedImage thumb = image;

        // For optimal results, use step by step bilinear resampling - halfing the size at each step.
        do {
            w /= 2;
            h /= 2;
            if (w < width) {
                w = width;
            }
            if (h < height) {
                h = height;
            }

            BufferedImage temp = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
            Graphics2D g2 = temp.createGraphics();
            g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                                RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            g2.drawImage(thumb, 0, 0, temp.getWidth(), temp.getHeight(), null);
            g2.dispose();

            thumb = temp;
        } while (w != width);

        return thumb;
    }

    public void setSecurityService(SecurityService securityService) {
        this.securityService = securityService;
    }

}
