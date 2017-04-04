package fr.treeptik.cloudunit.orchestrator.docker.controller;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.hamcrest.Matcher;
import org.hamcrest.Matchers;
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

import fr.treeptik.cloudunit.orchestrator.core.VariableRole;
import fr.treeptik.cloudunit.orchestrator.docker.test.ContainerTemplate;
import fr.treeptik.cloudunit.orchestrator.resource.ContainerResource;
import fr.treeptik.cloudunit.orchestrator.resource.VariableResource;

@SpringBootTest
@AutoConfigureMockMvc
public class ContainerDependencyControllerIT {
    @ClassRule
    public static final SpringClassRule SPRING_CLASS_RULE = new SpringClassRule();

    @Rule
    public final SpringMethodRule springMethodRule = new SpringMethodRule();

    @Autowired
    private ContainerTemplate containerTemplate;
    
    @Test
    public void testAddDependency() throws Exception {
        ContainerResource module = containerTemplate.createAndAssumeContainer("mymodule", "mysql:5.5");
        ContainerResource server = containerTemplate.createAndAssumeContainer("myserver", "tomcat:8");
        try {
            ResultActions result = containerTemplate.addDependency(server, module);
            result.andExpect(status().isCreated());
            containerTemplate.waitWhilePending(server);
            
            Resources<VariableResource> moduleVariables = containerTemplate.getVariables(module);
            Resources<VariableResource> serverVariables = containerTemplate.getVariables(server);
            
            assertThat(serverVariables.getContent(), hasItems(importedVariables(moduleVariables)));
        } finally {
            containerTemplate.waitWhilePending(server);
            containerTemplate.deleteContainerAndWait(server);
            containerTemplate.waitWhilePending(module);
            containerTemplate.deleteContainerAndWait(module);
        }
    }

    @Test
    public void testAddDependencyAddVariable() throws Exception {
        ContainerResource module = containerTemplate.createAndAssumeContainer("mymodule", "mysql:5.5");
        ContainerResource server = containerTemplate.createAndAssumeContainer("myserver", "tomcat:8");
        try {
            containerTemplate.addDependency(server, module);
            containerTemplate.waitWhilePending(server);
            
            ResultActions result = containerTemplate.addVariable(module, "BOO", "123");
            VariableResource variable = containerTemplate.getVariable(result);
            containerTemplate.waitWhilePending(module);
            containerTemplate.waitWhilePending(server);
            
            Resources<VariableResource> serverVariables = containerTemplate.getVariables(server);
            
            assertThat(serverVariables.getContent(), hasItem(importedVariable(variable)));
        } finally {
            containerTemplate.waitWhilePending(server);
            containerTemplate.deleteContainerAndWait(server);
            containerTemplate.waitWhilePending(module);
            containerTemplate.deleteContainerAndWait(module);
        }
    }
    
    @SuppressWarnings("unchecked")
    private Matcher<VariableResource>[] importedVariables(Resources<VariableResource> variables) {
        return (Matcher<VariableResource>[]) variables.getContent().stream()
                .map(v -> importedVariable(v))
                .toArray(Matcher[]::new);
    }

    private Matcher<VariableResource> importedVariable(VariableResource variable) {
        return Matchers.<VariableResource>both(hasProperty("key", is(variable.getKey())))
                .and(hasProperty("value", is(variable.getValue())))
                .and(hasProperty("role", is(VariableRole.IMPORT)));
    }
}
