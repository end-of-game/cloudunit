package fr.treeptik.cloudunit.integration.scenarii.deployments;

import fr.treeptik.cloudunit.exception.ServiceException;
import fr.treeptik.cloudunit.initializer.CloudUnitApplicationContext;
import fr.treeptik.cloudunit.model.User;
import fr.treeptik.cloudunit.service.UserService;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.junit.*;
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

import javax.inject.Inject;
import javax.servlet.Filter;
import java.io.*;
import java.net.URL;
import java.util.Random;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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

    @Test( timeout = 60000 )
    public void test10_DeploySimpleApplicationTest()
        throws Exception
    {
        logger.info( "Deploy an helloworld application" );
        ResultActions resultats =
            mockMvc.perform(MockMvcRequestBuilders.fileUpload("/application/" + applicationName + "/deploy").file(downloadAndPrepareFileToDeploy("https://github.com/Treeptik/CloudUnit/releases/download/0.9/helloworld.war")).session(session).contentType(MediaType.MULTIPART_FORM_DATA));
        resultats.andExpect( status().is2xxSuccessful() );
        String urlToCall = "http://" + applicationName.toLowerCase() + "-johndoe-admin.cloudunit.dev";
        Assert.assertTrue( getUrlContentPage( urlToCall ).contains( "CloudUnit PaaS" ) );
    }

    private String getUrlContentPage( String url )
        throws ParseException, IOException
    {
        HttpGet request = new HttpGet( url );
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpResponse response = httpClient.execute( request );
        HttpEntity entity = response.getEntity();

        return EntityUtils.toString( entity );
    }

    private MockMultipartFile downloadAndPrepareFileToDeploy( String path )
        throws IOException
    {
        URL url;
        OutputStream outputStream = null;
        File file = new File( "helloworld.war" );
        try
        {
            url = new URL( path );
            InputStream input = url.openStream();

            outputStream = new FileOutputStream( file );
            int read = 0;
            byte[] bytes = new byte[1024];

            while ( ( read = input.read( bytes ) ) != -1 )
            {
                outputStream.write( bytes, 0, read );
            }
        }
        catch ( IOException e )
        {
            e.printStackTrace();
        }
        finally
        {
            outputStream.close();
        }
        return new MockMultipartFile( "file", file.getName(), "multipart/form-data", new FileInputStream( file ) );

    }
}
