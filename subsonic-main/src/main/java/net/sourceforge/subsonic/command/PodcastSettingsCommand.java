package net.sourceforge.subsonic.command;

import net.sourceforge.subsonic.controller.PodcastSettingsController;

/**
 * Command used in {@link PodcastSettingsController}.
 *
 * @author Sindre Mehus
 */
public class PodcastSettingsCommand {

    private String interval;
    private String folder;
    private String episodeRetentionCount;
    private String episodeDownloadCount;

    public String getInterval() {
        return interval;
    }

    public void setInterval(String interval) {
        this.interval = interval;
    }

    public String getFolder() {
        return folder;
    }

    public void setFolder(String folder) {
        this.folder = folder;
    }

    public String getEpisodeRetentionCount() {
        return episodeRetentionCount;
    }

    public void setEpisodeRetentionCount(String episodeRetentionCount) {
        this.episodeRetentionCount = episodeRetentionCount;
    }

    public String getEpisodeDownloadCount() {
        return episodeDownloadCount;
    }

    public void setEpisodeDownloadCount(String episodeDownloadCount) {
        this.episodeDownloadCount = episodeDownloadCount;
    }
}
