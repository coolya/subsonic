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
package net.sourceforge.subsonic.domain;

/**
 * Contains the configuration for a transcoding, i.e., a specification of how a given media format
 * should be converted to another.
 * <br/>
 * A transcoding may contain up to three steps. Typically you need to convert in several steps, for
 * instance from OGG to WAV to MP3.
 *
 * @author Sindre Mehus
 */
public class Transcoding {

    private Integer id;
    private String name;
    private String sourceFormat;
    private String targetFormat;
    private String step1;
    private String step2;
    private String step3;
    private boolean isDefaultActive;
    private boolean isEnabled;

    /**
     * Creates a new transcoding specification.
     *
     * @param id              The system-generated ID.
     * @param name            The user-defined name.
     * @param sourceFormat    The source format, e.g., OGG.
     * @param targetFormat    The target format, e.g., MP3.
     * @param step1           The command to execute in step 1.
     * @param step2           The command to execute in step 2.
     * @param step3           The command to execute in step 3.
     * @param isEnabled       Whether this transcoding is enabled.
     * @param isDefaultActive Whether this transcoding is default active for new players.
     */
    public Transcoding(Integer id, String name, String sourceFormat, String targetFormat, String step1,
                       String step2, String step3, boolean isEnabled, boolean isDefaultActive) {
        this.id = id;
        this.name = name;
        this.sourceFormat = sourceFormat;
        this.targetFormat = targetFormat;
        this.step1 = step1;
        this.step2 = step2;
        this.step3 = step3;
        this.isEnabled = isEnabled;
        this.isDefaultActive = isDefaultActive;
    }

    /**
     * Returns the system-generated ID.
     *
     * @return The system-generated ID.
     */
    public Integer getId() {
        return id;
    }

    /**
     * Sets the system-generated ID.
     *
     * @param id The system-generated ID.
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * Returns the user-defined name.
     *
     * @return The user-defined name.
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the user-defined name.
     *
     * @param name The user-defined name.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Returns the source format, e.g., OGG.
     *
     * @return The source format, e.g., OGG.
     */
    public String getSourceFormat() {
        return sourceFormat;
    }

    /**
     * Sets the source format, e.g., OGG.
     *
     * @param sourceFormat The source format, e.g., OGG.
     */
    public void setSourceFormat(String sourceFormat) {
        this.sourceFormat = sourceFormat;
    }

    /**
     * Returns the target format, e.g., MP3.
     *
     * @return The target format, e.g., MP3.
     */
    public String getTargetFormat() {
        return targetFormat;
    }

    /**
     * Sets the target format, e.g., MP3.
     *
     * @param targetFormat The target format, e.g., MP3.
     */
    public void setTargetFormat(String targetFormat) {
        this.targetFormat = targetFormat;
    }

    /**
     * Returns the command to execute in step 1.
     *
     * @return The command to execute in step 1.
     */
    public String getStep1() {
        return step1;
    }

    /**
     * Sets the command to execute in step 1.
     *
     * @param step1 The command to execute in step 1.
     */
    public void setStep1(String step1) {
        this.step1 = step1;
    }

    /**
     * Returns the command to execute in step 2.
     *
     * @return The command to execute in step 2.
     */
    public String getStep2() {
        return step2;
    }

    /**
     * Sets the command to execute in step 2.
     *
     * @param step2 The command to execute in step 2.
     */
    public void setStep2(String step2) {
        this.step2 = step2;
    }

    /**
     * Returns the command to execute in step 3.
     *
     * @return The command to execute in step 3.
     */
    public String getStep3() {
        return step3;
    }

    /**
     * Sets the command to execute in step 3.
     *
     * @param step3 The command to execute in step 3.
     */
    public void setStep3(String step3) {
        this.step3 = step3;
    }

    /**
     * Returns whether this trancsoding is default active for new players.
     *
     * @return Whether this trancsoding is default active for new players.
     */
    public boolean isDefaultActive() {
        return isDefaultActive;
    }

    /**
     * Sets whether this trancsoding is default active for new players.
     *
     * @param isDefaultActive Whether this trancsoding is default active for new players.
     */
    public void setDefaultActive(boolean isDefaultActive) {
        this.isDefaultActive = isDefaultActive;
    }

    /**
     * Sets whether this transcoding is enabled.
     *
     * @return Whether this transcoding is enabled.
     */
    public boolean isEnabled() {
        return isEnabled;
    }

    /**
     * Returns whether this transcoding is enabled.
     *
     * @param isEnabled Whether this transcoding is enabled.
     */
    public void setEnabled(boolean isEnabled) {
        this.isEnabled = isEnabled;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Transcoding that = (Transcoding) o;
        return !(id != null ? !id.equals(that.id) : that.id != null);
    }

    public int hashCode() {
        return (id != null ? id.hashCode() : 0);
    }
}