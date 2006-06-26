package net.sourceforge.subsonic.service;

import net.sourceforge.subsonic.dao.*;
import net.sourceforge.subsonic.domain.*;

import java.util.*;

/**
 * Provides services for user rating and comments, as well
 * as details about how often and how recent albums have been played.

 * @author Sindre Mehus
 */
public class MusicInfoService {

    private MusicFileInfoDao musicFileInfoDao = new MusicFileInfoDao();

    /**
    * Returns music file info for the given path.
    * @return Music file info for the given path, or <code>null</code> if not found.
    */
    public MusicFileInfo getMusicFileInfoForPath(String path) {
        return musicFileInfoDao.getMusicFileInfoForPath(path);
    }

    /**
     * Returns info for the highest rated music files.
     * @param offset Number of files to skip.
     * @param count Maximum number of files to return.
     * @return Info for the highest rated music files.
     */
    public MusicFileInfo[] getHighestRated(int offset, int count) {
        return musicFileInfoDao.getHighestRated(offset, count);
    }

    /**
     * Returns info for the most frequently played music files.
     * @param offset Number of files to skip.
     * @param count Maximum number of elements to return.
     * @return Info for the most frequently played music files.
     */
    public MusicFileInfo[] getMostFrequentlyPlayed(int offset, int count) {
        return musicFileInfoDao.getMostFrequentlyPlayed(offset, count);
    }

    /**
     * Returns info for the most recently played music files.
     * @param offset Number of files to skip.
     * @param count Maximum number of elements to return.
     * @return Info for the most recently played music files.
     */
    public MusicFileInfo[] getMostRecentlyPlayed(int offset, int count) {
        return musicFileInfoDao.getMostRecentlyPlayed(offset, count);
    }

    /**
     * Creates a new music file info.
     * @param info The music file info to create.
     */
    public void createMusicFileInfo(MusicFileInfo info) {
        musicFileInfoDao.createMusicFileInfo(info);
    }

    /**
     * Updates the given music file info.
     * @param info The music file info to update.
     */
    public void updateMusicFileInfo(MusicFileInfo info) {
        musicFileInfoDao.updateMusicFileInfo(info);
    }

    /**
     * Increments the play count and last played date for the given music file.
     * @param file The music file.
     */
    public void incrementPlayCount(MusicFile file) {
        MusicFileInfo info = getMusicFileInfoForPath(file.getPath());
        if (info == null) {
            info = new MusicFileInfo(file.getPath());
            info.setLastPlayed(new Date());
            info.setPlayCount(1);
            createMusicFileInfo(info);
        } else {
            info.setLastPlayed(new Date());
            info.setPlayCount(info.getPlayCount() + 1);
            updateMusicFileInfo(info);
        }
    }
}
