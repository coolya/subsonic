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
package net.sourceforge.subsonic.controller;

import net.sourceforge.subsonic.command.CoverArtSettingsCommand;
import net.sourceforge.subsonic.domain.CoverArtReport;
import net.sourceforge.subsonic.service.CoverArtBatchService;
import net.sourceforge.subsonic.service.SettingsService;
import org.apache.commons.lang.StringUtils;
import org.springframework.web.servlet.mvc.SimpleFormController;

import javax.servlet.http.HttpServletRequest;

/**
 * Controller for the page used to administrate the cover art batch process.
 *
 * @author Christian Nedregård
 */
public class CoverArtSettingsController extends SimpleFormController {
    private CoverArtBatchService coverArtBatchService;
    private SettingsService settingsService;

    protected Object formBackingObject(HttpServletRequest request) throws Exception {
        CoverArtReport coverArtReport = coverArtBatchService.getCoverArtReport();
        CoverArtSettingsCommand command = new CoverArtSettingsCommand(coverArtReport);

        if (StringUtils.isNumeric(request.getParameter("pageNumber"))) {
            command.setPageNumber(Integer.parseInt(request.getParameter("pageNumber")));
        }

        if (request.getParameter("runBatch") != null) {
            coverArtBatchService.startBatch();
        }

        command.setBatchRunning(coverArtBatchService.isBatchRunning());
        command.setAuto(settingsService.isAutoCoverBatch());
        return command;
    }

    protected void doSubmitAction(Object comm) throws Exception {
        CoverArtSettingsCommand command = (CoverArtSettingsCommand) comm;
        settingsService.setAutoCoverBatch(command.isAuto());
        settingsService.save();
    }

    public void setCoverArtBatchService(CoverArtBatchService coverArtBatchService) {
        this.coverArtBatchService = coverArtBatchService;
    }

    public void setSettingsService(SettingsService settingsService) {
        this.settingsService = settingsService;
    }
}