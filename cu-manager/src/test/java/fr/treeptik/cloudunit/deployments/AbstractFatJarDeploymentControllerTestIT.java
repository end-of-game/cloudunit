package fr.treeptik.cloudunit.deployments;

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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(classes = {CloudUnitApplicationContext.class, MockServletContext.class})
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@ActiveProfiles("integration")
public abstract class AbstractFatJarDeploymentControllerTestIT

{
    private static String applicationName;

    private final Logger logger = LoggerFactory.getLogger(AbstractFatJarDeploymentControllerTestIT.class);

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
        applicationName = "App" + new Random().nextInt(100000);
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

    @Test
    public void test010_DeploySimpleApplicationTest()
            throws Exception {
        logger.info("Deploy an SpringBoot application");
        String binary = "spring-boot.jar";

        try {
            logger.info("Create " + binary + " application.");
            String jsonString = "{\"applicationName\":\"" + applicationName + "\", \"serverName\":\"" + release + "\"}";
            ResultActions resultats =
                    mockMvc.perform(post("/application").session(session).contentType(MediaType.APPLICATION_JSON).content(jsonString));
            resultats.andExpect(status().isOk());

            // OPEN THE PORT
            jsonString =
                    "{\"applicationName\":\"" + applicationName
                            + "\",\"portToOpen\":\"8080\",\"portNature\":\"web\"}";
            resultats =
                    this.mockMvc.perform(post("/application/ports")
                            .session(session)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(jsonString));
            resultats.andExpect(status().isOk()).andDo(print());
        } catch (Exception e) {
            logger.error(e.getMessage());
        }

        ResultActions resultats =
                mockMvc.perform(MockMvcRequestBuilders.fileUpload("/application/" + applicationName + "/deploy")
                        .file(downloadAndPrepareFileToDeploy(binary,
                                "https://github.com/Treeptik/CloudUnit/releases/download/1.0/" + binary))
                        .session(session).contentType(MediaType.MULTIPART_FORM_DATA)).andDo(print());
        resultats.andExpect(status().is2xxSuccessful());

        String urlToCall = "http://" + applicationName.toLowerCase() + "-johndoe-forward-8080.cloudunit.dev";
        logger.debug(urlToCall);
        int i = 0;
        String content = null;
        while(i++ < 10) {
            content = getUrlContentPage(urlToCall);
            logger.debug(content);
            Thread.sleep(1000);
            if (content != null && content.contains("502")){ continue; }
            else { break; }
        }
        logger.debug(content);
        Assert.assertTrue(content.contains("Greetings from Spring Boot!"));
    }

    @Test
    public void test020_DeployMysqlApplicationTest()
            throws Exception {
        logger.info("Deploy an Mysql SpringBoot application");
        String binary = "spring-boot-mysql.jar";

        // Create an application
        logger.info("Create " + binary + " application.");
        String jsonString = "{\"applicationName\":\"" + applicationName + "\", \"serverName\":\"" + release + "\"}";
        ResultActions resultats =
                mockMvc.perform(post("/application").session(session).contentType(MediaType.APPLICATION_JSON).content(jsonString));
        resultats.andExpect(status().isOk());

        // Open the port 8080
        jsonString =
                "{\"applicationName\":\"" + applicationName
                        + "\",\"portToOpen\":\"8080\",\"portNature\":\"web\"}";
        resultats =
                this.mockMvc.perform(post("/application/ports")
                        .session(session)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonString));
        resultats.andExpect(status().isOk()).andDo(print());

        // Add a module MYSQL
        jsonString = "{\"applicationName\":\"" + applicationName + "\", \"imageName\":\"mysql-5-5\"}";
        resultats = mockMvc.perform(post("/module")
                .session(session)
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonString));
        resultats.andExpect(status().isOk());

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
        while(i++ < 20) {
            content = getUrlContentPage(urlToCall);
            logger.debug(content);
            Thread.sleep(1000);
            if (content != null && content.contains("502")){ continue; }
            else { break; }
        }
        logger.debug(content);
        Assert.assertTrue(content.contains("CloudUnit PaaS"));

        String url2AddAnUser = "http://" + applicationName.toLowerCase()
                + "-johndoe-forward-8080.cloudunit.dev/create?email=johndoe@gmail.com&name=johndoe";

        // Add a module MYSQL
        resultats = mockMvc.perform(get(url2AddAnUser)
                .session(session)
                .contentType(MediaType.APPLICATION_JSON));
        resultats.andExpect(status().isOk());
        content = getUrlContentPage(url2AddAnUser);
        Assert.assertTrue(content.contains("User succesfully created!"));

    }

    @After
    public void deleteApplication()
            throws Exception {
        logger.info("Delete application : " + applicationName);
        ResultActions resultats =
                mockMvc.perform(delete("/application/" + applicationName).session(session).contentType(MediaType.APPLICATION_JSON));
        resultats.andExpect(status().isOk());
    }

}
