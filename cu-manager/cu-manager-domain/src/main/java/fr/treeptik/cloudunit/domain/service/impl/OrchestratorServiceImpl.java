package fr.treeptik.cloudunit.domain.service.impl;

import static org.springframework.hateoas.client.Hop.*;

import java.net.URI;
import java.util.List;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.MediaTypes;
import org.springframework.hateoas.Resources;
import org.springframework.hateoas.client.Traverson;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestOperations;

import fr.treeptik.cloudunit.domain.core.Application;
import fr.treeptik.cloudunit.domain.core.Image;
import fr.treeptik.cloudunit.domain.core.Service;
import fr.treeptik.cloudunit.domain.repository.ImageRepository;
import fr.treeptik.cloudunit.domain.service.OrchestratorService;
import fr.treeptik.cloudunit.orchestrator.resource.ContainerResource;
import fr.treeptik.cloudunit.orchestrator.resource.ImageResource;

@Component
@ConfigurationProperties("cloudunit.orchestrator")
public class OrchestratorServiceImpl implements OrchestratorService, InitializingBean {
    private static final ParameterizedTypeReference<Resources<ImageResource>> IMAGE_RESOURCES_TYPE = new ParameterizedTypeReference<Resources<ImageResource>>() {};

    private static final long DEFAULT_MONITOR_DELAY = 2;

    @Autowired
    private RestOperations rest;
    
    @Autowired
    private ScheduledExecutorService executor;
    
    @Autowired
    private ImageRepository imageRepository;
    
    private Traverson t;
    
    private URI baseUri;

    private long monitorDelay = DEFAULT_MONITOR_DELAY;

    public ScheduledExecutorService getExecutor() {
        return executor;
    }
    
    public void setExecutor(ScheduledExecutorService executor) {
        this.executor = executor;
    }
    
    public URI getBaseUri() {
        return baseUri;
    }
    
    public void setBaseUri(URI baseUri) {
        this.baseUri = baseUri;
    }
    
    public long getMonitorDelay() {
        return monitorDelay;
    }
    
    /**
     * Set the number of seconds between container monitor requests.
     * 
     * @param monitorDelay a number of seconds, strictly positive
     */
    public void setMonitorDelay(long monitorDelay) {
        if (monitorDelay <= 0) {
            throw new IllegalArgumentException("monitor delay must not be negative or zero");
        }
        this.monitorDelay = monitorDelay;
    }
    
    @Override
    public void afterPropertiesSet() throws Exception {
        t = new Traverson(baseUri, MediaTypes.HAL_JSON);
        
        executor.scheduleWithFixedDelay(this::monitorImages, 0, monitorDelay, TimeUnit.SECONDS);
    }
    
    private void monitorImages() {
        List<Image> images = findAllImages();
        
        List<Image> existingImages = imageRepository.findAll();
        
        existingImages.stream()
            .filter(i -> !images.contains(i))
            .forEach(imageRepository::delete);
        
        images.stream()
            .filter(i -> !existingImages.contains(i))
            .forEach(imageRepository::save);
    }
    
    @Override
    public List<Image> findAllImages() {
        Resources<ImageResource> images = t.follow("cu:images")
                .toObject(IMAGE_RESOURCES_TYPE);
        
        return images.getContent().stream()
                .map(ir -> new Image(ir.getName(), ir.getServiceName(), ir.getType()))
                .collect(Collectors.toList());
    }

    @Override
    public void createContainer(Application application, Service service) {
        String uri = t.follow("cu:containers").asLink().getHref();
        ContainerResource request = new ContainerResource(service.getContainerName(), service.getImageName());
        ContainerResource container = rest.postForObject(uri, request, ContainerResource.class);
        service.setContainerUrl(container.getLink(Link.REL_SELF).getHref());
    }

    @Override
    public void deleteContainer(Application application, String containerName) {
        String uri = t
                .follow(rel("cu:containers"))
                .follow(rel("cu:container").withParameter("name", containerName))
                .asLink().getHref();
        rest.delete(uri);
    }

    @Override
    public void startContainer(String containerName) {
        String uri = t
                .follow(rel("cu:containers"))
                .follow(rel("cu:container").withParameter("name", containerName))
                .follow(rel("cu:start"))
                .asLink().getHref();
        rest.postForEntity(uri, null, Void.class);
    }

    @Override
    public void stopContainer(String containerName) {
        String uri = t
                .follow(rel("cu:containers"))
                .follow(rel("cu:container").withParameter("name", containerName))
                .follow(rel("cu:stop"))
                .asLink().getHref();
        rest.postForEntity(uri, null, Void.class);
    }
}
