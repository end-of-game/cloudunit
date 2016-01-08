/*
 * LICENCE : CloudUnit is available under the Affero Gnu Public License GPL V3 : https://www.gnu.org/licenses/agpl-3.0.html
 * but CloudUnit is licensed too under a standard commercial license.
 * Please contact our sales team if you would like to discuss the specifics of our Enterprise license.
 * If you are not sure whether the GPL is right for you,
 * you can always test our software under the GPL and inspect the source code before you contact us
 * about purchasing a commercial license.
 *
 * LEGAL TERMS : "CloudUnit" is a registered trademark of Treeptik and can't be used to endorse
 * or promote products derived from this project without prior written permission from Treeptik.
 * Products or services derived from this software may not be called "CloudUnit"
 * nor may "Treeptik" or similar confusing terms appear in their names without prior written permission.
 * For any questions, contact us : contact@treeptik.fr
 */

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

package fr.treeptik.cloudunit.alias;

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
import org.springframework.test.context.jdbc.Sql;
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
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
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
@Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = "classpath:sql/beforeAliasTest.sql")
public class AliasControllerTestIT {

    protected String release = "tomcat-8";

    private final Logger logger = LoggerFactory.getLogger(AliasControllerTestIT.class);

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

    private static String applicationName1;
    private static String applicationName2;
    private static boolean isAppCreated = false;

    private final String alias = "myAlias.cloudunit.dev";

    @BeforeClass
    public static void initEnv() {
        applicationName1 = "App" + new Random().nextInt(10000);
        applicationName2 = "App" + new Random().nextInt(10000);
        if (applicationName1.equalsIgnoreCase(applicationName2)) {
            applicationName2 = "App" + new Random().nextInt(10000);
        }
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

        Authentication authentication = null;
        if (user != null) {
            authentication = new UsernamePasswordAuthenticationToken(user.getLogin(), user.getPassword());
        }
        Authentication result = authenticationManager.authenticate(authentication);
        SecurityContext securityContext = SecurityContextHolder.getContext();
        securityContext.setAuthentication(result);
        session = new MockHttpSession();
        session.setAttribute(
                HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY,
                securityContext);

        if (!isAppCreated) {
            try {
                logger.info("**********************************");
                logger.info("       Create Tomcat server       ");
                logger.info("**********************************");
                String jsonString = "{\"applicationName\":\"" + applicationName1
                        + "\", \"serverName\":\"" + release + "\"}";
                ResultActions resultats = mockMvc
                        .perform(
                                post("/application").session(session)
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(jsonString));
                resultats.andExpect(status().isOk());

                logger.info("Create Tomcat server");
                jsonString = "{\"applicationName\":\"" + applicationName2
                        + "\", \"serverName\":\"" + release + "\"}";
                resultats = mockMvc
                        .perform(
                                post("/application").session(session)
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(jsonString));
                resultats.andExpect(status().isOk());

                isAppCreated = true;

            } catch (Exception e) {
                logger.error(e.getMessage());
            }
        }
    }

    @After
    public void teardown() {
        logger.info("**********************************");
        logger.info("           teardown               ");
        logger.info("**********************************");

        SecurityContextHolder.clearContext();
        session.invalidate();
    }

    @Test(timeout = 120000)
    public void test00_createAliasTest() throws Exception {
        logger.info("*********************************************************");
        logger.info("create an alias for the application : " + applicationName1);
        logger.info("*********************************************************");
        String alias = "myapp.cloudunit.dev";
        String jsonString = "{\"applicationName\":\"" + applicationName1 + "\",\"alias\":\"" + alias + "\"}";
        ResultActions resultats = this.mockMvc
                .perform(
                        post("/application/alias").session(session)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(jsonString)).andDo(print());
        resultats.andExpect(status().isOk());

        // List the alias for the application to verify it is really created into database
        resultats = this.mockMvc
                .perform(
                        get("/application/" + applicationName1.toLowerCase() + "/alias").session(session)
                                .contentType(MediaType.APPLICATION_JSON));
        resultats.andExpect(status().isOk());
        resultats.andDo(print())
                .andExpect(jsonPath("$[0]").value(alias));

        logger.info("*********************************************************");
        logger.info("Cannot create an alias it is exists already for app : " + applicationName2);
        logger.info("*********************************************************");

        jsonString = "{\"applicationName\":\"" + applicationName2 + "\",\"alias\":\"" + alias + "\"}";
        resultats = this.mockMvc
                .perform(
                        post("/application/alias").session(session)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(jsonString)).andDo(print());
        resultats.andExpect(status().is4xxClientError());

        logger.info("*********************************************************");
        logger.info("Cannot create an alias it is exists already for app : " + applicationName1);
        logger.info("*********************************************************");

        jsonString = "{\"applicationName\":\"" + applicationName1 + "\",\"alias\":\"" + alias + "\"}";
        resultats = this.mockMvc
                .perform(
                        post("/application/alias").session(session)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(jsonString)).andDo(print());
        resultats.andExpect(status().is4xxClientError());
    }

    @Test(timeout = 60000)
    public void test10_CreationWithWrongSyntax() throws Exception {
        creationWithWrongSyntax("http://");
    }

    @Test(timeout = 60000)
    public void test11_CreationWithWrongSyntax() throws Exception {
        creationWithWrongSyntax("https://");
    }

    @Test(timeout = 60000)
    public void test12_CreationWithWrongSyntax() throws Exception {
        creationWithWrongSyntax("ftp://");
    }

    @Test(timeout = 60000)
    public void test20_CreationWithWrongSyntax() throws Exception {

        String wrongAlias = "hello://" + alias;
        String jsonString = "{\"applicationName\":\"" + applicationName1 + "\",\"alias\":\"" + wrongAlias + "\"}";
        ResultActions resultats = this.mockMvc
                .perform(
                        post("/application/alias").session(session)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(jsonString)).andDo(print());
        resultats.andExpect(status().is4xxClientError());

        wrongAlias = "error:\\" + alias;
        jsonString = "{\"applicationName\":\"" + applicationName1 + "\",\"alias\":\"" + wrongAlias + "\"}";
        resultats = this.mockMvc
                .perform(
                        post("/application/alias").session(session)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(jsonString)).andDo(print());
        resultats.andExpect(status().is4xxClientError());

        wrongAlias = ":" + alias;
        jsonString = "{\"applicationName\":\"" + applicationName1 + "\",\"alias\":\"" + wrongAlias + "\"}";
        resultats = this.mockMvc
                .perform(
                        post("/application/alias").session(session)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(jsonString)).andDo(print());
        resultats.andExpect(status().is4xxClientError());

        wrongAlias = ":" + alias;
        jsonString = "{\"applicationName\":\"" + applicationName1 + "\",\"alias\":error$cloudunit.dev\"" + wrongAlias + "\"}";
        resultats = this.mockMvc
                .perform(
                        post("/application/alias").session(session)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(jsonString)).andDo(print());
        resultats.andExpect(status().is4xxClientError());

    }

    @Test(timeout = 60000)
    public void test30_CreationThenDelete() throws Exception {

        logger.info("*********************************************************");
        logger.info("Create an alias then delete it");
        logger.info("*********************************************************");

        String jsonString = "{\"applicationName\":\"" + applicationName1 + "\",\"alias\":\"" + alias + "\"}";
        // create the alias
        ResultActions resultats = this.mockMvc
                .perform(
                        post("/application/alias").session(session)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(jsonString)).andDo(print());
        resultats.andExpect(status().isOk());

        // delete the alias
        resultats = this.mockMvc
                .perform(
                        delete("/application/" + applicationName1 + "/alias/" + alias).session(session)
                                .contentType(MediaType.APPLICATION_JSON)).andDo(print());
        resultats.andExpect(status().isOk());

        // List the alias for the application to verify it is really deleted into database
        resultats = this.mockMvc
                .perform(
                        get("/application/" + applicationName1.toLowerCase() + "/alias").session(session)
                                .contentType(MediaType.APPLICATION_JSON));
        resultats.andExpect(status().isOk());
        resultats.andDo(print())
                .andExpect(jsonPath("$[0]").doesNotExist());
    }

    @Test(timeout = 60000)
    public void test40_deleteFails() throws Exception {
        logger.info("*********************************************************");
        logger.info("Delete an unexisting alias onto an real application");
        logger.info("*********************************************************");
        // delete the alias
        ResultActions resultats = this.mockMvc
                .perform(
                        delete("/application/" + applicationName1 + "/alias/xxx").session(session)
                                .contentType(MediaType.APPLICATION_JSON)).andDo(print());
        resultats.andExpect(status().is4xxClientError());
    }

    @Test(timeout = 60000)
    public void test50_CreationThenRestartApp() throws Exception {

        logger.info("*********************************************************");
        logger.info("Create an alias then delete it");
        logger.info("*********************************************************");

        String jsonString = "{\"applicationName\":\"" + applicationName1 + "\",\"alias\":\"" + alias + "\"}";
        // create the alias
        ResultActions resultats = this.mockMvc
                .perform(
                        post("/application/alias").session(session)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(jsonString)).andDo(print());
        resultats.andExpect(status().isOk());

        jsonString = "{\"applicationName\":\"" + applicationName1 + "\"}";
        resultats =
                this.mockMvc.perform(post("/application/stop")
                        .session(session)
                        .contentType(MediaType.APPLICATION_JSON).content(jsonString));
        resultats.andExpect(status().isOk());
        resultats =
                this.mockMvc.perform(post("/application/start")
                        .session(session)
                        .contentType(MediaType.APPLICATION_JSON).content(jsonString));
        resultats.andExpect(status().isOk());
        resultats.andDo(print())
                .andExpect(jsonPath("$[0]").doesNotExist());
    }


    @Test(timeout = 60000)
    public void test90_cleanEnv() throws Exception {
        logger.info("*********************************************************");
        logger.info("Delete an unexisting alias onto an real application");
        logger.info("*********************************************************");

        ResultActions resultats =
                this.mockMvc.perform(
                        delete("/application/" + applicationName1).
                                session(session).contentType(MediaType.APPLICATION_JSON));
        resultats.andExpect(status().isOk());

        resultats =
                this.mockMvc.perform(
                        delete("/application/" + applicationName2).
                                session(session).contentType(MediaType.APPLICATION_JSON));
        resultats.andExpect(status().isOk());

    }


    // PRIVATE METHODS

    private void creationWithWrongSyntax(String prefix) throws Exception {

        logger.info("*********************************************************");
        logger.info("Create an alias with a wrong syntax : " + prefix);
        logger.info("*********************************************************");

        String wrongAlias = prefix + alias;
        final String jsonString = "{\"applicationName\":\"" + applicationName1 + "\",\"alias\":\"" + wrongAlias + "\"}";
        ResultActions resultats = this.mockMvc
                .perform(
                        post("/application/alias").session(session)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(jsonString)).andDo(print());

        resultats.andExpect(status().isOk());

        resultats = this.mockMvc
                .perform(
                        get("/application/" + applicationName1.toLowerCase() + "/alias").session(session)
                                .contentType(MediaType.APPLICATION_JSON));
        resultats.andExpect(status().isOk());
        resultats.andDo(print())
                .andExpect(jsonPath("$[0]").value(alias.toLowerCase()));

    }


}