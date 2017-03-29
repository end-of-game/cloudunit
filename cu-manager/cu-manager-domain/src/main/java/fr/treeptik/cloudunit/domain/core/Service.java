package fr.treeptik.cloudunit.domain.core;

import fr.treeptik.cloudunit.orchestrator.core.ContainerState;
import fr.treeptik.cloudunit.orchestrator.core.ImageType;

public abstract class Service {
    public static Service of(Application application, Image image) {
        ImageType type = image.getType();
        
        switch (type) {
        case SERVER:
            return new Server(application, image);

        case MODULE:
            return new Module(application, image);
        default:
            throw new IllegalArgumentException();
        }
    }
    
    private static String containerName(String applicationName, String basename) {
        return String.format("%s-%s", applicationName, basename);
    }

    private String imageName;
    private String name;
    private String containerName;
    private String containerUrl;
    private ContainerState state;
    
    protected Service() {}
    
    public Service(Application application, Image image) {
        this.name = image.getName();
        this.containerName = containerName(application.getName(), image.getName());
        this.imageName = image.getName();
        this.state = ContainerState.STOPPED;
    }

    public String getImageName() {
        return imageName;
    }
    
    public String getName() {
        return name;
    }
    
    public ContainerState getState() {
        return state;
    }
    
    public void setState(ContainerState state) {
        this.state = state;
    }
    
    public String getContainerName() {
        return containerName;
    }
    
    public String getContainerUrl() {
        return containerUrl;
    }
    
    public void setContainerUrl(String containerUrl) {
        this.containerUrl = containerUrl;
    }

    public abstract <R> R accept(ServiceVisitor<R> visitor);
}
