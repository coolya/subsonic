package net.sourceforge.subsonic.controller;

import net.sourceforge.subsonic.domain.*;
import net.sourceforge.subsonic.service.*;
import net.sourceforge.subsonic.servlet.*;
import net.sourceforge.subsonic.util.*;
import org.springframework.web.servlet.*;
import org.springframework.web.servlet.mvc.*;
import org.springframework.web.servlet.support.*;

import javax.servlet.http.*;
import java.util.*;

/**
 * Controller for the status page.
 *
 * @author Sindre Mehus
 */
public class StatusController extends ParameterizableViewController {

    private StatusService statusService;

    protected ModelAndView handleRequestInternal(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Map<String, Object> map = new HashMap<String, Object>();

        StreamStatus[] streamStatuses = statusService.getAllStreamStatuses();
        DownloadStatus[] downloadStatuses = statusService.getAllDownloadStatuses();

        Locale locale = RequestContextUtils.getLocale(request);
        String[] bytesStreamed = new String[streamStatuses.length];
        String[] bytesDownloaded = new String[downloadStatuses.length];

        for (int i = 0; i < streamStatuses.length; i++) {
            StreamStatus streamStatus = streamStatuses[i];
            bytesStreamed[i] = StringUtil.formatBytes(streamStatus.getBytesStreamed(), locale);
        }
        for (int i = 0; i < downloadStatuses.length; i++) {
            DownloadStatus downloadStatus = downloadStatuses[i];
            bytesDownloaded[i] = StringUtil.formatBytes(downloadStatus.getBytesStreamed(), locale);
        }

        map.put("streamStatuses", streamStatuses);
        map.put("downloadStatuses", downloadStatuses);
        map.put("bytesStreamed", bytesStreamed);
        map.put("bytesDownloaded", bytesDownloaded);
        map.put("chartWidth", StatusChartServlet.IMAGE_WIDTH);
        map.put("chartHeight", StatusChartServlet.IMAGE_HEIGHT);

        ModelAndView result = super.handleRequestInternal(request, response);
        result.addObject("model", map);
        return result;
    }

    public void setStatusService(StatusService statusService) {
        this.statusService = statusService;
    }
}
