package net.sourceforge.subsonic.controller;

import net.sourceforge.subsonic.domain.PodcastEpisode;
import net.sourceforge.subsonic.service.PodcastService;
import org.apache.commons.lang.StringUtils;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractController;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.SortedSet;
import java.util.TreeSet;

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
        if (request.getParameter("downloadChannel") != null ||
            request.getParameter("downloadEpisode") != null) {
            download(parseIds(request.getParameter("downloadChannel")),
                     parseIds(request.getParameter("downloadEpisode")));
        }
        if (request.getParameter("deleteChannel") != null) {
            for (int channelId : parseIds(request.getParameter("deleteChannel"))) {
                podcastService.deleteChannel(channelId);
            }
        }
        if (request.getParameter("deleteEpisode") != null) {
            for (int episodeId : parseIds(request.getParameter("deleteEpisode"))) {
                podcastService.deleteEpisode(episodeId, true);
            }
        }
        if (request.getParameter("refresh") != null) {
            podcastService.refreshAllChannels(true);
        }
    }

    private void download(int[] channelIds, int[] episodeIds) {
        SortedSet<Integer> uniqueEpisodeIds = new TreeSet<Integer>();
        for (int episodeId : episodeIds) {
            uniqueEpisodeIds.add(episodeId);
        }
        for (int channelId : channelIds) {
            PodcastEpisode[] episodes = podcastService.getEpisodes(channelId, false);
            for (PodcastEpisode episode : episodes) {
                uniqueEpisodeIds.add(episode.getId());
            }
        }

        for (Integer episodeId : uniqueEpisodeIds) {
            PodcastEpisode episode = podcastService.getEpisode(episodeId, false);
            if (episode != null && episode.getUrl() != null &&
                (episode.getStatus() == PodcastEpisode.Status.NEW ||
                 episode.getStatus() == PodcastEpisode.Status.ERROR ||
                 episode.getStatus() == PodcastEpisode.Status.SKIPPED)) {

                podcastService.downloadEpisode(episode);
            }
        }
    }

    // TODO: Move to StringUtil. Duplicate code in PlaylistController.
    private int[] parseIds(String s) {
        if (s == null) {
            return new int[0];
        }

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
