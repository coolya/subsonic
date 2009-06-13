/*
 * This file is part of Subsonic. Subsonic is free software: you can
 * redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version. Subsonic is distributed in
 * the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the
 * implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See
 * the GNU General Public License for more details. You should have received a
 * copy of the GNU General Public License along with Subsonic. If not, see
 * <http://www.gnu.org/licenses/>. Copyright 2009 (C) Sindre Mehus
 */
package net.sourceforge.subsonic.controller;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sourceforge.subsonic.ajax.RenameService;
import net.sourceforge.subsonic.controller.EditTagsController.Song;
import net.sourceforge.subsonic.domain.MetaDataParser;
import net.sourceforge.subsonic.domain.MusicFile;
import net.sourceforge.subsonic.service.MusicFileService;
import net.sourceforge.subsonic.service.SettingsService;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.ParameterizableViewController;

/**
 * Controller for the page used to edit MP3 tags.
 * @author crisBtreets
 */
public class RenameController extends ParameterizableViewController {

  private SettingsService mSettingsService;

  private MusicFileService mMusicFileService;

  public void setMusicFileService(MusicFileService pMusicFileService) {
    mMusicFileService = pMusicFileService;
  }

  /**
   * @param pSettingsService the settingsService to set
   */
  public void setSettingsService(SettingsService pSettingsService) {
    mSettingsService = pSettingsService;
  }

  private Song createSong(MusicFile file, int index) {
    MetaDataParser parser = MetaDataParser.Factory.getInstance()
        .getParser(file);
    MusicFile.MetaData metaData = parser.getRawMetaData(file);

    Song song = new Song();
    song.setPath(file.getPath());
    song.setFileName(file.getName());
    song.setTrack(metaData.getTrackNumber());
    song.setSuggestedTrack(index + 1);
    song.setTitle(metaData.getTitle());
    song.setSuggestedTitle(parser.guessTitle(file));
    song.setArtist(metaData.getArtist());
    song.setAlbum(metaData.getAlbum());
    song.setYear(metaData.getYear());
    song.setGenre(metaData.getGenre());
    return song;
  }

  @Override
  protected ModelAndView handleRequestInternal(HttpServletRequest request,
      HttpServletResponse response) throws Exception {

    String path = request.getParameter("path");
    MusicFile dir = mMusicFileService.getMusicFile(path);
    List<MusicFile> files = dir.getChildren(false, true);

    Map<String, Object> map = new HashMap<String, Object>();

    List<Song> songs = new ArrayList<Song>();
    for (int i = 0; i < files.size(); i++) {
      songs.add(createSong(files.get(i), i));
    }
    map.put("path", path);
    map.put("songs", songs);
    map.put("pattern", RenameService.DEFAULT_PATTERN);
    // TODO make default pattern a setting
    // TODO pattern for multi artist albums
    map.put("musicFolders", mSettingsService.getAllMusicFolders());
    map.put("albumPattern", RenameService.ALBUM_PATTERN);
    map.put("artistPattern", RenameService.ARTIST_PATTERN);
    map.put("genrePattern", RenameService.GENRE_PATTERN);
    map.put("titlePattern", RenameService.TITLE_PATTERN);
    map.put("yearPattern", RenameService.YEAR_PATTERN);
    map.put("trackPattern", RenameService.TRACK_NUMBER_PATTERN);
    map.put("directorySep", File.separator);

    ModelAndView result = super.handleRequestInternal(request, response);
    result.addObject("model", map);
    return result;
  }
}
