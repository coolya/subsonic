package net.sourceforge.subsonic.ajax;

import net.sourceforge.subsonic.*;
import net.sourceforge.subsonic.service.*;
import net.sourceforge.subsonic.util.*;
import net.sourceforge.subsonic.domain.*;
import org.apache.commons.lang.*;

/**
 * Provides AJAX-enabled services for editing tags in music files.
 * This class is used by the DWR framework (http://getahead.ltd.uk/dwr/).
 *
 * @author Sindre Mehus
 */
public class TagService {

    private static final Logger LOG = Logger.getLogger(TagService.class);

    private MusicFileService musicFileService;

    /**
    * Updated tags for a given music file.
    * @param path The path of the music file.
    * @param artist The artist name.
    * @param album The album name.
    * @param title The song title.
    * @param year The release year.
    * @return "UPDATED" if the new tags were updated, "SKIPPED" if no update was necessary.
    *          Otherwise the error message is returned.
    */
    public String setTags(String path, String artist, String album, String title, String year) {

        artist = StringUtils.trimToNull(artist);
        album = StringUtils.trimToNull(album);
        title = StringUtils.trimToNull(title);
        year = StringUtils.trimToNull(year);

        try {

            Mp3Parser parser = new Mp3Parser();
            MusicFile file = musicFileService.createMusicFile(path);

            if (!parser.isApplicable(file)) {
                return "Tag editing of "+ StringUtil.getSuffix(file.getName()) + " files is not supported.";
            }

            MusicFile.MetaData existingMetaData = parser.getRawMetaData(file);

            if (StringUtils.equals(artist, existingMetaData.getArtist()) &&
                StringUtils.equals(album, existingMetaData.getAlbum()) &&
                StringUtils.equals(title, existingMetaData.getTitle()) &&
                StringUtils.equals(year, existingMetaData.getYear())) {

                return "SKIPPED";
            }

            MusicFile.MetaData newMetaData = new MusicFile.MetaData();
            newMetaData.setArtist(artist);
            newMetaData.setAlbum(album);
            newMetaData.setTitle(title);
            newMetaData.setYear(year);
            parser.setMetaData(file, newMetaData);
            return "UPDATED";

        } catch (Exception x) {
            LOG.warn("Failed to update tags for " + path, x);
            return x.getMessage();
        }
    }

    public void setMusicFileService(MusicFileService musicFileService) {
        this.musicFileService = musicFileService;
    }
}
