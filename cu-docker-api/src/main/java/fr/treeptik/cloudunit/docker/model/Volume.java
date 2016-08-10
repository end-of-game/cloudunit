package fr.treeptik.cloudunit.docker.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by nicolas on 10/08/2016.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Volume {

    @JsonProperty("Name")
    private String name;

    public String getName() {
        return name;
    }


    public void setName(String name) {
        this.name = name;
    }
}
