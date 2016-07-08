package fr.treeptik.cloudunit.servers;

import fr.treeptik.cloudunit.exception.ServiceException;
import fr.treeptik.cloudunit.initializer.CloudUnitApplicationContext;
import fr.treeptik.cloudunit.model.User;
import fr.treeptik.cloudunit.service.DockerService;
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
        SecurityContextHolder.clearContext();
        session.invalidate();
    }

    private void deleteApplication() {
        try {
            ResultActions resultats =
                    mockMvc.perform(delete("/application/" + applicationName).session(session).contentType(MediaType.APPLICATION_JSON));
            resultats.andExpect(status().isOk());
        } catch (Exception e) {
            logger.error(e.getLocalizedMessage());
        }
    }

    private void createApplication() {
        try {
            final String jsonString =
                    "{\"applicationName\":\"" + applicationName + "\", \"serverName\":\"" + release + "\"}";
            ResultActions resultats =
                    mockMvc.perform(post("/application").session(session).contentType(MediaType.APPLICATION_JSON).content(jsonString));
            resultats.andExpect(status().isOk());

            resultats =
                    mockMvc.perform(get("/application/" + applicationName).session(session).contentType(MediaType.APPLICATION_JSON));
            resultats.andExpect(jsonPath("name").value(applicationName.toLowerCase()));
        } catch(Exception e) {
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
        createApplication();

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

        deleteApplication();
    }

    @Test()
    public void test040_ChangeJvmMemorySizeApplicationTest()
            throws Exception {
        createApplication();
        logger.info("Change JVM Memory !");
        final String jsonString =
                "{\"applicationName\":\"" + applicationName
                        + "\",\"jvmMemory\":\"512\",\"jvmOptions\":\"\",\"jvmRelease\":\"jdk1.8.0_25\",\"location\":\"webui\"}";
        ResultActions resultats =
                this.mockMvc.perform(put("/server/configuration/jvm").session(session).contentType(MediaType.APPLICATION_JSON).content(jsonString));
        resultats.andExpect(status().isOk());
        deleteApplication();
    }

    @Test(timeout = 30000)
    public void test041_ChangeInvalidJvmMemorySizeApplicationTest()
            throws Exception {
        createApplication();
        logger.info("Change JVM Memory size with an incorrect value : number not allowed");
        String jsonString =
                "{\"applicationName\":\"" + applicationName
                        + "\",\"jvmMemory\":\"666\",\"jvmOptions\":\"\",\"jvmRelease\":\"\"}";
        ResultActions resultats =
                mockMvc.perform(put("/server/configuration/jvm").session(session).contentType(MediaType.APPLICATION_JSON).content(jsonString));
        resultats.andExpect(status().is4xxClientError());

        logger.info("Change JVM Memory size with an empty value");
        jsonString =
                "{\"applicationName\":\"" + applicationName
                        + "\",\"jvmMemory\":\"\",\"jvmOptions\":\"\",\"jvmRelease\":\"jdk1.8.0_25\"}";
        resultats =
                mockMvc.perform(put("/server/configuration/jvm").session(session).contentType(MediaType.APPLICATION_JSON).content(jsonString));
        resultats.andExpect(status().is4xxClientError());
        deleteApplication();
    }

    @Test(timeout = 60000)
    public void test050_ChangeJvmOptionsApplicationTest()
            throws Exception {
        createApplication();
        logger.info("Change JVM Options !");
        final String jsonString =
                "{\"applicationName\":\"" + applicationName
                        + "\",\"jvmMemory\":\"512\",\"jvmOptions\":\"-Dkey1=value1\",\"jvmRelease\":\"jdk1.8.0_25\"}";
        ResultActions resultats =
                mockMvc.perform(put("/server/configuration/jvm").session(session).contentType(MediaType.APPLICATION_JSON).content(jsonString));
        resultats.andExpect(status().isOk());

        resultats =
                mockMvc.perform(get("/application/" + applicationName).session(session).contentType(MediaType.APPLICATION_JSON));
        resultats.andExpect(jsonPath("$.servers[0].jvmMemory").value(512)).andExpect(jsonPath(
                "$.servers[0].jvmRelease").value("jdk1.7.0_55"));
        deleteApplication();
    }

    @Test(timeout = 30000)
    public void test051_ChangeFailWithXmsJvmOptionsApplicationTest()
            throws Exception {
        createApplication();
        logger.info("Change JVM With Xms : not allowed");
        final String jsonString =
                "{\"applicationName\":\"" + applicationName
                        + "\",\"jvmMemory\":\"512\",\"jvmOptions\":\"-Xms=512m\",\"jvmRelease\":\"jdk1.8.0_25\"}";
        ResultActions resultats =
                mockMvc.perform(put("/server/configuration/jvm").session(session).contentType(MediaType.APPLICATION_JSON).content(jsonString));
        resultats.andExpect(status().is4xxClientError());
        deleteApplication();
    }

    @Test()
    public void test050_OpenAPort()
            throws Exception {
        createApplication();
        logger.info("Open custom ports !");
        String jsonString =
                "{\"applicationName\":\"" + applicationName
                        + "\",\"portToOpen\":\"6115\",\"alias\":\"access6115\"}";
        ResultActions resultats =
                this.mockMvc.perform(post("/server/ports/open").session(session).contentType(MediaType.APPLICATION_JSON).content(jsonString));
        resultats.andExpect(status().isOk());

        logger.info("Close custom ports !");
        jsonString =
                "{\"applicationName\":\"" + applicationName
                        + "\",\"portToOpen\":\"6115\",\"alias\":\"access6115\"}";
        resultats =
                this.mockMvc.perform(post("/server/ports/close").session(session).contentType(MediaType.APPLICATION_JSON).content(jsonString));
        resultats.andExpect(status().isOk());
        deleteApplication();
    }

    @Test
    public void test060_DeployPageOnServer() throws Exception {
        createApplication();
        String filePath = "/var/www";
        String containerId = dockerService.getContainerId("int-johndoe-"+applicationName+"-" +release).substring(0, 12);

        String jsonString =
                "{\"fileContent\":\"<? phpinfo(); ?>\", \"filePath\":\""+filePath+"\", \"fileName\":\"index.php\"}";
        String url = "/file/content/container/" + containerId + "/application/"+ applicationName;
        logger.debug(url);
        ResultActions resultats = this.mockMvc
                .perform(
                        put(url)
                                .content(jsonString).contentType(MediaType.APPLICATION_JSON)
                                .session(session));
        String contentAsString = resultats.andReturn().getResponse().getContentAsString();
        logger.debug(contentAsString);
        resultats.andExpect(status().isOk());

        String urlGet = "/file/content/container/"+containerId+"/application/"+ applicationName + "/path" + filePath + "/index.php";
        logger.debug(urlGet);
        resultats = this.mockMvc
                .perform(
                    get(urlGet)
                        .session(session));

        String contentAsString2 = resultats.andReturn().getResponse().getContentAsString();
        int response = resultats.andReturn().getResponse().getStatus();
        logger.debug(contentAsString2);
        System.out.println("contentAsString2 = " + contentAsString2);
        resultats.andExpect(status().isOk());
        logger.debug(String.valueOf(response));
        System.out.println("response = " + response);
        Assert.assertEquals(contentAsString, contentAsString2);
        //deleteApplication();
    }
}