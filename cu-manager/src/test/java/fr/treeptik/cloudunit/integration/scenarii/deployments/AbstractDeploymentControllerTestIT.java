package fr.treeptik.cloudunit.integration.scenarii.deployments;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.Random;

import javax.inject.Inject;
import javax.servlet.Filter;

import org.junit.Assert;
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
import org.springframework.mock.web.MockMultipartFile;
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

import fr.treeptik.cloudunit.exception.ServiceException;
import fr.treeptik.cloudunit.initializer.CloudUnitApplicationContext;
import fr.treeptik.cloudunit.model.User;
import fr.treeptik.cloudunit.service.UserService;
import fr.treeptik.cloudunit.utils.RestUtils;

@RunWith( SpringJUnit4ClassRunner.class )
@WebAppConfiguration
@ContextConfiguration( classes = { CloudUnitApplicationContext.class, MockServletContext.class } )
@FixMethodOrder( MethodSorters.NAME_ASCENDING )
@ActiveProfiles( "integration" )
public abstract class AbstractDeploymentControllerTestIT

{
    protected String release;

    private final Logger logger = LoggerFactory.getLogger( AbstractDeploymentControllerTestIT.class );

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

    private static boolean isAppCreated = false;

    private static String applicationName;

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

    @Test( timeout = 30000 )
    public void test061_DeploySimpleApplicationTest()
        throws Exception
    {
        logger.info( "Deploy an helloworld application" );
        URL url = new URL( "https://web-actions.googlecode.com/files/helloworld.war" );
        InputStream input = url.openStream();
        File file = new File( "helloworld.war" );
        OutputStream outputStream = new FileOutputStream( file );
        int read = 0;
        byte[] bytes = new byte[1024];

        while ( ( read = input.read( bytes ) ) != -1 )
        {
            outputStream.write( bytes, 0, read );
        }
        MockMultipartFile fileToDeploy =
            new MockMultipartFile( "file", file.getName(), "multipart/form-data", new FileInputStream( file ) );

        ResultActions resultats =
            this.mockMvc.perform( MockMvcRequestBuilders.fileUpload( "/application/" + applicationName + "/deploy" ).file( fileToDeploy ).session( session ).contentType( MediaType.MULTIPART_FORM_DATA ) ).andDo( print() );
        resultats.andExpect( status().is2xxSuccessful() );
//        String response = RestUtils.sendGetCommand( "http://" + applicationName + "-johndoe-treeptik.cloudunit.dev" );
//        logger.info( "http response= " + response );
//        Assert.assertTrue( response.contains( "Helloworld" ) );

    }
}
