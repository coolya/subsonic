package net.sourceforge.subsonic.service;

import net.sourceforge.subsonic.domain.*;

import java.util.*;

/**
 * Provides services for maintaining the list of stream, download and upload statuses.
 *
 * @see TransferStatus
 * @author Sindre Mehus
 */
public class StatusService {

    private List<TransferStatus> streamStatuses = new ArrayList<TransferStatus>();
    private List<TransferStatus> downloadStatuses = new ArrayList<TransferStatus>();
    private List<TransferStatus> uploadStatuses = new ArrayList<TransferStatus>();

    /** Map is used to keep the bitrate history even if the player reconnects. */
    private Map<String, TransferStatus> streamHistoryCache = new HashMap<String, TransferStatus>();

    public synchronized TransferStatus createStreamStatus(Player player) {
        return createStatus(player, streamStatuses, true);
    }

    public synchronized void removeStreamStatus(TransferStatus status) {
        streamStatuses.remove(status);
        streamHistoryCache.put(status.getPlayer().getId(), status);
    }

    public synchronized List<TransferStatus> getAllStreamStatuses() {
        return new ArrayList<TransferStatus>(streamStatuses);
    }

    public synchronized List<TransferStatus> getStreamStatusesForPlayer(Player player) {
        List<TransferStatus> result = new ArrayList<TransferStatus>();
        for (TransferStatus status : getAllStreamStatuses()) {
            if (status.getPlayer().getId().equals(player.getId())) {
                result.add(status);
            }
        }
        return result;
    }

    public synchronized TransferStatus createDownloadStatus(Player player) {
        return createStatus(player, downloadStatuses, false);
    }

    public synchronized void removeDownloadStatus(TransferStatus status) {
        downloadStatuses.remove(status);
    }

    public synchronized List<TransferStatus> getAllDownloadStatuses() {
        return new ArrayList<TransferStatus>(downloadStatuses);
    }

    public synchronized TransferStatus createUploadStatus(Player player) {
        return createStatus(player, uploadStatuses, false);
    }

    public synchronized void removeUploadStatus(TransferStatus status) {
        uploadStatuses.remove(status);
    }

    public synchronized List<TransferStatus> getAllUploadStatuses() {
        return new ArrayList<TransferStatus>(uploadStatuses);
    }

    private synchronized TransferStatus createStatus(Player player, List<TransferStatus> statusList, boolean isStream) {
        TransferStatus status = new TransferStatus();
        status.setPlayer(player);

        if (isStream) {
            TransferStatus previousStatus = getPreviousStatus(player);
            if (previousStatus != null) {
                status.setHistory(previousStatus.getHistory());
                status.setBytesTransfered(previousStatus.getBytesTransfered());
            }
        }

        statusList.add(status);
        return status;
    }

    private TransferStatus getPreviousStatus(Player player) {
        TransferStatus result;
        List<TransferStatus> statuses = getStreamStatusesForPlayer(player);
        if (!statuses.isEmpty()) {
            result = statuses.get(0);
        } else {
            result = streamHistoryCache.get(player.getId());
        }

        // Don't use previous statistics if too old to show in chart.
        if (result != null) {
            TransferStatus.SampleHistory history = result.getHistory();
            if (history.isEmpty() || System.currentTimeMillis() - history.getLast().getTimestamp() > result.getHistoryLengthMillis()) {
                result = null;
            }
        }

        return result;
    }
}