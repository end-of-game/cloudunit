package fr.treeptik.cloudunit.orchestrator.docker.controller;

import fr.treeptik.cloudunit.orchestrator.core.ContainerState;
import fr.treeptik.cloudunit.orchestrator.docker.test.ContainerTemplate;
import fr.treeptik.cloudunit.orchestrator.resource.ContainerResource;
import fr.treeptik.cloudunit.orchestrator.resource.VariableResource;
import org.awaitility.Awaitility;
import org.awaitility.Duration;
import org.hamcrest.Matchers;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.hateoas.Resources;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.rules.SpringClassRule;
import org.springframework.test.context.junit4.rules.SpringMethodRule;
import org.springframework.test.web.servlet.ResultActions;

import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.Matchers.isEmptyString;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Created by nicolas on 07/03/2017.
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("dev")
public class VariableControllerIT {

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
    public void testAddVariable() throws Exception {
        container = containerTemplate.createAndAssumeContainer(CONTAINER_NAME, IMAGE_NAME);
        try {
            ResultActions result = containerTemplate.addVariable(container, "KEY1", "VALUE1");
            ResultActions resultActions = result.andExpect(status().isCreated());
            await().atMost(Duration.TEN_SECONDS).until(() -> {
                container = containerTemplate.refreshContainer(container);
                return container.getState() == ContainerState.STOPPED;
            });
            Resources<VariableResource> variables = containerTemplate.getVariables(container);
            assertThat(variables.getContent(), hasItem(allOf(
                    hasProperty("key", is("KEY1")),
                    hasProperty("value", is("VALUE1")))));
        } finally {
            containerTemplate.deleteContainer(container);
        }
    }

}
