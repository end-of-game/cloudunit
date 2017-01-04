package fr.treeptik.cloudunit.servers;

import fr.treeptik.cloudunit.exception.ServiceException;
import fr.treeptik.cloudunit.initializer.CloudUnitApplicationContext;
import fr.treeptik.cloudunit.model.User;
import fr.treeptik.cloudunit.service.DockerService;
import fr.treeptik.cloudunit.service.UserService;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.junit.*;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
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

import java.util.Random;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(classes = {CloudUnitApplicationContext.class, MockServletContext.class})
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@ActiveProfiles("integration")
public class Apache2ControllerTestIT {

    private final Logger logger = LoggerFactory.getLogger(Apache2ControllerTestIT.class);

    protected String release = "apache-2-2";

    @Inject
    private AuthenticationManager authenticationManager;

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private Filter springSecurityFilterChain;

    @Inject
    private UserService userService;

    @Inject
    private DockerService dockerService;

    private MockHttpSession session;

    private static String applicationName;

    private MockMvc mockMvc;

    @BeforeClass
    public static void initEnv() {
        applicationName = "app" + new Random().nextInt(100000);
    }

    @Before
    public void setup() {
        logger.info("setup");

        this.mockMvc = MockMvcBuilders.webAppContextSetup(context).addFilters(springSecurityFilterChain).build();

        User user = null;
        try {
            user = userService.findByLogin("johndoe");
        } catch (ServiceException e) {
            logger.error(e.getLocalizedMessage());
        }

        Authentication authentication = null;
        if (user != null) {
            authentication = new UsernamePasswordAuthenticationToken(user.getLogin(), user.getPassword());
        }
        Authentication result = authenticationManager.authenticate(authentication);
        SecurityContext securityContext = SecurityContextHolder.getContext();
        securityContext.setAuthentication(result);
        session = new MockHttpSession();
        session.setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY, securityContext);
    }

    @After
    public void teardown() throws Exception {
        logger.info("teardown");

        ResultActions resultats =
                mockMvc.perform(delete("/application/" + applicationName).session(session).contentType(MediaType.APPLICATION_JSON));
        resultats.andExpect(status().isOk());

        SecurityContextHolder.clearContext();
        session.invalidate();
    }

    @Before
    public void createApplication() {
        try {
            final String jsonString =
                    "{\"applicationName\":\"" + applicationName + "\", \"serverName\":\"" + release + "\"}";
            ResultActions resultats =
                    mockMvc.perform(post("/application").session(session).contentType(MediaType.APPLICATION_JSON).content(jsonString));
            resultats.andExpect(status().isOk());

            resultats =
                    mockMvc.perform(get("/application/" + applicationName).session(session).contentType(MediaType.APPLICATION_JSON));
            resultats.andExpect(jsonPath("name").value(applicationName.toLowerCase()));
        } catch (Exception e) {
            logger.error(e.getLocalizedMessage());
        }
    }

    /**
     * We cannot create an application with an empty name.
     *
     * @throws Exception
     */
    @Test
    public void test011_FailCreateEmptyNameApplication()
            throws Exception {
        logger.info("Create application with an empty name");
        final String jsonString = "{\"applicationName\":\"" + "" + "\", \"serverName\":\"" + release + "\"}";
        ResultActions resultats =
                this.mockMvc.perform(post("/application")
                        .session(session)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonString));
        resultats.andExpect(status().is4xxClientError());
    }

    /**
     * We cannot create an application with an wrong syntax name.
     *
     * @throws Exception
     */
    @Test(timeout = 30000)
    public void test012_FailCreateWrongNameApplication()
            throws Exception {
        logger.info("Create application with a wrong syntax name");
        final String jsonString = "{\"applicationName\":\"" + "         " + "\", \"serverName\":\"" + release + "\"}";
        ResultActions resultats =
                this.mockMvc.perform(post("/application").session(session).contentType(MediaType.APPLICATION_JSON).content(jsonString));
        resultats.andExpect(status().is4xxClientError());
    }


    @Test(timeout = 30000)
    public void test013_CreateAccentNameApplication()
            throws Exception {

        String accentName = "àéèîôù";
        String deAccentName = "aeeiou";

        logger.info("**************************************");
        logger.info("Create application with accent name " + accentName);
        logger.info("**************************************");

        final String jsonString = "{\"applicationName\":\"" + accentName + "\", \"serverName\":\"" + release + "\"}";
        ResultActions resultats =
                this.mockMvc.perform(post("/application").session(session).contentType(MediaType.APPLICATION_JSON).content(jsonString));
        resultats.andExpect(status().isOk());

        logger.info("**************************************");
        logger.info("Delete application : " + deAccentName);
        logger.info("**************************************");
        resultats =
                mockMvc.perform(delete("/application/" + deAccentName).session(session).contentType(MediaType.APPLICATION_JSON));
        resultats.andExpect(status().isOk());
    }

    @Test()
    public void test031_StartStopStartApplicationTest()
            throws Exception {
        logger.info("Start the application : " + applicationName);
        String jsonString = "{\"applicationName\":\"" + applicationName + "\"}";
        ResultActions resultats = mockMvc.perform(post("/application/start").session(session).contentType(MediaType.APPLICATION_JSON).content(jsonString));
        resultats.andExpect(status().isOk());

        logger.info("Stop the application : " + applicationName);
        resultats = mockMvc.perform(post("/application/stop").session(session).contentType(MediaType.APPLICATION_JSON).content(jsonString));
        resultats.andExpect(status().isOk());

        logger.info("Start the application : " + applicationName);
        resultats = mockMvc.perform(post("/application/start").session(session).contentType(MediaType.APPLICATION_JSON).content(jsonString));
        resultats.andExpect(status().isOk());
    }

}