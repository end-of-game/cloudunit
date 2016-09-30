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

package fr.treeptik.cloudunit.explorer;

import com.fasterxml.jackson.databind.ObjectMapper;
import fr.treeptik.cloudunit.dto.FileRequestBody;
import fr.treeptik.cloudunit.exception.FatalDockerJSONException;
import fr.treeptik.cloudunit.exception.ServiceException;
import fr.treeptik.cloudunit.initializer.CloudUnitApplicationContext;
import fr.treeptik.cloudunit.model.User;
import fr.treeptik.cloudunit.service.DockerService;
import fr.treeptik.cloudunit.service.UserService;
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
import java.io.File;
import java.io.FileInputStream;
import java.util.Random;

import static fr.treeptik.cloudunit.utils.TestUtils.downloadAndPrepareFileToDeploy;
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
public class FileControllerTestIT {

    protected String release = "tomcat-8";

    private final Logger logger = LoggerFactory.getLogger(FileControllerTestIT.class);

    @Autowired
    private WebApplicationContext context;

    private MockMvc mockMvc;

    @Inject
    private AuthenticationManager authenticationManager;

    @Autowired
    private Filter springSecurityFilterChain;

    @Inject
    private UserService userService;

    @Inject
    private DockerService dockerService;

    private MockHttpSession session;

    private static String applicationName;

    @BeforeClass
    public static void initEnv() {
        applicationName = "app" + new Random().nextInt(1000000);
    }

    @Before
    public void setup() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(context).addFilters(springSecurityFilterChain).build();
        User user = null;
        try {
            user = userService.findByLogin("johndoe");
        } catch (ServiceException e) {
            logger.error(e.getLocalizedMessage());
        }
        Authentication authentication = null;
        if (user != null) { authentication = new UsernamePasswordAuthenticationToken(user.getLogin(), user.getPassword()); }
        Authentication result = authenticationManager.authenticate(authentication);
        SecurityContext securityContext = SecurityContextHolder.getContext();
        securityContext.setAuthentication(result);
        session = new MockHttpSession();
        session.setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY, securityContext);
    }

    @After
    public void teardown() throws Exception {
        SecurityContextHolder.clearContext();
        session.invalidate();
    }

    @Test
    public void test_listFiles() throws Exception {
        createApplication();
        ResultActions resultActions = listFiles("/opt/cloudunit");
        String contentAsString = resultActions.andReturn().getResponse().getContentAsString();
        Assert.assertTrue(contentAsString.contains("beats-agents"));
        deleteApplication();
    }


    @Test
    public void test_uploadFile() throws Exception {
        createApplication();
        File local = new File(".");
        File fileArchive = new File(local.getAbsolutePath()
                +"/src/test/java/fr/treeptik/cloudunit/explorer/archive.zip");
        ResultActions resultActions = upload(fileArchive, "/opt/cloudunit/");
        resultActions.andExpect(status().isOk());
        resultActions = listFiles("/opt/cloudunit/");
        resultActions.andExpect(status().isOk());
        String contentAsString = resultActions.andReturn().getResponse().getContentAsString();
        Assert.assertTrue(contentAsString.contains("archive.zip"));
        deleteApplication();
    }

    @Test
    public void test_unzipFile() throws Exception {
        createApplication();
        File local = new File(".");
        File fileArchive = new File(local.getAbsolutePath()
                +"/src/test/java/fr/treeptik/cloudunit/explorer/archive.zip");
        ResultActions resultActions = upload(fileArchive, "/opt/cloudunit/");
        resultActions.andExpect(status().isOk());

        String containerId = "int-johndoe-"+applicationName+"-tomcat-8";
        String url = "/file/unzip/container/"+containerId+"/application/"+applicationName
                +"?path=/opt/cloudunit&fileName=archive.zip";
        resultActions = this.mockMvc
                .perform(
                        put(url).contentType(MediaType.APPLICATION_JSON)
                                .session(session));
        resultActions.andExpect(status().isOk());

        resultActions = listFiles("/opt/cloudunit/");
        String contentAsString = resultActions.andReturn().getResponse().getContentAsString();
        Assert.assertTrue(contentAsString.contains("CLI-GUIDE.md"));
        deleteApplication();
    }

    @Test
    public void test_displayContentFileFromContainer() throws Exception {
        createApplication();
        ResultActions resultActions = getContentAsString("/opt/cloudunit/tomcat/conf", "context.xml");
        resultActions.andExpect(status().isOk());
        String contentAsString = resultActions.andReturn().getResponse().getContentAsString();
        Assert.assertTrue("File contains keyword 'Context'", contentAsString.contains("Context"));
        deleteApplication();
    }

    @Test
    public void test_to_gather_an_wrong_file() throws Exception {
        createApplication();
        ResultActions resultActions = getContentAsString("/opt/cloudunit/tomcat/conf", "undef.xml");
        resultActions.andExpect(status().is5xxServerError());
        deleteApplication();
    }

    @Test
    public void test_saveContentFileIntoContainer() throws Exception {
        createApplication();
        String keyWord = "HelloWorld";
        String remotePath = "/opt/cloudunit/tomcat/conf";
        String remoteFile = "context.xml";
        saveContentIntoRemoteFile(remoteFile, remotePath, keyWord);
        ResultActions resultActions = getContentAsString(remotePath, remoteFile);
        resultActions.andExpect(status().isOk());
        String contentAsString = resultActions.andReturn().getResponse().getContentAsString();
        Assert.assertTrue(contentAsString.contains(keyWord));
        deleteApplication();
    }

    private void saveContentIntoRemoteFile(String fileName, String path, String content) throws Exception {
        String containerId = "int-johndoe-"+applicationName+"-tomcat-8";
        FileRequestBody body = new FileRequestBody();
        body.setFileName(fileName);
        body.setFilePath(path);
        body.setFileContent(content);
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonString = objectMapper.writeValueAsString(body);
        String url = "/file/content/container/"+containerId+"/application/"+applicationName;
        ResultActions resultats = this.mockMvc
                .perform(
                        put(url)
                                .content(jsonString).contentType(MediaType.APPLICATION_JSON)
                                .session(session));
        resultats.andExpect(status().isOk());
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

    private ResultActions upload(File localFile, String remotePath) throws Exception {
        String container = "int-johndoe-"+applicationName+"-tomcat-8";
        String url = "/file/container/"+container+"/application/"+applicationName+"?path="+remotePath;
        MockMultipartFile mockMultipartFile = new MockMultipartFile("file", localFile.getName(),
                "multipart/form-data", new FileInputStream(localFile));
        return mockMvc.perform(MockMvcRequestBuilders.fileUpload(url)
                .file(mockMultipartFile)
                .session(session).contentType(MediaType.MULTIPART_FORM_DATA))
                .andDo(print());
    }

    private ResultActions getContentAsString(String remotePath, String remoteFileName) throws Exception {
        String container = "int-johndoe-"+applicationName+"-tomcat-8";
        String url = "/file/content/container/"+container+"/application/"+applicationName
                +"?path="+remotePath
                +"&fileName="+remoteFileName;
        logger.debug(url);
        ResultActions resultats = this.mockMvc
                .perform(
                        get(url)
                                .session(session));
        return resultats;
    }

    private ResultActions listFiles(String remotePath) throws Exception {
        String container = "int-johndoe-"+applicationName+"-tomcat-8";
        String url = "/file/container/"+container+"?path="+remotePath;
        ResultActions resultats = this.mockMvc
                .perform(
                        get(url)
                                .session(session));
        return resultats;
    }

}