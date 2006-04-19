package net.sourceforge.subsonic.command;

import net.sourceforge.subsonic.controller.*;
import net.sourceforge.subsonic.domain.*;

import java.util.*;

/**
 * Command used in {@link PlayerSettingsController}.
 *
 * @author Sindre Mehus
 */
public class PlayerSettingsCommand {
    private String playerId;
    private String name;
    private String description;
    private String type;
    private Date lastSeen;
    private boolean isDynamicIp;
    private boolean isAutoControlEnabled;
    private String coverArtSchemeName;
    private String transcodeSchemeName;

    private boolean transcodingSupported;
    private EnumHolder[] transcodeSchemeHolders;
    private EnumHolder[] coverArtSchemeHolders;
    private Player[] players;

    public String getPlayerId() {
        return playerId;
    }

    public void setPlayerId(String playerId) {
        this.playerId = playerId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Date getLastSeen() {
        return lastSeen;
    }

    public void setLastSeen(Date lastSeen) {
        this.lastSeen = lastSeen;
    }

    public boolean isDynamicIp() {
        return isDynamicIp;
    }

    public void setDynamicIp(boolean dynamicIp) {
        isDynamicIp = dynamicIp;
    }

    public boolean isAutoControlEnabled() {
        return isAutoControlEnabled;
    }

    public void setAutoControlEnabled(boolean autoControlEnabled) {
        isAutoControlEnabled = autoControlEnabled;
    }

    public String getCoverArtSchemeName() {
        return coverArtSchemeName;
    }

    public void setCoverArtSchemeName(String coverArtSchemeName) {
        this.coverArtSchemeName = coverArtSchemeName;
    }

    public String getTranscodeSchemeName() {
        return transcodeSchemeName;
    }

    public void setTranscodeSchemeName(String transcodeSchemeName) {
        this.transcodeSchemeName = transcodeSchemeName;
    }

    public boolean isTranscodingSupported() {
        return transcodingSupported;
    }

    public void setTranscodingSupported(boolean transcodingSupported) {
        this.transcodingSupported = transcodingSupported;
    }

    public EnumHolder[] getTranscodeSchemeHolders() {
        return transcodeSchemeHolders;
    }

    public void setTranscodeSchemes(TranscodeScheme[] transcodeSchemes) {
        transcodeSchemeHolders = new EnumHolder[transcodeSchemes.length];
        for (int i = 0; i < transcodeSchemes.length; i++) {
            TranscodeScheme scheme = transcodeSchemes[i];
            transcodeSchemeHolders[i] = new EnumHolder(scheme.name(), scheme.toString());
        }
    }

    public EnumHolder[] getCoverArtSchemeHolders() {
        return coverArtSchemeHolders;
    }

    public void setCoverArtSchemes(CoverArtScheme[] coverArtSchemes) {
        coverArtSchemeHolders = new EnumHolder[coverArtSchemes.length];
        for (int i = 0; i < coverArtSchemes.length; i++) {
            CoverArtScheme scheme = coverArtSchemes[i];
            coverArtSchemeHolders[i] = new EnumHolder(scheme.name(), scheme.toString());
        }
    }

    public Player[] getPlayers() {
        return players;
    }

    public void setPlayers(Player[] players) {
        this.players = players;
    }

    /**
     * Holds the name and description of an enum value.
     */
    public static class EnumHolder {
        private String name;
        private String description;

        public EnumHolder(String name, String description) {
            this.name = name;
            this.description = description;
        }

        public String getName() {
            return name;
        }

        public String getDescription() {
            return description;
        }
    }
}