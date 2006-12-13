package net.sourceforge.subsonic.controller;

import net.sourceforge.subsonic.domain.*;
import net.sourceforge.subsonic.service.*;
import org.springframework.web.servlet.*;
import org.springframework.web.servlet.mvc.*;

import javax.servlet.http.*;
import java.util.*;

/**
 * Controller for the page used to edit MP3 tags.
 *
 * @author Sindre Mehus
 */
public class EditTagsController extends ParameterizableViewController {

    private MusicFileService musicFileService;

    protected ModelAndView handleRequestInternal(HttpServletRequest request, HttpServletResponse response) throws Exception {

        String path = request.getParameter("path");
        MusicFile dir = musicFileService.getMusicFile(path);
        List<MusicFile> files = dir.getChildren(false, true);

        Map<String, Object> map = new HashMap<String, Object>();
        if (!files.isEmpty()) {
            MusicFile.MetaData metaData = files.get(0).getMetaData();
            map.put("defaultArtist", metaData.getArtist());
            map.put("defaultAlbum", metaData.getAlbum());
            map.put("defaultYear", metaData.getYear());
        }

        List<Song> songs = new ArrayList<Song>();
        for (MusicFile file : files) {
            songs.add(createSong(file));
        }
        map.put("path", path);
        map.put("songs", songs);

        ModelAndView result = super.handleRequestInternal(request, response);
        result.addObject("model", map);
        return result;
    }

    private Song createSong(MusicFile file) {
        //  TODO: Support OGG
        Mp3Parser parser = new Mp3Parser();
        MusicFile.MetaData metaData = parser.isApplicable(file) ? parser.getRawMetaData(file) : new MusicFile.MetaData();

        Song song = new Song();
        song.setPath(file.getPath());
        song.setFileName(file.getName());
        song.setSuggestedTitle(parser.guessTitle(file));
        song.setTrack(metaData.getTrackNumber());
        song.setTitle(metaData.getTitle());
        song.setArtist(metaData.getArtist());
        song.setAlbum(metaData.getAlbum());
        song.setYear(metaData.getYear());
        return song;
    }

    public void setMusicFileService(MusicFileService musicFileService) {
        this.musicFileService = musicFileService;
    }

    /**
     * Contains information about a single song.
     */
    public static class Song {
        private String path;
        private String fileName;
        private Integer track;
        private String suggestedTitle;
        private String title;
        private String artist;
        private String album;
        private String year;

        public String getPath() {
            return path;
        }

        public void setPath(String path) {
            this.path = path;
        }

        public String getFileName() {
            return fileName;
        }

        public void setFileName(String fileName) {
            this.fileName = fileName;
        }

        public Integer getTrack() {
            return track;
        }

        public void setTrack(Integer track) {
            this.track = track;
        }

        public String getSuggestedTitle() {
            return suggestedTitle;
        }

        public void setSuggestedTitle(String suggestedTitle) {
            this.suggestedTitle = suggestedTitle;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getArtist() {
            return artist;
        }

        public void setArtist(String artist) {
            this.artist = artist;
        }

        public String getAlbum() {
            return album;
        }

        public void setAlbum(String album) {
            this.album = album;
        }

        public String getYear() {
            return year;
        }

        public void setYear(String year) {
            this.year = year;
        }
    }
}
