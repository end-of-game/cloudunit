package fr.treeptik.cloudunit.domain.service;

import fr.treeptik.cloudunit.domain.core.Application;
import fr.treeptik.cloudunit.domain.core.Service;
import fr.treeptik.cloudunit.orchestrator.core.ContainerState;

public interface ApplicationService {
    public Application create(String name);

    public void delete(Application application);

    public Service addService(Application application, String imageName);

    public void startApplication(Application application);
    
    public void stopApplication(Application application);

    public void updateContainerState(Application application, String containerName, ContainerState state);
}
