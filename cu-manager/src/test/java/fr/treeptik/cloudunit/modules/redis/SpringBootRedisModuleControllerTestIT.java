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

package fr.treeptik.cloudunit.modules.redis;

import com.jayway.jsonpath.JsonPath;
import fr.treeptik.cloudunit.exception.ServiceException;
import fr.treeptik.cloudunit.initializer.CloudUnitApplicationContext;
import fr.treeptik.cloudunit.model.User;
import fr.treeptik.cloudunit.service.UserService;
import fr.treeptik.cloudunit.utils.TestUtils;
import junit.framework.TestCase;
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
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.JsonPathResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import javax.inject.Inject;
import javax.servlet.Filter;
import java.util.Random;

import static fr.treeptik.cloudunit.utils.TestUtils.downloadAndPrepareFileToDeploy;
import static fr.treeptik.cloudunit.utils.TestUtils.getUrlContentPage;
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
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@ActiveProfiles("integration")
public class SpringBootRedisModuleControllerTestIT extends TestCase {

    private final Logger logger = LoggerFactory
        .getLogger(SpringBootRedisModuleControllerTestIT.class);

    @Autowired
    private WebApplicationContext context;

    private MockMvc mockMvc;

    @Inject
    private AuthenticationManager authenticationManager;

    @Autowired
    private Filter springSecurityFilterChain;

    @Inject
    private UserService userService;

    @Value("${cloudunit.instance.name}")
    private String cuInstanceName;

    private MockHttpSession session;

    private static String applicationName;

    protected String server = "fatjar";
    protected String module = "redis-3-0";
    protected String managerPrefix = "redmin";
    protected String managerSuffix = "";
    protected String managerPageContent = "Redis";

    @BeforeClass
    public static void initEnv() {
        applicationName = "app" + new Random().nextInt(100000);
    }

    @Before
    public void setup() {
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

    @After
    public void teardown() {
        logger.info("teardown");
        SecurityContextHolder.clearContext();
        session.invalidate();
    }

    @Test
    public void test00_FailToAddModuleBecauseBadAppName() throws Exception {
        logger.info("Cannot add a module because application name missing");
        String jsonString = "{\"applicationName\":\"" + "" + "\", \"imageName\":\"" + module + "\"}";
        ResultActions resultats = mockMvc.perform(post("/module")
            .session(session)
            .contentType(MediaType.APPLICATION_JSON)
            .content(jsonString));
        resultats.andExpect(status().is5xxServerError());
    }

    @Test
    public void test01_FailToAddModuleBecauseAppNonExist() throws Exception {
        logger.info("Cannot add a module because application name missing");
        String jsonString = "{\"applicationName\":\"" + "UFO" + "\", \"imageName\":\"" + module + "\"}";
        ResultActions resultats = mockMvc.perform(post("/module")
                .session(session)
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonString));
        resultats.andExpect(status().is5xxServerError());
    }

    @Test
    public void test02_FailToAddModuleBecauseModuleEmpty() throws Exception {
        logger.info("Cannot add a module because module name empty");
        String jsonString = "{\"applicationName\":\"" + "REALAPP" + "\", \"imageName\":\"" + "" + "\"}";
        ResultActions resultats = mockMvc.perform(post("/module")
                .session(session)
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonString));
        resultats.andExpect(status().is5xxServerError());
    }

    @Test
    public void test03_FailToAddModuleBecauseModuleNonExisting() throws Exception {
        logger.info("Cannot add a module because module name empty");
        String jsonString = "{\"applicationName\":\"" + "REALAPP" + "\", \"imageName\":\"" + "UFO" + "\"}";
        ResultActions resultats = mockMvc.perform(post("/module")
                .session(session)
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonString));
        resultats.andExpect(status().is5xxServerError());
    }

    @Test
    public void test10_CreateServerThenAddModuleThenTestManagerThenRemoveModule() throws Exception {
        logger.info("Create an application, add a " + module + " module and delete it");

        // create an application server
        String jsonString = "{\"applicationName\":\"" + applicationName + "\", \"serverName\":\"" + server + "\"}";
        ResultActions resultats = mockMvc.perform(post("/application").session(session).contentType(MediaType.APPLICATION_JSON).content(jsonString));
        resultats.andExpect(status().isOk());

        // verify if app exists
        resultats = mockMvc.perform(get("/application/" + applicationName).session(session).contentType(MediaType.APPLICATION_JSON));
        resultats.andExpect(jsonPath("name").value(applicationName.toLowerCase()));

        // add a module
        jsonString = "{\"applicationName\":\"" + applicationName + "\", \"imageName\":\"" + module + "\"}";
        resultats = mockMvc.perform(post("/module")
                .session(session)
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonString));
        resultats.andExpect(status().isOk());

        // Expected values
        String genericModule = cuInstanceName.toLowerCase() + "-johndoe-" + applicationName.toLowerCase() + "-" + module + "-1";
        String gitModule = cuInstanceName.toLowerCase() + "-johndoe-" + applicationName.toLowerCase() + "-git-1";
        String managerExpected = "http://" + managerPrefix + "1-" + applicationName.toLowerCase() + "-johndoe-admin.cloudunit.dev/" + managerSuffix;

        // get the detail of the applications to verify modules addition
        resultats = mockMvc.perform(get("/application/" + applicationName)
                .session(session).contentType(MediaType.APPLICATION_JSON)).andDo(print());
        resultats
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.modules[0].name").value(gitModule))
                .andExpect(jsonPath("$.modules[1].name").value(genericModule))
                .andExpect(jsonPath("$.modules[1].managerLocation").value(managerExpected));

        String fullContent = resultats.andReturn().getResponse().getContentAsString();

        String userNameRedis = JsonPath.read(fullContent, "$.modules[1].moduleInfos.username");
        String passwordRedis = JsonPath.read(fullContent, "$.modules[1].moduleInfos.password");
        String managerUrlAuth = "http://" + userNameRedis + ":" + passwordRedis + "@"
                + managerPrefix + "1-"
                + applicationName.toLowerCase()
                + "-johndoe-admin.cloudunit.dev/"
                + managerSuffix;

        String contentPage = getUrlContentPage(managerUrlAuth);
        int counter = 0;
        while (contentPage.contains("DOCTYPE")==false && counter++ < 10) {
            contentPage = getUrlContentPage(managerUrlAuth);
            Thread.sleep(1000);
        }

        Assert.assertTrue(contentPage.contains(managerPageContent));

        // remove a module
        resultats = mockMvc.perform(delete("/module/" + applicationName + "/" + genericModule)
                .session(session)
                .contentType(MediaType.APPLICATION_JSON));
        resultats.andExpect(status().isOk());

        // get the detail of the applications to verify modules addition
        resultats = mockMvc.perform(get("/application/" + applicationName)
                .session(session).contentType(MediaType.APPLICATION_JSON)).andDo(print());
        resultats
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.modules[0].name").value(gitModule))
                .andExpect(jsonPath("$.modules[0].status").value("START"))
                .andExpect(jsonPath("$.modules[1]").doesNotExist());

        logger.info("Delete application : " + applicationName);
        resultats = mockMvc.perform(delete("/application/" + applicationName).session(session).contentType(MediaType.APPLICATION_JSON));
        resultats.andExpect(status().isOk());
    }

    @Test
    public void test20_CreateServerThenAddModuleThenDeployApplicationThenRemoveModule() throws Exception {
        logger.info("Create an application, add a " + module + " module and delete it");
        String binary = "spring-boot-redis-1.0.0.jar";

        // create an application server
        String jsonString = "{\"applicationName\":\"" + applicationName + "\", \"serverName\":\"" + server + "\"}";
        ResultActions resultats = mockMvc.perform(post("/application").session(session).contentType(MediaType.APPLICATION_JSON).content(jsonString));
        resultats.andExpect(status().isOk());

        // verify if app exists
        resultats = mockMvc.perform(get("/application/" + applicationName).session(session).contentType(MediaType.APPLICATION_JSON));
        resultats.andExpect(jsonPath("name").value(applicationName.toLowerCase()));

        // add a module
        jsonString = "{\"applicationName\":\"" + applicationName + "\", \"imageName\":\"" + module + "\"}";
        resultats = mockMvc.perform(post("/module")
                .session(session)
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonString));
        resultats.andExpect(status().isOk());

        // Change to Java 8 for compliance with example fatjar
        jsonString =
                "{\"applicationName\":\"" + applicationName
                        + "\",\"jvmMemory\":\"512\",\"jvmOptions\":\"-Dkey1=value1\",\"jvmRelease\":\"jdk1.8.0_25\"}";
        resultats =
                mockMvc.perform(put("/server/configuration/jvm").session(session).contentType(MediaType.APPLICATION_JSON).content(jsonString));
        resultats.andExpect(status().isOk());

        // Open the 8080 port for fatjar
        jsonString =
                "{\"applicationName\":\"" + applicationName
                        + "\",\"portToOpen\":\"8080\",\"portNature\":\"web\"}";
        resultats =
                this.mockMvc.perform(post("/application/ports")
                        .session(session)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonString));
        resultats.andExpect(status().isOk()).andDo(print());

        // Expected values
        String genericModule = cuInstanceName.toLowerCase() + "-johndoe-" + applicationName.toLowerCase() + "-" + module + "-1";
        String gitModule = cuInstanceName.toLowerCase() + "-johndoe-" + applicationName.toLowerCase() + "-git-1";
        String managerExpected = "http://" + managerPrefix + "1-" + applicationName.toLowerCase() + "-johndoe-admin.cloudunit.dev/" + managerSuffix;

        // get the detail of the applications to verify modules addition
        resultats = mockMvc.perform(get("/application/" + applicationName)
                .session(session).contentType(MediaType.APPLICATION_JSON)).andDo(print());
        System.out.println(resultats.andReturn().getResponse().getContentAsString());
        resultats
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.modules[0].name").value(gitModule))
                .andExpect(jsonPath("$.modules[0].status").value("START"))
                .andExpect(jsonPath("$.modules[1].status").value("START"))
                .andExpect(jsonPath("$.modules[1].name").value(genericModule))
                .andExpect(jsonPath("$.modules[1].managerLocation").value(managerExpected));

        String fullContent = resultats.andReturn().getResponse().getContentAsString();

        String userNameRedis = JsonPath.read(fullContent, "$.modules[1].moduleInfos.username");
        String passwordRedis = JsonPath.read(fullContent, "$.modules[1].moduleInfos.password");

        // Deploy the fat Jar
        resultats =
                mockMvc.perform(MockMvcRequestBuilders.fileUpload("/application/" + applicationName + "/deploy")
                        .file(downloadAndPrepareFileToDeploy(binary,
                                "https://github.com/Treeptik/CloudUnit/releases/download/1.0/" + binary))
                        .session(session).contentType(MediaType.MULTIPART_FORM_DATA)).andDo(print());
        resultats.andExpect(status().is2xxSuccessful());
        String urlToCall = "http://" + applicationName.toLowerCase() + "-johndoe-forward-8080.cloudunit.dev";
        logger.debug(urlToCall);
        int i = 0;
        String content = null;
        // Wait for the deployment
        while(i++ < TestUtils.NB_ITERATION_MAX) {
            content = getUrlContentPage(urlToCall);
            Thread.sleep(1000);
            if (content == null || content.contains("Redis is great")) { break; }
        }
        logger.debug(content);
        if (content != null) {
            Assert.assertTrue(content.contains("Redis is great"));
        }

        logger.info("Delete application : " + applicationName);
        resultats = mockMvc.perform(delete("/application/" + applicationName).session(session).contentType(MediaType.APPLICATION_JSON));
        resultats.andExpect(status().isOk());
    }


}