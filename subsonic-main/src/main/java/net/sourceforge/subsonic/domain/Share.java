package net.sourceforge.subsonic.domain;

import java.util.Date;

/**
 * A collection of media files that is shared with someone, and accessible via a direct URL.
 *
 * @author Sindre Mehus
 * @version $Id$
 */
public class Share {

    private int id;
    private String name;
    private String description;
    private String username;
    private Date created;
    private Date expires;
    private Date lastVisited;
    private int visitCount;

    public Share() {
    }

    public Share(int id, String name, String description, String username, Date created,
            Date expires, Date lastVisited, int visitCount) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.username = username;
        this.created = created;
        this.expires = expires;
        this.lastVisited = lastVisited;
        this.visitCount = visitCount;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getUsername() {
        return username;
    }

    public Date getCreated() {
        return created;
    }

    public Date getExpires() {
        return expires;
    }

    public Date getLastVisited() {
        return lastVisited;
    }

    public int getVisitCount() {
        return visitCount;
    }
}
