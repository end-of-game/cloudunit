package fr.treeptik.cloudunit.deployments;

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

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.servlet.Filter;
import java.util.Random;

import static fr.treeptik.cloudunit.utils.TestUtils.downloadAndPrepareFileToDeploy;
import static fr.treeptik.cloudunit.utils.TestUtils.getUrlContentPage;
import static org.hamcrest.Matchers.containsString;
import static org.junit.Assert.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(classes = {CloudUnitApplicationContext.class, MockServletContext.class})
@ActiveProfiles("integration")
public abstract class AbstractTomcatDeploymentControllerTestIT

{
    private static String applicationName;

    private final Logger logger = LoggerFactory.getLogger(AbstractTomcatDeploymentControllerTestIT.class);

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
    public void init() {
        if (subdomain != null) {
            domain = subdomain + domainSuffix;
        } else {
            domain = domainSuffix;
        }
    }

    @BeforeClass
    public static void initEnv() {
        applicationName = "App" + new Random().nextInt(100000);
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

    @Test
    public void test_deploySimpleWithoutContextApplicationTest() throws Exception {
        deploySimpleApplicationTest("ROOT.war", "/");
    }

    @Test
    public void test_deploySimpleWithContextApplicationTest() throws Exception {
        deploySimpleApplicationTest("helloworld.war", "/helloworld");
    }

    @Test
    public void test_deployMysql55_BasedApplicationTest()
            throws Exception {
        deployApplicationWithModule("mysql-5-5", "pizzashop-mysql", "Pizzas");
    }

    @Test
    public void test_deployMysql56_BasedApplicationTest()
            throws Exception {
        deployApplicationWithModule("mysql-5-6", "pizzashop-mysql", "Pizzas");
    }

    @Test
    public void test_deployMysql57_BasedApplicationTest()
            throws Exception {
        deployApplicationWithModule("mysql-5-7", "pizzashop-mysql", "Pizzas");
    }

    @Test
    public void test_deployPostGres93BasedApplicationTest()
            throws Exception {
        deployApplicationWithModule("postgresql-9-3", "pizzashop-postgres", "Pizzas");
    }

    @Test
    public void test_deployPostGres94BasedApplicationTest()
            throws Exception {
        deployApplicationWithModule("postgresql-9-4", "pizzashop-postgres", "Pizzas");
    }

    @Test
    public void test_deployPostGres95BasedApplicationTest()
            throws Exception {
        deployApplicationWithModule("postgresql-9-5", "pizzashop-postgres", "Pizzas");
    }

    private void deployApplicationWithModule(String module, String appName, String keywordInPage)
            throws Exception {
        createApplication();
        try {
            // add the module before deploying war
            ResultActions resultats = addModule(module);
            resultats.andExpect(status().isOk());

            // deploy the war
            logger.info("Deploy an " + module + " based application");
            resultats = deployApp(appName);

            // test the application content page
            resultats.andExpect(status().is2xxSuccessful());
            String urlToCall = String.format("http://%s-johndoe-admin%s/%s",
                    applicationName.toLowerCase(),
                    domain, appName);

            String contentPage = getUrlContentPage(urlToCall);
            assertThat(contentPage, containsString(keywordInPage));

            // remove the module
            resultats = removeModule(module);
            resultats.andExpect(status().isOk());
        } finally {
            deleteApplication();
        }
    }

    private void deploySimpleApplicationTest(String archiveName, String context)
            throws Exception {
        createApplication();
        try {
            logger.info("Deploy an helloworld application");
            deployArchive(
                    archiveName,
                    "https://github.com/Treeptik/CloudUnit/releases/download/1.0/" + archiveName);

            String urlToCall = String.format("http://%s-johndoe-admin%s/%s",
                    applicationName.toLowerCase(),
                    domain, context);
            String content = getUrlContentPage(urlToCall);
            assertThat(content, containsString("CloudUnit PaaS"));
        } finally {
            deleteApplication();
        }
    }

    private ResultActions deployApp(String appName) throws Exception {
        logger.info("Deploy application : " + appName);
        return mockMvc.perform(MockMvcRequestBuilders.fileUpload("/application/" + applicationName + "/deploy")
                .file(downloadAndPrepareFileToDeploy(appName + ".war", "https://github.com/Treeptik/CloudUnit/releases/download/1.0/" + appName + ".war"))
                .session(session).contentType(MediaType.MULTIPART_FORM_DATA))
                .andDo(print());
    }

    private ResultActions removeModule(String module) throws Exception {
        logger.info("Remove module : " + module);
        return mockMvc.perform(delete("/module/" + applicationName + "/" + module).session(session).contentType(MediaType.APPLICATION_JSON)).andDo(print());
    }

    private ResultActions addModule(String module) throws Exception {
        logger.info("Add module : " + module);
        String jsonString = "{\"applicationName\":\"" + applicationName + "\", \"imageName\":\"" + module + "\"}";
        return mockMvc.perform(post("/module").session(session).contentType(MediaType.APPLICATION_JSON).content(jsonString)).andDo(print());
    }

    private ResultActions createApplication() throws Exception {
        logger.info("Create Tomcat server");
        String jsonString = "{\"applicationName\":\"" + applicationName + "\", \"serverName\":\"" + release + "\"}";
        return mockMvc.perform(post("/application").session(session).contentType(MediaType.APPLICATION_JSON).content(jsonString));
    }

    private ResultActions deployArchive(String nameArchive, String urlArchive) throws Exception {
        logger.info("Deploy archive : " + nameArchive);
        return mockMvc.perform(MockMvcRequestBuilders.fileUpload("/application/" + applicationName + "/deploy")
                .file(downloadAndPrepareFileToDeploy(nameArchive, urlArchive)).
                        session(session).contentType(MediaType.MULTIPART_FORM_DATA)).andDo(print());
    }

    private ResultActions deleteApplication()
            throws Exception {
        logger.info("Delete application : " + applicationName);
        return mockMvc.perform(delete("/application/" + applicationName).session(session).contentType(MediaType.APPLICATION_JSON));
    }

}
