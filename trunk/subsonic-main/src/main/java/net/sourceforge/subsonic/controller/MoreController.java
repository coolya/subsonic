package net.sourceforge.subsonic.controller;

import org.springframework.web.servlet.mvc.*;
import org.springframework.web.servlet.*;
import net.sourceforge.subsonic.service.*;
import net.sourceforge.subsonic.domain.*;

import javax.servlet.http.*;
import java.util.*;
import java.io.*;

/**
 * Controller for the "more" page.
 *
 * @author Sindre Mehus
 */
public class MoreController extends ParameterizableViewController {

    private SettingsService settingsService;
    private SecurityService securityService;

    protected ModelAndView handleRequestInternal(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Map<String, Object> map = new HashMap<String, Object>();

        User user = securityService.getCurrentUser(request);
        map.put("uploadEnabled", user.isUploadRole());

        String uploadDirectory = null;
        MusicFolder[] musicFolders = settingsService.getAllMusicFolders();
        if (musicFolders.length > 0) {
            uploadDirectory = new File(musicFolders[0].getPath(), "Incoming").getPath();
        }

        ModelAndView result = super.handleRequestInternal(request, response);
        result.addObject("model", map);
        map.put("uploadDirectory", uploadDirectory);
        return result;
    }

    public void setSettingsService(SettingsService settingsService) {
        this.settingsService = settingsService;
    }

    public void setSecurityService(SecurityService securityService) {
        this.securityService = securityService;
    }
}
