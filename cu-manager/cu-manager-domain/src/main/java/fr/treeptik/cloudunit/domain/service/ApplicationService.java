package fr.treeptik.cloudunit.domain.service;

import fr.treeptik.cloudunit.domain.core.Application;
import fr.treeptik.cloudunit.domain.core.Service;
import fr.treeptik.cloudunit.orchestrator.core.ContainerState;

public interface ApplicationService {
    Application create(String name);

    void delete(Application application);

    Service addService(Application application, String imageName);

    void removeService(Application application, Service service);

    void startApplication(Application application);
    
    void stopApplication(Application application);

    void updateContainerState(Application application, String containerName, ContainerState state);
}
