package net.sourceforge.subsonic.domain;

import java.util.Date;

/**
 * A Podcast episode belonging to a channel.
 *
 * @author Sindre Mehus
 * @see PodcastChannel
 */
public class PodcastEpisode {

    private Integer id;
    private Integer channelId;
    private String url;
    private String path;
    private String title;
    private String description;
    private Date date;
    private String duration;
    private Status status;

    public PodcastEpisode(Integer id, Integer channelId, String url, String path, String title,
                          String description, Date date, String duration, Status status) {
        this.id = id;
        this.channelId = channelId;
        this.url = url;
        this.path = path;
        this.title = title;
        this.description = description;
        this.date = date;
        this.duration = duration;
        this.status = status;
    }

    public Integer getId() {
        return id;
    }

    public Integer getChannelId() {
        return channelId;
    }

    public String getUrl() {
        return url;
    }

    public String getPath() {
        return path;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public Date getDate() {
        return date;
    }

    public String getDuration() {
        return duration;
    }

    public Status getStatus() {
        return status;
    }

    public static enum Status {
        NEW, DOWNLOADING, DOWNLOADED, ERROR
    }
}
