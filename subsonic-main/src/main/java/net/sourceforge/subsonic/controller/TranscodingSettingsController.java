package net.sourceforge.subsonic.controller;

import net.sourceforge.subsonic.domain.*;
import net.sourceforge.subsonic.service.*;
import org.springframework.web.servlet.*;
import org.springframework.web.servlet.mvc.*;
import org.apache.commons.lang.*;

import javax.servlet.http.*;
import java.util.*;

/**
 * Controller for the page used to administrate the set of transcoding configurations.
 *
 * @author Sindre Mehus
 */
public class TranscodingSettingsController extends ParameterizableViewController {

    private TranscodingService transcodingService;

    protected ModelAndView handleRequestInternal(HttpServletRequest request, HttpServletResponse response) throws Exception {

        Map<String, Object> map = new HashMap<String, Object>();

        if (isFormSubmission(request)) {
            handleParameters(request, map);
        }

        ModelAndView result = super.handleRequestInternal(request, response);
        map.put("transcodings", transcodingService.getAllTranscodings(true));
        map.put("transcodeDirectory", transcodingService.getTranscodeDirectory());

        result.addObject("model", map);
        return result;
    }

    /**
     * Determine if the given request represents a form submission.
     * @param request current HTTP request
     * @return if the request represents a form submission
     */
    private boolean isFormSubmission(HttpServletRequest request) {
        return "POST".equals(request.getMethod());
    }

    private void handleParameters(HttpServletRequest request, Map<String, Object> map) {
        String id = StringUtils.trimToNull(request.getParameter("id"));
        String name = StringUtils.trimToNull(request.getParameter("name"));
        String sourceFormat = StringUtils.trimToNull(request.getParameter("sourceFormat"));
        String targetFormat = StringUtils.trimToNull(request.getParameter("targetFormat"));
        String step1 = StringUtils.trimToNull(request.getParameter("step1"));
        String step2 = StringUtils.trimToNull(request.getParameter("step2"));
        String step3 = StringUtils.trimToNull(request.getParameter("step3"));
        boolean enabled = request.getParameter("enabled") != null;
        boolean create = request.getParameter("create") != null;
        boolean delete = request.getParameter("delete") != null;

        Transcoding transcoding = new Transcoding(id == null? null : new Integer(id), name, sourceFormat, targetFormat, step1, step2, step3, enabled);

        if (delete) {
            transcodingService.deleteTranscoding(new Integer(id));
        } else {

            if (name == null) {
                map.put("error", "transcodingsettings.noname");
            } else if (sourceFormat == null) {
                map.put("error", "transcodingsettings.nosourceformat");
            } else if (targetFormat == null) {
                map.put("error", "transcodingsettings.notargetformat");
            } else if (step1 == null) {
                map.put("error", "transcodingsettings.nostep1");
            } else if (create) {
                transcodingService.createTranscoding(transcoding);
            } else if (id != null) {
                transcodingService.updateTranscoding(transcoding);
            }
        }

        if (create && map.containsKey("error")) {
            map.put("newTranscoding", transcoding);
        }
    }

    public void setTranscodingService(TranscodingService transcodingService) {
        this.transcodingService = transcodingService;
    }
}
