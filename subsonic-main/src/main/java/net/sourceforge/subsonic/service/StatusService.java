package net.sourceforge.subsonic.service;

import net.sourceforge.subsonic.domain.*;

import java.util.*;

/**
 * Provides services for maintaining the list of stream, download and upload statuses.
 *
 * @see TransferStatus
 * @author Sindre Mehus
 * @version $Revision: 1.5 $ $Date: 2006/01/08 17:29:14 $
 */
public class StatusService {

    private List<TransferStatus> streamStatuses = new ArrayList<TransferStatus>();
    private List<TransferStatus> downloadStatuses = new ArrayList<TransferStatus>();
    private List<TransferStatus> uploadStatuses = new ArrayList<TransferStatus>();

    public synchronized void addStreamStatus(TransferStatus status) {
        streamStatuses.add(status);
    }

    public synchronized void removeStreamStatus(TransferStatus status) {
        streamStatuses.remove(status);
    }

    public synchronized TransferStatus[] getAllStreamStatuses() {
        return streamStatuses.toArray(new TransferStatus[0]);
    }

    public synchronized TransferStatus[] getStreamStatusesForPlayer(Player player) {
        List<TransferStatus> result = new ArrayList<TransferStatus>();
        for (TransferStatus status : getAllStreamStatuses()) {
            if (status.getPlayer().getId().equals(player.getId())) {
                result.add(status);
            }
        }
        return result.toArray(new TransferStatus[0]);
    }

    public synchronized void addDownloadStatus(TransferStatus status) {
        downloadStatuses.add(status);
    }

    public synchronized void removeDownloadStatus(TransferStatus status) {
        downloadStatuses.remove(status);
    }

    public synchronized TransferStatus[] getAllDownloadStatuses() {
        return downloadStatuses.toArray(new TransferStatus[0]);
    }

    public synchronized void addUploadStatus(TransferStatus status) {
        uploadStatuses.add(status);
    }

    public synchronized void removeUploadStatus(TransferStatus status) {
        uploadStatuses.remove(status);
    }

    public synchronized TransferStatus[] getAllUploadStatuses() {
        return uploadStatuses.toArray(new TransferStatus[0]);
    }
}