package fr.treeptik.cloudunit.controller.servers;

import fr.treeptik.cloudunit.initializer.ApplicationContext;
import fr.treeptik.cloudunit.exception.ServiceException;
import fr.treeptik.cloudunit.model.User;
import fr.treeptik.cloudunit.service.UserService;
import junit.framework.TestCase;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Created by nicolas on 08/09/15.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(classes = {
        ApplicationContext.class,
        MockServletContext.class
})
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public abstract class  AbstractApplicationControllerTest extends TestCase {

    protected String release;

    private static String SEC_CONTEXT_ATTR = HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY;

    private final Logger logger = LoggerFactory
            .getLogger(AbstractApplicationControllerTest.class);

    @Autowired
    private WebApplicationContext context;

    private MockMvc mockMvc;

    @Inject
    private AuthenticationManager authenticationManager;

    @Autowired
    private Filter springSecurityFilterChain;

    @Inject
    private UserService userService;

    private Authentication authentication;
    private MockHttpSession session;

    private static String applicationName;

    @BeforeClass
    public static void initEnv() {
        applicationName = "App"+new Random().nextInt(1000);
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

        Authentication authentication = new UsernamePasswordAuthenticationToken(user.getLogin(), user.getPassword());
        Authentication result = authenticationManager.authenticate(authentication);
        SecurityContext securityContext = SecurityContextHolder.getContext();
        securityContext.setAuthentication(result);
        session = new MockHttpSession();
        session.setAttribute(
                HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY,
                securityContext);
    }

    @Test
    public void test010_CreateApplication() throws Exception {
        logger.info("Create Tomcat server");
        final String jsonString = "{\"applicationName\":\""+applicationName
                + "\", \"serverName\":\""+release+"\"}";
        ResultActions resultats = mockMvc
                .perform(
                        post("/application").session(session)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(jsonString));
        resultats.andExpect(status().isOk());

        resultats = mockMvc
                .perform(
                        get("/application/" + applicationName).session(session)
                                .contentType(MediaType.APPLICATION_JSON));
        resultats.andDo(print()).andExpect(jsonPath("name").value(applicationName.toLowerCase()));
    }

    @After
    public void teardown() {
        logger.info("teardown");
        SecurityContextHolder.clearContext();
        session.invalidate();
    }

    /**
     * We cannot create an application with an empty name.
     * @throws Exception
     */
    @Test
    public void test011_FailCreateEmptyNameApplication() throws Exception {
        logger.info("Create application with an empty name");
        final String jsonString = "{\"applicationName\":\""+""
                + "\", \"serverName\":"+release+"\"}";
        ResultActions resultats = this.mockMvc
                .perform(
                        post("/application").session(session)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(jsonString));
        resultats.andExpect(status().is4xxClientError()).andDo(print());
    }

    /**
     * We cannot create an application with an wrong syntax name.
     * @throws Exception
     */
    @Test(timeout = 30000)
    public void test012_FailCreateWrongNameApplication() throws Exception {
        logger.info("Create application with a wrong syntax name");
        final String jsonString = "{\"applicationName\":\""+"WRONG-NAME"
                + "\", \"serverName\":\""+release+"\"}";
        ResultActions resultats = this.mockMvc
                .perform(
                        post("/application").session(session)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(jsonString));
        resultats.andExpect(status().is4xxClientError()).andDo(print());
    }

    @Test(timeout = 30000)
    public void test02_StopApplicationTest() throws Exception {
        logger.info("Stop the application : " + applicationName);
        final String jsonString = "{\"applicationName\":\""+applicationName+"\"}";
        ResultActions resultats = this.mockMvc
                .perform(
                        post("/application/stop").session(session)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(jsonString));
        resultats.andExpect(status().isOk());
    }

    @Test(timeout = 30000)
    public void test03_StartApplicationTest() throws Exception {
        logger.info("Start the application : " + applicationName);
        final String jsonString = "{\"applicationName\":\""+applicationName+"\"}";
        ResultActions resultats = this.mockMvc
                    .perform(
                            post("/application/start").session(session)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(jsonString));
        resultats.andExpect(status().isOk());
    }

    @Test(timeout = 60000)
    public void test040_ChangeJvmMemorySizeApplicationTest() throws Exception {
        logger.info("Change JVM Memory !");
        final String jsonString = "{\"applicationName\":\""+applicationName+"\",\"jvmMemory\":\"512\",\"jvmOptions\":\"\",\"jvmRelease\":\"8\",\"location\":\"webui\"}";
        ResultActions resultats = this.mockMvc
                    .perform(
                            put("/server/configuration/jvm").session(session)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(jsonString)).andDo(print());
        resultats.andExpect(status().isOk());
    }

    @Test(timeout = 30000)
    public void test041_ChangeInvalidJvmMemorySizeApplicationTest() throws Exception {
        logger.info("Change JVM Memory size with an incorrect value : number not allowed");
        final String jsonString = "{\"applicationName\":\""+applicationName+"\",\"jvmMemory\":\"666\",\"jvmOptions\":\"\",\"jvmRelease\":\"8\",\"location\":\"webui\"}";
        ResultActions resultats = this.mockMvc
            .perform(
                    put("/server/configuration/jvm").session(session)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(jsonString)).andDo(print());
        resultats.andExpect(status().is4xxClientError());
    }

    @Test(timeout = 30000)
    public void test042_ChangeIncorrectJvmMemorySizeApplicationTest() throws Exception {
        logger.info("Change JVM Memory size with an incorrect value : not a number");
        final String jsonString = "{\"applicationName\":\""+applicationName+"\",\"jvmMemory\":\"XXX\",\"jvmOptions\":\"\",\"jvmRelease\":\"8\",\"location\":\"webui\"}";
        ResultActions resultats = this.mockMvc
                .perform(
                        put("/server/configuration/jvm").session(session)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(jsonString)).andDo(print());
        resultats.andExpect(status().is4xxClientError());
    }

    @Test(timeout = 30000)
    public void test043_ChangeEmptyJvmMemorySizeApplicationTest() throws Exception {
        logger.info("Change JVM Memory size with an empty value");
        final String jsonString = "{\"applicationName\":\""+applicationName+"\",\"jvmMemory\":\"\",\"jvmOptions\":\"\",\"jvmRelease\":\"8\",\"location\":\"webui\"}";
        ResultActions resultats = this.mockMvc
                .perform(
                        put("/server/configuration/jvm").session(session)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(jsonString)).andDo(print());
        resultats.andExpect(status().is4xxClientError());
    }

    @Test(timeout = 60000)
    public void test050_ChangeJvmOptionsApplicationTest() throws Exception {
        logger.info("Change JVM Options !");
        final String jsonString = "{\"applicationName\":\""+applicationName+"\",\"jvmMemory\":\"512\",\"jvmOptions\":\"-Dkey1=value1\",\"jvmRelease\":\"8\",\"location\":\"webui\"}";
        ResultActions resultats = this.mockMvc
                .perform(
                        put("/server/configuration/jvm").session(session)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(jsonString)).andDo(print());
        resultats.andExpect(status().isOk());
    }

    @Test(timeout = 30000)
    public void test051_ChangeFailWithXmsJvmOptionsApplicationTest() throws Exception {
        logger.info("Change JVM With Xms : not allowed");
        final String jsonString = "{\"applicationName\":\""+applicationName+"\",\"jvmMemory\":\"512\",\"jvmOptions\":\"-Xms=512m\",\"jvmRelease\":\"8\",\"location\":\"webui\"}";
        ResultActions resultats = this.mockMvc
                    .perform(
                            put("/server/configuration/jvm").session(session)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(jsonString)).andDo(print());
        resultats.andExpect(status().is4xxClientError());
    }

    @Test(timeout = 30000)
    public void test09_DeleteApplication() throws Exception {
        logger.info("Delete application : " + applicationName);
        ResultActions resultats = this.mockMvc
                .perform(
                        delete("/application/" + applicationName).session(session)
                                .contentType(MediaType.MULTIPART_FORM_DATA));
        resultats.andExpect(status().isOk());
    }


}