package net.sourceforge.subsonic.controller;

import net.sourceforge.subsonic.Logger;
import net.sourceforge.subsonic.domain.Avatar;
import net.sourceforge.subsonic.service.SettingsService;
import net.sourceforge.subsonic.service.SecurityService;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.lang.StringUtils;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Date;

/**
 * Controller which receives uploaded avatar images.
 *
 * @author Sindre Mehus
 */
public class AvatarUploadController implements Controller {

    private static final Logger LOG = Logger.getLogger(AvatarUploadController.class);

    private SettingsService settingsService;
    private SecurityService secturityService;

    public ModelAndView handleRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
        // Check that we have a file upload request
        if (!ServletFileUpload.isMultipartContent(request)) {
            throw new Exception("Illegal request.");
        }

        FileItemFactory factory = new DiskFileItemFactory();
        ServletFileUpload upload = new ServletFileUpload(factory);
        List<?> items = upload.parseRequest(request);

        // Look for file items.
        for (Object o : items) {
            FileItem item = (FileItem) o;

            if (!item.isFormField()) {
                String fileName = item.getName();
                byte[] data = item.get();

                if (StringUtils.isNotBlank(fileName) && data.length > 0) {
                    String username = secturityService.getCurrentUsername(request);
                    createAvatar(fileName, data, username);
                }
            }
        }

        return new ModelAndView(new RedirectView("personalSettings.view?"));
    }

    private void createAvatar(String name, byte[] data, String username) {
        Avatar avatar = new Avatar(0, name, new Date(),  "image/png", 48, 48, data );
        settingsService.setCustomAvatar(avatar, username);
        LOG.info("Uploaded avatar '" + name + "' (" + data.length + " bytes) for user " + username);
    }

    public void setSettingsService(SettingsService settingsService) {
        this.settingsService = settingsService;
    }

    public void setSecturityService(SecurityService secturityService) {
        this.secturityService = secturityService;
    }
}
