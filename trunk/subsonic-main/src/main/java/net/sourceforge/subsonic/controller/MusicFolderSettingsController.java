package net.sourceforge.subsonic.controller;

import net.sourceforge.subsonic.service.*;
import net.sourceforge.subsonic.domain.*;
import org.springframework.web.servlet.*;
import org.springframework.web.servlet.mvc.*;

import javax.servlet.http.*;
import java.util.*;
import java.io.*;

/**
 * Controller for the page used to administrate the set of music folders.
 *
 * @author Sindre Mehus
 */
public class MusicFolderSettingsController extends ParameterizableViewController {

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
        map.put("musicFolders", settingsService.getAllMusicFolders(true));

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
        String path = request.getParameter("path");
        String name = request.getParameter("name");
        boolean enabled = request.getParameter("enabled") != null;
        boolean create = request.getParameter("create") != null;
        boolean delete = request.getParameter("delete") != null;

        if (delete) {
            settingsService.deleteMusicFolder(new Integer(id));
        } else {

            if (path.length() == 0) {
                return "musicfoldersettings.nopath";
            }

            File file = new File(path);
            if (name.length() == 0) {
                name = file.getName();
            }

            if (create) {
                settingsService.createMusicFolder(new MusicFolder(file, name, enabled));
            } else if (id != null) {
                settingsService.updateMusicFolder(new MusicFolder(new Integer(id), file, name, enabled));
            }
        }
        return null;
    }

    public void setSettingsService(SettingsService settingsService) {
        this.settingsService = settingsService;
    }

}