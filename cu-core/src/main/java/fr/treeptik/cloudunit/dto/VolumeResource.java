package fr.treeptik.cloudunit.dto;

import fr.treeptik.cloudunit.model.Volume;

import java.io.Serializable;

/**
 * Created by nicolas on 13/09/2016.
 */
public class VolumeResource implements Serializable {

    private Integer id;
    private String name;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public VolumeResource() {
    }

    public VolumeResource(Volume volume) {
        this.id = volume.getId();
        this.name = volume.getName();
    }
}
