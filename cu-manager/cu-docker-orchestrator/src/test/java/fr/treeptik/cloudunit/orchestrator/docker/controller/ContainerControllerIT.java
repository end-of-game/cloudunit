package fr.treeptik.cloudunit.orchestrator.docker.controller;

import static fr.treeptik.cloudunit.orchestrator.docker.test.TestCaseConstants.*;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import fr.treeptik.cloudunit.orchestrator.core.ContainerState;
import fr.treeptik.cloudunit.orchestrator.docker.test.ContainerTemplate;
import fr.treeptik.cloudunit.orchestrator.docker.test.ImageTemplate;
import fr.treeptik.cloudunit.orchestrator.resource.ContainerResource;
import fr.treeptik.cloudunit.orchestrator.resource.ImageResource;
import fr.treeptik.cloudunit.orchestrator.resource.VariableResource;
import org.junit.ClassRule;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.hateoas.Resources;
import org.springframework.test.context.junit4.rules.SpringClassRule;
import org.springframework.test.context.junit4.rules.SpringMethodRule;
import org.springframework.test.web.servlet.ResultActions;

@SpringBootTest
@AutoConfigureMockMvc
public class ContainerControllerIT {

    public static final String ARTIFACT_URL = "https://github.com/Treeptik/cloudunit/releases/download/1.0/helloworld.war";

    @ClassRule
    public static final SpringClassRule SPRING_CLASS_RULE = new SpringClassRule();

    @Rule
    public final SpringMethodRule springMethodRule = new SpringMethodRule();

    @Autowired
    private ContainerTemplate containerTemplate;
    
    @Autowired
    private ImageTemplate imageTemplate;
    
    @Test
    public void testCreateContainer() throws Exception {
        containerTemplate.assumeContainerDoesNotExist(CONTAINER_NAME);

        ResultActions result = containerTemplate.createContainer(CONTAINER_NAME, IMAGE_NAME);
        result.andExpect(status().isCreated());
        
        ContainerResource container = containerTemplate.getContainer(result);
        
        try {
            assertThat(container.getState(), isOneOf(ContainerState.STOPPING, ContainerState.STOPPED));
            
            ImageResource image = imageTemplate.getImage(IMAGE_NAME);
            
            Resources<VariableResource> expectedVariables = imageTemplate.getVariables(image);
            
            Resources<VariableResource> variables = containerTemplate.getVariables(container);
            
            for (VariableResource variable : expectedVariables.getContent()) {
                assertThat(variables.getContent(), hasItem(hasProperty("key", is(variable.getKey()))));
            }
            
        } finally {
        	containerTemplate.waitWhilePending(container);
            containerTemplate.deleteContainerAndWait(container);
        }
    }

    @Test
    public void testCreateDeleteContainer() throws Exception {
        containerTemplate.assumeContainerDoesNotExist(CONTAINER_NAME);

        ContainerResource container = containerTemplate.createAndAssumeContainer(CONTAINER_NAME, IMAGE_NAME);
        
        containerTemplate.deleteContainer(container)
            .andExpect(status().isNoContent());
    }
    
    @Test
    public void testStartContainer() throws Exception {
        containerTemplate.assumeContainerDoesNotExist(CONTAINER_NAME);

        ContainerResource container = containerTemplate.createAndAssumeContainer(CONTAINER_NAME, IMAGE_NAME);
        
        try {
            containerTemplate.startContainer(container)
                .andExpect(status().isNoContent());
            
            containerTemplate.waitWhilePending(container);
            container = containerTemplate.refreshContainer(container);
            
            assertEquals(ContainerState.STARTED, container.getState());
        } finally {
            containerTemplate.waitWhilePending(container);
            containerTemplate.deleteContainerAndWait(container);
        }
    }
    
    @Test
    public void testStartStopContainer() throws Exception {
        containerTemplate.assumeContainerDoesNotExist(CONTAINER_NAME);

        ContainerResource container = containerTemplate.createAndAssumeContainer(CONTAINER_NAME, IMAGE_NAME);

        try {
            containerTemplate.startContainer(container)
                .andExpect(status().isNoContent());
            
            containerTemplate.waitWhilePending(container);
            
            container = containerTemplate.refreshContainer(container);
            containerTemplate.stopContainer(container)
                .andExpect(status().isNoContent());
            
            containerTemplate.waitWhilePending(container);
            container = containerTemplate.refreshContainer(container);
            
            assertEquals(ContainerState.STOPPED, container.getState());
        } finally {
            containerTemplate.waitWhilePending(container);
            containerTemplate.deleteContainerAndWait(container);
        }
    }

}
