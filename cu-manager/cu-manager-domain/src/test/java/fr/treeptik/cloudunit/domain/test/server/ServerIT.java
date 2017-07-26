package fr.treeptik.cloudunit.domain.test.server;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.Arrays;
import java.util.Collection;

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
            { "tomcat:8" },
        });
    }
    
    @Autowired
    private ApplicationTemplate applicationTemplate;
    
    private final String serverName;
    
    public ServerIT(String serverName) {
        this.serverName = serverName;
    }
    
    @Test
    public void testAddServer() throws Exception {
        ApplicationResource application = applicationTemplate.createAndAssumeApplication();
        try {
            ResultActions result = applicationTemplate.addService(application, serverName);
            result.andExpect(status().isCreated());
            
            ServiceResource service = applicationTemplate.getService(result);
            
            assertThat(service.getImageName(), containsString(serverName));
            
            // TODO : Services don't know the container at the moment
            // assertTrue(service.hasLink("cu:container"));
        } finally {
            applicationTemplate.waitWhilePending(application);
            applicationTemplate.deleteApplication(application);
        }
    }
    
    @Test
    public void testAddRemoveServer() throws Exception {
        ApplicationResource application = applicationTemplate.createAndAssumeApplication();
        try {
            ResultActions result = applicationTemplate.addService(application, serverName);
            
            ServiceResource service = applicationTemplate.getService(result);
            
            applicationTemplate.waitWhilePending(application);
            
            result = applicationTemplate.removeService(service);
            result.andExpect(status().isNoContent());
        } finally {
            applicationTemplate.waitWhilePending(application);
            applicationTemplate.deleteApplication(application);
        }
    }
    
    @Test
    public void testStartStopServer() throws Exception {
        ApplicationResource application = applicationTemplate.createAndAssumeApplication();
        try {
            ResultActions result = applicationTemplate.addService(application, serverName);
            result.andExpect(status().isCreated());
            
            ServiceResource service = applicationTemplate.getService(result);
            
            applicationTemplate.waitWhilePending(application);
            
            applicationTemplate.startApplication(application)
                .andExpect(status().isNoContent());
            
            applicationTemplate.waitWhilePending(application);
            
            service = applicationTemplate.refreshService(service);
            
            assertEquals(ContainerState.STARTED, service.getState());
            
            application = applicationTemplate.refreshApplication(application);
            applicationTemplate.stopApplication(application)
                .andExpect(status().isNoContent());
        } finally {
            applicationTemplate.waitWhilePending(application);
            applicationTemplate.deleteApplication(application);
        }
    }
}
