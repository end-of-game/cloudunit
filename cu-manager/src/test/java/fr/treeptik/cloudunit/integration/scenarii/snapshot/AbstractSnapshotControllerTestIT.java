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

package fr.treeptik.cloudunit.integration.scenarii.snapshot;

import fr.treeptik.cloudunit.exception.ServiceException;
import fr.treeptik.cloudunit.initializer.CloudUnitApplicationContext;
import fr.treeptik.cloudunit.model.User;
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
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
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
 * Created by nicolas on 08/09/15.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(classes = {CloudUnitApplicationContext.class, MockServletContext.class})
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@ActiveProfiles("integration")
public abstract class AbstractSnapshotControllerTestIT {

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

    private final static String tagName = "myTag";

    protected String release;

    @BeforeClass
    public static void initEnv() {
        applicationName = "App" + new Random().nextInt(10000);
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

        Authentication authentication = new UsernamePasswordAuthenticationToken(user.getLogin(), user.getPassword());
        Authentication result = authenticationManager.authenticate(authentication);
        SecurityContext securityContext = SecurityContextHolder.getContext();
        securityContext.setAuthentication(result);
        session = new MockHttpSession();
        session.setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY, securityContext);

    }

    @After
    public void teardown() {
        logger.info("teardown");

        SecurityContextHolder.clearContext();
        session.invalidate();
    }

    @Test()
    public void test010_CreateSimpleApplicationSnapshot()
        throws Exception {
        logger.info("**************************************");
        logger.info("Create Tomcat server");
        logger.info("**************************************");

        String jsonString = "{\"applicationName\":\"" + applicationName + "\", \"serverName\":\"" + release + "\"}";
        ResultActions resultats =
            mockMvc.perform(post("/application").session(session).contentType(MediaType.APPLICATION_JSON).content(jsonString));
        resultats.andExpect(status().isOk());

        logger.info("**************************************");
        logger.info("Create a snapshot");
        logger.info("**************************************");

        jsonString =
            "{\"applicationName\":\"" + applicationName + "\", \"tag\":\"" + tagName
                + "\", \"description\":\"This is a test snapshot\"}";
        logger.info(jsonString);
        resultats =
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

        resultats = mockMvc.perform(delete("/snapshot/" + tagName).session(session)).andDo(print());
        resultats.andExpect(status().isOk());

        logger.info("**************************************");
        logger.info("Delete application : " + applicationName);
        logger.info("**************************************");
        resultats =
            mockMvc.perform(delete("/application/" + applicationName).session(session).contentType(MediaType.APPLICATION_JSON));
        resultats.andExpect(status().isOk());
    }

    @Test()
    public void test011_CreateHelloworldApplicationSnapshot()
        throws Exception {
        logger.info("**************************************");
        logger.info("Create Tomcat server");
        logger.info("**************************************");

        String jsonString = "{\"applicationName\":\"" + applicationName + "\", \"serverName\":\"" + release + "\"}";
        ResultActions resultats =
            mockMvc.perform(post("/application").session(session).contentType(MediaType.APPLICATION_JSON).content(jsonString));
        resultats.andExpect(status().isOk());

        logger.info("**************************************");
        logger.info("Deploy a helloworld Application");
        logger.info("**************************************");

        resultats =
            mockMvc.perform(MockMvcRequestBuilders.fileUpload("/application/" + applicationName + "/deploy").file(downloadAndPrepareFileToDeploy("helloworld.war",
                "https://github.com/Treeptik/CloudUnit/releases/download/1.0/helloworld.war")).session(session).contentType(MediaType.MULTIPART_FORM_DATA)).andDo(print());
        resultats.andExpect(status().is2xxSuccessful());
        String urlToCall = "http://" + applicationName.toLowerCase() + "-johndoe-admin.cloudunit.dev";
        String contentPage = getUrlContentPage(urlToCall);
        if (release.contains("jboss")) {
            int counter = 0;
            while (contentPage.contains("Welcome to WildFly") && counter++ < 10) {
                contentPage = getUrlContentPage(urlToCall);
                Thread.sleep(1000);
            }
        }
        Assert.assertTrue(contentPage.contains("CloudUnit PaaS"));

        logger.info("**************************************");
        logger.info("Create a snapshot");
        logger.info("**************************************");

        jsonString =
            "{\"applicationName\":\"" + applicationName + "\", \"tag\":\"" + tagName
                + "\", \"description\":\"This is a test snapshot\"}";
        logger.info(jsonString);
        resultats =
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

        resultats = mockMvc.perform(delete("/snapshot/" + tagName).session(session)).andDo(print());
        resultats.andExpect(status().isOk());

        logger.info("**************************************");
        logger.info("Delete application : " + applicationName);
        logger.info("**************************************");
        resultats =
            mockMvc.perform(delete("/application/" + applicationName).session(session).contentType(MediaType.APPLICATION_JSON));
        resultats.andExpect(status().isOk());
    }

    @Test()
    public void test012_CreateAMysqlBasedApplicationSnapshot()
        throws Exception {
        createApplicationSnapshotWithAModuleAndADeployment("mysql-5-5", "pizzashop-mysql", "Pizzas");
    }

    @Test()
    public void test013_CreateAPostGresBasedApplicationSnapshot()
        throws Exception {
        createApplicationSnapshotWithAModuleAndADeployment("postgresql-9-3", "pizzashop-postgres", "Pizzas");
    }

    @Test()
    public void test014_CreateAMongoBasedApplicationSnapshot()
        throws Exception {
        createApplicationSnapshotWithAModuleAndADeployment("mongo-2-6", "mongo", "Country");
    }

    @Test()
    public void test020_CloneASimpleApplicationSnapshot()
        throws Exception {
        logger.info("**************************************");
        logger.info("Create Tomcat server");
        logger.info("**************************************");

        String jsonString = "{\"applicationName\":\"" + applicationName + "\", \"serverName\":\"" + release + "\"}";
        ResultActions resultats =
            mockMvc.perform(post("/application").session(session).contentType(MediaType.APPLICATION_JSON).content(jsonString));
        resultats.andExpect(status().isOk());

        logger.info("**************************************");
        logger.info("Create a snapshot");
        logger.info("**************************************");

        jsonString =
            "{\"applicationName\":\"" + applicationName + "\", \"tag\":\"" + tagName
                + "\", \"description\":\"This is a test snapshot\"}";
        logger.info(jsonString);
        resultats =
            mockMvc.perform(post("/snapshot").session(session).contentType(MediaType.APPLICATION_JSON).content(jsonString)).andDo(print());
        resultats.andExpect(status().isOk());

        logger.info("**************************************");
        logger.info("List the snapshot");
        logger.info("**************************************");

        resultats = mockMvc.perform(get("/snapshot/list").session(session)).andDo(print());
        resultats.andExpect(status().isOk()).andExpect(jsonPath("$[0].tag").value(tagName.toLowerCase()));

        logger.info("**************************************");
        logger.info("Delete the original application : " + applicationName);
        logger.info("**************************************");
        resultats =
            mockMvc.perform(delete("/application/" + applicationName).session(session).contentType(MediaType.APPLICATION_JSON));
        resultats.andExpect(status().isOk());

        logger.info("**************************************");
        logger.info("Clone an application : " + applicationName + "cloned");
        logger.info("**************************************");

        jsonString =
            "{\"applicationName\":\"" + applicationName + "cloned" + "\", \"tag\":\"" + tagName
                + "\", \"description\":\"This is a test snapshot\"}";

        resultats =
            mockMvc.perform(post("/snapshot/clone").session(session).contentType(MediaType.APPLICATION_JSON).content(jsonString)).andDo(print());
        resultats.andExpect(status().isOk());

        logger.info("**************************************");
        logger.info("Delete the snapshot");
        logger.info("**************************************");

        resultats = mockMvc.perform(delete("/snapshot/" + tagName).session(session)).andDo(print());
        resultats.andExpect(status().isOk());

        logger.info("**************************************");
        logger.info("Delete the cloned application");
        logger.info("**************************************");

        resultats =
            mockMvc.perform(delete("/application/" + applicationName + "cloned").session(session).contentType(MediaType.APPLICATION_JSON));
        resultats.andExpect(status().isOk());
    }

    @Test
    public void test021_CloneAHelloworldApplicationSnapshot()
        throws Exception {
        logger.info("**************************************");
        logger.info("Create Tomcat server");
        logger.info("**************************************");

        String jsonString = "{\"applicationName\":\"" + applicationName + "\", \"serverName\":\"" + release + "\"}";
        ResultActions resultats =
            mockMvc.perform(post("/application").session(session).contentType(MediaType.APPLICATION_JSON).content(jsonString));
        resultats.andExpect(status().isOk());

        logger.info("**************************************");
        logger.info("Deploy a helloworld Application");
        logger.info("**************************************");

        resultats =
            mockMvc.perform(fileUpload("/application/" + applicationName + "/deploy").file(downloadAndPrepareFileToDeploy("helloworld.war",
                "https://github.com/Treeptik/CloudUnit/releases/download/1.0/helloworld.war")).session(session).contentType(MediaType.MULTIPART_FORM_DATA)).andDo(print());
        resultats.andExpect(status().is2xxSuccessful());
        String urlToCall = "http://" + applicationName.toLowerCase() + "-johndoe-admin.cloudunit.dev";
        String contentPage = getUrlContentPage(urlToCall);
        if (release.contains("jboss")) {
            int counter = 0;
            while (contentPage.contains("Welcome to WildFly") && counter++ < 10) {
                contentPage = getUrlContentPage(urlToCall);
                Thread.sleep(1000);
            }
        }
        Assert.assertTrue(contentPage.contains("CloudUnit PaaS"));

        logger.info("**************************************");
        logger.info("Create a snapshot");
        logger.info("**************************************");

        jsonString =
            "{\"applicationName\":\"" + applicationName + "\", \"tag\":\"" + tagName
                + "\", \"description\":\"This is a test snapshot\"}";
        logger.info(jsonString);
        resultats =
            mockMvc.perform(post("/snapshot").session(session).contentType(MediaType.APPLICATION_JSON).content(jsonString)).andDo(print());
        resultats.andExpect(status().isOk());

        logger.info("**************************************");
        logger.info("List the snapshot");
        logger.info("**************************************");

        resultats = mockMvc.perform(get("/snapshot/list").session(session)).andDo(print());
        resultats.andExpect(status().isOk()).andExpect(jsonPath("$[0].tag").value(tagName.toLowerCase()));

        logger.info("**************************************");
        logger.info("Delete the original application : " + applicationName);
        logger.info("**************************************");
        resultats =
            mockMvc.perform(delete("/application/" + applicationName).session(session).contentType(MediaType.APPLICATION_JSON));
        resultats.andExpect(status().isOk());

        logger.info("**************************************");
        logger.info("Clone an application : " + applicationName + "cloned");
        logger.info("**************************************");

        jsonString =
            "{\"applicationName\":\"" + applicationName + "cloned" + "\", \"tag\":\"" + tagName
                + "\", \"description\":\"This is a test snapshot\"}";

        resultats =
            mockMvc.perform(post("/snapshot/clone").session(session).contentType(MediaType.APPLICATION_JSON).content(jsonString)).andDo(print());
        resultats.andExpect(status().isOk());

        logger.info("**************************************");
        logger.info("Start the application : " + applicationName + "cloned");
        logger.info("**************************************");

        jsonString = "{\"applicationName\":\"" + applicationName + "cloned" + "\"}";
        resultats =
            this.mockMvc.perform(post("/application/start").session(session).contentType(MediaType.APPLICATION_JSON).content(jsonString));
        resultats.andExpect(status().isOk());

        logger.info("**************************************");
        logger.info("Check the keyword of " + applicationName + "cloned");
        logger.info("**************************************");

        urlToCall = "http://" + applicationName.toLowerCase() + "cloned" + "-johndoe-admin.cloudunit.dev";
        contentPage = getUrlContentPage(urlToCall);
        if (release.contains("jboss")) {
            int counter = 0;
            while (contentPage.contains("Welcome to WildFly") && counter++ < 10) {
                contentPage = getUrlContentPage(urlToCall);
                Thread.sleep(1000);
            }
        }
        Assert.assertTrue(contentPage.contains("CloudUnit PaaS"));

        logger.info("**************************************");
        logger.info("Delete the snapshot");
        logger.info("**************************************");

        resultats = mockMvc.perform(delete("/snapshot/" + tagName).session(session)).andDo(print());
        resultats.andExpect(status().isOk());

        logger.info("**************************************");
        logger.info("Delete the cloned application");
        logger.info("**************************************");

        resultats =
            mockMvc.perform(delete("/application/" + applicationName + "cloned").session(session).contentType(MediaType.APPLICATION_JSON));
        resultats.andExpect(status().isOk());
    }

    @Test
    public void test022_CloneAMysqlBasedApplicationSnapshot()
        throws Exception {
        cloneASnapshotWithApplicationWithModuleAndADeployment("mysql-5-5", "pizzashop-mysql", "Pizzas");
    }

    @Test()
    public void test023_CloneAPostGresBasedApplicationSnapshot()
        throws Exception {
        cloneASnapshotWithApplicationWithModuleAndADeployment("postgresql-9-3", "pizzashop-postgres", "Pizzas");
    }

    @Test()
    public void test024_CloneAMongoBasedApplicationSnapshot()
        throws Exception {
        cloneASnapshotWithApplicationWithModuleAndADeployment("mongo-2-6", "mongo", "Country");
    }
    
    @Test()
    public void test030_ChangeJvmOptionsApplicationTest()
        throws Exception {
        
        logger.info("**************************************");
        logger.info("Create app server");
        logger.info("**************************************");

        String jsonString = "{\"applicationName\":\"" + applicationName + "\", \"serverName\":\"" + release + "\"}";
        ResultActions resultats =
            mockMvc.perform(post("/application").session(session).contentType(MediaType.APPLICATION_JSON).content(jsonString));
        resultats.andExpect(status().isOk());
            
        logger.info("Change JVM Options !");
        
        jsonString =
            "{\"applicationName\":\"" + applicationName
                + "\",\"jvmMemory\":\"1024\",\"jvmOptions\":\"-Dkey1=value1\",\"jvmRelease\":\"jdk1.8.0_25\"}";
        resultats =
            mockMvc.perform(put("/server/configuration/jvm").session(session).contentType(MediaType.APPLICATION_JSON).content(jsonString));
        resultats.andExpect(status().isOk());

        resultats =
            mockMvc.perform(get("/application/" + applicationName).session(session).contentType(MediaType.APPLICATION_JSON));
        resultats.andExpect(jsonPath("$.servers[0].jvmMemory").value(1024)).andExpect(jsonPath(
            "$.servers[0].jvmRelease").value("jdk1.8.0_25")).andExpect(jsonPath(
            "$.servers[0].jvmOptions").value("-Dkey1=value1"));
        
        logger.info("**************************************");
        logger.info("Create a snapshot");
        logger.info("**************************************");

        jsonString =
            "{\"applicationName\":\"" + applicationName + "\", \"tag\":\"" + tagName
                + "\", \"description\":\"This is a test snapshot\"}";
        logger.info(jsonString);
        resultats =
            mockMvc.perform(post("/snapshot").session(session).contentType(MediaType.APPLICATION_JSON).content(jsonString)).andDo(print());
        resultats.andExpect(status().isOk());

        logger.info("**************************************");
        logger.info("List the snapshot");
        logger.info("**************************************");

        resultats = mockMvc.perform(get("/snapshot/list").session(session)).andDo(print());
        resultats.andExpect(status().isOk()).andExpect(jsonPath("$[0].tag").value(tagName.toLowerCase()));

        logger.info("**************************************");
        logger.info("Delete the original application : " + applicationName);
        logger.info("**************************************");
        resultats =
            mockMvc.perform(delete("/application/" + applicationName).session(session).contentType(MediaType.APPLICATION_JSON));
        resultats.andExpect(status().isOk());

        logger.info("**************************************");
        logger.info("Clone an application : " + applicationName + "cloned");
        logger.info("**************************************");

        jsonString =
            "{\"applicationName\":\"" + applicationName + "cloned" + "\", \"tag\":\"" + tagName
                + "\", \"description\":\"This is a test snapshot\"}";

        resultats =
            mockMvc.perform(post("/snapshot/clone").session(session).contentType(MediaType.APPLICATION_JSON).content(jsonString)).andDo(print());
        resultats.andExpect(status().isOk());

        logger.info("**************************************");
        logger.info("Start the application : " + applicationName + "cloned");
        logger.info("**************************************");

        jsonString = "{\"applicationName\":\"" + applicationName + "cloned" + "\"}";
        resultats =
            this.mockMvc.perform(post("/application/start").session(session).contentType(MediaType.APPLICATION_JSON).content(jsonString));
        resultats.andExpect(status().isOk());        
        resultats =
        mockMvc.perform(get("/application/" + applicationName+"cloned").session(session).contentType(MediaType.APPLICATION_JSON));
        resultats.andExpect(jsonPath("$.servers[0].jvmMemory").value(1024)).andExpect(jsonPath(
        "$.servers[0].jvmRelease").value("jdk1.8.0_25")).andExpect(jsonPath(
        "$.servers[0].jvmOptions").value("-Dkey1=value1"));
         logger.info("**************************************");
         logger.info("Delete the snapshot");
         logger.info("**************************************");

         resultats = mockMvc.perform(delete("/snapshot/" + tagName).session(session)).andDo(print());
         resultats.andExpect(status().isOk());

         logger.info("**************************************");
         logger.info("Delete the cloned application");
         logger.info("**************************************");

          resultats =
          mockMvc.perform(delete("/application/" + applicationName + "cloned").session(session).contentType(MediaType.APPLICATION_JSON));
          resultats.andExpect(status().isOk());
    }

    private void cloneASnapshotWithApplicationWithModuleAndADeployment(String module, String appName,
                                                                       String keywordIntoPage)
        throws Exception {
        logger.info("**************************************");
        logger.info("Create app server");
        logger.info("**************************************");

        String jsonString = "{\"applicationName\":\"" + applicationName + "\", \"serverName\":\"" + release + "\"}";
        ResultActions resultats =
            mockMvc.perform(post("/application").session(session).contentType(MediaType.APPLICATION_JSON).content(jsonString));
        resultats.andExpect(status().isOk());

        logger.info("**************************************");
        logger.info("Add the module");
        logger.info("**************************************");

        jsonString = "{\"applicationName\":\"" + applicationName + "\", \"imageName\":\"" + module + "\"}";
        resultats =
            mockMvc.perform(post("/module").session(session).contentType(MediaType.APPLICATION_JSON).content(jsonString)).andDo(print());
        resultats.andExpect(status().isOk());

        logger.info("**************************************");
        logger.info("Deploy a helloworld Application");
        logger.info("**************************************");

        logger.info("Deploy an " + module + " based application");
        resultats =
            mockMvc.perform(MockMvcRequestBuilders.fileUpload("/application/" + applicationName + "/deploy").file(downloadAndPrepareFileToDeploy(appName
                    + ".war",
                "https://github.com/Treeptik/CloudUnit/releases/download/1.0/"
                    + appName + ".war")).session(session).contentType(MediaType.MULTIPART_FORM_DATA)).andDo(print());
        // test the application content page
        resultats.andExpect(status().is2xxSuccessful());
        String urlToCall = "http://" + applicationName.toLowerCase() + "-johndoe-admin.cloudunit.dev";
        String contentPage = getUrlContentPage(urlToCall);
        if (release.contains("jboss")) {
            int counter = 0;
            while (contentPage.contains("Welcome to WildFly") && counter++ < 10) {
                contentPage = getUrlContentPage(urlToCall);
                Thread.sleep(1000);
            }
        }
        Assert.assertTrue(contentPage.contains(keywordIntoPage));

        logger.info("**************************************");
        logger.info("Create a snapshot");
        logger.info("**************************************");

        jsonString =
            "{\"applicationName\":\"" + applicationName + "\", \"tag\":\"" + tagName
                + "\", \"description\":\"This is a test snapshot\"}";
        logger.info(jsonString);
        resultats =
            mockMvc.perform(post("/snapshot").session(session).contentType(MediaType.APPLICATION_JSON).content(jsonString)).andDo(print());
        resultats.andExpect(status().isOk());

        logger.info("**************************************");
        logger.info("List the snapshot");
        logger.info("**************************************");

        resultats = mockMvc.perform(get("/snapshot/list").session(session)).andDo(print());
        resultats.andExpect(status().isOk()).andExpect(jsonPath("$[0].tag").value(tagName.toLowerCase()));

        logger.info("**************************************");
        logger.info("Delete the original application : " + applicationName);
        logger.info("**************************************");
        resultats =
            mockMvc.perform(delete("/application/" + applicationName).session(session).contentType(MediaType.APPLICATION_JSON));
        resultats.andExpect(status().isOk());

        logger.info("**************************************");
        logger.info("Clone an application : " + applicationName + "cloned");
        logger.info("**************************************");

        jsonString =
            "{\"applicationName\":\"" + applicationName + "cloned" + "\", \"tag\":\"" + tagName
                + "\", \"description\":\"This is a test snapshot\"}";

        resultats =
            mockMvc.perform(post("/snapshot/clone").session(session).contentType(MediaType.APPLICATION_JSON).content(jsonString)).andDo(print());
        resultats.andExpect(status().isOk());

        logger.info("**************************************");
        logger.info("Start the application : " + applicationName + "cloned");
        logger.info("**************************************");

        jsonString = "{\"applicationName\":\"" + applicationName + "cloned" + "\"}";
        resultats =
            this.mockMvc.perform(post("/application/start").session(session).contentType(MediaType.APPLICATION_JSON).content(jsonString));
        resultats.andExpect(status().isOk());

        logger.info("**************************************");
        logger.info("Check the contentPage of " + applicationName + "cloned");
        logger.info("**************************************");

        urlToCall = "http://" + applicationName.toLowerCase() + "cloned" + "-johndoe-admin.cloudunit.dev";
        if (release.contains("jboss")) {
            int counter = 0;
            contentPage = getUrlContentPage(urlToCall);
            while (contentPage.contains("Welcome to WildFly") && counter++ < 10) {
                contentPage = getUrlContentPage(urlToCall);
                Thread.sleep(1000);
            }
        }
        Assert.assertTrue(contentPage.contains(keywordIntoPage));

        logger.info("**************************************");
        logger.info("Delete the snapshot");
        logger.info("**************************************");

        resultats = mockMvc.perform(delete("/snapshot/" + tagName).session(session)).andDo(print());
        resultats.andExpect(status().isOk());

        logger.info("**************************************");
        logger.info("Delete the cloned application");
        logger.info("**************************************");

        resultats =
            mockMvc.perform(delete("/application/" + applicationName + "cloned").session(session).contentType(MediaType.APPLICATION_JSON));
        resultats.andExpect(status().isOk());
    }

    private void createApplicationSnapshotWithAModuleAndADeployment(String module, String appName, String keywordIntoPage)
        throws Exception {
        logger.info("**************************************");
        logger.info("Create Tomcat server");
        logger.info("**************************************");

        String jsonString = "{\"applicationName\":\"" + applicationName + "\", \"serverName\":\"" + release + "\"}";
        ResultActions resultats =
            mockMvc.perform(post("/application").session(session).contentType(MediaType.APPLICATION_JSON).content(jsonString));
        resultats.andExpect(status().isOk());

        logger.info("**************************************");
        logger.info("Add the module");
        logger.info("**************************************");

        jsonString = "{\"applicationName\":\"" + applicationName + "\", \"imageName\":\"" + module + "\"}";
        resultats =
            mockMvc.perform(post("/module").session(session).contentType(MediaType.APPLICATION_JSON).content(jsonString)).andDo(print());
        resultats.andExpect(status().isOk());

        logger.info("**************************************");
        logger.info("Deploy a helloworld Application");
        logger.info("**************************************");

        logger.info("Deploy an " + module + " based application");
        resultats =
            mockMvc.perform(MockMvcRequestBuilders.fileUpload("/application/" + applicationName + "/deploy").file(downloadAndPrepareFileToDeploy(appName
                    + ".war",
                "https://github.com/Treeptik/CloudUnit/releases/download/1.0/"
                    + appName
                    + ".war")).session(session).contentType(MediaType.MULTIPART_FORM_DATA)).andDo(print());
        // test the application content page
        resultats.andExpect(status().is2xxSuccessful());
        String urlToCall = "http://" + applicationName.toLowerCase() + "-johndoe-admin.cloudunit.dev";
        String contentPage = getUrlContentPage(urlToCall);
        if (release.contains("jboss")) {
            int counter = 0;
            while (contentPage.contains("Welcome to WildFly") && counter++ < 10) {
                contentPage = getUrlContentPage(urlToCall);
                Thread.sleep(1000);
            }
        }
        Assert.assertTrue(contentPage.contains(keywordIntoPage));

        logger.info("**************************************");
        logger.info("Create a snapshot");
        logger.info("**************************************");

        jsonString =
            "{\"applicationName\":\"" + applicationName + "\", \"tag\":\"" + tagName
                + "\", \"description\":\"This is a test snapshot\"}";
        logger.info(jsonString);
        resultats =
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

        resultats = mockMvc.perform(delete("/snapshot/" + tagName).session(session)).andDo(print());
        resultats.andExpect(status().isOk());

        logger.info("**************************************");
        logger.info("Delete application : " + applicationName);
        logger.info("**************************************");
        resultats =
            mockMvc.perform(delete("/application/" + applicationName).session(session).contentType(MediaType.APPLICATION_JSON));
        resultats.andExpect(status().isOk());
    }

}