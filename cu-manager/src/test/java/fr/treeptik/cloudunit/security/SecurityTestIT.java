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

package fr.treeptik.cloudunit.security;

import fr.treeptik.cloudunit.exception.ServiceException;
import fr.treeptik.cloudunit.initializer.CloudUnitApplicationContext;
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
import org.springframework.test.annotation.DirtiesContext;
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
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
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

/**
 * This scenario is to verify the protection about resouces between users
 * If an UserA creates an application, UserB should not modify it.
 * We tests between the profils the security for each route.
 *
 */

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@ActiveProfiles("integration")
@DirtiesContext
public class SecurityTestIT extends TestCase {

    private final Logger logger = LoggerFactory
        .getLogger(SecurityTestIT.class);

    @Autowired
    private WebApplicationContext context;

    private MockMvc mockMvc;

    @Inject
    private AuthenticationManager authenticationManager;

    @Autowired
    private Filter springSecurityFilterChain;

    @Inject
    private UserService userService;

    private MockHttpSession session1;
    private MockHttpSession session2;

    private static String applicationName;

    // Persist the context for user1
    private User user1 = null;

    @BeforeClass
    public static void initEnv() {
        applicationName = "App"+new Random().nextInt(1000);
    }

    @Before
    public void setup() {
        logger.info("*********************************");
        logger.info("             setup               ");
        logger.info("*********************************");

        this.mockMvc = MockMvcBuilders.webAppContextSetup(context)
                .addFilters(springSecurityFilterChain)
                .build();

        // If user1 is null (first test) we create its session and its application
        try {
            logger.info("Create session for user1 : " + user1);
            // we affect the user to skip this branch too
            User user1 = userService.findByLogin("usertest1");
            Authentication authentication = new UsernamePasswordAuthenticationToken(user1.getLogin(), user1.getPassword());
            Authentication result = authenticationManager.authenticate(authentication);
            SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
            securityContext.setAuthentication(result);
            session1 = new MockHttpSession();
            session1.setAttribute(
                HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY,
                securityContext);
        } catch (ServiceException e) {
            logger.error(e.getLocalizedMessage());
        }

        // After the first tests, all others are for User2
        try {
            logger.info("Create session for user2");
            User user2 = userService.findByLogin("usertest2");
            Authentication authentication = new UsernamePasswordAuthenticationToken(user2.getLogin(), user2.getPassword());
            Authentication result = authenticationManager.authenticate(authentication);
            SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
            securityContext.setAuthentication(result);
            session2 = new MockHttpSession();
            session2.setAttribute(
                HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY,
                securityContext);
        } catch (ServiceException e) {
            logger.error(e.getLocalizedMessage());
        }


    }

    @After
    public void teardown() {
        logger.info("*********************************");
        logger.info("             teardown            ");
        logger.info("*********************************");
        SecurityContextHolder.clearContext();
        session1.invalidate();
        session2.invalidate();
    }

    /**
     * The First test 00 is only for User1
     * @throws Exception
     */
    @Test
    public void test00_createApplicationUser1() throws Exception {
        logger.info("*********************************");
        logger.info(" Create an application for User1 ");
        logger.info("*********************************");
        final String jsonString = "{\"applicationName\":\""+applicationName
                + "\", \"serverName\":\""+"tomcat-8"+"\"}";
        ResultActions resultats = this.mockMvc
                .perform(
                    post("/application").session(session1)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(jsonString));
        resultats.andExpect(status().isOk());

        logger.info("*********************************");
        logger.info(" Delete the application for User1 ");
        logger.info("*********************************");
        resultats = this.mockMvc
                .perform(
                        delete("/application/" + applicationName).session(session1)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(jsonString)).andDo(print());
        resultats.andExpect(status().isOk());
    }

    // ALL TESTS ARE FOR USER 2 NOW

    @Test
    public void test10_User2triesToManageApplicationUser1() throws Exception {
        logger.info("************************************************");
        logger.info(" User2 attemps to manage the application's User1  ");
        logger.info("************************************************");
        final String jsonString = "{\"applicationName\":\""+applicationName+"\"}";
        this.mockMvc
                .perform(
                    post("/application/stop").session(session2)
                                .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonString)).andDo(print())
                .andExpect(status().is5xxServerError());

        this.mockMvc
            .perform(
                post("/application/start").session(session2)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(jsonString)).andDo(print())
            .andExpect(status().is5xxServerError());

        this.mockMvc
            .perform(
                delete("/application/" + applicationName).session(session2)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(jsonString)).andDo(print())
            .andExpect(status().is5xxServerError());
    }

    @Test
    public void test11_User2triesToManageAliasForApplicationUser1() throws Exception {

        logger.info("************************************************");
        logger.info(" User2 attemps to manage the application's User1  ");
        logger.info("************************************************");

        String jsonString = "{\"applicationName\":\"" + applicationName + "\",\"alias\":\"myAlias\"}";
        // create the alias
        ResultActions resultats = this.mockMvc
            .perform(
                post("/application/alias").session(session2)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(jsonString)).andDo(print());
        resultats.andExpect(status().is5xxServerError());

        // delete the alias
        resultats = this.mockMvc
            .perform(
                delete("/application/" + applicationName + "/alias/myalias").session(session2)
                    .contentType(MediaType.APPLICATION_JSON)).andDo(print());
        resultats.andExpect(status().is5xxServerError());
    }

    @Test
    public void test12_User2triesToChangeConfigForApplicationUser1() throws Exception {

        logger.info("************************************************");
        logger.info(" User2 attemps to manage the application's User1  ");
        logger.info("************************************************");

        final String jsonString =
            "{\"applicationName\":\"" + applicationName
                + "\",\"jvmMemory\":\"512\",\"jvmOptions\":\"\",\"jvmRelease\":\"java8\",\"location\":\"webui\"}";
        ResultActions resultats =
            this.mockMvc.perform(put("/server/configuration/jvm").session(session2).contentType(MediaType.APPLICATION_JSON).content(jsonString)).andDo(print());
        resultats.andExpect(status().is5xxServerError());
    }

}