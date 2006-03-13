package net.sourceforge.subsonic.service;

import net.sourceforge.subsonic.domain.*;

import java.util.*;

/**
 * Provides services for maintaining the list of stream and download status objects.
 *
 * @see StreamStatus
 * @author Sindre Mehus
 * @version $Revision: 1.5 $ $Date: 2006/01/08 17:29:14 $
 */
public class StatusService {

    private List<StreamStatus> streamStatuses = new ArrayList<StreamStatus>();
    private List<DownloadStatus> downloadStatuses = new ArrayList<DownloadStatus>();

    public synchronized void addStreamStatus(StreamStatus status) {
        streamStatuses.add(status);
    }

    public synchronized void removeStreamStatus(StreamStatus status) {
        streamStatuses.remove(status);
    }

    public synchronized StreamStatus[] getAllStreamStatuses() {
        return streamStatuses.toArray(new StreamStatus[0]);
    }

    public synchronized StreamStatus[] getStreamStatusesForPlayer(Player player) {
        List<StreamStatus> result = new ArrayList<StreamStatus>();
        for (StreamStatus status : getAllStreamStatuses()) {
            if (status.getPlayer().getId().equals(player.getId())) {
                result.add(status);
            }
        }
        return result.toArray(new StreamStatus[0]);
    }

    public synchronized void addDownloadStatus(DownloadStatus status) {
        downloadStatuses.add(status);
    }

    public synchronized void removeDownloadStatus(DownloadStatus status) {
        downloadStatuses.remove(status);
    }

    public synchronized DownloadStatus[] getAllDownloadStatuses() {
        return downloadStatuses.toArray(new DownloadStatus[0]);
    }
}