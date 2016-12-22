/*
 * LICENCE : CloudUnit is available under the Affero Gnu Public License GPL V3 : https://www.gnu.org/licenses/agpl-3.0.html
 *     but CloudUnit is licensed too under a standard commercial license.
 *     Please contact our sales team if you would like to discuss the specifics of our Enterprise license.
 *     If you are not sure whether the GPL is right for you,
 *     you can always test our software under the GPL and inspect the source code before you contact us
 *     about purchasing a commercial license.
 *
 *     LEGAL TERMS : "CloudUnit" is a registered trademark of Treeptik and can't be used to endorse
 *     or promote products derived from this project without prior written permission from Treeptik.
 *     Products or services derived from this software may not be called "CloudUnit"
 *     nor may "Treeptik" or similar confusing terms appear in their names without prior written permission.
 *     For any questions, contact us : contact@treeptik.fr
 */

package fr.treeptik.cloudunit.modules;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import fr.treeptik.cloudunit.dto.EnvUnit;
import fr.treeptik.cloudunit.dto.ModulePortResource;
import fr.treeptik.cloudunit.exception.ServiceException;
import fr.treeptik.cloudunit.initializer.CloudUnitApplicationContext;
import fr.treeptik.cloudunit.model.User;
import fr.treeptik.cloudunit.service.UserService;
import fr.treeptik.cloudunit.utils.CheckBrokerConnectionUtils;
import fr.treeptik.cloudunit.utils.SpyMatcherDecorator;
import junit.framework.TestCase;
import org.apache.commons.io.FilenameUtils;
import org.junit.*;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.mock.web.MockServletContext;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import javax.inject.Inject;
import javax.servlet.Filter;
import java.io.FileInputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import static org.awaitility.Awaitility.await;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


/**
 * Tests for Module lifecycle
 */
@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(classes = {
    CloudUnitApplicationContext.class,
    MockServletContext.class
})
@ActiveProfiles("integration")
public class ModulesControllerTestIT extends TestCase {

    protected final Logger logger = LoggerFactory
        .getLogger(ModulesControllerTestIT.class);

    @Autowired
    protected WebApplicationContext context;

    protected MockMvc mockMvc;

    @Inject
    private ObjectMapper objectMapper;

    @Inject
    private AuthenticationManager authenticationManager;

    @Autowired
    private Filter springSecurityFilterChain;

    @Inject
    private UserService userService;

    protected MockHttpSession session;

    protected static String applicationName;

    @Inject
    private CheckBrokerConnectionUtils checkBrokerConnectionUtils;

    protected String server = "tomcat-8";
    protected String module;
    protected String numberPort;
    protected String managerPrefix;
    protected String managerSuffix;
    protected String managerPageContent;
    protected String testScriptPath;

    @BeforeClass
    public static void initEnv() {
        applicationName = "app" + new Random().nextInt(100000);
    }

    @Before
    public void setup() throws Exception {
        logger.info("setup");

        this.mockMvc = MockMvcBuilders.webAppContextSetup(context)
            .addFilters(springSecurityFilterChain)
            .build();

        User user = null;
        try {
            user = userService.findByLogin("johndoe");
        } catch (ServiceException e) {
            logger.error(e.getLocalizedMessage());
        }

        assert user != null;
        Authentication authentication = new UsernamePasswordAuthenticationToken(user.getLogin(), user.getPassword());
        Authentication result = authenticationManager.authenticate(authentication);
        SecurityContext securityContext = SecurityContextHolder.getContext();
        securityContext.setAuthentication(result);
        session = new MockHttpSession();
        String secContextAttr = HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY;
        session.setAttribute(secContextAttr,
            securityContext);

    }

    private void createApplication() throws Exception {
        // create an application server
        String jsonString = "{\"applicationName\":\"" + applicationName + "\", \"serverName\":\"" + server + "\"}";
        mockMvc.perform(post("/application")
        		.session(session)
				.contentType(MediaType.APPLICATION_JSON)
				.content(jsonString))
        	.andExpect(status().isOk());
    }

    @After
    public void teardown() throws Exception {
        logger.info("teardown");
        SecurityContextHolder.clearContext();
        session.invalidate();
    }

    @Test
    public void test_addTwoModulesThenRemoveApp() throws Exception {
        createApplication();
        requestAddModule("mysql-5-5");
        requestAddModule("postgresql-9-5");
        deleteApplication();
    }

    private ResultActions deleteApplication() throws Exception {
        logger.info("Delete application : " + applicationName);
        return mockMvc.perform(delete("/application/" + applicationName)
                .session(session)
                .contentType(MediaType.APPLICATION_JSON));
    }

    private ResultActions requestPublishPort(Integer id, String number) throws Exception {
        ModulePortResource request = ModulePortResource.of()
                .withPublishPort(true)
                .build();
        String jsonString = objectMapper.writeValueAsString(request);
        return mockMvc.perform(put("/module/" + id + "/ports/" + number)
                .session(session)
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonString))
                .andDo(print());
    }

    private ResultActions requestAddModule(String module) throws Exception {
        String jsonString = "{\"applicationName\":\"" + applicationName + "\", \"imageName\":\"" + module + "\"}";
        return mockMvc.perform(post("/module")
                .session(session)
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonString))
                .andDo(print());
    }

    private ResultActions requestApplication() throws Exception {
        return mockMvc.perform(get("/application/" + applicationName)
                .session(session)
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print());
    }

}