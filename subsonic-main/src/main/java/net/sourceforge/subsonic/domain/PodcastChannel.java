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


    public PodcastChannel(Integer id, String url, String title, String description) {
        this.id = id;
        this.url = url;
        this.title = title;
        this.description = description;
    }


    public Integer getId() {
        return id;
    }

    public String getUrl() {
        return url;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }
}