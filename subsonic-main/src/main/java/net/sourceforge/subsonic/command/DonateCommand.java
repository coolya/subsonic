package net.sourceforge.subsonic.command;

import net.sourceforge.subsonic.controller.DonateController;

import java.util.Date;

/**
 * Command used in {@link DonateController}.
 *
 * @author Sindre Mehus
 */
public class DonateCommand {

    private String path;
    private String emailAddress;
    private String license;
    private Date licenseDate;
    private boolean licenseValid;
    private String brand;

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public String getLicense() {
        return license;
    }

    public void setLicense(String license) {
        this.license = license;
    }

    public Date getLicenseDate() {
        return licenseDate;
    }

    public void setLicenseDate(Date licenseDate) {
        this.licenseDate = licenseDate;
    }

    public boolean isLicenseValid() {
        return licenseValid;
    }

    public void setLicenseValid(boolean licenseValid) {
        this.licenseValid = licenseValid;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }
}
