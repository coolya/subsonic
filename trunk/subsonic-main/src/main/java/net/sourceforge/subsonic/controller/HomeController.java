/*
 This file is part of Subsonic.

 Subsonic is free software: you can redistribute it and/or modify
 it under the terms of the GNU General Public License as published by
 the Free Software Foundation, either version 3 of the License, or
 (at your option) any later version.

 Subsonic is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with Subsonic.  If not, see <http://www.gnu.org/licenses/>.

 Copyright 2009 (C) Sindre Mehus
 */
package net.sourceforge.subsonic.controller;

import net.sourceforge.subsonic.Logger;
import net.sourceforge.subsonic.domain.MusicFile;
import net.sourceforge.subsonic.domain.MusicFileInfo;
import net.sourceforge.subsonic.domain.User;
import net.sourceforge.subsonic.service.MusicFileService;
import net.sourceforge.subsonic.service.MusicInfoService;
import net.sourceforge.subsonic.service.SearchService;
import net.sourceforge.subsonic.service.SecurityService;
import net.sourceforge.subsonic.service.SettingsService;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.ParameterizableViewController;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Controller for the home page.
 *
 * @author Sindre Mehus
 */
public class HomeController extends ParameterizableViewController {

    private static final Logger LOG = Logger.getLogger(HomeController.class);

    private static final int DEFAULT_LIST_SIZE    =   10;
    private static final int MAX_LIST_SIZE        =  500;
    private static final int DEFAULT_LIST_OFFSET  =    0;
    private static final int MAX_LIST_OFFSET      = 5000;

    private SettingsService settingsService;
    private SearchService searchService;
    private MusicInfoService musicInfoService;
    private MusicFileService musicFileService;
    private SecurityService securityService;

    protected ModelAndView handleRequestInternal(HttpServletRequest request, HttpServletResponse response) throws Exception {

        User user = securityService.getCurrentUser(request);
        if (user.isAdminRole() && settingsService.isGettingStartedEnabled()) {
            return new ModelAndView(new RedirectView("gettingStarted.view"));
        }

        int listSize = DEFAULT_LIST_SIZE;
        int listOffset = DEFAULT_LIST_OFFSET;
        if (request.getParameter("listSize") != null) {
            listSize = Math.max(0, Math.min(Integer.parseInt(request.getParameter("listSize")), MAX_LIST_SIZE));
        }
        if (request.getParameter("listOffset") != null) {
            listOffset = Math.max(0, Math.min(Integer.parseInt(request.getParameter("listOffset")), MAX_LIST_OFFSET));
        }

        String listType = request.getParameter("listType");
        if (listType == null) {
            listType = "random";
        }

        List<Album> albums;
        if ("highest".equals(listType)) {
            albums = getHighestRated(listOffset, listSize);
        } else if ("frequent".equals(listType)) {
            albums = getMostFrequent(listOffset, listSize);
        } else if ("recent".equals(listType)) {
            albums = getMostRecent(listOffset, listSize);
        } else if ("newest".equals(listType)) {
            albums = getNewest(listOffset, listSize);
        } else {
            albums = getRandom(listSize);
        }

        Map<String, Object> map = new HashMap<String, Object>();
        map.put("albums", albums);
        map.put("welcomeTitle", settingsService.getWelcomeTitle());
        map.put("welcomeSubtitle", settingsService.getWelcomeSubtitle());
        map.put("welcomeMessage", settingsService.getWelcomeMessage());
        map.put("isIndexBeingCreated", searchService.isIndexBeingCreated());
        map.put("listType", listType);
        map.put("listSize", listSize);
        map.put("listOffset", listOffset);

        ModelAndView result = super.handleRequestInternal(request, response);
        result.addObject("model", map);
        return result;
    }

    List<Album> getHighestRated(int offset, int count) {
        List<Album> result = new ArrayList<Album>();
        for (MusicFile musicFile : musicInfoService.getHighestRated(offset, count)) {
            Album album = createAlbum(musicFile);
            if (album != null) {
                album.setRating((int) Math.round(musicInfoService.getAverageRating(musicFile) * 10.0D));
                result.add(album);
            }
        }
        return result;
    }

    List<Album> getMostFrequent(int offset, int count) {
        List<Album> result = new ArrayList<Album>();
        for (MusicFileInfo info : musicInfoService.getMostFrequentlyPlayed(offset, count)) {
            Album album = createAlbum(info);
            if (album != null) {
                album.setPlayCount(info.getPlayCount());
                result.add(album);
            }
        }
        return result;
    }

    List<Album> getMostRecent(int offset, int count) {
        List<Album> result = new ArrayList<Album>();
        for (MusicFileInfo info : musicInfoService.getMostRecentlyPlayed(offset, count)) {
            Album album = createAlbum(info);
            if (album != null) {
                album.setLastPlayed(info.getLastPlayed());
                result.add(album);
            }
        }
        return result;
    }

    List<Album> getNewest(int offset, int count) throws IOException {
        List<Album> result = new ArrayList<Album>();
        for (MusicFile file : searchService.getNewestAlbums(offset, count)) {
            Album album = createAlbum(file);
            if (album != null) {
                Date created = searchService.getCreationDate(file);
                if (created == null) {
                    created = new Date(file.lastModified());
                }
                album.setCreated(created);
                result.add(album);
            }
        }
        return result;
    }

    List<Album> getRandom(int count) throws IOException {
        List<Album> result = new ArrayList<Album>();
        for (MusicFile file : searchService.getRandomAlbums(count)) {
            Album album = createAlbum(file);
            if (album != null) {
                result.add(album);
            }
        }
        return result;
    }

    private Album createAlbum(MusicFile file) {
        Album album = new Album();
        album.setPath(file.getPath());
        try {
            resolveArtistAndAlbumTitle(album, file);
            resolveCoverArt(album, file);
        } catch (Exception x) {
            LOG.warn("Failed to create albumTitle list entry for " + file.getPath(), x);
            return null;
        }
        return album;
    }

    private Album createAlbum(MusicFileInfo info) {
        try {
            return createAlbum(musicFileService.getMusicFile(info.getPath()));
        } catch (Exception x) {
            LOG.warn("Failed to create albumTitle list entry for " + info.getPath(), x);
            return null;
        }
    }

    private void resolveArtistAndAlbumTitle(Album album, MusicFile file) throws IOException {

        // If directory, find  title and artist from metadata in child.
        if (file.isDirectory()) {
            file = file.getFirstChild();
            if (file == null) {
                return;
            }
        }

        album.setArtist(file.getMetaData().getArtist());
        album.setAlbumTitle(file.getMetaData().getAlbum());
    }

    private void resolveCoverArt(Album album, MusicFile file) {
        try {
            if (file.isFile()) {
                file = file.getParent();
            }

            if (file.isRoot()) {
                return;
            }
            File coverArt = musicFileService.getCoverArt(file);
            if (coverArt != null) {
                album.setCoverArtPath(coverArt.getPath());
            }
        } catch (IOException x) {
            LOG.warn("Failed to resolve cover art for " + file, x);
        }
    }

    public void setSettingsService(SettingsService settingsService) {
        this.settingsService = settingsService;
    }

    public void setSearchService(SearchService searchService) {
        this.searchService = searchService;
    }

    public void setMusicInfoService(MusicInfoService musicInfoService) {
        this.musicInfoService = musicInfoService;
    }

    public void setMusicFileService(MusicFileService musicFileService) {
        this.musicFileService = musicFileService;
    }

    public void setSecurityService(SecurityService securityService) {
        this.securityService = securityService;
    }


    /**
     * Contains info for a single album.
     */
    public static class Album {
        private String path;
        private String coverArtPath;
        private String artist;
        private String albumTitle;
        private Date created;
        private Date lastPlayed;
        private Integer playCount;
        private Integer rating;

        public String getPath() {
            return path;
        }

        public void setPath(String path) {
            this.path = path;
        }

        public String getCoverArtPath() {
            return coverArtPath;
        }

        public void setCoverArtPath(String coverArtPath) {
            this.coverArtPath = coverArtPath;
        }

        public String getArtist() {
            return artist;
        }

        public void setArtist(String artist) {
            this.artist = artist;
        }

        public String getAlbumTitle() {
            return albumTitle;
        }

        public void setAlbumTitle(String albumTitle) {
            this.albumTitle = albumTitle;
        }

        public Date getCreated() {
            return created;
        }

        public void setCreated(Date created) {
            this.created = created;
        }

        public Date getLastPlayed() {
            return lastPlayed;
        }

        public void setLastPlayed(Date lastPlayed) {
            this.lastPlayed = lastPlayed;
        }

        public Integer getPlayCount() {
            return playCount;
        }

        public void setPlayCount(Integer playCount) {
            this.playCount = playCount;
        }

        public Integer getRating() {
            return rating;
        }

        public void setRating(Integer rating) {
            this.rating = rating;
        }
    }
}
