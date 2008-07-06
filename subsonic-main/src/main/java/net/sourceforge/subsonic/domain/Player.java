package net.sourceforge.subsonic.domain;

import java.util.Date;

/**
 * Represens a remote player.  A player has a unique ID, a user-defined name, a logged-on user,
 * miscellaneous identifiers, and an associated playlist.
 *
 * @author Sindre Mehus
 */
public class Player {

    private String id;
    private String name;
    private String type;
    private String username;
    private String ipAddress;
    private boolean isDynamicIp = true;
    private boolean isAutoControlEnabled = true;
    private boolean isClientSidePlaylist = false;
    private boolean isJukebox = false;
    private Date lastSeen;
    private CoverArtScheme coverArtScheme = CoverArtScheme.MEDIUM;
    private TranscodeScheme transcodeScheme = TranscodeScheme.OFF;
    private Playlist playlist;

    /**
     * Returns the player ID.
     *
     * @return The player ID.
     */
    public String getId() {
        return id;
    }

    /**
     * Sets the player ID.
     *
     * @param id The player ID.
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Returns the user-defined player name.
     *
     * @return The user-defined player name.
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the user-defined player name.
     *
     * @param name The user-defined player name.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Returns the player type, e.g., WinAmp, iTunes.
     *
     * @return The player type.
     */
    public String getType() {
        return type;
    }

    /**
     * Sets the player type, e.g., WinAmp, iTunes.
     *
     * @param type The player type.
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * Returns the logged-in user.
     *
     * @return The logged-in user.
     */
    public String getUsername() {
        return username;
    }

    /**
     * Sets the logged-in username.
     *
     * @param username The logged-in username.
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * Returns whether the player is automatically started.
     *
     * @return Whether the player is automatically started.
     */
    public boolean isAutoControlEnabled() {
        return isAutoControlEnabled;
    }

    /**
     * Sets whether the player is automatically started.
     *
     * @param isAutoControlEnabled Whether the player is automatically started.
     */
    public void setAutoControlEnabled(boolean isAutoControlEnabled) {
        this.isAutoControlEnabled = isAutoControlEnabled;
    }

    /**
     * Returns whether the player itself controls the playlist.
     *
     * @return Whether the player itself controls the playlist.
     */
    public boolean isClientSidePlaylist() {
        return isClientSidePlaylist;
    }

    /**
     * Sets whether the player itself controls the playlist.
     *
     * @param isClientSidePlaylist Whether the player itself controls the playlist.
     */
    public void setClientSidePlaylist(boolean isClientSidePlaylist) {
        this.isClientSidePlaylist = isClientSidePlaylist;
    }


    /**
     * Returns whether this player operates in jukebox mode.
     *
     * @return Whether this player operates in jukebox mode.
     */
    public boolean isJukebox() {
        return isJukebox;
    }

    /**
     * Sets whether this player operates in jukebox mode.
     *
     * @param jukebox Whether this player operates in jukebox mode.
     */
    public void setJukebox(boolean jukebox) {
        isJukebox = jukebox;
    }

    /**
     * Returns the time when the player was last seen.
     *
     * @return The time when the player was last seen.
     */
    public Date getLastSeen() {
        return lastSeen;
    }

    /**
     * Sets the time when the player was last seen.
     *
     * @param lastSeen The time when the player was last seen.
     */
    public void setLastSeen(Date lastSeen) {
        this.lastSeen = lastSeen;
    }

    /**
     * Returns the cover art scheme.
     *
     * @return The cover art scheme.
     */
    public CoverArtScheme getCoverArtScheme() {
        return coverArtScheme;
    }

    /**
     * Sets the cover art scheme.
     *
     * @param coverArtScheme The cover art scheme.
     */
    public void setCoverArtScheme(CoverArtScheme coverArtScheme) {
        this.coverArtScheme = coverArtScheme;
    }

    /**
     * Returns the transcode scheme.
     *
     * @return The transcode scheme.
     */
    public TranscodeScheme getTranscodeScheme() {
        return transcodeScheme;
    }

    /**
     * Sets the transcode scheme.
     *
     * @param transcodeScheme The transcode scheme.
     */
    public void setTranscodeScheme(TranscodeScheme transcodeScheme) {
        this.transcodeScheme = transcodeScheme;
    }

    /**
     * Returns the IP address of the player.
     *
     * @return The IP address of the player.
     */
    public String getIpAddress() {
        return ipAddress;
    }

    /**
     * Sets the IP address of the player.
     *
     * @param ipAddress The IP address of the player.
     */
    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    /**
     * Returns whether this player has a dynamic IP address.
     *
     * @return Whether this player has a dynamic IP address.
     */
    public boolean isDynamicIp() {
        return isDynamicIp;
    }

    /**
     * Sets whether this player has a dynamic IP address.
     *
     * @param dynamicIp Whether this player has a dynamic IP address.
     */
    public void setDynamicIp(boolean dynamicIp) {
        isDynamicIp = dynamicIp;
    }

    /**
     * Returns the player's playlist.
     *
     * @return The player's playlist
     */
    public Playlist getPlaylist() {
        return playlist;
    }

    /**
     * Sets the player's playlist.
     *
     * @param playlist The player's playlist.
     */
    public void setPlaylist(Playlist playlist) {
        this.playlist = playlist;
    }

    /**
     * Returns a string representation of the player.
     *
     * @return A string representation of the player.
     */
    public String getDescription() {
        StringBuffer buf = new StringBuffer();
        if (name != null) {
            buf.append(name);
        } else {
            buf.append("Player ").append(id);
        }

        buf.append(" [").append(username).append('@').append(ipAddress).append(']');
        return buf.toString();
    }

    /**
     * Returns a string representation of the player.
     *
     * @return A string representation of the player.
     * @see #getDescription()
     */
    public String toString() {
        return getDescription();
    }
}
