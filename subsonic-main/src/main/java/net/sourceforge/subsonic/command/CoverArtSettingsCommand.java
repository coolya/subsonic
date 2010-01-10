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
package net.sourceforge.subsonic.command;

import net.sourceforge.subsonic.domain.CoverArtReport;

import java.util.ArrayList;
import java.util.List;

/**
 * Command used in {@link net.sourceforge.subsonic.controller.CoverArtSettingsController}.
 *
 * @author Christian Nedregård
 */
public class CoverArtSettingsCommand {
    private static final int PAGINATION_SIZE = 100;

    private CoverArtReport coverArtReport;
    private boolean auto;
    private boolean batchRunning;
    private int pageNumber = 1;

    public CoverArtReport getCoverArtReport() {
        return coverArtReport;
    }

    public void setCoverArtReport(CoverArtReport coverArtReport) {
        this.coverArtReport = coverArtReport;
    }

    public boolean isAuto() {
        return auto;
    }

    public void setAuto(boolean auto) {
        this.auto = auto;
    }

    public boolean isBatchRunning() {
        return batchRunning;
    }

    public void setBatchRunning(boolean batchRunning) {
        this.batchRunning = batchRunning;
    }

    public void setPageNumber(int pageNumber) {
        this.pageNumber = Math.max(1, pageNumber); // avoid 0 or negative page numbers
    }

    public int getPageNumber() {
        return pageNumber;
    }

    public int getStartIndex() {
        int startPosition = (getPageNumber() - 1) * PAGINATION_SIZE;
        return getSafeListIndex(startPosition, coverArtReport.getAlbumsWithoutCover());
    }

    public int getEndIndex() {
        int stopPosition = getStartIndex() + PAGINATION_SIZE - 1;
        return getSafeListIndex(stopPosition, coverArtReport.getAlbumsWithoutCover());
    }

    public List<PaginationElement> getPaginationElements() {
        List<PaginationElement> paginationElements = new ArrayList<PaginationElement>();
        int numberOfHits = coverArtReport.getAlbumsWithoutCover().size();
        int numberOfpages = (int) Math.ceil((double) numberOfHits / (double) PAGINATION_SIZE);

        if (numberOfpages > 1) { // we do not produce pagination elements for a single page result
            for (int i = 1; i <= numberOfpages; ++i) {
                paginationElements.add(new PaginationElement(i, i == getPageNumber()));
            }
        }
        return paginationElements;
    }

    private int getSafeListIndex(int suggestedIndex, List list) {
        int maxListIndex = list.size() - 1;
        int safeSuggestedIndex = Math.abs(suggestedIndex);
        return Math.min(maxListIndex, safeSuggestedIndex);
    }

    public static class PaginationElement {
        private int position;
        private boolean active;

        public PaginationElement(int position, boolean active) {
            this.position = position;
            this.active = active;
        }

        public int getPosition() {
            return position;
        }

        public boolean isActive() {
            return active;
        }
    }
}