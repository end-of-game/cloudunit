package fr.treeptik.cloudunit.orchestrator.docker.controller;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

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
    
    @Test
    public void testCreateContainer() throws Exception {
        ResultActions result = containerTemplate.createContainer(CONTAINER_NAME, IMAGE_NAME);
        result.andExpect(status().isCreated());
        
        ContainerResource container = containerTemplate.getContainer(result);
        try {
            assertThat(container.getState(), isOneOf(ContainerState.STOPPING, ContainerState.STOPPED));
        } finally {
            containerTemplate.deleteContainer(container)
                .andExpect(status().isNoContent());
            
            containerTemplate.waitForRemoval(container);
        }
    }
    
    @Test
    public void testStartContainer() throws Exception {
        ContainerResource container = containerTemplate.createAndAssumeContainer(CONTAINER_NAME, IMAGE_NAME);
        
        try {
            containerTemplate.startContainer(container)
                .andExpect(status().isNoContent());            
        } finally {
            containerTemplate.waitWhilePending(container);
            containerTemplate.deleteContainerAndWait(container);
        }
    }
    
    @Test
    public void testStartStopContainer() throws Exception {
        ContainerResource container = containerTemplate.createAndAssumeContainer(CONTAINER_NAME, IMAGE_NAME);
        
        try {
            containerTemplate.startContainer(container)
                .andExpect(status().isNoContent());
            
            containerTemplate.waitWhilePending(container);
            
            container = containerTemplate.refreshContainer(container);
            containerTemplate.stopContainer(container)
                .andExpect(status().isNoContent());
        } finally {
            containerTemplate.waitWhilePending(container);
            containerTemplate.deleteContainerAndWait(container);
        }
    }
}
