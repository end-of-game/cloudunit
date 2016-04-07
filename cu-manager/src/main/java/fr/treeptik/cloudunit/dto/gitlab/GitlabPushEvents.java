package fr.treeptik.cloudunit.dto.gitlab;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

/**
 * Created by nicolas on 07/04/2016.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class GitlabPushEvents {

    @JsonProperty("object_kind")
    private String objectKind;

    @JsonProperty("before")
    private String before;

    @JsonProperty("after")
    private String after;

    public String getObjectKind() {
        return objectKind;
    }

    public void setObjectKind(String objectKind) {
        this.objectKind = objectKind;
    }

    public String getBefore() {
        return before;
    }

    public void setBefore(String before) {
        this.before = before;
    }

    @Override
    public String toString() {
        return "GitlabPushEvents{" +
                "objectKind='" + objectKind + '\'' +
                ", before='" + before + '\'' +
                ", after='" + after + '\'' +
                '}';
    }

    public String getAfter() {
        return after;
    }

    public void setAfter(String after) {
        this.after = after;
    }
}
