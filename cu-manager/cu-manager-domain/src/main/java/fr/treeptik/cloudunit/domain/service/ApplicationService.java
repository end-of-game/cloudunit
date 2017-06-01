package fr.treeptik.cloudunit.domain.service;

import org.springframework.web.multipart.MultipartFile;

import fr.treeptik.cloudunit.domain.core.Application;
import fr.treeptik.cloudunit.domain.core.Deployment;
import fr.treeptik.cloudunit.domain.core.Service;
import fr.treeptik.cloudunit.orchestrator.core.ContainerState;

public interface ApplicationService {
    Application create(String name);

    void delete(Application application);

    Service addService(Application application, String imageName);

    void removeService(Application application, Service service);

    void startApplication(Application application);
    
    void stopApplication(Application application);
    
    Deployment addDeployment(Application application, Service service, String contextPath, MultipartFile file, String string);
    
    void updateContainerState(Application application, String containerName, ContainerState state);
    
}
