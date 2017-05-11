package fr.treeptik.cloudunit.domain.processor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.integration.annotation.MessageEndpoint;

import fr.treeptik.cloudunit.domain.DomainChannels;
import fr.treeptik.cloudunit.domain.repository.ApplicationRepository;
import fr.treeptik.cloudunit.domain.service.ApplicationService;
import fr.treeptik.cloudunit.orchestrator.resource.ContainerEvent;

@MessageEndpoint
public class ContainerEventProcessor {
    @Autowired
    private ApplicationRepository applicationRepository;
    
    @Autowired
    private ApplicationService applicationService;
    
    @StreamListener(DomainChannels.CONTAINERS)
    public void onContainerEvent(ContainerEvent event) {
        String containerName = event.getContainer().getName();
        applicationRepository.findByServicesContainerName(containerName).ifPresent(application -> {
            applicationService.updateContainerState(application, containerName, event.getContainer().getState());
        });
    }
}
