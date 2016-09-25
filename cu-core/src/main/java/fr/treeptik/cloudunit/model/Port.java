package fr.treeptik.cloudunit.model;

import fr.treeptik.cloudunit.enums.PortType;

import javax.persistence.*;
import java.io.Serializable;

/**
 * Created by guillaume on 25/09/16.
 */
@Entity
public class Port implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Enumerated(EnumType.STRING)
    private PortType portType;

    private String hostValue;

    private String containerValue;

    private Boolean opened;

    @ManyToOne
    private Module module;

    public Port() {
    }

    public Port(PortType portType, String containerValue, String hostValue, Boolean opened, Module module) {
        this.portType = portType;
        this.hostValue = hostValue;
        this.containerValue = containerValue;
        this.opened = opened;
        this.module = module;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }


    public PortType getPortType() {
        return portType;
    }

    public void setPortType(PortType portType) {
        this.portType = portType;
    }

    public String getHostValue() {
        return hostValue;
    }

    public void setHostValue(String hostValue) {
        this.hostValue = hostValue;
    }

    public String getContainerValue() {
        return containerValue;
    }

    public void setContainerValue(String containerValue) {
        this.containerValue = containerValue;
    }

    public Boolean getOpened() {
        return opened;
    }

    public void setOpened(Boolean opened) {
        this.opened = opened;
    }

    public Module getModule() {
        return module;
    }

    public void setModule(Module module) {
        this.module = module;
    }
}
