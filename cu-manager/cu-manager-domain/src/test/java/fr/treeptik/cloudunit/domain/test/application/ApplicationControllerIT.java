package fr.treeptik.cloudunit.domain.test.application;

import static org.junit.Assert.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.rules.SpringClassRule;
import org.springframework.test.context.junit4.rules.SpringMethodRule;
import org.springframework.test.web.servlet.ResultActions;

import fr.treeptik.cloudunit.domain.resource.ApplicationResource;
import fr.treeptik.cloudunit.domain.test.ApplicationTemplate;

@SpringBootTest
@AutoConfigureMockMvc
public class ApplicationControllerIT {
    @ClassRule
    public static final SpringClassRule SPRING_CLASS_RULE = new SpringClassRule();

    @Rule
    public final SpringMethodRule springMethodRule = new SpringMethodRule();
    
    @Autowired
    private ApplicationTemplate applicationTemplate;

    @Autowired
    @Qualifier("testApplicationName")
    private String applicationName;
    
    @Test
    public void test_createApplication() throws Exception {
        ResultActions result = applicationTemplate.createApplication(applicationName);
        result.andExpect(status().isCreated());
        
        ApplicationResource application = applicationTemplate.getApplication(result);
        
        assertEquals(application.getName(), applicationName);
        
        applicationTemplate.deleteApplication(application);
    }
}
