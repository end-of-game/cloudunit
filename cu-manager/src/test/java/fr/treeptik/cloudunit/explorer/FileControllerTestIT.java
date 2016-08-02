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

package fr.treeptik.cloudunit.explorer;

import fr.treeptik.cloudunit.exception.ServiceException;
import fr.treeptik.cloudunit.initializer.CloudUnitApplicationContext;
import fr.treeptik.cloudunit.model.User;
import fr.treeptik.cloudunit.service.DockerService;
import fr.treeptik.cloudunit.service.SpotifyDockerService;
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
 * Created by nicolas on 08/09/15.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(classes = {
    CloudUnitApplicationContext.class,
    MockServletContext.class
})
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@ActiveProfiles("integration")
public class FileControllerTestIT {

    protected String release = "tomcat-8";

    private final Logger logger = LoggerFactory.getLogger(FileControllerTestIT.class);

    @Autowired
    private WebApplicationContext context;

    private MockMvc mockMvc;

    @Inject
    private AuthenticationManager authenticationManager;

    @Autowired
    private Filter springSecurityFilterChain;

    @Inject
    private UserService userService;

    @Inject
    private SpotifyDockerService dockerService;

    private MockHttpSession session;

    private static String applicationName;

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

        try {
            logger.info("Create Tomcat server");
            final String jsonString =
                "{\"applicationName\":\"" + applicationName + "\", \"serverName\":\"" + release + "\"}";
            ResultActions resultats =
                mockMvc.perform(post("/application").session(session).contentType(MediaType.APPLICATION_JSON).content(jsonString));
            resultats.andExpect(status().isOk());

            resultats =
                mockMvc.perform(get("/application/" + applicationName).session(session).contentType(MediaType.APPLICATION_JSON));
            resultats.andExpect(jsonPath("name").value(applicationName.toLowerCase()));
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }

    @After
    public void teardown() throws Exception {
        logger.info("**********************************");
        logger.info("           teardown               ");
        logger.info("**********************************");

        logger.info("Delete application : " + applicationName);
        ResultActions resultats =
            mockMvc.perform(delete("/application/" + applicationName).session(session).contentType(MediaType.APPLICATION_JSON));
        resultats.andExpect(status().isOk());

        SecurityContextHolder.clearContext();
        session.invalidate();
    }

    @Test
    public void dir_exists() throws Exception {
        String containerId = dockerService.getContainerId("int-johndoe-"+applicationName+"-tomcat-8").substring(0, 12);
        ResultActions resultActions =
            mockMvc.perform(get("/file/container/"+containerId+"/path/__cloudunit__")
                .session(session)
                .contentType(MediaType.APPLICATION_JSON));
        resultActions = resultActions.andExpect(status().isOk());
        String content = resultActions.andReturn().getResponse().getContentAsString();
        Assert.assertTrue(content.contains("/cloudunit/appconf"));
    }

    @Test
    public void displayContentFileFromContainer() throws Exception {
        String containerId = dockerService.getContainerId("int-johndoe-"+applicationName+"-tomcat-8").substring(0, 12);
        String url = "/file/content/container/"+containerId+"/application/"+applicationName+"/path/__cloudunit__appconf__conf/fileName/context.xml";
        logger.debug(url);
        ResultActions resultats = this.mockMvc
                .perform(
                        get(url)
                                .session(session));
        String contentAsString = resultats.andReturn().getResponse().getContentAsString();
        logger.debug(contentAsString);
        resultats.andExpect(status().isOk());

        url = "/file/content/container/"+containerId+"/application/"+applicationName+"/path/__cloudunit__appconf__conf/fileName/UNDEF.xml";
        logger.debug(url);
        resultats = this.mockMvc
                .perform(
                        get(url)
                                .session(session));
        contentAsString = resultats.andReturn().getResponse().getContentAsString();
        logger.debug(contentAsString);
        Assert.assertTrue(contentAsString.contains("No such file or directory"));
    }

    @Test
    public void saveContentFileIntoContainer() throws Exception {
        String containerId = dockerService.getContainerId("int-johndoe-"+applicationName+"-tomcat-8").substring(0, 12);
        String urlGet = "/file/content/container/"+containerId+"/application/"+applicationName+"/path/__cloudunit__appconf__conf/fileName/context.xml";
        logger.debug(urlGet);
        ResultActions resultats = this.mockMvc
                .perform(
                        get(urlGet)
                                .session(session));
        String contentAsString = resultats.andReturn().getResponse().getContentAsString();
        logger.debug(contentAsString);
        resultats.andExpect(status().isOk());

        String filePath = "__cloudunit__appconf__conf";
        String fileName = "context.xml";
        final String jsonString =
                "{\"fileContent\":\"Hello\", \"filePath\":\""+filePath+"\", \"fileName\":\"" + fileName + "\"}";
        String url = "/file/content/container/"+containerId+"/application/"+applicationName;
        logger.debug(url);
        resultats = this.mockMvc
                .perform(
                        put(url)
                                .content(jsonString).contentType(MediaType.APPLICATION_JSON)
                                .session(session));
        contentAsString = resultats.andReturn().getResponse().getContentAsString();
        logger.debug(contentAsString);
        resultats.andExpect(status().isOk());

        logger.debug(urlGet);
        resultats = this.mockMvc
                .perform(
                        get(urlGet)
                                .session(session));
        contentAsString = resultats.andReturn().getResponse().getContentAsString();
        System.out.println(contentAsString);
        logger.debug(contentAsString);
        Assert.assertTrue(contentAsString.contains("Hello"));
    }


//    @Test
//    public void unzipFileIntoContainer() throws Exception {
//        String containerId = dockerService.getContainerId("int-johndoe-"+applicationName+"-tomcat-8").substring(0, 12);
//        String url = "/file/unzip/container/"+containerId+"/application/"+applicationName+"/path/__cloudunit__appconf__conf/fileName/context.xml";
//        logger.debug(url);
//        ResultActions resultats = this.mockMvc
//                .perform(
//                        put(url)
//                                .session(session));
//        String contentAsString = resultats.andReturn().getResponse().getContentAsString();
//        logger.debug(contentAsString);
//        Assert.assertTrue(contentAsString.contains("Extension is not right"));
//    }

}