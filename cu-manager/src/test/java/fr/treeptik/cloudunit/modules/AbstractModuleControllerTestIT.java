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
import com.mongodb.MongoClient;
import fr.treeptik.cloudunit.dto.EnvUnit;
import fr.treeptik.cloudunit.dto.ModulePortResource;
import fr.treeptik.cloudunit.exception.ServiceException;
import fr.treeptik.cloudunit.initializer.CloudUnitApplicationContext;
import fr.treeptik.cloudunit.model.User;
import fr.treeptik.cloudunit.service.UserService;
import fr.treeptik.cloudunit.utils.CheckBrokerConnectionUtils;
import fr.treeptik.cloudunit.utils.NamingUtils;
import fr.treeptik.cloudunit.utils.SpyMatcherDecorator;
import fr.treeptik.cloudunit.utils.TestUtils;
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
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.exceptions.JedisConnectionException;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.servlet.Filter;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.UnknownHostException;
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
public abstract class AbstractModuleControllerTestIT extends TestCase {

    protected static String applicationName;
    protected final Logger logger = LoggerFactory
            .getLogger(AbstractModuleControllerTestIT.class);
    @Autowired
    protected WebApplicationContext context;
    protected MockMvc mockMvc;
    @Value("${database.hostname}")
    protected String databaseHostname;
    protected MockHttpSession session;
    protected String domain;
    protected String server;
    protected String module;
    protected String numberPort;
    protected String managerPrefix;
    protected String managerSuffix;
    protected String managerPageContent;
    protected String testScriptPath;
    @Inject
    private ObjectMapper objectMapper;
    @Inject
    private AuthenticationManager authenticationManager;
    @Autowired
    private Filter springSecurityFilterChain;
    @Inject
    private UserService userService;
    @Value("${cloudunit.instance.name}")
    private String cuInstanceName;
    @Value("#{systemEnvironment['CU_DOMAIN']}")
    private String domainSuffix;
    @Value("#{systemEnvironment['CU_SUB_DOMAIN']}")
    private String subdomainPrefix;
    @Inject
    private CheckBrokerConnectionUtils checkBrokerConnectionUtils;

    @PostConstruct
    public void init() {
        if (subdomainPrefix != null) {
            domain = subdomainPrefix + "." + domainSuffix;
        } else {
            domain = "." + domainSuffix;
        }
    }

    @Before
    public void setUp() throws Exception {
        logger.info("setup");
        applicationName = "app" + new Random().nextInt(100000);
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

        logger.info("Delete application : " + applicationName);

        mockMvc.perform(delete("/application/" + applicationName)
                .session(session)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

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
        resultats.andExpect(status().is4xxClientError());
    }

    @Test
    public void test01_FailToAddModuleBecauseAppNonExist() throws Exception {
        logger.info("Cannot add a module because application name missing");
        String jsonString = "{\"applicationName\":\"" + "UFO" + "\", \"imageName\":\"" + module + "\"}";
        ResultActions resultats = mockMvc.perform(post("/module")
                .session(session)
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonString));
        resultats.andExpect(status().is4xxClientError());
    }

    @Test
    public void test02_FailToAddModuleBecauseModuleEmpty() throws Exception {
        logger.info("Cannot add a module because module name empty");
        String jsonString = "{\"applicationName\":\"" + "REALAPP" + "\", \"imageName\":\"" + "" + "\"}";
        ResultActions resultats = mockMvc.perform(post("/module")
                .session(session)
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonString));
        resultats.andExpect(status().is4xxClientError());
    }

    @Test
    public void test03_FailToAddModuleBecauseModuleNonExisting() throws Exception {
        logger.info("Cannot add a module because module name empty");
        String jsonString = "{\"applicationName\":\"" + "REALAPP" + "\", \"imageName\":\"" + "UFO" + "\"}";
        ResultActions resultats = mockMvc.perform(post("/module")
                .session(session)
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonString));
        resultats.andExpect(status().is4xxClientError());
    }

    @Test
    public void test10_CreateServerThenAddModule() throws Exception {
        logger.info("Create an application, add a " + module + " module and delete it");

        // verify if app exists
        ResultActions resultats = mockMvc.perform(get("/application/" + applicationName).session(session).contentType(MediaType.APPLICATION_JSON));
        resultats.andExpect(jsonPath("name").value(applicationName.toLowerCase()));

        // add a module
        String jsonString = "{\"applicationName\":\"" + applicationName + "\", \"imageName\":\"" + module + "\"}";
        resultats = mockMvc.perform(post("/module")
                .session(session)
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonString));
        resultats.andExpect(status().isOk());

        // Expected values
        String genericModule = NamingUtils.getContainerName(applicationName, module, "johndoe");

        // get the detail of the applications to verify modules addition
        resultats = mockMvc.perform(get("/application/" + applicationName)
                .session(session).contentType(MediaType.APPLICATION_JSON)).andDo(print());
        resultats
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.modules[0].status").value("START"))
                .andExpect(jsonPath("$.modules[0].name").value(genericModule));

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
                .andExpect(jsonPath("$.modules[0]").doesNotExist());
    }

    @Test
    public void test20_FailCreateServerThenAddTwoModule() throws Exception {
        logger.info("Create an application, add two " + module + " modules, stop them then delete all");

        // verify if app exists
        ResultActions resultats = mockMvc.perform(get("/application/" + applicationName).session(session).contentType(MediaType.APPLICATION_JSON));
        resultats.andExpect(jsonPath("name").value(applicationName.toLowerCase()));

        // add a first module
        String jsonString = "{\"applicationName\":\"" + applicationName + "\", \"imageName\":\"" + module + "\"}";
        resultats = mockMvc.perform(post("/module")
                .session(session)
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonString));
        resultats.andExpect(status().isOk());

        // Expected values
        String module1 = NamingUtils.getContainerName(applicationName, module, "johndoe");

        // get the detail of the applications to verify modules addition
        resultats = mockMvc.perform(get("/application/" + applicationName)
                .session(session).contentType(MediaType.APPLICATION_JSON)).andDo(print());
        resultats
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.modules[0].status").value("START"))
                .andExpect(jsonPath("$.modules[0].name").value(module1));

        // add a second module
        jsonString = "{\"applicationName\":\"" + applicationName + "\", \"imageName\":\"" + module + "\"}";
        resultats = mockMvc.perform(post("/module")
                .session(session)
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonString));
        resultats.andExpect(status().is4xxClientError());
    }

    @Test
    public void test30_AddModuleThenRestart() throws Exception {
        logger.info("Create an application, add a " + module + " modules, restart");

        // verify if app exists
        ResultActions resultats = mockMvc.perform(get("/application/" + applicationName).session(session).contentType(MediaType.APPLICATION_JSON));
        resultats.andExpect(jsonPath("name").value(applicationName.toLowerCase()));

        // add a first module
        String jsonString = "{\"applicationName\":\"" + applicationName + "\", \"imageName\":\"" + module + "\"}";
        resultats = mockMvc.perform(post("/module")
                .session(session)
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonString));
        resultats.andDo(print());
        resultats.andExpect(status().isOk());

        // Expected values
        String module1 = NamingUtils.getContainerName(applicationName, module, "johndoe");

        // Stop the application
        jsonString = "{\"applicationName\":\"" + applicationName + "\"}";
        resultats = mockMvc.perform(post("/application/stop").session(session).contentType(MediaType.APPLICATION_JSON).content(jsonString));
        resultats.andDo(print());
        resultats.andExpect(status().isOk());

        // Start the application
        jsonString = "{\"applicationName\":\"" + applicationName + "\"}";
        resultats = mockMvc.perform(post("/application/start").session(session).contentType(MediaType.APPLICATION_JSON).content(jsonString));
        resultats.andDo(print());
        resultats.andExpect(status().isOk());

        // get the detail of the applications to verify modules addition
        resultats = mockMvc.perform(get("/application/" + applicationName)
                .session(session).contentType(MediaType.APPLICATION_JSON)).andDo(print());
        resultats.andDo(print());
        resultats
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.modules[0].status").value("START"))
                .andExpect(jsonPath("$.modules[0].name").value(module1));
    }

    @Test
    public void test_PublishPort() throws Exception {
        logger.info("Publish module port for external access");

        requestAddModule()
                .andExpect(status().isOk());

        SpyMatcherDecorator<Integer> responseModuleIdSpy = new SpyMatcherDecorator<>(Integer.class);
        SpyMatcherDecorator<List> forwardedPort = new SpyMatcherDecorator<>(List.class);

        requestApplication()
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.modules[0].id", responseModuleIdSpy));

        Integer moduleId = responseModuleIdSpy.getMatchedValue();
        requestPublishPort(moduleId, numberPort)
                .andExpect(status().isOk());

        requestApplication()
                .andExpect(status().isOk())
                .andDo(print());
        requestApplication()
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath(String.format("$.modules[0].ports[?(@.containerValue == %s)].hostValue", numberPort)
                        , forwardedPort));
        String port = forwardedPort.getMatchedValue().stream().findFirst().get().toString();
        logger.info("checkConnection for : " + port);
        checkConnection(port);
    }

    protected abstract void checkConnection(String forwardedPort);

    private String getContainerName() {
        return NamingUtils.getContainerName(applicationName, module, "johndoe");
    }

    @Test
    public void test_runScript() throws Exception {
        requestAddModule();
        String filename = FilenameUtils.getName(testScriptPath);
        if (filename == null) {
            logger.info("No script found - test escape");
        } else {
            MockMultipartFile file = new MockMultipartFile(
                    "file",
                    filename,
                    "application/sql",
                    new FileInputStream(testScriptPath));

            String genericModule = NamingUtils.getContainerName(applicationName, module, "johndoe");

            ResultActions result = mockMvc.perform(
                    fileUpload("/module/{moduleName}/run-script", genericModule)
                            .file(file)
                            .session(session)
                            .contentType(MediaType.MULTIPART_FORM_DATA))
                    .andDo(print());
            result.andExpect(status().isOk());
        }
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

    private ResultActions requestAddModule() throws Exception {
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

    /**
     * Inner class to check relational database connection
     */
    public class CheckDatabaseConnection {

        public void invoke(String forwardedPort, String keyUser, String keyPassword,
                           String keyDB, String driver, String jdbcUrlPrefix) {
            try {
                String urlToCall = "/application/" + applicationName + "/container/" + getContainerName() + "/env";
                ResultActions resultats = mockMvc.perform(get(urlToCall).session(session).contentType(MediaType.APPLICATION_JSON));
                String contentResult = resultats.andReturn().getResponse().getContentAsString();
                List<EnvUnit> envs = objectMapper.readValue(contentResult, new TypeReference<List<EnvUnit>>() {
                });
                final String user = envs.stream().filter(e -> e.getKey().equals(keyUser)).findFirst().orElseThrow(() -> new RuntimeException("Missing " + keyUser)).getValue();
                final String password = envs.stream().filter(e -> e.getKey().equals(keyPassword)).findFirst().orElseThrow(() -> new RuntimeException("Missing " + keyPassword)).getValue();
                final String database = envs.stream().filter(e -> e.getKey().equals(keyDB)).findFirst().orElseThrow(() -> new RuntimeException("Missing " + keyDB)).getValue();
                final String jdbcUrl = jdbcUrlPrefix + databaseHostname + ":" + forwardedPort + "/" + database;
                Class.forName(driver);
                await("Testing database connection...").atMost(5, TimeUnit.SECONDS)
                        .and().ignoreExceptions()
                        .until(() -> {
                            try (final Connection connection = DriverManager.getConnection(jdbcUrl, user, password)) {
                                return connection.isValid(1000);
                            }
                        });
            } catch (Exception e) {
                e.printStackTrace();
                Assert.fail();
            }
        }
    }

    /**
     * Inner class to check message broker
     */
    public class CheckBrokerConnection {
        public void invoke(String forwardedPort, String keyUser, String keyPassword,
                           String keyDB, String protocol) {
            String user = null;
            String password = null;
            String vhost = null;
            String urlToCall = "/application/" + applicationName + "/container/" + getContainerName() + "/env";
            String contentResult = null;
            try {
                ResultActions resultats = mockMvc.perform(get(urlToCall).session(session).contentType(MediaType.APPLICATION_JSON));
                contentResult = resultats.andReturn().getResponse().getContentAsString();
                List<EnvUnit> envs = objectMapper.readValue(contentResult, new TypeReference<List<EnvUnit>>() {
                });
                contentResult = resultats.andReturn().getResponse().getContentAsString();
                user = envs.stream()
                        .filter(e -> e.getKey().equals(keyUser))
                        .findFirst()
                        .orElseThrow(() -> new RuntimeException("Missing " + keyUser))
                        .getValue();
                password = envs.stream()
                        .filter(e -> e.getKey().equals(keyPassword))
                        .findFirst()
                        .orElseThrow(() -> new RuntimeException("Missing " + keyPassword))
                        .getValue();
                vhost = envs.stream()
                        .filter(e -> e.getKey().equals(keyDB))
                        .findFirst()
                        .orElseThrow(() -> new RuntimeException("Missing " + keyDB))
                        .getValue();
                String brokerURL = databaseHostname + ":" + forwardedPort;
                String message = "Hello world!";

                switch (protocol) {
                    case "JMS":
                        Assert.assertEquals(message,
                                checkBrokerConnectionUtils.checkActiveMQJMSProtocol(message, brokerURL));
                        break;
                    case "AMQP":
                        Assert.assertEquals(message,
                                checkBrokerConnectionUtils.checkRabbitMQAMQPProtocol(message, brokerURL, user, password, vhost));
                        break;
                    default:
                        throw new RuntimeException("Protocol " + keyDB + " not supported yet");
                }


            } catch (Exception e) {
                e.printStackTrace();
                Assert.fail();
            }

        }
    }

    /**
     * Inner class to check elasticsearch connection
     */
    public class CheckElasticSearchConnection {
        public void invoke(String forwardedPort) {
            String url = String.format("http://%s:%s", databaseHostname, forwardedPort);
            try {
                await("Testing database connection...").atMost(5, TimeUnit.SECONDS)
                        .and().ignoreExceptions()
                        .until(() -> {
                            try {
                                "elasticsearch".contains(TestUtils.getUrlContentPage(url));
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        });
            } catch (Exception e) {
                Assert.fail();
                e.printStackTrace();
            }
        }
    }

    /**
     * Inner class to check redis connection
     */
    public class CheckRedisConnection {

        public void invoke(String forwardedPort) {
            try (JedisPool pool = new JedisPool(
                    new JedisPoolConfig(), databaseHostname, Integer.parseInt(forwardedPort), 3000)) {
                Jedis jedis = pool.getResource();
            } catch (JedisConnectionException e) {
                Assert.fail();
                e.printStackTrace();
            }
        }
    }

    /**
     * Inner class to check redis connection
     */
    public class CheckMongoConnection {
        public void invoke(String forwardedPort) {
            MongoClient mongo = null;
            try {
                mongo = new MongoClient(databaseHostname, Integer.parseInt(forwardedPort));
            } catch (UnknownHostException e) {
                Assert.fail();
                e.printStackTrace();
            } finally {
                mongo.close();
            }
        }
    }

}