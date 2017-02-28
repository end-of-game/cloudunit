package fr.treeptik.cloudunit.domain.service.impl;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import fr.treeptik.cloudunit.domain.core.Application;
import fr.treeptik.cloudunit.domain.core.Image;
import fr.treeptik.cloudunit.domain.core.Service;
import fr.treeptik.cloudunit.domain.repository.ApplicationRepository;
import fr.treeptik.cloudunit.domain.repository.ImageRepository;
import fr.treeptik.cloudunit.domain.service.ApplicationService;
import fr.treeptik.cloudunit.domain.service.OrchestratorService;

@Component
public class ApplicationServiceImpl implements ApplicationService {
    @Autowired
    private ApplicationRepository applicationRepository;
    
    @Autowired
    private ImageRepository imageRepository;
    
    @Autowired
    private OrchestratorService orchestratorService;
    
    public void setApplicationRepository(ApplicationRepository applicationRepository) {
        this.applicationRepository = applicationRepository;
    }

    /**
     * Create an application.
     * 
     * Each application must have at least one container, either server or module.
     * 
     * @param name  The name of the new application, not blank
     */
    @Override
    public Application create(String name) {
        Optional<Application> preexistingApplication = applicationRepository.findByName(name);
        
        if (preexistingApplication.isPresent()) {
            throw new IllegalArgumentException(String.format("An application already exists with the name %s", name));
        }
        
        Application application = new Application(name);
        
        application = applicationRepository.save(application);
        
        return application;
    }
    
    @Override
    public void delete(Application application) {
        // Use of stream avoids ConcurrentModificationException
        application.getServices().stream().forEach(service -> {
            removeService(application, service);
        });
        
        applicationRepository.delete(application);
    }
    
    @Override
    public Service addService(Application application, String imageName) {
        Image image = imageRepository.findByName(imageName)
                .orElseThrow(() -> new IllegalArgumentException(String.format("Image %s could not be found", imageName)));
        
        Service service = application.addService(image);
        
        orchestratorService.createContainer(application, service.getContainerName(), image);
        
        applicationRepository.save(application);
        
        return service;
    }
    
    public void removeService(Application application, Service service) {
        orchestratorService.deleteContainer(application, service.getContainerName());
        
        application.removeService(service.getName());
    }

}
