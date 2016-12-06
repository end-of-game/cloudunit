package fr.treeptik.cloudunit.docker.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;

/**
 * Created by guillaume on 24/10/16.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Network {

    @JsonProperty("Name")
    private String name;
    @JsonProperty("Id")
    private String id;

    @JsonProperty("Labels")
    private Map<String, String> labels;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Map<String, String> getLabels() {
        return labels;
    }

    public void setLabels(Map<String, String> labels) {
        this.labels = labels;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}

