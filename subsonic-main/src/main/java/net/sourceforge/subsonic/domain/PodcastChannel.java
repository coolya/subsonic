package net.sourceforge.subsonic.domain;

/**
 * A Podcast channel. Each channel contain several episodes.
 *
 * @author Sindre Mehus
 * @see PodcastEpisode
 */
public class PodcastChannel {

    private Integer id;
    private String url;
    private String title;
    private String description;
    private PodcastStatus status;
    private String errorMessage;

    public PodcastChannel(Integer id, String url, String title, String description,
                          PodcastStatus status, String errorMessage) {
        this.id = id;
        this.url = url;
        this.title = title;
        this.description = description;
        this.status = status;
        this.errorMessage = errorMessage;
    }

    public PodcastChannel(String url) {
        this.url = url;
        status = PodcastStatus.NEW;
    }

    public Integer getId() {
        return id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
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

    public PodcastStatus getStatus() {
        return status;
    }

    public void setStatus(PodcastStatus status) {
        this.status = status;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}