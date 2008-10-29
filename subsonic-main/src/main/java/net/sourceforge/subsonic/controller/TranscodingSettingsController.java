package net.sourceforge.subsonic.controller;

import net.sourceforge.subsonic.domain.Transcoding;
import net.sourceforge.subsonic.service.TranscodingService;
import net.sourceforge.subsonic.service.SettingsService;
import org.apache.commons.lang.StringUtils;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.ParameterizableViewController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

/**
 * Controller for the page used to administrate the set of transcoding configurations.
 *
 * @author Sindre Mehus
 */
public class TranscodingSettingsController extends ParameterizableViewController {

    private TranscodingService transcodingService;
    private SettingsService settingsService;

    @Override
    protected ModelAndView handleRequestInternal(HttpServletRequest request, HttpServletResponse response) throws Exception {

        Map<String, Object> map = new HashMap<String, Object>();

        if (isFormSubmission(request)) {
            handleParameters(request, map);
        }

        ModelAndView result = super.handleRequestInternal(request, response);
        map.put("transcodings", transcodingService.getAllTranscodings(true));
        map.put("transcodeDirectory", transcodingService.getTranscodeDirectory());
        map.put("brand", settingsService.getBrand());

        result.addObject("model", map);
        return result;
    }

    /**
     * Determine if the given request represents a form submission.
     *
     * @param request current HTTP request
     * @return if the request represents a form submission
     */
    private boolean isFormSubmission(HttpServletRequest request) {
        return "POST".equals(request.getMethod());
    }

    private void handleParameters(HttpServletRequest request, Map<String, Object> map) {

        for (Transcoding transcoding : transcodingService.getAllTranscodings(true)) {
            Integer id = transcoding.getId();
            String name = getParameter(request, "name", id);
            String sourceFormat = getParameter(request, "sourceFormat", id);
            String targetFormat = getParameter(request, "targetFormat", id);
            String step1 = getParameter(request, "step1", id);
            String step2 = getParameter(request, "step2", id);
            String step3 = getParameter(request, "step3", id);
            boolean enabled = getParameter(request, "enabled", id) != null;
            boolean defaultActive = getParameter(request, "defaultActive", id) != null;
            boolean delete = getParameter(request, "delete", id) != null;

            if (delete) {
                transcodingService.deleteTranscoding(id);
            } else if (name == null) {
                map.put("error", "transcodingsettings.noname");
            } else if (sourceFormat == null) {
                map.put("error", "transcodingsettings.nosourceformat");
            } else if (targetFormat == null) {
                map.put("error", "transcodingsettings.notargetformat");
            } else if (step1 == null) {
                map.put("error", "transcodingsettings.nostep1");
            } else {
                transcoding.setName(name);
                transcoding.setSourceFormat(sourceFormat);
                transcoding.setTargetFormat(targetFormat);
                transcoding.setStep1(step1);
                transcoding.setStep2(step2);
                transcoding.setStep3(step3);
                transcoding.setEnabled(enabled);
                transcoding.setDefaultActive(defaultActive);
                transcodingService.updateTranscoding(transcoding);
            }
        }

        String name = StringUtils.trimToNull(request.getParameter("name"));
        String sourceFormat = StringUtils.trimToNull(request.getParameter("sourceFormat"));
        String targetFormat = StringUtils.trimToNull(request.getParameter("targetFormat"));
        String step1 = StringUtils.trimToNull(request.getParameter("step1"));
        String step2 = StringUtils.trimToNull(request.getParameter("step2"));
        String step3 = StringUtils.trimToNull(request.getParameter("step3"));
        boolean enabled = StringUtils.trimToNull(request.getParameter("enabled")) != null;
        boolean defaultActive = StringUtils.trimToNull(request.getParameter("defaultActive")) != null;

        if (name != null || sourceFormat != null || targetFormat != null || step1 != null || step2 != null || step3 != null) {
            Transcoding transcoding = new Transcoding(null, name, sourceFormat, targetFormat, step1, step2, step3, enabled, defaultActive);
            if (name == null) {
                map.put("error", "transcodingsettings.noname");
            } else if (sourceFormat == null) {
                map.put("error", "transcodingsettings.nosourceformat");
            } else if (targetFormat == null) {
                map.put("error", "transcodingsettings.notargetformat");
            } else if (step1 == null) {
                map.put("error", "transcodingsettings.nostep1");
            } else {
                transcodingService.createTranscoding(transcoding);
            }
            if (map.containsKey("error")) {
                map.put("newTranscoding", transcoding);
            }
        }
    }

    private String getParameter(HttpServletRequest request, String name, Integer id) {
        return StringUtils.trimToNull(request.getParameter(name + "[" + id + "]"));
    }

    public void setTranscodingService(TranscodingService transcodingService) {
        this.transcodingService = transcodingService;
    }

    public void setSettingsService(SettingsService settingsService) {
        this.settingsService = settingsService;
    }
}
