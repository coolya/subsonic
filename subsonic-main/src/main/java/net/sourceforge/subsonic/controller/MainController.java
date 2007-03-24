package net.sourceforge.subsonic.controller;

import net.sourceforge.subsonic.domain.CoverArtScheme;
import net.sourceforge.subsonic.domain.MusicFile;
import net.sourceforge.subsonic.domain.MusicFileInfo;
import net.sourceforge.subsonic.domain.Player;
import net.sourceforge.subsonic.service.AdService;
import net.sourceforge.subsonic.service.MusicFileService;
import net.sourceforge.subsonic.service.MusicInfoService;
import net.sourceforge.subsonic.service.PlayerService;
import net.sourceforge.subsonic.service.SecurityService;
import net.sourceforge.subsonic.service.SettingsService;
import org.apache.commons.lang.StringUtils;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.ParameterizableViewController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.LinkedList;

/**
 * Controller for the main page.
 *
 * @author Sindre Mehus
 */
public class MainController extends ParameterizableViewController {

    private SecurityService securityService;
    private PlayerService playerService;
    private SettingsService settingsService;
    private MusicInfoService musicInfoService;
    private MusicFileService musicFileService;
    private AdService adService;

    protected ModelAndView handleRequestInternal(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Map<String, Object> map = new HashMap<String, Object>();

        Player player = playerService.getPlayer(request, response);
        String path = request.getParameter("path");
        MusicFile dir = musicFileService.getMusicFile(path);
        List<MusicFile> children = dir.getChildren(true, true);

        map.put("dir", dir);
        map.put("ancestors", getAncestors(dir));
        map.put("children", children);
        map.put("player", player);
        map.put("user", securityService.getCurrentUser(request));
        map.put("multipleArtists", isMultipleArtists(children));
        map.put("visibility", settingsService.getUserSettings(securityService.getCurrentUsername(request)).getMainVisibility());
        map.put("updateNowPlaying", request.getParameter("updateNowPlaying") != null);
        map.put("adReferrer", adService.getAdReferrer(dir));

        MusicFileInfo musicInfo = musicInfoService.getMusicFileInfoForPath(path);
        int playCount = musicInfo == null ? 0 : musicInfo.getPlayCount();
        String comment = musicInfo == null ? null : musicInfo.getComment();
        Date lastPlayed = musicInfo == null ? null : musicInfo.getLastPlayed();
        String username = securityService.getCurrentUsername(request);
        Integer userRating = musicInfoService.getRatingForUser(username, dir);
        Double averageRating = musicInfoService.getAverageRating(dir);

        if (userRating == null) {
            userRating = 0;
        }

        if (averageRating == null) {
            averageRating = 0.0D;
        }

        map.put("userRating", 10 * userRating);
        map.put("averageRating", Math.round(10.0D * averageRating));
        map.put("playCount", playCount);
        map.put("comment", comment);
        map.put("lastPlayed", lastPlayed);

        CoverArtScheme scheme = player.getCoverArtScheme();
        if (scheme != CoverArtScheme.OFF) {
            int limit = settingsService.getCoverArtLimit();
            if (limit == 0) {
                limit = Integer.MAX_VALUE;
            }
            List<File> coverArts = musicFileService.getCoverArt(dir, limit);
            int size = coverArts.size() > 1 ? scheme.getSize() : scheme.getSize() * 2;
            map.put("coverArts", coverArts);
            map.put("coverArtSize", size);
            if (coverArts.isEmpty() && dir.isAlbum()) {
                map.put("showGenericCoverArt", true);
            }

        }

        setPreviousAndNextAlbums(dir, map);

        ModelAndView result = super.handleRequestInternal(request, response);
        result.addObject("model", map);
        return result;
    }

    private List<MusicFile> getAncestors(MusicFile dir) throws IOException {
        LinkedList<MusicFile> result = new LinkedList<MusicFile>();

        MusicFile parent = dir.getParent();
        while (parent != null && !parent.isRoot()) {
            result.addFirst(parent);
            parent = parent.getParent();
        }
        return result;
    }

    private void setPreviousAndNextAlbums(MusicFile dir, Map<String, Object> map) throws IOException {
        if (dir.isAlbum() && !dir.getParent().isRoot()) {
            List<MusicFile> sieblings = dir.getParent().getChildren(true, true);
            for (Iterator<MusicFile> iterator = sieblings.iterator(); iterator.hasNext();) {
                MusicFile siebling = iterator.next();
                if (siebling.isFile()) {
                    iterator.remove();
                }
            }

            int index = sieblings.indexOf(dir);
            if (index > 0) {
                map.put("previousAlbum", sieblings.get(index - 1));
            }
            if (index < sieblings.size() - 1) {
                map.put("nextAlbum", sieblings.get(index + 1));
            }
        }
    }

    private boolean isMultipleArtists(List<MusicFile> children) {
        // Collect unique artist names.
        Set<String> artists = new HashSet<String>();
        for (MusicFile child : children) {
            MusicFile.MetaData metaData = child.getMetaData();
            if (metaData != null && metaData.getArtist() != null) {
                artists.add(metaData.getArtist().toLowerCase());
            }
        }

        // If zero or one artist, it is definitely not multiple artists.
        if (artists.size() < 2) {
            return false;
        }

        // Fuzzily compare artist names, allowing for some differences in spelling, whitespace etc.
        List<String> artistList = new ArrayList<String>(artists);
        for (String artist : artistList) {
            if (StringUtils.getLevenshteinDistance(artist, artistList.get(0)) > 3) {
                return true;
            }
        }
        return false;
    }

    public void setSecurityService(SecurityService securityService) {
        this.securityService = securityService;
    }

    public void setPlayerService(PlayerService playerService) {
        this.playerService = playerService;
    }

    public void setSettingsService(SettingsService settingsService) {
        this.settingsService = settingsService;
    }

    public void setMusicInfoService(MusicInfoService musicInfoService) {
        this.musicInfoService = musicInfoService;
    }

    public void setMusicFileService(MusicFileService musicFileService) {
        this.musicFileService = musicFileService;
    }

    public void setAdService(AdService adService) {
        this.adService = adService;
    }
}
