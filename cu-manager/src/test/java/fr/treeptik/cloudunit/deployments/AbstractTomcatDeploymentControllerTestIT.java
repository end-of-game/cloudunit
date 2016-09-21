package fr.treeptik.cloudunit.deployments;

import static fr.treeptik.cloudunit.utils.TestUtils.downloadAndPrepareFileToDeploy;
import static fr.treeptik.cloudunit.utils.TestUtils.getUrlContentPage;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import fr.treeptik.cloudunit.exception.ServiceException;
import fr.treeptik.cloudunit.initializer.CloudUnitApplicationContext;
import fr.treeptik.cloudunit.model.User;
import fr.treeptik.cloudunit.service.UserService;
import fr.treeptik.cloudunit.utils.TestUtils;
import org.junit.After;
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
import org.springframework.beans.factory.annotation.Value;
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

import java.util.Random;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.servlet.Filter;
import javax.validation.Valid;

@RunWith( SpringJUnit4ClassRunner.class )
@WebAppConfiguration
@ContextConfiguration( classes = { CloudUnitApplicationContext.class, MockServletContext.class } )
@ActiveProfiles( "integration" )
public abstract class AbstractTomcatDeploymentControllerTestIT

{
    private static String applicationName;

    private final Logger logger = LoggerFactory.getLogger( AbstractTomcatDeploymentControllerTestIT.class );

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

    @Value("${suffix.cloudunit.io}")
    private String domainSuffix;

    @Value("#{systemEnvironment['CU_SUB_DOMAIN']}")
    private String subdomain;

    private String domain;
    @PostConstruct
    public void init () {
        if (subdomain != null) {
            domain = subdomain + domainSuffix;
        } else {
            domain = domainSuffix;
        }
    }

    @BeforeClass
    public static void initEnv()
    {
        applicationName = "App" + new Random().nextInt(100000);
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

        Authentication authentication = null;
        if (user != null) {
            authentication = new UsernamePasswordAuthenticationToken( user.getLogin(), user.getPassword() );
        }
        Authentication result = authenticationManager.authenticate( authentication );
        SecurityContext securityContext = SecurityContextHolder.getContext();
        securityContext.setAuthentication( result );
        session = new MockHttpSession();
        session.setAttribute( HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY, securityContext );
    }

    @Test( timeout = 2400000 )
    public void test_deploySimpleApplicationTest()
        throws Exception {
        createApplication();
        logger.info( "Deploy an helloworld application" );
        deployArchive("helloworld.war",
                "https://github.com/Treeptik/CloudUnit/releases/download/1.0/helloworld.war",
                "CloudUnit PaaS");
        deleteApplication();
    }

    @Test( timeout = 2400000 )
    public void test020_DeployMysql55_BasedApplicationTest()
        throws Exception {
        deployApplicationWithModule( "mysql-5-5", "pizzashop-mysql", "Pizzas" );
    }

    @Test( timeout = 2400000 )
    public void test020_DeployMysql56_BasedApplicationTest()
            throws Exception
    {
        deployApplicationWithModule( "mysql-5-6", "pizzashop-mysql", "Pizzas" );
    }

    @Test( timeout = 2400000 )
    public void test020_DeployMysql57_BasedApplicationTest()
            throws Exception
    {
        deployApplicationWithModule( "mysql-5-7", "pizzashop-mysql", "Pizzas" );
    }


    @Test( timeout = 2400000 )
    public void test030_DeployPostGres93BasedApplicationTest()
        throws Exception
    {
        deployApplicationWithModule( "postgresql-9-3", "pizzashop-postgres", "Pizzas" );
    }

    @Test( timeout = 2400000 )
    public void test030_DeployPostGres94BasedApplicationTest()
            throws Exception
    {
        deployApplicationWithModule( "postgresql-9-4", "pizzashop-postgres", "Pizzas" );
    }

    @Test( timeout = 2400000 )
    public void test030_DeployPostGres95BasedApplicationTest()
            throws Exception
    {
        deployApplicationWithModule( "postgresql-9-5", "pizzashop-postgres", "Pizzas" );
    }

    private void deployApplicationWithModule(String module, String appName, String keywordInPage)
        throws Exception
    {
        createApplication();
        // add the module before deploying war
        String jsonString = "{\"applicationName\":\"" + applicationName + "\", \"imageName\":\"" + module + "\"}";
        ResultActions resultats =
            mockMvc.perform( post( "/module" ).session( session ).contentType( MediaType.APPLICATION_JSON ).content( jsonString ) ).andDo( print() );
        resultats.andExpect( status().isOk() );

        // deploy the war
        logger.info( "Deploy an " + module + " based application" );
        resultats =
            mockMvc.perform(MockMvcRequestBuilders.fileUpload("/application/" + applicationName + "/deploy")
                .file(downloadAndPrepareFileToDeploy(appName + ".war", "https://github.com/Treeptik/CloudUnit/releases/download/1.0/" + appName + ".war"))
                .session(session).contentType(MediaType.MULTIPART_FORM_DATA))
                .andDo(print());
        // test the application content page
        resultats.andExpect( status().is2xxSuccessful() );
        String urlToCall = "http://" + applicationName.toLowerCase() + "-johndoe-admin" + domain;
        String contentPage = getUrlContentPage(urlToCall);
        System.out.println(contentPage);
        Assert.assertTrue(contentPage.contains(keywordInPage));

        // remove the module
        resultats =
            mockMvc.perform( delete( "/module/" + applicationName + "/" + module).session( session ).contentType( MediaType.APPLICATION_JSON ) ).andDo( print() );
        resultats.andExpect( status().isOk() );

        deleteApplication();
    }

    private void createApplication() throws Exception {
        logger.info( "Create Tomcat server" );
        String jsonString = "{\"applicationName\":\"" + applicationName + "\", \"serverName\":\"" + release + "\"}";
        ResultActions resultats =
                mockMvc.perform( post( "/application" ).session( session ).contentType( MediaType.APPLICATION_JSON ).content( jsonString ) );
        resultats.andExpect( status().isOk() );
    }

    private void deployArchive(String nameArchive, String urlArchive, String keyword) throws Exception {
        ResultActions resultats =
                mockMvc.perform( MockMvcRequestBuilders.fileUpload( "/application/" + applicationName + "/deploy" )
                        .file( downloadAndPrepareFileToDeploy(nameArchive,urlArchive) ).
                                session( session ).contentType( MediaType.MULTIPART_FORM_DATA ) ).andDo( print() );
        resultats.andExpect( status().is2xxSuccessful() );
        String urlToCall = "http://" + applicationName.toLowerCase() + "-johndoe-admin" + domain;
        Assert.assertTrue( getUrlContentPage( urlToCall ).contains(keyword) );
    }

    private void deleteApplication()
        throws Exception
    {
        logger.info( "Delete application : " + applicationName );
        ResultActions resultats =
            mockMvc.perform( delete( "/application/" + applicationName ).session( session ).contentType( MediaType.APPLICATION_JSON ) );
        resultats.andExpect( status().isOk() );
    }

}
