package fr.treeptik.cloudunit.docker.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

/**
 * Created by nicolas on 06/01/2016.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Mounts implements Serializable {

    @JsonProperty("Name")
    private String name;

    @JsonProperty("Driver")
    private String driver;

    @JsonProperty("Source")
    private String source;

    @JsonProperty("Destination")
    private String destination;

    @JsonProperty("Mode")
    private String mode;

    public String getPropagation() {
        return propagation;
    }

    public void setPropagation(String propagation) {
        this.propagation = propagation;
    }

    @JsonProperty("Propagation")
    private String propagation;

    @JsonProperty("RW")
    private Boolean readWrite;

    @Override
    public String toString() {
        return "Mounts{" +
                "source='" + source + '\'' +
                ", destination='" + destination + '\'' +
                ", mode='" + mode + '\'' +
                ", readWrite=" + readWrite +
                '}';
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public Boolean getReadWrite() {
        return readWrite;
    }

    public void setReadWrite(Boolean readWrite) {
        this.readWrite = readWrite;
    }
}
