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

import net.sourceforge.subsonic.command.NetworkSettingsCommand;
import net.sourceforge.subsonic.service.NetworkService;
import net.sourceforge.subsonic.service.SettingsService;
import org.apache.commons.lang.ObjectUtils;
import org.springframework.web.servlet.mvc.SimpleFormController;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;

/**
 * Controller for the page used to change the network settings.
 *
 * @author Sindre Mehus
 */
public class NetworkSettingsController extends SimpleFormController {

    private static final long TRIAL_DAYS = 30L;

    private SettingsService settingsService;
    private NetworkService networkService;

    protected Object formBackingObject(HttpServletRequest request) throws Exception {

        NetworkSettingsCommand command = new NetworkSettingsCommand();
        command.setPortForwardingEnabled(settingsService.isPortForwardingEnabled());
        command.setPortForwardingPublicPort(settingsService.getPortForwardingPublicPort());
        command.setUrlRedirectionEnabled(settingsService.isUrlRedirectionEnabled());
        command.setUrlRedirectFrom(settingsService.getUrlRedirectFrom());
        return command;
    }

    protected void doSubmitAction(Object cmd) throws Exception {
        NetworkSettingsCommand command = (NetworkSettingsCommand) cmd;

        if (command.isPortForwardingEnabled() != settingsService.isPortForwardingEnabled() ||
            command.getPortForwardingPublicPort() != settingsService.getPortForwardingPublicPort()) {
            settingsService.setPortForwardingEnabled(command.isPortForwardingEnabled());
            settingsService.setPortForwardingPublicPort(command.getPortForwardingPublicPort());
            settingsService.save();
            networkService.initPortForwarding();
        }

        if (command.isUrlRedirectionEnabled() != settingsService.isUrlRedirectionEnabled() ||
            !ObjectUtils.equals(command.getUrlRedirectFrom(), settingsService.getUrlRedirectFrom())) {
            settingsService.setUrlRedirectionEnabled(command.isUrlRedirectionEnabled());
            settingsService.setUrlRedirectFrom(command.getUrlRedirectFrom());

            if (!settingsService.isLicenseValid() && settingsService.getUrlRedirectTrialExpires() == null) {
                Date expiryDate = new Date(System.currentTimeMillis() + TRIAL_DAYS * 24L * 3600L * 1000L);
                settingsService.setUrlRedirectTrialExpires(expiryDate);
            }

            settingsService.save();
            networkService.initUrlRedirection();
        }
    }

    public void setSettingsService(SettingsService settingsService) {
        this.settingsService = settingsService;
    }

    public void setNetworkService(NetworkService networkService) {
        this.networkService = networkService;
    }
}