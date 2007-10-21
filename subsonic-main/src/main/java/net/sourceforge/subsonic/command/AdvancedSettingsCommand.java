package net.sourceforge.subsonic.command;

import net.sourceforge.subsonic.controller.AdvancedSettingsController;

/**
 * Command used in {@link AdvancedSettingsController}.
 *
 * @author Sindre Mehus
 */
public class AdvancedSettingsCommand {
    private String downsampleCommand;
    private String coverArtLimit;
    private String downloadLimit;
    private String uploadLimit;
    private String streamPort;
    private boolean isReloadNeeded;

    public String getDownsampleCommand() {
        return downsampleCommand;
    }

    public void setDownsampleCommand(String downsampleCommand) {
        this.downsampleCommand = downsampleCommand;
    }

    public String getCoverArtLimit() {
        return coverArtLimit;
    }

    public void setCoverArtLimit(String coverArtLimit) {
        this.coverArtLimit = coverArtLimit;
    }

    public String getDownloadLimit() {
        return downloadLimit;
    }

    public void setDownloadLimit(String downloadLimit) {
        this.downloadLimit = downloadLimit;
    }

    public String getUploadLimit() {
        return uploadLimit;
    }

    public String getStreamPort() {
        return streamPort;
    }

    public void setStreamPort(String streamPort) {
        this.streamPort = streamPort;
    }

    public void setUploadLimit(String uploadLimit) {
        this.uploadLimit = uploadLimit;
    }

    public boolean isReloadNeeded() {
        return isReloadNeeded;
    }

    public void setReloadNeeded(boolean reloadNeeded) {
        isReloadNeeded = reloadNeeded;
    }
}
