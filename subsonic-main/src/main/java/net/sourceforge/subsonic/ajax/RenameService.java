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
package net.sourceforge.subsonic.ajax;

import java.io.File;
import java.util.Calendar;

import net.sf.ehcache.Ehcache;
import net.sourceforge.subsonic.Logger;
import net.sourceforge.subsonic.domain.MusicFile;
import net.sourceforge.subsonic.domain.MusicFile.MetaData;
import net.sourceforge.subsonic.service.MusicFileService;

import org.apache.commons.lang.StringUtils;

/**
 * Provides AJAX-enabled services for renaming music files. This class is used
 * by the DWR framework (http://getahead.ltd.uk/dwr/).
 * @author crisbtreets
 */
public class RenameService {

  // Constants ----------------------------------------------------------------

  /**
   * Pattern for Track Number.
   */
  public static final char TRACK_NUMBER_PATTERN = '#';
  /**
   * Pattern for Title.
   */
  public static final String TITLE_PATTERN = "%title%";
  /**
   * Pattern for Artist.
   */
  public static final String ARTIST_PATTERN = "%artist%";
  /**
   * Pattern for Album.
   */
  public static final String ALBUM_PATTERN = "%album%";
  /**
   * Pattern for Genre.
   */
  public static final String GENRE_PATTERN = "%genre%";
  /**
   * Pattern for Year.
   */
  public static final String YEAR_PATTERN = "%year%";

  /**
   * Default pattern of "Artist/Album/# Title".
   */
  public static final String DEFAULT_PATTERN = RenameService.ARTIST_PATTERN
      + File.separator + RenameService.ALBUM_PATTERN + File.separator
      + RenameService.TRACK_NUMBER_PATTERN + RenameService.TRACK_NUMBER_PATTERN
      + " " + RenameService.TITLE_PATTERN;

  /**
   * String to use if the Track Number is null.
   */
  public static final String UNKNOWN_TRACK = "00";
  /**
   * String to use if the Title is null.
   */
  public static final String UNKNOWN_TITLE = "Unknown";
  /**
   * String to use if the Artist is null.
   */
  public static final String UNKNOWN_ARTIST = "Unknown Artist";

  /**
   * String to use if the Album is null.
   */
  public static final String UNKNOWN_ALBUM = "Unknown Album";

  /**
   * String to use if the Genre is null.
   */
  public static final String UNKNOWN_GENRE = "Unknown Genre";

  /**
   * String to use if the Year is null. Defaults to current year.
   */
  public static final String UNKNOWN_YEAR = Integer.valueOf(
      Calendar.getInstance().get(Calendar.YEAR)).toString();

  /**
   * Characters that are not allowed in filenames.
   */
  public static final char[] ILLEGAL_FILENAME_CHARS = new char[] { ':', '*',
      '?', '"', '<', '>', '|', '%', '#', '$', '!', '+', '{', '}', '&', '[',
      ']', ';', ',', '¥' };

  /**
   * The characters to replace illegal characters with.
   */
  public static final char REPLACEMENT_FILENAME_CHAR = '_';

  // Member Variables ---------------------------------------------------------

  /**
   * Logger.
   */
  private static final Logger LOG = Logger.getLogger(RenameService.class);

  /**
   * MusicFileService.
   */
  private MusicFileService mMusicFileService;

  /**
   * MusicFileCache.
   */
  private Ehcache mMusicFileCache;

  // Public Methods -----------------------------------------------------------
  /**
   * Gets the renamed file path based on a pattern.
   * @param pPath The path to the music file to move.
   * @param pPattern The pattern to rename the file with.
   * @return The path of the renamed file.
   */
  public String getFileDestinationPath(final String pPath, final String pPattern) {
    File file = new File(pPath);
    MusicFile musicFile = mMusicFileService.getMusicFile(file);

    String destination = getMusicFileDestinationPath(musicFile.getMetaData(),
        pPattern)
        + "." + musicFile.getSuffix();

    return destination;
  }

  /**
   * Renames a music file based on a given pattern.
   * @param pPath The path to the file to rename
   * @param pPattern The pattern used to rename the file
   * @param pMusicRoot The root path to move file to
   * @return The renamed file path
   */
  public String renameMusicFile(String pPath, String pPattern, String pMusicRoot) {
    File file = new File(pPath);
    String destination = getFileDestinationPath(pPath, pPattern);

    File destinationFile = new File(pMusicRoot, destination);
    if (destinationFile.equals(file)) {
      return "SKIPPED";
    }

    if (destinationFile.exists()) {
      LOG.error("Cannot rename " + file.getAbsolutePath() + " to "
          + destinationFile.getAbsolutePath() + ". File already exists.");
      return "ERROR:File already exists:" + destinationFile.getAbsolutePath();
    }
    LOG.debug("Renaming file to " + destinationFile.getAbsolutePath());

    if (!destinationFile.getParentFile().exists()
        && !destinationFile.getParentFile().mkdirs()) {
      return "ERROR:Error creating directory "
          + destinationFile.getParentFile().getAbsolutePath();
    }
    boolean renamePass = file.renameTo(destinationFile);

    if (!renamePass) {
      return "ERROR:Error renaming file from " + file.getAbsolutePath()
          + " to " + destinationFile.getAbsolutePath();
    }

    // TODO move album art

    // remove music file cache
    mMusicFileCache.remove(file);

    // TODO update search engine

    // Delete empty source directory
    File parentSource = file.getParentFile();
    // TODO account for album art
    if (parentSource.listFiles().length == 0) {
      boolean deletedParent = parentSource.delete();
      try {
        if (!deletedParent) {
          LOG.error("Could not delete directory "
              + parentSource.getAbsolutePath());
        }
      }
      catch (Exception e) {
        LOG.error("Could not delete directory "
            + parentSource.getAbsolutePath(), e);
      }
    }

    return destinationFile.getAbsolutePath();

  }

  /**
   * @param pMusicFileCache the musicFileCache to set
   */
  public void setMusicFileCache(Ehcache pMusicFileCache) {
    mMusicFileCache = pMusicFileCache;
  }

  /**
   * @param pMusicFileService the musicFileService to set
   */
  public void setMusicFileService(final MusicFileService pMusicFileService) {
    mMusicFileService = pMusicFileService;
  }

  /**
   * Finds the length of the first instance of the TRACK_NUMER_PATTERN
   * character.
   * @param pPattern The string to search
   * @return The number of TRACK_NUMER_PATTERN characters in a row.
   */
  private int findLenghtOfTrackPattern(final String pPattern) {
    int count = 0;
    int index = pPattern.indexOf(TRACK_NUMBER_PATTERN);
    while (index >= 0 && index < pPattern.length()) {
      if (pPattern.charAt(index) == TRACK_NUMBER_PATTERN) {
        count++;
        index++;
      }
      else {
        break;
      }
    }
    return count;
  }

  // Private Methods ----------------------------------------------------------

  /**
   * Gets the file name for a music file based on the supplied pattern.
   * @param pMetaData The music file meta data
   * @param pPattern The file name pattern for rename
   * @return The string of the renamed file
   */
  private String getMusicFileDestinationPath(final MetaData pMetaData,
      final String pPattern) {

    if (pMetaData == null) {
      return null;
    }

    String destination = pPattern;

    String trackStr = null;
    if (pMetaData.getTrackNumber() == null) {
      trackStr = UNKNOWN_TRACK;
      destination = destination.replaceAll(Character
          .toString(RenameService.TRACK_NUMBER_PATTERN), trackStr);
    }
    else {
      StringBuffer destBuff = new StringBuffer(destination);
      int index = destBuff.indexOf(Character.toString(TRACK_NUMBER_PATTERN));
      while (index >= 0) {
        int trackPadding = findLenghtOfTrackPattern(destBuff.substring(index));
        trackStr = getPaddedInt(pMetaData.getTrackNumber().intValue(),
            trackPadding);
        destBuff.replace(index, index + trackPadding, trackStr);

        index = destBuff.indexOf(Character.toString(TRACK_NUMBER_PATTERN));
      }
      destination = destBuff.toString();
    }

    String titleStr = StringUtils.trimToNull(pMetaData.getTitle());
    if (titleStr == null) {
      titleStr = UNKNOWN_TITLE;
    }
    destination = destination.replaceAll(TITLE_PATTERN, titleStr);

    String artistStr = StringUtils.trimToNull(pMetaData.getArtist());
    if (artistStr == null) {
      artistStr = UNKNOWN_ARTIST;
    }
    destination = destination.replaceAll(ARTIST_PATTERN, artistStr);

    String albumStr = StringUtils.trimToNull(pMetaData.getAlbum());
    if (albumStr == null) {
      albumStr = UNKNOWN_ALBUM;
    }
    destination = destination.replaceAll(ALBUM_PATTERN, albumStr);

    String genreStr = StringUtils.trimToNull(pMetaData.getGenre());
    if (genreStr == null) {
      genreStr = UNKNOWN_GENRE;
    }
    destination = destination.replaceAll(GENRE_PATTERN, genreStr);

    String yearStr = StringUtils.trimToNull(pMetaData.getYear());
    if (yearStr == null) {
      yearStr = UNKNOWN_YEAR;
    }
    destination = destination.replaceAll(YEAR_PATTERN, yearStr);

    destination = replaceIllegalFileNameCharacters(destination,
        ILLEGAL_FILENAME_CHARS, REPLACEMENT_FILENAME_CHAR);
    return destination;
  }

  /**
   * Adds leading zero padding so that a given integer represented as a string
   * is as least a given length.
   * @param pInt The integer to convert to a string with padding.
   * @param pNumCharacters The minimum number of characters the output should
   *          be. Leading zeros are added to reach the minimum length if needed.
   * @return A zero padded string
   */
  private String getPaddedInt(final int pInt, final int pNumCharacters) {
    return String.format("%1$0" + pNumCharacters + "d", Integer.valueOf(pInt));
  }

  /**
   * Replaces all illegal filename characters with a given character.
   * @param pFilename The filename string to replace characters in
   * @param pIllegalChars The illegal characters to replace
   * @param pReplacementChar The character to replace with
   * @return The filename string with illegal characters replaced
   */
  private String replaceIllegalFileNameCharacters(final String pFilename,
      final char[] pIllegalChars, final char pReplacementChar) {
    String returnString = pFilename;
    for (char c : pIllegalChars) {
      returnString = returnString.replace(c, pReplacementChar);
    }
    return returnString;
  }
}
