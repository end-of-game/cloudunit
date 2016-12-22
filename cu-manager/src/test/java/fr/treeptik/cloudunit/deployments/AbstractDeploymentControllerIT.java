package fr.treeptik.cloudunit.deployments;

import static fr.treeptik.cloudunit.utils.TestUtils.*;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.io.IOException;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Stream;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.servlet.Filter;

import org.apache.http.ParseException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import fr.treeptik.cloudunit.exception.ServiceException;
import fr.treeptik.cloudunit.model.User;
import fr.treeptik.cloudunit.service.UserService;
import fr.treeptik.cloudunit.utils.TestUtils;

public abstract class AbstractDeploymentControllerIT {

    protected static String applicationName;
    protected final Logger logger = LoggerFactory.getLogger(AbstractDeploymentControllerIT.class);
    protected String release;
    protected MockMvc mockMvc;
    protected MockHttpSession session;
    @Autowired
    private WebApplicationContext context;
    @Inject
    private AuthenticationManager authenticationManager;
    @Autowired
    private Filter springSecurityFilterChain;
    @Inject
    private UserService userService;
    @Value("#{systemEnvironment['CU_DOMAIN']}")
    private String domainSuffix;
    @Value("#{systemEnvironment['CU_SUB_DOMAIN']}")
    private String subdomainPrefix;
    protected String domain;

    public AbstractDeploymentControllerIT() {
        super();
    }

    @PostConstruct
    public void init() {
        if (subdomainPrefix != null) {
            domain = subdomainPrefix + "." + domainSuffix;
        } else {
            domain = "." + domainSuffix;
        }
    }

    protected ResultActions deployApp(String appName) throws Exception {
        logger.info("Deploy application : " + appName);
        return mockMvc.perform(MockMvcRequestBuilders.fileUpload("/application/" + applicationName + "/deploy")
                .file(downloadAndPrepareFileToDeploy(appName + ".war", "https://github.com/Treeptik/CloudUnit/releases/download/1.0/" + appName + ".war"))
                .session(session).contentType(MediaType.MULTIPART_FORM_DATA))
                .andDo(print());
    }

    protected ResultActions removeModule(String module) throws Exception {
        logger.info("Remove module : " + module);
        return mockMvc.perform(delete("/module/" + applicationName + "/" + module).session(session).contentType(MediaType.APPLICATION_JSON)).andDo(print());
    }

    protected ResultActions addModule(String module) throws Exception {
        logger.info("Add module : " + module);
        String jsonString = "{\"applicationName\":\"" + applicationName + "\", \"imageName\":\"" + module + "\"}";
        return mockMvc.perform(post("/module").session(session).contentType(MediaType.APPLICATION_JSON).content(jsonString)).andDo(print());
    }

    protected ResultActions createApplication() throws Exception {
        logger.info("Create Tomcat server");
        String jsonString = "{\"applicationName\":\"" + applicationName + "\", \"serverName\":\"" + release + "\"}";
        return mockMvc.perform(post("/application").session(session).contentType(MediaType.APPLICATION_JSON).content(jsonString));
    }

    protected ResultActions deployArchive(String nameArchive, String urlArchive) throws Exception {
        logger.info("Deploy archive : " + nameArchive);
        return mockMvc.perform(MockMvcRequestBuilders.fileUpload("/application/" + applicationName + "/deploy")
                .file(downloadAndPrepareFileToDeploy(nameArchive, urlArchive)).
                        session(session).contentType(MediaType.MULTIPART_FORM_DATA)).andDo(print());
    }

    protected ResultActions deleteApplication() throws Exception {
        logger.info("Delete application : " + applicationName);
        return mockMvc.perform(delete("/application/" + applicationName).session(session).contentType(MediaType.APPLICATION_JSON));
    }
    
    protected Optional<String> waitForContent(String url) {
        return Stream.generate(() -> {
                    try {
                        Thread.sleep(1000);
                        return getUrlContentPage(url);
                    } catch (ParseException | IOException | InterruptedException e) {
                        return null;
                    } finally {
                        
                    }
                })
            .limit(TestUtils.NB_ITERATION_MAX)
            .filter(content -> content != null && !content.contains("404"))
            .findFirst();
    }

    @Before
    public void setup() {
        logger.info("setup");
        applicationName = "App" + new Random().nextInt(100000);
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
    public void test_deployMysql55_BasedApplicationTest() throws Exception {
        deployApplicationWithModule("mysql-5-5", "pizzashop-mysql", "Pizzas");
    }

    @Test
    public void test_deployMysql56_BasedApplicationTest() throws Exception {
        deployApplicationWithModule("mysql-5-6", "pizzashop-mysql", "Pizzas");
    }

    @Test
    public void test_deployMysql57_BasedApplicationTest() throws Exception {
        deployApplicationWithModule("mysql-5-7", "pizzashop-mysql", "Pizzas");
    }

    @Test
    public void test_deployPostGres93BasedApplicationTest() throws Exception {
        deployApplicationWithModule("postgresql-9-3", "pizzashop-postgres", "Pizzas");
    }

    @Test
    public void test_deployPostGres94BasedApplicationTest() throws Exception {
        deployApplicationWithModule("postgresql-9-4", "pizzashop-postgres", "Pizzas");
    }

    @Test
    public void test_deployPostGres95BasedApplicationTest() throws Exception {
        deployApplicationWithModule("postgresql-9-5", "pizzashop-postgres", "Pizzas");
    }

    private void deployApplicationWithModule(String module, String appName, String keywordInPage) throws Exception {
        createApplication();
        try {
            // add the module before deploying war
            addModule(module)
                    .andExpect(status().isOk());
    
            // deploy the war
            logger.info("Deploy an " + module + " based application");
            deployApp(appName)
                .andExpect(status().is2xxSuccessful());
    
            // test the application content page
            String urlToCall = String.format("http://%s-johndoe%s/%s",
                    applicationName.toLowerCase(),
                    domain, appName);
    
            Optional<String> contentPage = waitForContent(urlToCall);
            assertTrue(contentPage.isPresent());
            assertThat(contentPage.get(), containsString(keywordInPage));
    
            // remove the module
            removeModule(module)
                .andExpect(status().isOk());
        } finally {
            deleteApplication();
        }
    }

    private void deploySimpleApplicationTest(String archiveName, String context) throws Exception {
        createApplication();
        try {
            logger.info("Deploy an helloworld application");
            deployArchive(
                    archiveName,
                    "https://github.com/Treeptik/CloudUnit/releases/download/1.0/" + archiveName);
            String urlToCall = String.format("http://%s-johndoe%s/%s",
                    applicationName.toLowerCase(),
                    domain, context);
            String content = getUrlContentPage(urlToCall);
            assertThat(content, containsString("CloudUnit PaaS"));
        } finally {
            deleteApplication();
        }
    }

}