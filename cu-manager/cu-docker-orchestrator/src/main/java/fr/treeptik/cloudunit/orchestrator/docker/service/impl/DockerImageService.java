package fr.treeptik.cloudunit.orchestrator.docker.service.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.spotify.docker.client.DockerClient;

import fr.treeptik.cloudunit.orchestrator.core.Image;
import fr.treeptik.cloudunit.orchestrator.core.ImageType;
import fr.treeptik.cloudunit.orchestrator.core.Variable;
import fr.treeptik.cloudunit.orchestrator.core.VariableRole;
import fr.treeptik.cloudunit.orchestrator.docker.repository.ImageRepository;

@Component
public class DockerImageService implements InitializingBean {
    private static final Logger LOGGER = LoggerFactory.getLogger(DockerImageService.class);

    private static final String CLOUDUNIT_LABEL = "io.cloudunit";

    private static final String VERSION_LABEL = "io.cloudunit.version";

    private static final String DISPLAY_NAME_LABEL = "io.cloudunit.name.display";

    private static final String IMAGE_TYPE_LABEL = "io.cloudunit.type";

    private static final String SERVICE_NAME_LABEL = "io.cloudunit.name.service";

    @Autowired
    private DockerClient docker;
    
    @Autowired
    private ImageRepository imageRepository;

    @Override
    public void afterPropertiesSet() throws Exception {
//        docker.listImages(ListImagesParam.withLabel(CLOUDUNIT_LABEL)).stream()
//            .map(i -> toImage(i))
//            .forEach(imageRepository::save);
        
        imageRepository.deleteAll();
        
        List<Variable> variables = new ArrayList<>();
        variables.add(new Variable("TOMCAT_USER", null, VariableRole.USER));
        variables.add(new Variable("TOMCAT_PASSWORD", null, VariableRole.PASSWORD));
        
        List<Image> images = Arrays.asList(
                Image.of("tomcat", "7", ImageType.SERVER, "cloudunit/tomcat:7")
                    .variable(new Variable("TOMCAT_USER", null, VariableRole.USER))
                    .build(),
                Image.of("tomcat", "8", ImageType.SERVER, "cloudunit/tomcat:8")
                    .variable(new Variable("TOMCAT_USER", null, VariableRole.USER))
                    .build(),
                Image.of("tomcat", "9", ImageType.SERVER, "cloudunit/tomcat:9")
                    .variable(new Variable("TOMCAT_USER", null, VariableRole.USER))
                    .build());
                
        images.forEach(image -> {
            imageRepository.save(image);
        });
    }

    private Image toImage(com.spotify.docker.client.messages.Image image) {
        String serviceName = image.labels().get(SERVICE_NAME_LABEL);
        ImageType type = ImageType.valueOf(image.labels().get(IMAGE_TYPE_LABEL));
        String repositoryTag = image.repoTags().get(0);
        String displayName = image.labels().get(DISPLAY_NAME_LABEL);
        String version = image.labels().get(VERSION_LABEL);
        
        Image.Builder builder = Image.of(serviceName, version, type, repositoryTag)
                .displayName(displayName);
        
        return builder.build();
    }
}
