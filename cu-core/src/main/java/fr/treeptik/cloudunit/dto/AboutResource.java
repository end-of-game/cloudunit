package fr.treeptik.cloudunit.dto;

/**
 * Resource for reporting version information.
 * 
 * @author William Bartlett
 */
public class AboutResource {
    private String version;
    private String timestamp;
    private String lastStartTime;

    public AboutResource() { }
    
    public AboutResource(String version, String timestamp, String lastStartTime) {
        super();
        this.version = version;
        this.timestamp = timestamp;
        this.lastStartTime = lastStartTime;
    }

    public String getVersion() {
        return version;
    }
    
    public String getTimestamp() {
        return timestamp;
    }

    public String getLastStartTime() {
        return lastStartTime;
    }

}
