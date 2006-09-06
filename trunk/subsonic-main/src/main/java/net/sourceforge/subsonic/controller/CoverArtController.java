package net.sourceforge.subsonic.controller;

import net.sourceforge.subsonic.*;
import net.sourceforge.subsonic.service.*;
import org.apache.commons.io.*;
import org.apache.commons.lang.*;
import org.springframework.web.servlet.*;
import org.springframework.web.servlet.mvc.*;

import javax.imageio.*;
import javax.servlet.http.*;
import java.awt.*;
import java.awt.geom.*;
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

        if (file != null && !securityService.isReadAllowed(file)) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
            return null;
        }

        InputStream in;
        if (file == null) {
            in = getClass().getResourceAsStream("default_cover.jpg");
        } else {
            in = new FileInputStream(file);
        }

        try {

            String s = request.getParameter("size");
            if (s != null) {
                int size = Integer.parseInt(s);
                BufferedImage image = ImageIO.read(in);
                image = scale(image, size, size);
                ImageIO.write(image, "jpeg", response.getOutputStream());
            } else {
                // Optimize if no scaling is required.
                IOUtils.copy(in, response.getOutputStream());
            }

        } finally {
            try {
                in.close();
            } catch (IOException e) {
                LOG.warn("Failed to close input stream for cover art (" + path + ").");
            }
        }
        return null;
    }

    private BufferedImage scale(BufferedImage original, int width, int height) {

        BufferedImage result = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        // scale to fit
        double xScale = (double)width  / original.getWidth();
        double yScale = (double)height / original.getHeight();

        // center thumbnail image
        double x = (width  - original.getWidth()  * xScale)/2;
        double y = (height - original.getHeight() * yScale)/2;

        AffineTransform at = AffineTransform.getTranslateInstance(x, y);
        at.scale(xScale, yScale);

        Graphics2D g2 = result.createGraphics();
        g2.drawRenderedImage(original, at);
        g2.dispose();

        return result;
    }

//    private BufferedImage scale(BufferedImage original, int width, int height) {
//
//        BufferedImage result = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
//
//        Graphics2D graphics2D = result.createGraphics();
//        // TODO: Make configurable?
//        graphics2D.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
//                                    RenderingHints.VALUE_INTERPOLATION_BICUBIC);
//        graphics2D.drawImage(original, 0, 0, width, height, null);
//        graphics2D.dispose();
//
//        return result;
//    }

    public void setSecurityService(SecurityService securityService) {
        this.securityService = securityService;
    }

}
