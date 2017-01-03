package fr.treeptik.cloudunit.deployments;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.Optional;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.mock.web.MockServletContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import fr.treeptik.cloudunit.initializer.CloudUnitApplicationContext;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(classes = { CloudUnitApplicationContext.class, MockServletContext.class })
@ActiveProfiles("integration")
public abstract class AbstractJBossDeploymentControllerTestIT extends AbstractDeploymentControllerIT {
    @Test
    public void test010_DeployEARApplicationTest()
            throws Exception
    {
        logger.info("Deploy a Wicket EAR application");
        
        createApplication();
        try {
            String earUrl = "https://github.com/Treeptik/cloudunit/releases/download/1.0/wildfly-wicket-ear-ear.ear";
            deployArchive("wildfly-wicket-ear-ear.ear", earUrl)
                .andExpect(status().is2xxSuccessful());
            String urlToCall = String.format("http://%s-johndoe%s/wildfly-wicket-ear-war",
                    applicationName.toLowerCase(),
                    domainSuffix);
            Optional<String> content = waitForContent(urlToCall);
            
            assertTrue(content.isPresent());
            assertThat(content.get(), containsString("Wicket"));
        } finally {
            deleteApplication();
        }
    }

}
