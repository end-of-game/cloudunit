/*
 * LICENCE : CloudUnit is available under the Gnu Public License GPL V3 : https://www.gnu.org/licenses/gpl.txt
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

package fr.treeptik.cloudunit.integration.scenarii.security;

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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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
public class SecurityTestIT extends TestCase {

    private static String SEC_CONTEXT_ATTR = HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY;

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

    private Authentication authentication;
    private MockHttpSession session;

    private static String applicationName;

    // Persist the context for user1
    private User user1 = null;

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

        // If user1 is null (first test) we create its session and its application
        if (user1 == null) {
            try {
                this.user1 = userService.findByLogin("usertest1");
                Authentication authentication = new UsernamePasswordAuthenticationToken(user1.getLogin(), user1.getPassword());
                Authentication result = authenticationManager.authenticate(authentication);
                SecurityContext securityContext = SecurityContextHolder.getContext();
                securityContext.setAuthentication(result);
                session = new MockHttpSession();
                session.setAttribute(
                        HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY,
                        securityContext);
            } catch (ServiceException e) {
                logger.error(e.getLocalizedMessage());
            }
        } else {
            // After the first tests, all others are for User2
            try {
                User user2 = userService.findByLogin("usertest2");
                Authentication authentication = new UsernamePasswordAuthenticationToken(user2.getLogin(), user2.getPassword());
                Authentication result = authenticationManager.authenticate(authentication);
                SecurityContext securityContext = SecurityContextHolder.getContext();
                securityContext.setAuthentication(result);
                session = new MockHttpSession();
                session.setAttribute(
                        HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY,
                        securityContext);
            } catch (ServiceException e) {
                logger.error(e.getLocalizedMessage());
            }
        }
    }

    @After
    public void teardown() {
        logger.info("teardown");
        SecurityContextHolder.clearContext();
        session.invalidate();
    }

    /**
     * The First test 00 is only for User1
     * @throws Exception
     */
    @Test
    public void test00_createApplicationUser1() throws Exception {
        logger.info("Create an application for User1");
        final String jsonString = "{\"applicationName\":\""+applicationName
                + "\", \"serverName\":\""+"tomcat-8"+"\"}";
        ResultActions resultats = this.mockMvc
                .perform(
                        post("/application").session(session)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(jsonString));
        resultats.andExpect(status().isOk());
    }

    // ALL TESTS ARE FOR USER 2 NOW

    @Test
    public void test10_User2triesToStopApplicationUser1() throws Exception {
        logger.info("User2 attemps to stop the application's User1");
        final String jsonString = "{\"applicationName\":\""+applicationName+"\"}";
        this.mockMvc
                .perform(
                        post("/application/stop").session(session)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(jsonString))
                .andExpect(status().is5xxServerError());
    }
}