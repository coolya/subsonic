package net.sourceforge.subsonic.domain;

/**
 * Enumeration of player technologies.
 *
 * @author Sindre Mehus
 */
public enum PlayerTechnology {

    /**
     * Plays music directly in the web browser using the integrated Flash player.
     */
    WEB,

    /**
     * Plays music in an external player, such as WinAmp or Windows Media Player.
     */
    EXTERNAL,

    /**
     * Same as above, but the playlist is managed by the player, rather than the Subsonic server.
     * In this mode, skipping within songs is possible.
     */
    EXTERNAL_WITH_PLAYLIST,

    /**
     * Plays music directly on the audio device of the Subsonic server.
     */
    JUKEBOX

}