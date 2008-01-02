package net.sourceforge.subsonic.controller;

import net.sourceforge.subsonic.command.AdvancedSettingsCommand;
import net.sourceforge.subsonic.service.SettingsService;
import org.springframework.web.servlet.mvc.SimpleFormController;

import javax.servlet.http.HttpServletRequest;

/**
 * Controller for the page used to administrate advanced settings.
 *
 * @author Sindre Mehus
 */
public class AdvancedSettingsController extends SimpleFormController {

    private SettingsService settingsService;

    protected Object formBackingObject(HttpServletRequest request) throws Exception {
        AdvancedSettingsCommand command = new AdvancedSettingsCommand();
        command.setCoverArtLimit(String.valueOf(settingsService.getCoverArtLimit()));
        command.setDownsampleCommand(settingsService.getDownsamplingCommand());
        command.setDownloadLimit(String.valueOf(settingsService.getDownloadBitrateLimit()));
        command.setUploadLimit(String.valueOf(settingsService.getUploadBitrateLimit()));
        command.setStreamPort(String.valueOf(settingsService.getStreamPort()));
        command.setLdapEnabled(settingsService.isLdapEnabled());
        command.setLdapUrl(settingsService.getLdapUrl());
        command.setLdapSearchFilter(settingsService.getLdapSearchFilter());

        return command;
    }

    protected void doSubmitAction(Object comm) throws Exception {
        AdvancedSettingsCommand command = (AdvancedSettingsCommand) comm;

        command.setReloadNeeded(false);
        settingsService.setDownsamplingCommand(command.getDownsampleCommand());

        try {
            settingsService.setCoverArtLimit(Integer.parseInt(command.getCoverArtLimit()));
        } catch (NumberFormatException x) { /* Intentionally ignored. */ }
        try {
            settingsService.setDownloadBitrateLimit(Long.parseLong(command.getDownloadLimit()));
        } catch (NumberFormatException x) { /* Intentionally ignored. */ }
        try {
            settingsService.setUploadBitrateLimit(Long.parseLong(command.getUploadLimit()));
        } catch (NumberFormatException x) { /* Intentionally ignored. */ }
        try {
            settingsService.setStreamPort(Integer.parseInt(command.getStreamPort()));
        } catch (NumberFormatException x) { /* Intentionally ignored. */ }

        settingsService.setLdapEnabled(command.isLdapEnabled());
        settingsService.setLdapUrl(command.getLdapUrl());
        settingsService.setLdapSearchFilter(command.getLdapSearchFilter());

        settingsService.save();
    }

    public void setSettingsService(SettingsService settingsService) {
        this.settingsService = settingsService;
    }
}
