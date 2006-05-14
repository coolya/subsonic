package net.sourceforge.subsonic.servlet;

import net.sourceforge.subsonic.service.*;
import net.sourceforge.subsonic.domain.*;
import org.jfree.chart.*;
import org.jfree.chart.axis.*;
import org.jfree.chart.plot.*;
import org.jfree.chart.renderer.xy.*;
import org.jfree.data.*;
import org.jfree.data.time.*;

import javax.servlet.http.*;
import java.awt.*;
import java.io.*;
import java.util.*;
import java.util.List;

/**
 * Servlet for generating a chart showing bitrate vs time.
 *
 * @author Sindre Mehus
 * @version $Revision: 1.7 $ $Date: 2005/12/07 19:18:57 $
 */
public class StatusChartServlet extends HttpServlet {

    public static final int IMAGE_WIDTH = 350;
    public static final int IMAGE_HEIGHT = 150;

    /**
     * Handles the given HTTP request.
     * @param request The HTTP request.
     * @param response The HTTP response.
     * @throws IOException If an I/O error occurs.
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String type = request.getParameter("type");
        int index = Integer.parseInt(request.getParameter("index"));

        TransferStatus[] statuses = new TransferStatus[0];
        if ("stream".equals(type)) {
            statuses = ServiceFactory.getStatusService().getAllStreamStatuses();
        } else if ("download".equals(type)) {
            statuses = ServiceFactory.getStatusService().getAllDownloadStatuses();
        } else if ("upload".equals(type)) {
            statuses = ServiceFactory.getStatusService().getAllUploadStatuses();
        }

        if (index < 0 || index >= statuses.length) {
            return;
        }
        TransferStatus status = statuses[index];

        TimeSeries series = new TimeSeries("Kbps", Millisecond.class);
        List<TransferStatus.Sample> history = status.getHistory();
        long to = System.currentTimeMillis();
        long from = to - status.getHistoryLengthMillis();
        Range range = new DateRange(from, to);

        if (!history.isEmpty()) {

            TransferStatus.Sample previous = history.get(0);

            for (int i = 1; i < history.size(); i++) {
                TransferStatus.Sample sample = history.get(i);

                long elapsedTimeMilis = sample.getTimestamp() - previous.getTimestamp();
                long bytesStreamed = sample.getBytesTransfered() - previous.getBytesTransfered();

                double kbps = (8.0 * bytesStreamed / 1024.0) / (elapsedTimeMilis / 1000.0);
                series.add(new Millisecond(new Date(sample.getTimestamp())), kbps);

                previous = sample;
            }
        }

        // Compute moving average.
        series = MovingAverage.createMovingAverage(series, "Kbps", 20000, 5000);

        // Find min and max values.
        double min = 100;
        double max = 250;
        for (Object obj : series.getItems()) {
            TimeSeriesDataItem item = (TimeSeriesDataItem) obj;
            double value = item.getValue().doubleValue();
            if (item.getPeriod().getFirstMillisecond() > from) {
                min = Math.min(min, value);
                max = Math.max(max, value);
            }
        }

        // Add 10% to max value.
        max *= 1.1D;

        // Subtract 10% from min value.
        min *= 0.9D;

        TimeSeriesCollection dataset = new TimeSeriesCollection();
        dataset.addSeries(series);
        JFreeChart chart = ChartFactory.createTimeSeriesChart(null, null, null, dataset, false, false, false);
        XYPlot plot = (XYPlot) chart.getPlot();

        plot.setRangeAxisLocation(AxisLocation.BOTTOM_OR_RIGHT);
        Paint background = new GradientPaint(0, 0, Color.lightGray, 0, IMAGE_HEIGHT, Color.white);
        plot.setBackgroundPaint(background);

        XYItemRenderer renderer = plot.getRendererForDataset(dataset);
        renderer.setPaint(Color.blue.darker());
        renderer.setStroke(new BasicStroke(2f));

        chart.setBackgroundPaint(new Color(0xEFEFEF));

        ValueAxis domainAxis = plot.getDomainAxis();
        domainAxis.setRange(range);

        ValueAxis rangeAxis = plot.getRangeAxis();
        rangeAxis.setRange(new Range(min, max));

        ChartUtilities.writeChartAsPNG(response.getOutputStream(), chart, IMAGE_WIDTH, IMAGE_HEIGHT);

    }
}
