package fr.treeptik.cloudunit.orchestrator.docker.controller;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import static fr.treeptik.cloudunit.orchestrator.docker.test.TestCaseConstants.*;

import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.hateoas.Resources;
import org.springframework.test.context.junit4.rules.SpringClassRule;
import org.springframework.test.context.junit4.rules.SpringMethodRule;
import org.springframework.test.web.servlet.ResultActions;

import fr.treeptik.cloudunit.orchestrator.docker.test.ContainerTemplate;
import fr.treeptik.cloudunit.orchestrator.resource.ContainerResource;
import fr.treeptik.cloudunit.orchestrator.resource.VariableResource;

@SpringBootTest
@AutoConfigureMockMvc
public class VariableControllerIT {
    @ClassRule
    public static final SpringClassRule SPRING_CLASS_RULE = new SpringClassRule();

    @Rule
    public final SpringMethodRule springMethodRule = new SpringMethodRule();

    @Autowired
    private ContainerTemplate containerTemplate;

    @Test
    public void testAddVariable() throws Exception {
        ContainerResource container = containerTemplate.createAndAssumeContainer(CONTAINER_NAME, IMAGE_NAME);
        try {
            ResultActions result = containerTemplate.addVariable(container, "KEY1", "VALUE1");
            result.andExpect(status().isCreated());
            
            containerTemplate.waitWhilePending(container);
            
            Resources<VariableResource> variables = containerTemplate.getVariables(container);
            assertThat(variables.getContent(), hasItem(allOf(
                    hasProperty("key", is("KEY1")),
                    hasProperty("value", is("VALUE1")))));
        } finally {
            containerTemplate.deleteContainerAndWait(container);
        }
    }
    
    @Test
    public void testUpdateVariable() throws Exception {
        ContainerResource container = containerTemplate.createAndAssumeContainer(CONTAINER_NAME, IMAGE_NAME);
        try {
            ResultActions result = containerTemplate.addVariable(container, "KEY1", "VALUE1");
            
            VariableResource variable = containerTemplate.getVariable(result);
            
            containerTemplate.waitWhilePending(container);
            
            result = containerTemplate.updateVariable(variable, "KEY1", "VALUE2");
            result.andExpect(status().isOk());
            
            variable = containerTemplate.getVariable(result);
            
            assertEquals("VALUE2", variable.getValue());
        } finally {
            containerTemplate.waitWhilePending(container);
            
            containerTemplate.deleteContainerAndWait(container);
        }
    }

    @Test
    public void testDeleteVariable() throws Exception {
        ContainerResource container = containerTemplate.createAndAssumeContainer(CONTAINER_NAME, IMAGE_NAME);
        try {
            ResultActions result = containerTemplate.addVariable(container, "KEY1", "VALUE1");
            
            VariableResource variable = containerTemplate.getVariable(result);
            
            containerTemplate.waitWhilePending(container);
            
            containerTemplate.deleteVariable(variable)
                .andExpect(status().isNoContent());

            containerTemplate.waitWhilePending(container);
            
            Resources<VariableResource> variables = containerTemplate.getVariables(container);
            assertThat(variables.getContent(), not(hasItem(hasProperty("key", is("KEY1")))));
        } finally {
            containerTemplate.deleteContainerAndWait(container);
        }
    }
}
