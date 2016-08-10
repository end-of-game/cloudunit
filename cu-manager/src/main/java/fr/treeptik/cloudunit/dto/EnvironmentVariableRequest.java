package fr.treeptik.cloudunit.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Created by stagiaire on 08/08/16.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class EnvironmentVariableRequest {

    private Integer id;

    private String key;

    private String value;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
