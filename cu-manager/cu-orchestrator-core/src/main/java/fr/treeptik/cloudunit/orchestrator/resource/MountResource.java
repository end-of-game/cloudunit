package fr.treeptik.cloudunit.orchestrator.resource;

import org.springframework.hateoas.ResourceSupport;

import fr.treeptik.cloudunit.orchestrator.core.Mount;

public class MountResource extends ResourceSupport {
    private VolumeResource volume;
    
    private String mountPoint;

    protected MountResource() {}
    
    public MountResource(String mountPoint) {
        this.mountPoint = mountPoint;
    }
    
    public MountResource(VolumeResource volume, String mountPoint) {
        this.volume = volume;
        this.mountPoint = mountPoint;
    }

    public MountResource(Mount mount) {
        this.mountPoint = mount.getMountPoint();
    }

    public String getMountPoint() {
        return mountPoint;
    }
    
    public void setMountPoint(String mountPoint) {
        this.mountPoint = mountPoint;
    }
    
    public VolumeResource getVolume() {
        return volume;
    }
    
    public void setVolume(VolumeResource volume) {
        this.volume = volume;
    }
}
