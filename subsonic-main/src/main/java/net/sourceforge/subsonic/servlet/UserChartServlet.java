package net.sourceforge.subsonic.servlet;

import net.sourceforge.subsonic.domain.*;
import net.sourceforge.subsonic.service.*;
import org.jfree.chart.*;
import org.jfree.chart.axis.*;
import org.jfree.chart.plot.*;
import org.jfree.chart.renderer.category.*;
import org.jfree.data.category.*;

import javax.servlet.http.*;
import java.awt.*;
import java.io.*;

/**
 * Servlet for generating a chart showing how much the different users have
 * streamed/uploaded/downloaded.
 *
 * @author Sindre Mehus
 * @version $Revision: 1.7 $ $Date: 2005/12/07 19:18:57 $
 */
public class UserChartServlet extends HttpServlet {

    public static final int IMAGE_WIDTH = 400;
    public static final int IMAGE_HEIGHT = 200;
    private static final long BYTES_PER_MB = 1024L * 1024L;

    /**
     * Handles the given HTTP request.
     * @param request The HTTP request.
     * @param response The HTTP response.
     * @throws IOException If an I/O error occurs.
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String type = request.getParameter("type");
        CategoryDataset dataset = createDataset(type);
        JFreeChart chart = createChart(dataset);

        ChartUtilities.writeChartAsPNG(response.getOutputStream(), chart, IMAGE_WIDTH, IMAGE_HEIGHT);
    }

    private CategoryDataset createDataset(String type) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        User[] users = ServiceFactory.getSecurityService().getAllUsers();
        for (User user : users) {
            double value;
            if ("stream".equals(type)) {
                value = user.getBytesStreamed();
            } else if ("download".equals(type)) {
                value = user.getBytesDownloaded();
            } else if ("upload".equals(type)) {
                value = user.getBytesUploaded();
            } else if ("total".equals(type)) {
                value = user.getBytesStreamed() + user.getBytesDownloaded() + user.getBytesUploaded();
            } else {
                throw new RuntimeException("Illegal chart type: " + type);
            }

            value /= BYTES_PER_MB;
            dataset.addValue(value, "Series", user.getUsername());
        }

        return dataset;
    }

    private JFreeChart createChart(CategoryDataset dataset) {
        JFreeChart chart = ChartFactory.createBarChart(null, null, null, dataset, PlotOrientation.HORIZONTAL, false, false, false);
        chart.setBackgroundPaint(new Color(0xEFEFEF));

        CategoryPlot plot = chart.getCategoryPlot();
        Paint background = new GradientPaint(0, 0, Color.lightGray, 0, IMAGE_HEIGHT, Color.white);
        plot.setBackgroundPaint(background);
        plot.setDomainGridlinePaint(Color.white);
        plot.setDomainGridlinesVisible(true);
        plot.setRangeGridlinePaint(Color.white);
        plot.setRangeAxisLocation(AxisLocation.BOTTOM_OR_LEFT);

        // Set the range axis to display integers only.
        final NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
        rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());

        // Disable bar outlines.
        BarRenderer renderer = (BarRenderer) plot.getRenderer();
        renderer.setDrawBarOutline(false);

        // Set up gradient paint for series.
        GradientPaint gp0 = new GradientPaint(
                0.0f, 0.0f, Color.blue,
                0.0f, 0.0f, new Color(0, 0, 64)
        );
        renderer.setSeriesPaint(0, gp0);

        // Rotate labels.
        CategoryAxis domainAxis = plot.getDomainAxis();
        domainAxis.setCategoryLabelPositions(CategoryLabelPositions.createUpRotationLabelPositions(Math.PI / 6.0));

        return chart;
    }
}
