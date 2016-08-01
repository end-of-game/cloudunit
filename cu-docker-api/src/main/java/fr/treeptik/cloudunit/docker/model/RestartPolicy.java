package fr.treeptik.cloudunit.docker.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

/**
 * Created by guillaume on 21/10/15.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class RestartPolicy implements Serializable {

    @JsonProperty("MaximumRetryCount")
    private Long maximumRetryCount;

    @JsonProperty("Name")
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getMaximumRetryCount() {
        return maximumRetryCount;
    }

    public void setMaximumRetryCount(Long maximumRetryCount) {
        this.maximumRetryCount = maximumRetryCount;
    }
}
