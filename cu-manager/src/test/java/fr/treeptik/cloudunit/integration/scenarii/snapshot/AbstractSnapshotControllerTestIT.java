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

package fr.treeptik.cloudunit.integration.scenarii.snapshot;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Random;

import javax.inject.Inject;
import javax.servlet.Filter;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
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

import fr.treeptik.cloudunit.exception.ServiceException;
import fr.treeptik.cloudunit.initializer.CloudUnitApplicationContext;
import fr.treeptik.cloudunit.model.User;
import fr.treeptik.cloudunit.service.UserService;

/**
 * Created by nicolas on 08/09/15.
 */
@RunWith( SpringJUnit4ClassRunner.class )
@WebAppConfiguration
@ContextConfiguration( classes = { CloudUnitApplicationContext.class, MockServletContext.class } )
@FixMethodOrder( MethodSorters.NAME_ASCENDING )
@ActiveProfiles( "integration" )
public abstract class AbstractSnapshotControllerTestIT
{

    private final Logger logger = LoggerFactory.getLogger( AbstractSnapshotControllerTestIT.class );

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

    private static boolean isAppCreated = false;

    @BeforeClass
    public static void initEnv()
    {
        applicationName = "App" + new Random().nextInt( 1000 );
    }

    @Before
    public void setup()
    {
        logger.info( "setup" );

        this.mockMvc = MockMvcBuilders.webAppContextSetup( context ).addFilters( springSecurityFilterChain ).build();

        User user = null;
        try
        {
            user = userService.findByLogin( "johndoe" );
        }
        catch ( ServiceException e )
        {
            logger.error( e.getLocalizedMessage() );
        }

        Authentication authentication = new UsernamePasswordAuthenticationToken( user.getLogin(), user.getPassword() );
        Authentication result = authenticationManager.authenticate( authentication );
        SecurityContext securityContext = SecurityContextHolder.getContext();
        securityContext.setAuthentication( result );
        session = new MockHttpSession();
        session.setAttribute( HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY, securityContext );

        if ( !isAppCreated )
        {
            try
            {
                logger.info( "Create Tomcat server" );
                String jsonString =
                    "{\"applicationName\":\"" + applicationName + "\", \"serverName\":\"" + release + "\"}";
                ResultActions resultats =
                    mockMvc.perform( post( "/application" ).session( session ).contentType( MediaType.APPLICATION_JSON ).content( jsonString ) );
                resultats.andExpect( status().isOk() );
                isAppCreated = true;
            }
            catch ( Exception e )
            {
                logger.error( e.getMessage() );
            }
        }
    }

    @After
    public void teardown()
    {
        logger.info( "teardown" );

        SecurityContextHolder.clearContext();
        session.invalidate();
    }

    @Test( timeout = 240000 )
    public void test010_CreateSimpleApplicationSnapshot()
        throws Exception
    {
        logger.info( "Create an application then snapshot it" );

        final String jsonString =
            "{\"applicationName\":\"" + applicationName + "\", \"tag\":\"" + tagName
                + "\", \"description\":\"This is a test snapshot\"}";

        logger.info( jsonString );

        ResultActions resultats =
            this.mockMvc.perform( post( "/snapshot" ).session( session ).contentType( MediaType.APPLICATION_JSON ).content( jsonString ) ).andDo( print() );
        resultats = resultats.andExpect( status().isOk() );

    }

    @Test( timeout = 120000 )
    public void test020_listAllTags()
        throws Exception
    {
        logger.info( "Create an application then snapshot it" );

        ResultActions resultats = this.mockMvc.perform( get( "/snapshot/list" ).session( session ) ).andDo( print() );
        resultats =
            resultats.andExpect( status().isOk() ).andExpect( jsonPath( "$[0].tag" ).value( tagName.toLowerCase() ) );

    }

    @Test( timeout = 120000 )
    public void test030_deleteASnapshot()
        throws Exception
    {
        logger.info( "Create an application then snapshot it" );

        ResultActions resultats = this.mockMvc.perform( delete( "/snapshot" ).session( session ) ).andDo( print() );
        resultats = resultats.andExpect( status().isOk() );

    }

    // @Test( timeout = 120000 )
    public void test030_CloneFromASimpleApplicationSnapshot()
        throws Exception
    {
        logger.info( "Create an application then snapshot it" );

        final String jsonString =
            "{\"applicationName\":\"" + applicationName
                + "\", \"tag\":\"myTag\", \"description\":\"This is a test snapshot\"}";

        logger.info( jsonString );

        ResultActions resultats =
            this.mockMvc.perform( post( "/snapshot" ).session( session ).contentType( MediaType.APPLICATION_JSON ).content( jsonString ) ).andDo( print() );
        resultats = resultats.andExpect( status().isOk() );

    }

    @Test( timeout = 30000 )
    public void test09_DeleteApplication()
        throws Exception
    {
        logger.info( "Delete application : " + applicationName );
        ResultActions resultats =
            this.mockMvc.perform( delete( "/application/" + applicationName ).session( session ).contentType( MediaType.MULTIPART_FORM_DATA ) );
        resultats.andExpect( status().isOk() );
    }

}