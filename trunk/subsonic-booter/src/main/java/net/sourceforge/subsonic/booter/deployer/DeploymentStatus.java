package net.sourceforge.subsonic.booter.deployer;

import java.util.Date;
import java.io.Serializable;

/**
 * @author Sindre Mehus
 */
public class DeploymentStatus implements Serializable {

    private final Date startTime;
    private final String url;
    private final int memoryUsed;
    private final String errorMessage;

    public DeploymentStatus(Date startTime, String url, int memoryUsed, String errorMessage) {
        this.startTime = startTime;
        this.url = url;
        this.memoryUsed = memoryUsed;
        this.errorMessage = errorMessage;
    }

    public String getURL() {
        return url;
    }

    public Date getStartTime() {
        return startTime;
    }

    public int getMemoryUsed() {
        return memoryUsed;
    }

    public String getErrorMessage() {
        return errorMessage;
    }
}
