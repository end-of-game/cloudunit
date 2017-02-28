package fr.treeptik.cloudunit.domain.core;

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

    private String imageName;
    private String name;
    private String containerName;
    
    protected Service() {}
    
    public Service(Application application, Image image) {
        this.name = image.getBasename();
        this.containerName = String.format("%s-%s", application.getName(), image.getBasename());
        this.imageName = image.getName();
    }

    public String getImageName() {
        return imageName;
    }
    
    public String getName() {
        return name;
    }

    public String getContainerName() {
        return containerName;
    }

    public abstract <R> R accept(ServiceVisitor<R> visitor);
}