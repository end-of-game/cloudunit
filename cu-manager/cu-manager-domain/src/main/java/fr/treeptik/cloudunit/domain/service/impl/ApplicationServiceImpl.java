package fr.treeptik.cloudunit.domain.service.impl;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import fr.treeptik.cloudunit.domain.model.Application;
import fr.treeptik.cloudunit.domain.model.Image;
import fr.treeptik.cloudunit.domain.repository.ApplicationRepository;
import fr.treeptik.cloudunit.domain.service.ApplicationService;
import fr.treeptik.cloudunit.domain.service.ImageService;

@Service
public class ApplicationServiceImpl implements ApplicationService {
    @Autowired
    private ApplicationRepository applicationRepository;
    
    @Autowired
    private ImageService imageService;
    
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
        applicationRepository.delete(application);
    }
    
    public void addService(String imageName) {
        Optional<Image> image = imageService.findByName(imageName);
        
        if (!image.isPresent()) {
            throw new IllegalArgumentException(String.format("Image %s could not be found", imageName));
        }        
    }

}
