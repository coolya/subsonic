package net.sourceforge.subsonic.ajax;

import net.sourceforge.subsonic.*;
import net.sourceforge.subsonic.util.*;
import net.sourceforge.subsonic.domain.*;

/**
 * Provides AJAX-enabled services for editing tags in music files.
 * This class is used by the DWR framework (http://getahead.ltd.uk/dwr/).
 *
 * @author Sindre Mehus
 * @version $Revision: 1.1 $ $Date: 2006/02/26 21:46:28 $
 */
public class TagService {

    private static final Logger LOG = Logger.getLogger(TagService.class);

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

        artist = "".equals(artist) ? null : artist;
        album = "".equals(album) ? null : album;
        title = "".equals(title) ? null : title;
        year = "".equals(year) ? null : year;

        try {

            Mp3Parser parser = new Mp3Parser();
            MusicFile file = new MusicFile(path);

            if (!parser.isApplicable(file)) {
                return "Tag editing of "+ StringUtil.getSuffix(file.getName()) + " files is not supported.";
            }

            MusicFile.MetaData existingMetaData = parser.getRawMetaData(file);
            MusicFile.MetaData newMetaData = new MusicFile.MetaData(artist, album, title, year);

            if (!newMetaData.equals(existingMetaData)) {
                parser.setMetaData(file, newMetaData);
                return "UPDATED";
            }
            return "SKIPPED";

        } catch (Exception x) {
            LOG.warn("Failed to update tags for " + path, x);
            return x.getMessage();
        }
    }
}
