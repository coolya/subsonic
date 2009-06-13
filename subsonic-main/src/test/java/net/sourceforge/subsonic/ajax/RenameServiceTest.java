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

import junit.extensions.PrivilegedAccessor;
import net.sourceforge.subsonic.domain.MusicFile;
import net.sourceforge.subsonic.domain.MusicFile.MetaData;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author crisbtreets
 */
public class RenameServiceTest {

  // Constants ----------------------------------------------------------------

  // Member Variables ---------------------------------------------------------

  // Public Methods -----------------------------------------------------------

  /**
   * Test method for
   * {@link RenameService#getMusicFileDestinationPath(MetaData, String)}.
   * @throws Exception Doesn't catch any exceptions thrown
   */
  @Test
  public void testGetMusicFileDestinationPath() throws Exception {
    RenameService renameService = new RenameService();
    Object returnVal = PrivilegedAccessor.invokeMethod(renameService,
        "getMusicFileDestinationPath("
            + "net.sourceforge.subsonic.domain.MusicFile$MetaData,"
            + "java.lang.String))", new Object[] { null,
            RenameService.DEFAULT_PATTERN });

    Assert.assertNull("Didn't return null for null MetaData", returnVal);

    MetaData mdNull = new MusicFile.MetaData();

    String allPattern = RenameService.GENRE_PATTERN + File.separator
        + RenameService.ARTIST_PATTERN + File.separator
        + RenameService.YEAR_PATTERN + File.separator
        + RenameService.ALBUM_PATTERN + File.separator
        + RenameService.TRACK_NUMBER_PATTERN + " "
        + RenameService.TITLE_PATTERN;

    String nullRenamedStr = RenameService.UNKNOWN_GENRE + File.separator
        + RenameService.UNKNOWN_ARTIST + File.separator
        + RenameService.UNKNOWN_YEAR + File.separator
        + RenameService.UNKNOWN_ALBUM + File.separator
        + RenameService.UNKNOWN_TRACK + " " + RenameService.UNKNOWN_TITLE;
    returnVal = PrivilegedAccessor.invokeMethod(renameService,
        "getMusicFileDestinationPath("
            + "net.sourceforge.subsonic.domain.MusicFile$MetaData,"
            + "java.lang.String))", new Object[] { mdNull, allPattern });
    Assert.assertEquals("Test of Rename of null values", nullRenamedStr,
        returnVal);

    MetaData md1 = new MusicFile.MetaData();
    md1.setTrackNumber(Integer.valueOf(1));
    md1.setArtist("Artist");
    md1.setAlbum("Album");
    md1.setTitle("Title");

    returnVal = PrivilegedAccessor.invokeMethod(renameService,
        "getMusicFileDestinationPath("
            + "net.sourceforge.subsonic.domain.MusicFile$MetaData,"
            + "java.lang.String))", new Object[] { md1,
            RenameService.DEFAULT_PATTERN });
    Assert.assertEquals("Test of Default Pattern with padding of 2", "Artist"
        + File.separator + "Album" + File.separator + "01 Title", returnVal);

    String patternPad1 = RenameService.ARTIST_PATTERN + File.separator
        + RenameService.ALBUM_PATTERN + File.separator
        + RenameService.TRACK_NUMBER_PATTERN + " "
        + RenameService.TITLE_PATTERN;

    returnVal = PrivilegedAccessor.invokeMethod(renameService,
        "getMusicFileDestinationPath("
            + "net.sourceforge.subsonic.domain.MusicFile$MetaData,"
            + "java.lang.String))", new Object[] { md1, patternPad1 });

    Assert.assertEquals("Test of Default Pattern with padding of 1", "Artist"
        + File.separator + "Album" + File.separator + "1 Title", returnVal);

    MetaData md21 = new MusicFile.MetaData();
    md21.setTrackNumber(Integer.valueOf(21));
    md21.setArtist("Artist");
    md21.setAlbum("Album");
    md21.setTitle("Title");
    md21.setGenre("Genre");
    md21.setYear("Year");

    returnVal = PrivilegedAccessor.invokeMethod(renameService,
        "getMusicFileDestinationPath("
            + "net.sourceforge.subsonic.domain.MusicFile$MetaData,"
            + "java.lang.String))", new Object[] { md21, allPattern });

    Assert.assertEquals("Test of All patterns with padding of 1", "Genre"
        + File.separator + "Artist" + File.separator + "Year" + File.separator
        + "Album" + File.separator + "21 Title", returnVal);
  }

  // Private Methods ----------------------------------------------------------
}
