package fr.treeptik.cloudunit.domain.test.server;

import static org.awaitility.Awaitility.*;
import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.Arrays;
import java.util.Collection;

import org.awaitility.Duration;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.rules.SpringClassRule;
import org.springframework.test.context.junit4.rules.SpringMethodRule;
import org.springframework.test.web.servlet.ResultActions;

import fr.treeptik.cloudunit.domain.core.ApplicationState;
import fr.treeptik.cloudunit.domain.resource.ApplicationResource;
import fr.treeptik.cloudunit.domain.resource.ServiceResource;
import fr.treeptik.cloudunit.domain.test.ApplicationTemplate;
import fr.treeptik.cloudunit.orchestrator.core.ContainerState;

@RunWith(Parameterized.class)
@SpringBootTest
@AutoConfigureMockMvc
public class ServerIT {
    @ClassRule
    public static final SpringClassRule SPRING_CLASS_RULE = new SpringClassRule();

    @Rule
    public final SpringMethodRule springMethodRule = new SpringMethodRule();
    
    @Parameters(name = "{index} {0}")
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][] {
            { "cloudunit/tomcat-8" },
        });
    }
    
    @Autowired
    private ApplicationTemplate applicationTemplate;
    
    private final String serverName;
    
    private ApplicationResource application;
    
    public ServerIT(String serverName) {
        this.serverName = serverName;
    }
    
    @Test
    public void testAddServer() throws Exception {
        application = applicationTemplate.createAndAssumeApplication();
        try {
            ResultActions result = applicationTemplate.addService(application, serverName);
            result.andExpect(status().isCreated());
            
            ServiceResource service = applicationTemplate.getService(result);
            
            assertThat(service.getImageName(), containsString(serverName));
        } finally {
            applicationTemplate.deleteApplication(application);
        }
    }
    
    @Test
    public void testStartStopServer() throws Exception {
        application = applicationTemplate.createAndAssumeApplication();
        try {
            ResultActions result = applicationTemplate.addService(application, serverName);
            result.andExpect(status().isCreated());
            
            ServiceResource service = applicationTemplate.getService(result);
            
            applicationTemplate.startApplication(application)
                .andExpect(status().isNoContent());
            
            await().atMost(Duration.TEN_SECONDS).until(() -> {
                application = applicationTemplate.refreshApplication(application);
                return application.getState() == ApplicationState.STARTED;
            });
            
            service = applicationTemplate.refreshService(service);
            
            assertEquals(ContainerState.STARTED, service.getState());
            
            applicationTemplate.stopApplication(application)
                .andExpect(status().isNoContent());
            
            await().atMost(Duration.TEN_SECONDS).until(() -> {
                application = applicationTemplate.refreshApplication(application);
                return application.getState() == ApplicationState.STOPPED;
            });
        } finally {
            applicationTemplate.deleteApplication(application);
        }
    }
}
