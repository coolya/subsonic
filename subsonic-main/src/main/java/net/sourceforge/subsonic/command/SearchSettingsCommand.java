package net.sourceforge.subsonic.command;

import net.sourceforge.subsonic.controller.*;

/**
 * Command used in {@link SearchSettingsController}.
 *
 * @author Sindre Mehus
 */
public class SearchSettingsCommand {
    private String interval;
    private String hour;
    private boolean isCreatingIndex;
    private String brand;

    public String getInterval() {
        return interval;
    }

    public void setInterval(String interval) {
        this.interval = interval;
    }

    public String getHour() {
        return hour;
    }

    public void setHour(String hour) {
        this.hour = hour;
    }

    public boolean isCreatingIndex() {
        return isCreatingIndex;
    }

    public void setCreatingIndex(boolean creatingIndex) {
        isCreatingIndex = creatingIndex;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }
}