package net.sourceforge.subsonic.ajax;

import net.sourceforge.subsonic.*;
import net.sourceforge.subsonic.domain.*;
import net.sourceforge.subsonic.service.*;
import net.sourceforge.subsonic.util.*;
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
     * @param track The track number.
     * @param artist The artist name.
     * @param album The album name.
     * @param title The song title.
     * @param year The release year.
    * @return "UPDATED" if the new tags were updated, "SKIPPED" if no update was necessary.
    *          Otherwise the error message is returned.
    */
    public String setTags(String path, String track, String artist, String album, String title, String year) {

        track =  StringUtils.trimToNull(track);
        artist = StringUtils.trimToNull(artist);
        album = StringUtils.trimToNull(album);
        title = StringUtils.trimToNull(title);
        year = StringUtils.trimToNull(year);

        Integer trackNumber = null;
        if (track != null) {
            try {
                trackNumber = new Integer(track);
            } catch (NumberFormatException x) {
                LOG.warn("Illegal track number: " + track, x);
            }
        }

        try {

            // TODO: Support OGG.
            Mp3Parser parser = new Mp3Parser();
            MusicFile file = musicFileService.getMusicFile(path);

            if (!parser.isApplicable(file)) {
                return "Tag editing of "+ StringUtil.getSuffix(file.getName()) + " files is not supported.";
            }

            MusicFile.MetaData existingMetaData = parser.getRawMetaData(file);

            if (StringUtils.equals(artist, existingMetaData.getArtist()) &&
                StringUtils.equals(album, existingMetaData.getAlbum()) &&
                StringUtils.equals(title, existingMetaData.getTitle()) &&
                StringUtils.equals(year, existingMetaData.getYear()) &&
                ObjectUtils.equals(trackNumber, existingMetaData.getTrackNumber())) {
                return "SKIPPED";
            }

            MusicFile.MetaData newMetaData = new MusicFile.MetaData();
            newMetaData.setArtist(artist);
            newMetaData.setAlbum(album);
            newMetaData.setTitle(title);
            newMetaData.setYear(year);
            newMetaData.setTrackNumber(trackNumber);
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
