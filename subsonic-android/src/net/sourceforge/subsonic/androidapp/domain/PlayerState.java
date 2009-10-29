/*
 * (c) Copyright WesternGeco. Unpublished work, created 2009. All rights
 * reserved under copyright laws. This information is confidential and is
 * the trade property of WesternGeco. Do not use, disclose, or reproduce
 * without the prior written permission of the owner.
 */
package net.sourceforge.subsonic.androidapp.domain;

/**
 * @author Sindre Mehus
 * @version $Id$
 */
public enum PlayerState {
    IDLE(""),
    INITIALIZED(""),
    DOWNLOADING("Downloading"),
    PREPARING("Buffering"),
    PREPARED(""),
    STARTED("Playing"),
    STOPPED(""),
    PAUSED("Paused"),
    COMPLETED(""),
    ERROR("Error");

    private final String description;

    PlayerState(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return description;
    }
}
