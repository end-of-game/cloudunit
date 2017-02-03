package fr.treeptik.cloudunit.utils;

import fr.treeptik.cloudunit.exception.ServiceException;
import fr.treeptik.cloudunit.initializer.CloudUnitApplicationContext;
import fr.treeptik.cloudunit.model.User;
import fr.treeptik.cloudunit.servers.AbstractApplicationControllerTestIT;
import fr.treeptik.cloudunit.service.UserService;
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

/**
 * Created by angular5 on 09/05/16.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(classes = {CloudUnitApplicationContext.class, MockServletContext.class})
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@ActiveProfiles("integration")
public class NameApplicationTestIT {

    private static String applicationName;
    private final Logger logger = LoggerFactory.getLogger(AbstractApplicationControllerTestIT.class);
    protected String release;
    @Autowired
    private WebApplicationContext context;
    private MockMvc mockMvc;
    @Inject
    private AuthenticationManager authenticationManager;
    @Autowired
    private Filter springSecurityFilterChain;
    @Inject
    private UserService userService;
    private MockHttpSession session;

    @BeforeClass
    public static void initEnv() {
        applicationName = "app" + new Random().nextInt(100000);
    }

    @Before
    public void setup() {
        logger.info("setup");

        this.release = "tomcat-8";

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
        SecurityContextHolder.clearContext();
        session.invalidate();
    }

    @Test
    public void deleteApplication() {
        try {
            ResultActions resultats =
                    mockMvc.perform(delete("/application/" + applicationName).session(session).contentType(MediaType.APPLICATION_JSON));
            resultats.andExpect(status().isOk());
        } catch (Exception e) {
            logger.error(e.getLocalizedMessage());
        }
    }

    @Test
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

    @Test
    public void test012_FailCreateSpaceNameApplication()
            throws Exception {
        logger.info("Create application with an only space name");
        final String jsonString = "{\"applicationName\":\"" + "     " + "\", \"serverName\":\"" + release + "\"}";
        ResultActions resultats =
                this.mockMvc.perform(post("/application")
                        .session(session)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonString));
        resultats.andExpect(status().is4xxClientError());
    }

    @Test
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

    @Test
    public void test014_FailCreateSpecialsSpecialsCharApplication()
            throws Exception {
        logger.info("Create application with an only specials characters name");
        final String jsonString = "{\"applicationName\":\"" + "©доあ" + "\", \"serverName\":\"" + release + "\"}";
        ResultActions resultats =
                this.mockMvc.perform(post("/application")
                        .session(session)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonString));
        resultats.andExpect(status().is5xxServerError());
    }

    @Test
    public void test015_CreateUpperCaseNameApplication()
            throws Exception {
        String upperCaseName = "AEIOU";
        String lowerCaseName = "aeiou";

        logger.info("**************************************");
        logger.info("Create application with upper case name " + upperCaseName);
        logger.info("**************************************");

        final String jsonString = "{\"applicationName\":\"" + upperCaseName + "\", \"serverName\":\"" + release + "\"}";
        ResultActions resultats =
                this.mockMvc.perform(post("/application").session(session).contentType(MediaType.APPLICATION_JSON).content(jsonString));
        resultats.andExpect(status().isOk());

        logger.info("**************************************");
        logger.info("Delete application : " + lowerCaseName);
        logger.info("**************************************");
        resultats =
                mockMvc.perform(delete("/application/" + lowerCaseName).session(session).contentType(MediaType.APPLICATION_JSON));
        resultats.andExpect(status().isOk());
    }
}
