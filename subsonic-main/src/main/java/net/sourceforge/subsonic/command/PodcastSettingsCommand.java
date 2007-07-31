package net.sourceforge.subsonic.command;

import net.sourceforge.subsonic.controller.PodcastSettingsController;

/**
 * Command used in {@link PodcastSettingsController}.
 *
 * @author Sindre Mehus
 */
public class PodcastSettingsCommand {

    private String interval;
    private String directory;
    private String episodeCount;

    public String getInterval() {
        return interval;
    }

    public void setInterval(String interval) {
        this.interval = interval;
    }

    public String getDirectory() {
        return directory;
    }

    public void setDirectory(String directory) {
        this.directory = directory;
    }

    public String getEpisodeCount() {
        return episodeCount;
    }

    public void setEpisodeCount(String episodeCount) {
        this.episodeCount = episodeCount;
    }
}
