package fr.treeptik.cloudunit.model;

import javax.persistence.*;
import java.io.Serializable;

@Entity
public class Volume implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    private String name;

    private String path;

    @ManyToOne
    private Application application;

    private String containerId;

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

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public Application getApplication() {
        return application;
    }

    public void setApplication(Application application) {
        this.application = application;
    }

    public String getContainerId() { return containerId; }

    public void setContainerId(String containerId) { this.containerId = containerId; }
}
