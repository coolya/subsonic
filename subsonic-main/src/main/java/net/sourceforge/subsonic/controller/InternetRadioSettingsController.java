package net.sourceforge.subsonic.controller;

import net.sourceforge.subsonic.domain.*;
import net.sourceforge.subsonic.service.*;
import org.springframework.web.servlet.*;
import org.springframework.web.servlet.mvc.*;

import javax.servlet.http.*;
import java.util.*;

/**
 * Controller for the page used to administrate the set of music folders.
 *
 * @author Sindre Mehus
 */
public class InternetRadioSettingsController extends ParameterizableViewController {

    private SettingsService settingsService;

    protected ModelAndView handleRequestInternal(HttpServletRequest request, HttpServletResponse response) throws Exception {

        Map<String, Object> map = new HashMap<String, Object>();

        if (isFormSubmission(request)) {
            String error = handleParameters(request);
            map.put("error", error);
            if (error == null) {
                map.put("reload", true);
            }
        }

        ModelAndView result = super.handleRequestInternal(request, response);
        map.put("internetRadios", settingsService.getAllInternetRadios(true));

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

    private String handleParameters(HttpServletRequest request) {
        String id = request.getParameter("id");
        String streamUrl = request.getParameter("streamUrl");
        String homepageUrl = request.getParameter("homepageUrl");
        String name = request.getParameter("name");
        boolean enabled = request.getParameter("enabled") != null;
        boolean create = request.getParameter("create") != null;
        boolean delete = request.getParameter("delete") != null;

        if (delete) {
            settingsService.deleteInternetRadio(new Integer(id));
        } else {

            if (name.length() == 0) {
                return "internetradiosettings.noname";
            }
            if (streamUrl.length() == 0) {
                return "internetradiosettings.nourl";
            }
            if (create) {
                settingsService.createInternetRadio(new InternetRadio(name, streamUrl, homepageUrl, enabled));
            } else {
                settingsService.updateInternetRadio(new InternetRadio(new Integer(id), name, streamUrl, homepageUrl, enabled));
            }
        }
        return null;
    }

    public void setSettingsService(SettingsService settingsService) {
        this.settingsService = settingsService;
    }

}
