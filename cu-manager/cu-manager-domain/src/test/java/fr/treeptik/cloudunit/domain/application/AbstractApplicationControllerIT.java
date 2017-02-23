package fr.treeptik.cloudunit.domain.application;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.Assume;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import com.fasterxml.jackson.databind.ObjectMapper;

import fr.treeptik.cloudunit.domain.resource.ApplicationResource;
import fr.treeptik.cloudunit.domain.test.ApplicationTemplate;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public abstract class AbstractApplicationControllerIT {
    @Autowired
    private ApplicationTemplate applicationTemplate;
    
    @Autowired
    @Qualifier("testApplicationName")
    private String applicationName;
    
    protected final String serverType;
    
    protected AbstractApplicationControllerIT(String serverType) {
        this.serverType = serverType;
    }
    
    @Test
    public void test_createApplication() throws Exception {
        Assume.assumeThat(applicationName, not(isEmptyOrNullString()));
        
        ResultActions result = applicationTemplate.createApplication(applicationName);
        result.andExpect(status().isCreated());
        
        ApplicationResource application = applicationTemplate.getApplication(result);
        
        assertEquals(application.getName(), applicationName);
        
        applicationTemplate.deleteApplication(application);
    }
}
