package fr.treeptik.cloudunit.utils;

import fr.treeptik.cloudunit.exception.ServiceException;
import fr.treeptik.cloudunit.initializer.CloudUnitApplicationContext;
import fr.treeptik.cloudunit.model.User;
import fr.treeptik.cloudunit.service.UserService;
import fr.treeptik.cloudunit.snapshot.AbstractSnapshotControllerTestIT;
import org.junit.*;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
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
public class NameSnapshotTestIT {
    private final Logger logger = LoggerFactory.getLogger(AbstractSnapshotControllerTestIT.class);

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

    private static String applicationName;

    private final static String tagName = "mytag";

    protected String release;

    @Value("${cloudunit.instance.name}")
    private String cuInstanceName;

    @BeforeClass
    public static void initEnv() {
        applicationName = "app" + new Random().nextInt(10000);
    }

    @Before
    public void setup() throws Exception {
        logger.info("setup");
        this.mockMvc = MockMvcBuilders.webAppContextSetup(context).addFilters(springSecurityFilterChain).build();

        this.release = "tomcat-8";

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

        logger.info("**************************************");
        logger.info("Create Tomcat server");
        logger.info("**************************************");

        String jsonString = "{\"applicationName\":\"" + applicationName + "\", \"serverName\":\"" + release + "\"}";
        ResultActions resultats =
                mockMvc.perform(post("/application").session(session).contentType(MediaType.APPLICATION_JSON).content(jsonString));
        resultats.andExpect(status().isOk());

    }

    @After
    public void teardown() throws Exception {
        logger.info("teardown");

        logger.info("**************************************");
        logger.info("Delete application : " + applicationName);
        logger.info("**************************************");
        ResultActions resultats =
                mockMvc.perform(delete("/application/" + applicationName).session(session).contentType(MediaType.APPLICATION_JSON));
        resultats.andExpect(status().isOk());

        SecurityContextHolder.clearContext();
        session.invalidate();
    }

    @Test()
    public void test010_CreateSimpleApplicationSnapshot()
            throws Exception {
        logger.info("**************************************");
        logger.info("Create a snapshot");
        logger.info("**************************************");

        String jsonString =
                "{\"applicationName\":\"" + applicationName + "\", \"tag\":\"" + tagName
                        + "\", \"description\":\"This is a test snapshot\"}";
        logger.info(jsonString);
        ResultActions resultats =
                mockMvc.perform(post("/snapshot").session(session).contentType(MediaType.APPLICATION_JSON).content(jsonString)).andDo(print());
        resultats.andExpect(status().isOk());

        logger.info("**************************************");
        logger.info("List the snapshot");
        logger.info("**************************************");

        resultats = mockMvc.perform(get("/snapshot/list").session(session)).andDo(print());
        resultats.andExpect(status().isOk()).andExpect(jsonPath("$[0].tag").value(tagName.toLowerCase()));

        logger.info("**************************************");
        logger.info("Delete the snapshot");
        logger.info("**************************************");

        resultats = mockMvc.perform(delete("/snapshot/" + tagName.toLowerCase()).session(session)).andDo(print());
        resultats.andExpect(status().isOk());
    }

    @Test
    public void test011_FailCreateEmptyNameSnapshot()
            throws Exception {
        logger.info("**************************************");
        logger.info("Create a snapshot");
        logger.info("**************************************");

        String jsonString =
                "{\"applicationName\":\"" + applicationName + "\", \"tag\":\"" + ""
                        + "\", \"description\":\"This is a test snapshot\"}";
        logger.info(jsonString);
        ResultActions resultats =
                mockMvc.perform(post("/snapshot").session(session).contentType(MediaType.APPLICATION_JSON).content(jsonString)).andDo(print());
        resultats.andExpect(status().is4xxClientError());
    }

    @Test
    public void test012_FailCreateSpaceNameSnapshot()
            throws Exception {
        logger.info("**************************************");
        logger.info("Create a snapshot");
        logger.info("**************************************");

        String jsonString =
                "{\"applicationName\":\"" + applicationName + "\", \"tag\":\"" + "         "
                        + "\", \"description\":\"This is a test snapshot\"}";
        logger.info(jsonString);
        ResultActions resultats =
                mockMvc.perform(post("/snapshot").session(session).contentType(MediaType.APPLICATION_JSON).content(jsonString)).andDo(print());
        resultats.andExpect(status().is4xxClientError());
    }

    @Test
    public void test013_CreateAccentNameSnapshot()
            throws Exception {

        String accentName = "àéèîôù";
        String deAccentName = "aeeiou";

        logger.info("**************************************");
        logger.info("Create a snapshot with accent name");
        logger.info("**************************************");

        String jsonString =
                "{\"applicationName\":\"" + applicationName + "\", \"tag\":\"" + accentName
                        + "\", \"description\":\"This is a test snapshot\"}";
        logger.info(jsonString);
        ResultActions resultats =
                mockMvc.perform(post("/snapshot").session(session).contentType(MediaType.APPLICATION_JSON).content(jsonString)).andDo(print());
        resultats.andExpect(status().isOk());

        logger.info("**************************************");
        logger.info("List the snapshot");
        logger.info("**************************************");

        resultats = mockMvc.perform(get("/snapshot/list").session(session)).andDo(print());
        resultats.andExpect(status().isOk()).andExpect(jsonPath("$[0].tag").value(deAccentName));

        logger.info("**************************************");
        logger.info("Delete the snapshot");
        logger.info("**************************************");

        resultats = mockMvc.perform(delete("/snapshot/" + deAccentName).session(session)).andDo(print());
        resultats.andExpect(status().isOk());
    }

    @Test
    public void test014_FailCreateSpecialsCharNameSnapshot()
            throws Exception {
        logger.info("**************************************");
        logger.info("Create a snapshot");
        logger.info("**************************************");

        String jsonString =
                "{\"applicationName\":\"" + applicationName + "\", \"tag\":\"" + "©доあ"
                        + "\", \"description\":\"This is a test snapshot\"}";
        logger.info(jsonString);
        ResultActions resultats =
                mockMvc.perform(post("/snapshot").session(session).contentType(MediaType.APPLICATION_JSON).content(jsonString)).andDo(print());
        resultats.andExpect(status().is4xxClientError());
    }

    @Test
    public void test015_CreateUpperCaseNameSnapshot()
            throws Exception {

        String upperCaseName = "AEIOU";
        String lowerCaseName = "aeiou";

        logger.info("**************************************");
        logger.info("Create a snapshot with upper case name");
        logger.info("**************************************");

        String jsonString =
                "{\"applicationName\":\"" + applicationName + "\", \"tag\":\"" + upperCaseName
                        + "\", \"description\":\"This is a test snapshot\"}";
        logger.info(jsonString);
        ResultActions resultats =
                mockMvc.perform(post("/snapshot").session(session).contentType(MediaType.APPLICATION_JSON).content(jsonString)).andDo(print());
        resultats.andExpect(status().isOk());

        logger.info("**************************************");
        logger.info("List the snapshot");
        logger.info("**************************************");

        resultats = mockMvc.perform(get("/snapshot/list").session(session)).andDo(print());
        resultats.andExpect(status().isOk()).andExpect(jsonPath("$[0].tag").value(lowerCaseName));

        logger.info("**************************************");
        logger.info("Delete the snapshot");
        logger.info("**************************************");

        resultats = mockMvc.perform(delete("/snapshot/" + lowerCaseName).session(session)).andDo(print());
        resultats.andExpect(status().isOk());
    }
}
