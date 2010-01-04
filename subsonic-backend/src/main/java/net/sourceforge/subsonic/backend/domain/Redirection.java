/*
 This file is part of Subsonic.

 Subsonic is free software: you can redistribute it and/or modify
 it under the terms of the GNU General Public License as published by
 the Free Software Foundation, either version 3 of the License, or
 (at your option) any later version.

 Subsonic is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with Subsonic.  If not, see <http://www.gnu.org/licenses/>.

 Copyright 2009 (C) Sindre Mehus
 */
package net.sourceforge.subsonic.backend.domain;

import java.util.Date;

/**
 * @author Sindre Mehus
 */
public class Redirection {

    private int id;
    private String licenseHolder;
    private String serverId;
    private String redirectFrom;
    private String redirectTo;
    private boolean trial;
    private Date trialExpires;
    private Date lastUpdated;
    private Date lastRead;

    public Redirection(int id, String licenseHolder, String serverId, String redirectFrom, String redirectTo, boolean trial,
            Date trialExpires, Date lastUpdated, Date lastRead) {
        this.id = id;
        this.licenseHolder = licenseHolder;
        this.serverId = serverId;
        this.redirectFrom = redirectFrom;
        this.redirectTo = redirectTo;
        this.trial = trial;
        this.trialExpires = trialExpires;
        this.lastUpdated = lastUpdated;
        this.lastRead = lastRead;
    }

    public int getId() {
        return id;
    }

    public String getLicenseHolder() {
        return licenseHolder;
    }

    public void setLicenseHolder(String licenseHolder) {
        this.licenseHolder = licenseHolder;
    }

    public String getServerId() {
        return serverId;
    }

    public void setServerId(String serverId) {
        this.serverId = serverId;
    }

    public String getRedirectFrom() {
        return redirectFrom;
    }

    public void setRedirectFrom(String redirectFrom) {
        this.redirectFrom = redirectFrom;
    }

    public String getRedirectTo() {
        return redirectTo;
    }

    public void setRedirectTo(String redirectTo) {
        this.redirectTo = redirectTo;
    }

    public boolean isTrial() {
        return trial;
    }

    public void setTrial(boolean trial) {
        this.trial = trial;
    }

    public Date getTrialExpires() {
        return trialExpires;
    }

    public void setTrialExpires(Date trialExpires) {
        this.trialExpires = trialExpires;
    }

    public Date getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(Date lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    public Date getLastRead() {
        return lastRead;
    }

    public void setLastRead(Date lastRead) {
        this.lastRead = lastRead;
    }

    @Override
    public String toString() {
        return "Redirection{" +
                "id=" + id +
                ", licenseHolder='" + licenseHolder + '\'' +
                ", serverId='" + serverId + '\'' +
                ", redirectFrom='" + redirectFrom + '\'' +
                ", redirectTo='" + redirectTo + '\'' +
                ", trial=" + trial +
                ", trialExpires=" + trialExpires +
                ", lastUpdated=" + lastUpdated +
                ", lastRead=" + lastRead +
                '}';
    }
}
