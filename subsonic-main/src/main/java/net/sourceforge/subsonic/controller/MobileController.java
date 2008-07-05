package net.sourceforge.subsonic.controller;

import net.sourceforge.subsonic.domain.MusicFile;
import net.sourceforge.subsonic.domain.MusicIndex;
import net.sourceforge.subsonic.domain.Player;
import net.sourceforge.subsonic.domain.Version;
import net.sourceforge.subsonic.service.MusicFileService;
import net.sourceforge.subsonic.service.MusicIndexService;
import net.sourceforge.subsonic.service.PlayerService;
import net.sourceforge.subsonic.service.SecurityService;
import net.sourceforge.subsonic.service.SettingsService;
import net.sourceforge.subsonic.service.TranscodingService;
import net.sourceforge.subsonic.service.VersionService;
import net.sourceforge.subsonic.util.StringUtil;
import org.apache.commons.io.IOUtils;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedMap;
import java.util.SortedSet;

/**
 * Multi-controller used for mobile phone pages.
 *
 * @author Sindre Mehus
 */
public class MobileController extends MultiActionController {

    private SettingsService settingsService;
    private PlayerService playerService;
    private MusicFileService musicFileService;
    private MusicIndexService musicIndexService;
    private TranscodingService transcodingService;
    private SecurityService securityService;
    private VersionService versionService;

    public ModelAndView playerJad(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Map<String, Object> map = new HashMap<String, Object>();

        // Note: The MIDP specification requires that the version is of the form X.Y[.Z], where X, Y, Z are
        // integers betweeen 0 and 99.
        String version = versionService.getLocalVersion().toString().replaceAll("\\.beta.*", "");

        map.put("baseUrl", getBaseUrl(request));
        map.put("jarSize", getJarSize());
        map.put("version", version);
        return new ModelAndView("mobile/playerJad", "model", map);
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

    public ModelAndView getIndexes(HttpServletRequest request, HttpServletResponse response) throws Exception {
        authenticate(request);

        response.setContentType("text/xml");
        response.setCharacterEncoding(StringUtil.ENCODING_UTF8);
        PrintWriter out = response.getWriter();
        SortedMap<MusicIndex, SortedSet<MusicIndex.Artist>> indexedArtists = musicIndexService.getIndexedArtists(settingsService.getAllMusicFolders());

        // TODO: Use XMLWriter.
        out.println("<?xml version='1.0' encoding='UTF-8'?>");
        out.println("<indexes>");
        for (Map.Entry<MusicIndex, SortedSet<MusicIndex.Artist>> entry : indexedArtists.entrySet()) {
            out.println(" <index name='" + entry.getKey().getIndex() + "'>");
            for (MusicIndex.Artist artist : entry.getValue()) {
                for (MusicFile musicFile : artist.getMusicFiles()) {
                    if (musicFile.isDirectory()) {
                        out.println("  <artist name='" + artist.getName() + "' path='" + StringUtil.utf8HexEncode(musicFile.getPath()) + "'/>");
                    }
                }
            }
            out.println(" </index>");
        }
        out.println("</indexes>");
        return null;
    }

    public ModelAndView getMusicDirectory(HttpServletRequest request, HttpServletResponse response) throws Exception {
        authenticate(request);

        Player player = playerService.getPlayer(request, response);

        MusicFile musicFile = musicFileService.getMusicFile(request.getParameter("path"));
        String baseUrl = getBaseUrl(request);

        // TODO: Share code with M3UController.
        // TODO: Make it work with SSL.
        response.setContentType("text/xml");
        response.setCharacterEncoding(StringUtil.ENCODING_UTF8);
        PrintWriter out = response.getWriter();
        out.println("<?xml version='1.0' encoding='UTF-8'?>");
        out.println("<directory name='" + musicFile.getName() + "' path='" + StringUtil.utf8HexEncode(musicFile.getPath()) + "'>");

        // TODO: Do not include contentType and URL if directory.
        for (MusicFile child : musicFile.getChildren(true, true)) {
            String suffix = transcodingService.getSuffix(player, child);
            String contentType = StringUtil.getMimeType(suffix);
            String url = baseUrl + "stream?pathUtf8Hex=" + StringUtil.utf8HexEncode(child.getPath()) + "&mobile";
            String path = StringUtil.utf8HexEncode(child.getPath());
            out.println("<child name='" + child.getTitle() + "' path='" + path + "' isDir='" + child.isDirectory() +
                        "' contentType='" + contentType + "' url='" + url + "'/>");
        }

        out.println("</directory>");
        return null;
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

    private void authenticate(HttpServletRequest request) {
        // TODO: What about LDAP-authenticated users?
        String username = request.getParameter("u");
        String password = request.getParameter("p");
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

    public void setSecurityService(SecurityService securityService) {
        this.securityService = securityService;
    }

    public void setMusicFileService(MusicFileService musicFileService) {
        this.musicFileService = musicFileService;
    }

    public void setMusicIndexService(MusicIndexService musicIndexService) {
        this.musicIndexService = musicIndexService;
    }

    public void setTranscodingService(TranscodingService transcodingService) {
        this.transcodingService = transcodingService;
    }

    public void setVersionService(VersionService versionService) {
        this.versionService = versionService;
    }
}