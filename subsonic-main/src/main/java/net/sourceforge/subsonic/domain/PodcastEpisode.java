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
    private Date publishDate;
    private String duration;
    private Long bytesTotal;
    private Long bytesDownloaded;
    private Status status;

    public PodcastEpisode(Integer id, Integer channelId, String url, String path, String title,
                          String description, Date publishDate, String duration, Long length, Long bytesDownloaded, Status status) {
        this.id = id;
        this.channelId = channelId;
        this.url = url;
        this.path = path;
        this.title = title;
        this.description = description;
        this.publishDate = publishDate;
        this.duration = duration;
        this.bytesTotal = length;
        this.bytesDownloaded = bytesDownloaded;
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

    public void setUrl(String url) {
        this.url = url;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getPublishDate() {
        return publishDate;
    }

    public void setPublishDate(Date publishDate) {
        this.publishDate = publishDate;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public Long getBytesTotal() {
        return bytesTotal;
    }

    public void setBytesTotal(Long bytesTotal) {
        this.bytesTotal = bytesTotal;
    }

    public Long getBytesDownloaded() {
        return bytesDownloaded;
    }

    public Double getCompletionRate() {
        if (bytesTotal == null || bytesTotal == 0) {
            return null;
        }
        if (bytesDownloaded == null) {
            return 0.0;
        }

        double d = bytesDownloaded;
        double t = bytesTotal;
        return d / t;
    }

    public void setBytesDownloaded(Long bytesDownloaded) {
        this.bytesDownloaded = bytesDownloaded;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public static enum Status {
        NEW, DOWNLOADING, DOWNLOADED, ERROR, DELETED
    }
}
