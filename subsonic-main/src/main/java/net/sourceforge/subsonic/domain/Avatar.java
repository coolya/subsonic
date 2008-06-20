package net.sourceforge.subsonic.domain;

import java.util.Date;

/**
 * An icon representing a user.
 *
 * @author Sindre Mehus
 */
public class Avatar {

    private int id;
    private String name;
    private Date createdDate;
    private String mimeType;
    private int width;
    private int height;
    private byte[] data;

    public Avatar(int id, String name, Date createdDate, String mimeType, int width, int height, byte[] data) {
        this.id = id;
        this.name = name;
        this.createdDate = createdDate;
        this.mimeType = mimeType;
        this.width = width;
        this.height = height;
        this.data = data;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public String getMimeType() {
        return mimeType;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public byte[] getData() {
        return data;
    }
}
