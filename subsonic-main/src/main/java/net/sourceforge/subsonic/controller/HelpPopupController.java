package net.sourceforge.subsonic.controller;

import org.springframework.web.servlet.*;
import org.springframework.web.servlet.mvc.*;

import javax.servlet.http.*;
import java.util.*;

import net.sourceforge.subsonic.service.SettingsService;

/**
 * Controller for the help popup.
 *
 * @author Sindre Mehus
 */
public class HelpPopupController extends ParameterizableViewController {

    private SettingsService settingsService;

    protected ModelAndView handleRequestInternal(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Map<String, Object> map = new HashMap<String, Object>();

        String topic = request.getParameter("topic").toLowerCase();
        map.put("topic", topic);
        map.put("brand", settingsService.getBrand());

        ModelAndView result = super.handleRequestInternal(request, response);
        result.addObject("model", map);
        return result;
    }

    public void setSettingsService(SettingsService settingsService) {
        this.settingsService = settingsService;
    }
}
