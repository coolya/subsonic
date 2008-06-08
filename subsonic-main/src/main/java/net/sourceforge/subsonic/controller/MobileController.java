package net.sourceforge.subsonic.controller;

import net.sourceforge.subsonic.service.MusicFileService;
import net.sourceforge.subsonic.service.MusicIndexService;
import net.sourceforge.subsonic.service.PlayerService;
import net.sourceforge.subsonic.service.PlaylistService;
import net.sourceforge.subsonic.service.SearchService;
import net.sourceforge.subsonic.service.SecurityService;
import net.sourceforge.subsonic.service.SettingsService;
import net.sourceforge.subsonic.util.StringUtil;
import org.apache.commons.io.IOUtils;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * Multi-controller used for mobile phone pages.
 *
 * @author Sindre Mehus
 */
public class MobileController extends MultiActionController {

    /*
    TODO:
    mobilePlayerJar.jar
    mobilePlayerJad.jad
    index.view
    getDitt.xml (Move to different controller?)
    getDatt.xml(Move to different controller?)
     */
    private SettingsService settingsService;
    private PlayerService playerService;
    private PlaylistService playlistService;
    private SearchService searchService;
    private SecurityService securityService;
    private MusicFileService musicFileService;
    private MusicIndexService musicIndexService;

    public ModelAndView index(HttpServletRequest request, HttpServletResponse response) throws Exception {
        return mobile(request, response);
    }

    public ModelAndView mobile(HttpServletRequest request, HttpServletResponse response) throws Exception {
        // TODO
        return null;
    }

    public ModelAndView playerJad(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("baseUrl", getBaseUrl(request));
        map.put("jarSize", getJarSize());
        return new ModelAndView("mobile/playerJad", "model", map);
    }

    private String getBaseUrl(HttpServletRequest request) {
        String baseUrl = request.getRequestURL().toString();
        baseUrl = baseUrl.replaceFirst("/mobile.*", "/");

        // Rewrite URLs in case we're behind a proxy.
        if (settingsService.isRewriteUrlEnabled()) {
            String referer = request.getHeader("referer");
            baseUrl = StringUtil.rewriteUrl(baseUrl, referer);
        }
        return baseUrl;
    }

    public ModelAndView playerJar(HttpServletRequest request, HttpServletResponse response) throws Exception {
        response.setContentType("application/java-archive");
        response.setContentLength(getJarSize());
        InputStream in = getJarInputStream();
        try {
            IOUtils.copy(in, response.getOutputStream());
        } finally {
            IOUtils.closeQuietly(in);
        }
        return null;
    }

    private int getJarSize() throws Exception {
        InputStream in = getJarInputStream();
        try {
            return IOUtils.toByteArray(in).length;
        } finally {
            IOUtils.closeQuietly(in);
        }
    }

    private InputStream getJarInputStream() {
        return getClass().getResourceAsStream("subsonic-jme-player.jar");
    }

    public void setSettingsService(SettingsService settingsService) {
        this.settingsService = settingsService;
    }

    public void setPlayerService(PlayerService playerService) {
        this.playerService = playerService;
    }

    public void setPlaylistService(PlaylistService playlistService) {
        this.playlistService = playlistService;
    }

    public void setSearchService(SearchService searchService) {
        this.searchService = searchService;
    }

    public void setSecurityService(SecurityService securityService) {
        this.securityService = securityService;
    }

    public void setMusicFileService(MusicFileService musicFileService) {
        this.musicFileService = musicFileService;
    }

    public void setMusicIndexService(MusicIndexService musicIndexService) {
        this.musicIndexService = musicIndexService;
    }
}