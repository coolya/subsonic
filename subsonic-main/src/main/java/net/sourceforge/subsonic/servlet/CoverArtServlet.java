package net.sourceforge.subsonic.servlet;

import net.sourceforge.subsonic.*;
import net.sourceforge.subsonic.service.*;
import org.apache.commons.io.*;

import javax.imageio.*;
import javax.servlet.http.*;
import java.awt.*;
import java.awt.geom.*;
import java.awt.image.*;
import java.io.*;

/**
 * A servlet which scales and transcodes cover art images.
 *
 * @author Sindre Mehus
 * @version $Revision: 1.3 $ $Date: 2005/06/15 18:10:40 $
 */
public class CoverArtServlet extends HttpServlet {

    private static final Logger LOG = Logger.getLogger(CoverArtServlet.class);

    /**
     * Handles the given HTTP request.
     * @param request The HTTP request.
     * @param response The HTTP response.
     * @throws IOException If an I/O error occurs.
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {

        String path = request.getParameter("path");
        File file = (path == null || path.length() == 0) ? null : new File(path);

        if (file != null && !ServiceFactory.getSecurityService().isReadAllowed(file)) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
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
