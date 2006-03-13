package net.sourceforge.subsonic.servlet;

import net.sourceforge.subsonic.service.*;

import javax.imageio.*;
import javax.servlet.http.*;
import java.awt.*;
import java.awt.geom.*;
import java.awt.image.*;
import java.io.*;
import java.net.*;

/**
 * A servlet which scales and transcodes cover art images.
 *
 * @author Sindre Mehus
 * @version $Revision: 1.3 $ $Date: 2005/06/15 18:10:40 $
 */
public class CoverArtServlet extends HttpServlet {
    private static final int DEFAULT_IMAGE_SIZE  = 300;

    /**
     * Handles the given HTTP request.
     * @param request The HTTP request.
     * @param response The HTTP response.
     * @throws IOException If an I/O error occurs.
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {

        String path = request.getParameter("path");
        File file = path == null ? null : new File(path);

        if (file != null && !ServiceFactory.getSecurityService().isReadAllowed(file)) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }

        String s = request.getParameter("size");
        int size = s == null ? DEFAULT_IMAGE_SIZE : Integer.parseInt(s);

        URL url = getClass().getResource("default_cover.jpg");
        BufferedImage image = file == null ? ImageIO.read(url) : ImageIO.read(file);
        image = scale(image, size, size);
        ImageIO.write(image, "jpeg", response.getOutputStream());
    }

    private BufferedImage scale(BufferedImage original, int width, int height) {

        BufferedImage result = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        Graphics2D g2 = result.createGraphics();
        g2.setPaint(Color.white);
        g2.fillRect(0, 0, width, height);

        // scale to fit
        double xScale = (double)width  / original.getWidth();
        double yScale = (double)height / original.getHeight();
        double scale = Math.min(xScale, yScale);

        // center thumbnail image
        double x = (width  - original.getWidth()  * scale)/2;
        double y = (height - original.getHeight() * scale)/2;

        AffineTransform at = AffineTransform.getTranslateInstance(x, y);
        at.scale(scale, scale);
        g2.drawRenderedImage(original, at);

        g2.dispose();

        return result;
    }
}
