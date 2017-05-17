package fr.treeptik.cloudunit.domain.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import fr.treeptik.cloudunit.domain.core.Application;
import fr.treeptik.cloudunit.domain.core.Deployment;
import fr.treeptik.cloudunit.domain.core.Image;
import fr.treeptik.cloudunit.domain.core.Service;
import fr.treeptik.cloudunit.domain.repository.ApplicationRepository;
import fr.treeptik.cloudunit.domain.service.ApplicationService;
import fr.treeptik.cloudunit.domain.service.OrchestratorService;
import fr.treeptik.cloudunit.domain.service.ServiceListener;
import fr.treeptik.cloudunit.orchestrator.core.ContainerState;

@Component
public class ApplicationServiceImpl implements ApplicationService {
    private static final Logger LOGGER = LoggerFactory.getLogger(ApplicationServiceImpl.class);

    @Autowired
    private ApplicationRepository applicationRepository;
    
    @Autowired
    private OrchestratorService orchestratorService;
    
    @Autowired
    private List<ServiceListener> listeners;
    
    public ApplicationServiceImpl() {
		listeners = new ArrayList<>();
	}
    
    public void setApplicationRepository(ApplicationRepository applicationRepository) {
        this.applicationRepository = applicationRepository;
    }
    
    public void setOrchestratorService(OrchestratorService orchestratorService) {
    	this.orchestratorService = orchestratorService;
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
        
        application.getServices().stream().forEach(service -> {
        	fireServiceDeleted(service);
        });
        
        applicationRepository.delete(application);
    }
    
    @Override
    public Service addService(Application application, String imageName) {
        
        Image image = orchestratorService.findImageByName(imageName)
        		.orElseThrow(() -> new IllegalArgumentException(String.format("Image %s could not be found", imageName)));
        
        if (!application.pending()) {
            throw new IllegalStateException("Cannot add service");
        }
        
        Service service = application.addService(image);

        applicationRepository.save(application);
        
        fireServiceCreated(service);
        
        return service;
    }
    
    @Override
    public void removeService(Application application, Service service) {
        application.removeService(service.getName());
        
        applicationRepository.save(application);
        
        fireServiceDeleted(service);
    }

    @Override
    public void startApplication(Application application) {
        if (!application.start()) {
            throw new IllegalStateException("Cannot start application");
        }
        
        applicationRepository.save(application);
        
        application.getServices().forEach(service -> {
        	fireServiceStarted(service);
        });
    }

    @Override
    public void stopApplication(Application application) {
        if (!application.stop()) {
            throw new IllegalStateException("Cannot stop application");
        }
        
        applicationRepository.save(application);
        
        application.getServices().forEach(service -> {
        	fireServiceStopped(service);
        });
    }
    
	@Override
	public Deployment addDeployment(Application application, Service service, String contextPath, MultipartFile file) {
		
		if (service.getState().isPending()) {
            throw new IllegalStateException(String.format("Cannot deploy archive with contextPath %s", contextPath));
        }
		
		Deployment deployment = service.addDeployment(contextPath);
		
		applicationRepository.save(application);
		
		orchestratorService.deploy(service.getContainerName(), contextPath, file);
		
		return deployment;
		
	}
	
	@Override
	public void removeDeployment(Application application, Service service, Deployment deployment) {
		service.removeDeployment(deployment.getContextPath());
		
		applicationRepository.save(application);
		
		orchestratorService.undeploy(service.getContainerName(), deployment.getContextPath());
	}
    
    @Override
    public void updateContainerState(Application application, String serviceName, ContainerState state) {
        Optional<Service> service = application.getServiceByContainerName(serviceName);
        
        if (!service.isPresent()) {
            LOGGER.warn("Tried to update an unknown service {} on application {}", serviceName, application.getName());
            return;
        }
        service.get().setState(state);
        
        List<ContainerState> serviceStates = application.getServices().stream()
                .map(s -> s.getState())
                .collect(Collectors.toList());
        
        LOGGER.debug("Application {} {} - service states: {}",
                application.getName(),
                application.getState(),
                serviceStates.stream()
                    .map(Object::toString)
                    .collect(Collectors.joining(",")));
        
        if (serviceStates.stream().anyMatch(s -> s.isPending())
                && !application.isPending()) {
            if (application.pending()) {
                LOGGER.info("Application {} pending", application.getName());
            }
        } else if (serviceStates.stream().allMatch(s -> s == ContainerState.STARTED)) {
            if (application.started()) {
                LOGGER.info("Application {} started", application.getName());
            }
        } else if (serviceStates.stream().allMatch(s -> s == ContainerState.STOPPED)) {
            if (application.stopped()) {
                LOGGER.info("Application {} stopped", application.getName());
            }
        }
        
        applicationRepository.save(application);
    }
    
    private void fireServiceCreated(Service service) {
        listeners.forEach(listener -> listener.onServiceCreated(service));
    }

    private void fireServiceDeleted(Service service) {
        listeners.forEach(listener -> listener.onServiceDeleted(service));
    }
    
    private void fireServiceStarted(Service service) {
        listeners.forEach(listener -> listener.onServiceStarted(service));
    }
    
    private void fireServiceStopped(Service service) {
        listeners.forEach(listener -> listener.onServiceStopped(service));
    }

}
