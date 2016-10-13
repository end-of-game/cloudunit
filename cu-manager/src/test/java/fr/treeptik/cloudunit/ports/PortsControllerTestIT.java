/*
 * LICENCE : CloudUnit is available under the GNU Affero General Public License : https://gnu.org/licenses/agpl.html
 * but CloudUnit is licensed too under a standard commercial license.
 * Please contact our sales team if you would like to discuss the specifics of our Enterprise license.
 * If you are not sure whether the AGPL is right for you,
 * you can always test our software under the AGPL and inspect the source code before you contact us
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

package fr.treeptik.cloudunit.ports;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;
import fr.treeptik.cloudunit.dto.PortResource;
import fr.treeptik.cloudunit.dto.VolumeResource;
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
@ActiveProfiles("integration")
public class PortsControllerTestIT {

    protected String release = "tomcat-8";

    private final Logger logger = LoggerFactory.getLogger(PortsControllerTestIT.class);

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

    private ObjectMapper mapper = new ObjectMapper();

    private static String applicationName;

    @BeforeClass
    public static void initEnv() {
        applicationName = "App" + new Random().nextInt(1000);
    }

    @After
    public void teardown() throws Exception {
        logger.info("teardown");
        SecurityContextHolder.clearContext();
        session.invalidate();
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
    public void test_openAndClosePort() throws Exception {
        createApplication();

        logger.info("Open custom ports !");

        PortResource port = new PortResource();
        port.setPortToOpen("6115");
        port.setAlias("access6115");
        port.setPortNature("web");
        port.setApplicationName(applicationName);
        String jsonString = getString(port, mapper);

        ResultActions resultats = openPort(jsonString);
        resultats.andExpect(status().isOk()).andDo(print());
        resultats = getApplication();
        resultats.andExpect(jsonPath("$.portsToOpen[0].port").value(6115));

        // REMOVE THE PORT
        resultats = removePort("6115");
        resultats.andExpect(status().isOk()).andDo(print());
        resultats.andExpect(jsonPath("$.portsToOpen[0]").doesNotExist());

        deleteApplication();
    }

    private ResultActions getApplication() throws Exception {
        ResultActions resultats;
        resultats =
                mockMvc.perform(get("/application/" + applicationName).session(session).contentType(MediaType.APPLICATION_JSON)).andDo(print());
        return resultats;
    }

    @Test
    public void test_addBadPort() throws Exception {
        logger.info("Add bad port values. FailCase");
        createApplication();

        PortResource port = new PortResource();
        port.setPortToOpen("-1");
        port.setAlias("access6115");
        port.setPortNature("web");
        port.setApplicationName(applicationName);
        String jsonString = getString(port, mapper);
        ResultActions resultActions = openPort(jsonString);
        resultActions.andExpect(status().is4xxClientError());

        port = new PortResource();
        port.setPortToOpen("0");
        port.setAlias("access6115");
        port.setPortNature("web");
        port.setApplicationName(applicationName);
        jsonString = getString(port, mapper);
        resultActions = openPort(jsonString);
        resultActions.andExpect(status().is4xxClientError());

        port = new PortResource();
        port.setPortToOpen("xxx");
        port.setAlias("access6115");
        port.setPortNature("web");
        port.setApplicationName(applicationName);
        jsonString = getString(port, mapper);
        resultActions = openPort(jsonString);
        resultActions.andExpect(status().is4xxClientError());

        port = new PortResource();
        port.setAlias("access6115");
        port.setPortNature("web");
        port.setApplicationName(applicationName);
        jsonString = getString(port, mapper);
        resultActions = openPort(jsonString);
        resultActions.andExpect(status().is4xxClientError());

        deleteApplication();
    }

    @Test
    public void test_addBadNature() throws Exception {
        logger.info("Add bad nature values. FailCase");
        createApplication();
        PortResource port = new PortResource();
        port.setPortToOpen("6115");
        port.setAlias("access6115");
        port.setPortNature("undef");
        port.setApplicationName(applicationName);
        String jsonString = getString(port, mapper);

        ResultActions resultActions = openPort(jsonString);
        resultActions.andExpect(status().is4xxClientError());
        deleteApplication();
    }

    @Test
    public void test21_closeFailPort() throws Exception {
        createApplication();
        ResultActions resultActions = removePort("xxx");
        resultActions.andExpect(status().is4xxClientError());
        deleteApplication();
    }

    private void createApplication() throws Exception {
        logger.info("Create Tomcat server");
        final String jsonString =
                "{\"applicationName\":\"" + applicationName + "\", \"serverName\":\"" + release + "\"}";
        ResultActions resultats =
                mockMvc.perform(post("/application").session(session).contentType(MediaType.APPLICATION_JSON).content(jsonString));
        resultats.andExpect(status().isOk());
    }

    private void deleteApplication() throws Exception {
        logger.info("Delete application : " + applicationName);
        ResultActions resultats =
                mockMvc.perform(delete("/application/" + applicationName).session(session).contentType(MediaType.APPLICATION_JSON));
        resultats.andExpect(status().isOk());
    }

    private ResultActions openPort(String jsonString) throws Exception {
        ResultActions resultats =
                this.mockMvc.perform(post("/application/ports")
                        .session(session)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonString));
        return resultats;
    }

    private ResultActions removePort(String port) throws Exception {
        ResultActions resultats =
                this.mockMvc.perform(delete("/application/" + applicationName + "/ports/"+port)
                        .session(session)
                        .contentType(MediaType.APPLICATION_JSON));
        return resultats;
    }

    private String getString(PortResource portResource, ObjectMapper mapper) throws JsonProcessingException {
        mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
        ObjectWriter ow = mapper.writer().withDefaultPrettyPrinter();
        return ow.writeValueAsString(portResource);
    }

}