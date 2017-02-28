package fr.treeptik.cloudunit.domain.service.impl;

import static org.springframework.hateoas.client.Hop.*;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.hateoas.MediaTypes;
import org.springframework.hateoas.Resources;
import org.springframework.hateoas.client.Traverson;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestOperations;

import fr.treeptik.cloudunit.domain.core.Application;
import fr.treeptik.cloudunit.domain.core.Image;
import fr.treeptik.cloudunit.domain.service.OrchestratorService;
import fr.treeptik.cloudunit.orchestrator.resource.ContainerResource;
import fr.treeptik.cloudunit.orchestrator.resource.ImageResource;

@Component
@ConfigurationProperties("cloudunit.orchestrator")
public class OrchestratorServiceImpl implements OrchestratorService, InitializingBean {
    private static final ParameterizedTypeReference<Resources<ImageResource>> IMAGE_RESOURCES_TYPE = new ParameterizedTypeReference<Resources<ImageResource>>() {};

    @Autowired
    private RestOperations rest;
    
    private Traverson t;
    
    private URI baseUri;
    
    public URI getBaseUri() {
        return baseUri;
    }
    
    public void setBaseUri(URI baseUri) {
        this.baseUri = baseUri;
    }
    
    @Override
    public void afterPropertiesSet() throws Exception {
        t = new Traverson(baseUri, MediaTypes.HAL_JSON);
    }
    
    @Override
    public List<Image> findAllImages() {
        Resources<ImageResource> images = t.follow("cu:images")
                .toObject(IMAGE_RESOURCES_TYPE);
        
        return images.getContent().stream()
                .map(ir -> new Image(ir.getName(), ir.getType()))
                .collect(Collectors.toList());
    }

    @Override
    public void createContainer(Application application, String containerName, Image image) {
        String uri = t.follow("cu:containers").asLink().getHref();
        ContainerResource request = new ContainerResource(containerName, image.getName());
        rest.postForObject(uri, request, ContainerResource.class);
    }

    @Override
    public void deleteContainer(Application application, String containerName) {
        String uri = t
                .follow(rel("cu:containers"))
                .follow(rel("cu:container").withParameter("name", containerName))
                .asLink().getHref();
        rest.delete(uri);
    }
}
