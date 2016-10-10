package fr.treeptik.cloudunit.dto;

/**
 * Resource for reporting version information.
 * 
 * @author William Bartlett
 */
public class AboutResource {
    private String version;
    private String timestamp;
    
    public AboutResource() { }
    
    public AboutResource(String version, String timestamp) {
        super();
        this.version = version;
        this.timestamp = timestamp;
    }

    public String getVersion() {
        return version;
    }
    
    public String getTimestamp() {
        return timestamp;
    }
}
