package net.sourceforge.subsonic.controller;

import net.sourceforge.subsonic.*;
import net.sourceforge.subsonic.domain.*;
import net.sourceforge.subsonic.service.*;
import org.springframework.web.servlet.*;
import org.springframework.web.servlet.mvc.*;

import javax.servlet.http.*;
import java.io.*;
import java.util.*;

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

    protected ModelAndView handleRequestInternal(HttpServletRequest request, HttpServletResponse response) throws Exception {

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

        // Start creating search index, if not already existing.
        checkIndexCreation();

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
        map.put("welcomeMessage", settingsService.getWelcomeMessage());
        map.put("isIndexBeingCreated", searchService.isIndexBeingCreated());
        map.put("listType", listType);
        map.put("listSize", listSize);
        map.put("listOffset", listOffset);

        ModelAndView result = super.handleRequestInternal(request, response);
        result.addObject("model", map);
        return result;
    }

    private List<Album> getHighestRated(int offset, int count) {
        List<Album> result = new ArrayList<Album>();
        for (MusicFileInfo info : musicInfoService.getHighestRated(offset, count)) {
            Album album = createAlbum(info);
            if (album != null) {
                album.setRating(info.getRating());
                result.add(album);
            }
        }
        return result;
    }

    private List<Album> getMostFrequent(int offset, int count) {
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

    private List<Album> getMostRecent(int offset, int count) {
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

    private List<Album> getNewest(int offset, int count) throws IOException {
        List<Album> result = new ArrayList<Album>();
        for (MusicFile file : searchService.getNewestAlbums(offset, count)) {
            Album album = createAlbum(file);
            if (album != null) {
                album.setLastModified(new Date(file.lastModified()));
                result.add(album);
            }
        }
        return result;
    }

    private List<Album> getRandom(int count) throws IOException {
        List<Album> result = new ArrayList<Album>();
        for (MusicFile file : searchService.getRandomMusicFiles(count)) {
            MusicFile dir = file.getParent();
            if (!dir.isRoot()) {
                Album album = createAlbum(dir);
                if (album != null) {
                    result.add(album);
                }
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
            return createAlbum(new MusicFile(info.getPath()));
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
            File[] coverArts = file.getCoverArt(1);
            if (coverArts.length > 0) {
                album.setCoverArtPath(coverArts[0].getPath());
            }
        } catch (IOException x) {
            LOG.warn("Failed to resolve cover art for " + file, x);
        }
    }

    private void checkIndexCreation() {
        MusicFolder[] folders = settingsService.getAllMusicFolders();

        if (!searchService.isIndexCreated() && folders.length > 0) {
            searchService.createIndex();
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


    /**
     * Contains info for a single album.
     */
    public static class Album {
        private String path;
        private String coverArtPath;
        private String artist;
        private String albumTitle;
        private Date lastModified;
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

        public Date getLastModified() {
            return lastModified;
        }

        public void setLastModified(Date lastModified) {
            this.lastModified = lastModified;
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
