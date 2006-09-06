package net.sourceforge.subsonic.controller;

import net.sourceforge.subsonic.domain.*;
import net.sourceforge.subsonic.service.*;
import org.jfree.chart.*;
import org.jfree.chart.axis.*;
import org.jfree.chart.plot.*;
import org.jfree.chart.renderer.category.*;
import org.jfree.data.category.*;
import org.springframework.web.servlet.*;

import javax.servlet.http.*;
import java.awt.*;

/**
 * Controller for generating a chart showing bitrate vs time.
 *
 * @author Sindre Mehus
 */
public class UserChartController extends AbstractChartController {

    private SecurityService securityService;

    public static final int IMAGE_WIDTH = 400;
    public static final int IMAGE_MIN_HEIGHT = 200;
    private static final long BYTES_PER_MB = 1024L * 1024L;

    public ModelAndView handleRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String type = request.getParameter("type");
        CategoryDataset dataset = createDataset(type);
        JFreeChart chart = createChart(dataset, request);

        int imageHeight = Math.max(IMAGE_MIN_HEIGHT, 15 * dataset.getColumnCount());

        ChartUtilities.writeChartAsPNG(response.getOutputStream(), chart, IMAGE_WIDTH, imageHeight);
        return null;
    }

    private CategoryDataset createDataset(String type) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        User[] users = securityService.getAllUsers();
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

    private JFreeChart createChart(CategoryDataset dataset, HttpServletRequest request) {
        JFreeChart chart = ChartFactory.createBarChart(null, null, null, dataset, PlotOrientation.HORIZONTAL, false, false, false);

        CategoryPlot plot = chart.getCategoryPlot();
        Paint background = new GradientPaint(0, 0, Color.lightGray, 0, IMAGE_MIN_HEIGHT, Color.white);
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

        // Set theme-specific colors.
        Color bgColor = getBackground(request);
        Color fgColor = getForeground(request);

        chart.setBackgroundPaint(bgColor);

        domainAxis.setTickLabelPaint(fgColor);
        domainAxis.setTickMarkPaint(fgColor);
        domainAxis.setAxisLinePaint(fgColor);

        rangeAxis.setTickLabelPaint(fgColor);
        rangeAxis.setTickMarkPaint(fgColor);
        rangeAxis.setAxisLinePaint(fgColor);

        return chart;
    }

    public void setSecurityService(SecurityService securityService) {
        this.securityService = securityService;
    }
}
