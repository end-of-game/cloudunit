package fr.treeptik.cloudunit.docker.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

/**
 * Created by guillaume on 22/10/15.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ExecStartBody implements Serializable {

    @JsonProperty("Detach")
    private Boolean detach;

    @JsonProperty("Tty")
    private Boolean tty;

    public Boolean getDetach() {
        return detach;
    }

    public void setDetach(Boolean detach) {
        this.detach = detach;
    }

    public Boolean getTty() {
        return tty;
    }

    public void setTty(Boolean tty) {
        this.tty = tty;
    }
}
