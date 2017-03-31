package fr.treeptik.cloudunit.orchestrator.docker.service.impl;

import java.io.IOException;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.spotify.docker.client.DockerClient;
import com.spotify.docker.client.DockerClient.ListImagesParam;

import fr.treeptik.cloudunit.orchestrator.core.Image;
import fr.treeptik.cloudunit.orchestrator.core.ImageType;
import fr.treeptik.cloudunit.orchestrator.core.Variable;
import fr.treeptik.cloudunit.orchestrator.core.VariableRole;
import fr.treeptik.cloudunit.orchestrator.docker.repository.ImageRepository;

@Component
public class DockerImageServiceImpl implements InitializingBean {
    private static final Logger LOGGER = LoggerFactory.getLogger(DockerImageServiceImpl.class);

    private static final String CLOUDUNIT_LABEL = "io.cloudunit";

    private static final String VARIABLES_LABEL = "io.cloudunit.variables";

    private static final String VERSION_LABEL = "io.cloudunit.version";

    private static final String IMAGE_TYPE_LABEL = "io.cloudunit.type";

    private static final String SERVICE_NAME_LABEL = "io.cloudunit.name.service";

    private static final String DISPLAY_NAME_LABEL = "io.cloudunit.name.display";

    @Autowired
    private DockerClient docker;
    
    @Autowired
    private ImageRepository imageRepository;
    
    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public void afterPropertiesSet() throws Exception {
        imageRepository.deleteAll();

        docker.listImages(
                ListImagesParam.withLabel(CLOUDUNIT_LABEL),
                ListImagesParam.danglingImages(false)).stream()
        .map(i -> toImage(i))
        .filter(i -> i != null)
        .forEach(image -> {
            LOGGER.info("Registering image {}", image);
            imageRepository.save(image);
        });
    }

    private Image toImage(com.spotify.docker.client.messages.Image image) {
        String serviceName = image.labels().get(SERVICE_NAME_LABEL);
        ImageType type = ImageType.valueOf(image.labels().get(IMAGE_TYPE_LABEL).toUpperCase());
        String repositoryTag = image.repoTags().get(0);
        String displayName = image.labels().get(DISPLAY_NAME_LABEL);
        String version = image.labels().get(VERSION_LABEL);
        String variableSpec = image.labels().get(VARIABLES_LABEL);
        
        Image.Builder builder = Image.of(serviceName, version, type, repositoryTag)
                .displayName(displayName);
        
        try {
            Map<String, String> variableSpecMap = objectMapper.readValue(variableSpec, new TypeReference<Map<String,String>>() {});
            
            variableSpecMap.entrySet().stream()
                .map(kv -> toVariable(kv))
                .forEach(variable -> builder.variable(variable));
        } catch (IOException e) {
            LOGGER.warn("Couldn't parse variable specification for image {}: {}", repositoryTag, variableSpec, e);
        }
        
        return builder.build();
    }

    private Variable toVariable(Entry<String, String> variableSpec) {
        return new Variable(variableSpec.getKey(), null, VariableRole.valueOf(variableSpec.getValue().toUpperCase()));
    }
}
