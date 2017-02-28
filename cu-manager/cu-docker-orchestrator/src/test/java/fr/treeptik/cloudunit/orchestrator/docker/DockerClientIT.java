package fr.treeptik.cloudunit.orchestrator.docker;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import java.util.List;
import java.util.stream.Collectors;

import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.rules.SpringClassRule;
import org.springframework.test.context.junit4.rules.SpringMethodRule;

import com.spotify.docker.client.DockerClient;
import com.spotify.docker.client.DockerClient.ListContainersParam;
import com.spotify.docker.client.DockerClient.ListImagesParam;
import com.spotify.docker.client.messages.Container;

@SpringBootTest
public class DockerClientIT {
    private static final String FILTER_LABEL_KEY = "origin";
    private static final String FILTER_LABEL_VALUE = "cloudunit";
    
    @ClassRule
    public static final SpringClassRule SPRING_CLASS_RULE = new SpringClassRule();
    
    @Rule
    public final SpringMethodRule springMethodRule = new SpringMethodRule();
    
    @Autowired
    private DockerClient docker;
    
    @Test
    public void testListContainers() throws Exception {
        List<Container> containers = docker.listContainers(
                ListContainersParam.allContainers(),
                ListContainersParam.withLabel(FILTER_LABEL_KEY, FILTER_LABEL_VALUE));
        
        assertThat(containers, hasSize(1));
    }
    
    @Test
    public void testListImages() throws Exception {
        List<String> images = docker.listImages(
                ListImagesParam.withLabel(FILTER_LABEL_KEY, FILTER_LABEL_VALUE))
                .stream()
                .flatMap(i -> i.repoTags().stream())
                .collect(Collectors.toList());
        
        assertThat(images, hasItem(containsString("cloudunit/base-16.04")));
    }
}
