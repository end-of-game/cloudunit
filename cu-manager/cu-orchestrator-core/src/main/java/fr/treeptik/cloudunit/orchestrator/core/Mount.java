package fr.treeptik.cloudunit.orchestrator.core;

public class Mount {
    private String volumeName;
    
    private String mountPoint;

    protected Mount() {}
    
    public Mount(String volumeName, String mountPoint) {
        this.volumeName = volumeName;
        this.mountPoint = mountPoint;
    }

    public String getVolumeName() {
        return volumeName;
    }

    public String getMountPoint() {
        return mountPoint;
    }
}
