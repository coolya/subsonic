/*
 This file is part of Subsonic.

 Subsonic is free software: you can redistribute it and/or modify
 it under the terms of the GNU General Public License as published by
 the Free Software Foundation, either version 3 of the License, or
 (at your option) any later version.

 Subsonic is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with Subsonic.  If not, see <http://www.gnu.org/licenses/>.

 Copyright 2009 (C) Sindre Mehus
 */
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

    public synchronized TransferStatus createStreamStatus(Player player) {
        // Use existing status, if possible.
        TransferStatus status = getStreamStatusForPlayer(player);
        if (status != null) {
            status.setActive(true);
        } else {
            status = createStatus(player, streamStatuses);
        }
        return status;
    }

    public synchronized void removeStreamStatus(TransferStatus status) {
        // Do not remove it, since it can be re-activated later.
        status.setActive(false);
    }

    public synchronized List<TransferStatus> getAllStreamStatuses() {
        return new ArrayList<TransferStatus>(streamStatuses);
    }

    public synchronized TransferStatus getStreamStatusForPlayer(Player player) {
        for (TransferStatus status : streamStatuses) {
            if (status.getPlayer().getId().equals(player.getId())) {
                return status;
            }
        }
        return null;
    }

    public synchronized TransferStatus createDownloadStatus(Player player) {
        return createStatus(player, downloadStatuses);
    }

    public synchronized void removeDownloadStatus(TransferStatus status) {
        downloadStatuses.remove(status);
    }

    public synchronized List<TransferStatus> getAllDownloadStatuses() {
        return new ArrayList<TransferStatus>(downloadStatuses);
    }

    public synchronized TransferStatus createUploadStatus(Player player) {
        return createStatus(player, uploadStatuses);
    }

    public synchronized void removeUploadStatus(TransferStatus status) {
        uploadStatuses.remove(status);
    }

    public synchronized List<TransferStatus> getAllUploadStatuses() {
        return new ArrayList<TransferStatus>(uploadStatuses);
    }

    private synchronized TransferStatus createStatus(Player player, List<TransferStatus> statusList) {
        TransferStatus status = new TransferStatus();
        status.setPlayer(player);
        statusList.add(status);
        return status;
    }

}