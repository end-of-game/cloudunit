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
import org.springframework.test.context.junit4.rules.SpringClassRule;
import org.springframework.test.context.junit4.rules.SpringMethodRule;
import org.springframework.test.web.servlet.ResultActions;

import fr.treeptik.cloudunit.orchestrator.docker.test.ContainerTemplate;
import fr.treeptik.cloudunit.orchestrator.resource.ContainerResource;

@SpringBootTest
@AutoConfigureMockMvc
public class ContainerControllerIT {
    @ClassRule
    public static final SpringClassRule SPRING_CLASS_RULE = new SpringClassRule();
    
    @Rule
    public final SpringMethodRule springMethodRule = new SpringMethodRule();

    @Autowired
    private ContainerTemplate containerTemplate;
    
    @Test
    public void testCreateContainer() throws Exception {
        ResultActions result = containerTemplate.createContainer("mycontainer", "cloudunit/tomcat-8");
        result.andExpect(status().isCreated());
        
        ContainerResource container = containerTemplate.getContainer(result);
        try {
            assertThat(container.getContainerId(), not(isEmptyString()));
        } finally {
            result = containerTemplate.deleteContainer(container);
            result.andExpect(status().isNoContent());
        }
    }
}
