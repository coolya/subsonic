package net.sourceforge.subsonic.controller;

import net.sourceforge.subsonic.service.PodcastService;
import org.apache.commons.lang.StringUtils;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractController;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Controller for the "Podcast receiver" page.
 *
 * @author Sindre Mehus
 */
public class PodcastReceiverAdminController extends AbstractController {

    private PodcastService podcastService;

    protected ModelAndView handleRequestInternal(HttpServletRequest request, HttpServletResponse response) throws Exception {
        handleParameters(request);
        return new ModelAndView(new RedirectView("podcastReceiver.view?"));
    }

    private void handleParameters(HttpServletRequest request) {
        if (request.getParameter("add") != null) {
            String url = request.getParameter("add");
            podcastService.createChannel(url);
        }
        if (request.getParameter("deleteChannel") != null) {
            for (int channelId : parseIds(request.getParameter("deleteChannel"))) {
                podcastService.deleteChannel(channelId);
            }
        }
        if (request.getParameter("deleteEpisode") != null) {
            for (int episodeId : parseIds(request.getParameter("deleteEpisode"))) {
                podcastService.deleteEpisode(episodeId);
            }
        }
        if (request.getParameter("refresh") != null) {
            podcastService.refresh(true);
        }
    }

    // TODO: Move to StringUtil. Duplicate code in PlaylistController.
    private int[] parseIds(String s) {
        String[] strings = StringUtils.split(s);
        int[] ints = new int[strings.length];
        for (int i = 0; i < strings.length; i++) {
            ints[i] = Integer.parseInt(strings[i]);
        }
        return ints;
    }

    public void setPodcastService(PodcastService podcastService) {
        this.podcastService = podcastService;
    }
}
