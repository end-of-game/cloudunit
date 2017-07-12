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

package fr.treeptik.cloudunit.volumes;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;
import fr.treeptik.cloudunit.dto.VolumeResource;
import fr.treeptik.cloudunit.exception.CheckException;
import fr.treeptik.cloudunit.exception.ServiceException;
import fr.treeptik.cloudunit.initializer.CloudUnitApplicationContext;
import fr.treeptik.cloudunit.model.User;
import fr.treeptik.cloudunit.service.UserService;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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
@ContextConfiguration(classes = {CloudUnitApplicationContext.class, MockServletContext.class})
@ActiveProfiles("integration")
public class VolumeControllerTestIT {

    protected String release;

    private final Logger logger = LoggerFactory.getLogger(VolumeControllerTestIT.class);

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

    @Before
    public void setup() throws Exception {
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
    }

    @After
    public void teardown() throws Exception {
        logger.info("teardown");
        SecurityContextHolder.clearContext();
        session.invalidate();
    }

    /**
     * Create and delete a volume
     *
     * @throws Exception
     */
    @Test
    public void createAndDeleteVolume()
        throws Exception {

        VolumeResource volumeResource = new VolumeResource();
        volumeResource.setName("shared"+new Random().nextInt(100000));

        ResultActions resultActions = createVolume(volumeResource);
        resultActions.andExpect(status().isCreated());
        volumeResource = getVolumeResource(mapper, resultActions);

        existVolume(volumeResource);

        deleteVolume(volumeResource.getId(), HttpStatus.NO_CONTENT);
    }

    /**
    * Cannot create two volumes with same name
    *
    * @throws Exception
    */
    @Test()
    public void createTwoVolumesWithError()
            throws Exception {
        VolumeResource volumeResource = new VolumeResource();

        volumeResource.setName("shared" + new Random().nextInt(100000));
        ResultActions resultActions = createVolume(volumeResource);
        resultActions.andExpect(status().isCreated());
        // Convert the result to pojo
        volumeResource = getVolumeResource(mapper, resultActions);

        // Try to create a second volume but error
        resultActions = createVolume(volumeResource);
        resultActions.andExpect(status().is4xxClientError());

        // delete the volume
        deleteVolume(volumeResource.getId(), HttpStatus.NO_CONTENT);
    }

    /**
     * Delete a missing volume
     *
     * @throws Exception
     */
    @Test
    public void deleteMissingVolume()
            throws Exception {
        // delete the volume
        deleteVolume(Integer.MIN_VALUE, HttpStatus.BAD_REQUEST);
    }

    private void existVolume(VolumeResource volumeResource) throws Exception {
        logger.info("List Volumes");
        String composerByName = "$[?(@.name == '%s')]";
        ResultActions resultats =
                mockMvc.perform(get("/volume").session(session).contentType(MediaType.APPLICATION_JSON));
        resultats.andExpect(status().isOk());
        resultats.andExpect(jsonPath(composerByName, volumeResource.getName()).exists());
    }

    private ResultActions createVolume(VolumeResource volumeResource) throws Exception {
        // Convert the pojo to a String to use it as content for url call
        String requestJson = getString(volumeResource, mapper);

        // call the api
        logger.info("Create Volume : " + volumeResource.getName());
        ResultActions resultats =
                mockMvc.perform(post("/volume").session(session).contentType(MediaType.APPLICATION_JSON).content(requestJson));
        return resultats;
    }

    private VolumeResource getVolumeResource(ObjectMapper mapper, ResultActions resultats) throws java.io.IOException {
        String contentResult = resultats.andReturn().getResponse().getContentAsString();
        return mapper.readValue(contentResult, VolumeResource.class);
    }

    private String getString(VolumeResource volumeResource, ObjectMapper mapper) throws JsonProcessingException {
        mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
        ObjectWriter ow = mapper.writer().withDefaultPrettyPrinter();
        return ow.writeValueAsString(volumeResource);
    }

    private void deleteVolume(Integer id, HttpStatus status) throws Exception {
        logger.info("Delete Volume : " + id);
        ResultActions resultats =
                mockMvc.perform(delete("/volume/"+id).session(session).contentType(MediaType.APPLICATION_JSON));
        resultats.andExpect(status().is(status.value()));
    }


}