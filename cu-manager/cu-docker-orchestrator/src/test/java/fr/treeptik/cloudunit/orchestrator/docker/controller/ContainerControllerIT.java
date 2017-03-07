package fr.treeptik.cloudunit.orchestrator.docker.controller;

import static org.awaitility.Awaitility.*;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.awaitility.Duration;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.rules.SpringClassRule;
import org.springframework.test.context.junit4.rules.SpringMethodRule;
import org.springframework.test.web.servlet.ResultActions;

import fr.treeptik.cloudunit.orchestrator.core.ContainerState;
import fr.treeptik.cloudunit.orchestrator.docker.test.ContainerTemplate;
import fr.treeptik.cloudunit.orchestrator.resource.ContainerResource;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("dev")
public class ContainerControllerIT {
    private static final String IMAGE_NAME = "cloudunit/tomcat-8";

    private static final String CONTAINER_NAME = "mycontainer";

    @ClassRule
    public static final SpringClassRule SPRING_CLASS_RULE = new SpringClassRule();
    
    @Rule
    public final SpringMethodRule springMethodRule = new SpringMethodRule();

    @Autowired
    private ContainerTemplate containerTemplate;

    private ContainerResource container;
    
    @Test
    public void testCreateContainer() throws Exception {
        ResultActions result = containerTemplate.createContainer(CONTAINER_NAME, IMAGE_NAME);
        result.andExpect(status().isCreated());
        
        container = containerTemplate.getContainer(result);
        try {
            assertThat(container.getContainerId(), not(isEmptyString()));
        } finally {
            result = containerTemplate.deleteContainer(container);
            result.andExpect(status().isNoContent());
        }
    }
    
    @Test
    public void testStartContainer() throws Exception {
        container = containerTemplate.createAndAssumeContainer(CONTAINER_NAME, IMAGE_NAME);
        
        try {
            ResultActions result = containerTemplate.startContainer(container);
            result.andExpect(status().isNoContent());
            
            await().atMost(Duration.FIVE_SECONDS).until(() -> {
                container = containerTemplate.refreshContainer(container);
                return container.getState() == ContainerState.STARTED;
            });
        } finally {
            containerTemplate.deleteContainer(container);
        }        
    }
    
    @Test
    public void testStartStopContainer() throws Exception {
        container = containerTemplate.createAndAssumeContainer(CONTAINER_NAME, IMAGE_NAME);
        
        try {
            ResultActions result = containerTemplate.startContainer(container);
            result.andExpect(status().isNoContent());
            
            await().atMost(Duration.FIVE_SECONDS).until(() -> {
                container = containerTemplate.refreshContainer(container);
                return container.getState() == ContainerState.STARTED;
            });
        
            result = containerTemplate.stopContainer(container);
            result.andExpect(status().isNoContent());
            
            await().atMost(Duration.FIVE_SECONDS).until(() -> {
                container = containerTemplate.refreshContainer(container);
                return container.getState() == ContainerState.STOPPED;
            });
        } finally {
            containerTemplate.deleteContainer(container);
        }        
    }
}
