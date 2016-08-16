package fr.treeptik.cloudunit.dto.gitlab;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

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

    @JsonProperty("user_email")
    private String userEmail;

    @JsonProperty("user_name")
    private String userName;

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
                ", userEmail='" + userEmail + '\'' +
                ", userName='" + userName + '\'' +
                '}';
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getAfter() {
        return after;
    }

    public void setAfter(String after) {
        this.after = after;
    }
}

/*
@JsonIgnoreProperties(ignoreUnknown = true)
class Project {

    @JsonProperty("name")
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
*/