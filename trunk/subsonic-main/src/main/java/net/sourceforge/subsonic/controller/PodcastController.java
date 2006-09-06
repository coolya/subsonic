package net.sourceforge.subsonic.controller;

import org.springframework.web.servlet.mvc.*;
import org.springframework.web.servlet.*;
import net.sourceforge.subsonic.service.*;
import net.sourceforge.subsonic.domain.*;
import net.sourceforge.subsonic.util.*;

import javax.servlet.http.*;
import java.util.*;
import java.io.*;
import java.text.*;

/**
 * Controller for the page used to generate the Podcast XML file.
 *
 * @author Sindre Mehus
 */
public class PodcastController extends ParameterizableViewController {

    private static final DateFormat RSS_DATE_FORMAT = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss Z", Locale.US);
    private PlaylistService playlistService;
    private SettingsService settingsService;

    protected ModelAndView handleRequestInternal(HttpServletRequest request, HttpServletResponse response) throws Exception {

        String url = request.getRequestURL().toString();
        File[] playlists = playlistService.getSavedPlaylists();
        List<Podcast> podcasts = new ArrayList<Podcast>();

        for (int i = 0; i < playlists.length; i++) {

            String name = StringUtil.removeSuffix(playlists[i].getName());
            String encodedName = StringUtil.urlEncode(playlists[i].getName());
            String publishDate = RSS_DATE_FORMAT.format(new Date(playlists[i].lastModified()));

            // Resolve content type.
            Playlist playlist = new Playlist();
            playlistService.loadPlaylist(playlist, playlists[i].getName());
            String suffix = getSuffix(playlist);
            String type = StringUtil.getMimeType(suffix);

            long length = playlist.length();
            String enclosureUrl = url.replaceFirst("/podcast.*", "/stream?playlist=" + encodedName + "&amp;suffix=" + suffix);

            // Change protocol and port, if specified. (To make it work with players that don't support SSL.)
            int streamPort = settingsService.getStreamPort();
            if (streamPort != 0) {
                enclosureUrl = StringUtil.toHttpUrl(enclosureUrl, streamPort);
            }

            podcasts.add(new Podcast(name, publishDate, enclosureUrl, length, type));
        }

        Map<String, Object> map = new HashMap<String, Object>();

        ModelAndView result = super.handleRequestInternal(request, response);
        map.put("url", url);
        map.put("podcasts", podcasts);

        result.addObject("model", map);
        return result;
    }

    private String getSuffix(Playlist playlist) {
        if (playlist.isEmpty()) {
            return null;
        }
        return playlist.getFile(0).getSuffix();
    }

    public void setPlaylistService(PlaylistService playlistService) {
        this.playlistService = playlistService;
    }

    public void setSettingsService(SettingsService settingsService) {
        this.settingsService = settingsService;
    }

    /**
     * Contains information about a single Podcast.
     */
    public static class Podcast {
        private String name;
        private String publishDate;
        private String enclosureUrl;
        private long length;
        private String type;

        public Podcast(String name, String publishDate, String enclosureUrl, long length, String type) {
            this.name = name;
            this.publishDate = publishDate;
            this.enclosureUrl = enclosureUrl;
            this.length = length;
            this.type = type;
        }

        public String getName() {
            return name;
        }

        public String getPublishDate() {
            return publishDate;
        }

        public String getEnclosureUrl() {
            return enclosureUrl;
        }

        public long getLength() {
            return length;
        }

        public String getType() {
            return type;
        }
    }
}