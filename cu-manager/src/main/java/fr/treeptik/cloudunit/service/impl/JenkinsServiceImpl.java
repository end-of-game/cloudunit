package fr.treeptik.cloudunit.service.impl;

import fr.treeptik.cloudunit.model.User;
import fr.treeptik.cloudunit.service.JenkinsService;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.FileEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

/**
 * This class is used for all interactions with Jenkins concerning users
 * Created by angular5 on 03/05/16.
 */
@Service
public class JenkinsServiceImpl implements JenkinsService {

    private static String JENKINS_IP = "192.168.50.4:9080";
    private final Logger logger = LoggerFactory
            .getLogger(JenkinsServiceImpl.class);

    @Value("${jenkins.token}")
    private String rootToken;

    private boolean jenkinsOpen;

    @Autowired
    public void init()
    {
        jenkinsOpen = testJenkins();
    }

    /**
     * Add a user to the Jenkins server
     *
     * @param user
     */
    public void addUser(User user) {
        try {
            logger.info("JenkinsService : addUser " + user.getLogin());

            if (rootToken == null) {
                logger.error("Cannot use this feature because no token for Jenkins");
                return;
            }

            if(jenkinsOpen) {
                HttpClient httpclient = HttpClientBuilder.create().build();

                ArrayList<NameValuePair> parameters = new ArrayList<NameValuePair>();

                HttpPost post = new HttpPost("http://" + JENKINS_IP + "/securityRealm/createAccountByAdmin");
                parameters.add(new BasicNameValuePair("username", user.getLogin()));
                parameters.add(new BasicNameValuePair("password1", user.getPassword()));
                parameters.add(new BasicNameValuePair("password2", user.getPassword()));
                parameters.add(new BasicNameValuePair("fullname", user.getFirstName() + " " + user.getLastName()));
                parameters.add(new BasicNameValuePair("email", user.getEmail()));
                parameters.add(new BasicNameValuePair("json", createJson(user)));
                parameters.add(new BasicNameValuePair("Submit", "Créer un utilisateur"));

                post.setEntity(new UrlEncodedFormEntity(parameters));

                post.setHeader("Authorization", rootToken);
                post.setHeader("Content-Type", "application/x-www-form-urlencoded");

                httpclient.execute(post);
            }
        } catch (IOException e) {
            logger.debug("JenkinsService : Exception addUser " + user.getLogin());
        }
    }

    /**
     * Delete a user from a Jenkins server
     *
     * @param username
     */
    public void deleteUser(String username) {
        try {
            logger.info("JenkinsService : deleteUser " + username);

            if (rootToken == null) {
                logger.error("Cannot use this feature because no token for Jenkins");
                return;
            }

            if(jenkinsOpen) {
                HttpClient httpclient = HttpClientBuilder.create().build();

                ArrayList<NameValuePair> parameters = new ArrayList<NameValuePair>();

                HttpPost post = new HttpPost("http://" + JENKINS_IP + "/securityRealm/user/" + username + "/doDelete");
                parameters.add(new BasicNameValuePair("json", "{}"));
                parameters.add(new BasicNameValuePair("Submit", "Oui"));

                post.setEntity(new UrlEncodedFormEntity(parameters));

                post.setHeader("Authorization", rootToken);
                post.setHeader("Content-Type", "application/x-www-form-urlencoded");

                httpclient.execute(post);
            }
        } catch (IOException e) {
            logger.debug("JenkinsService : Exception deleteUser " + username);
        }
    }

    /**
     * Create a job identifiying a project in Jenkins
     *
     * @param applicationName
     * @param repository
     */
    public void createProject(String applicationName, String repository) {
        try {
            logger.info("JenkinsService : createProject " + applicationName);

            if (rootToken == null) {
                logger.error("Cannot use this feature because no token for Jenkins");
                return;
            }

            if(jenkinsOpen) {
                HttpClient httpclient = HttpClientBuilder.create().build();
                File config = new File("src/main/resources/config.xml");
                if(config.exists()) {
                    PrintWriter writer = new PrintWriter(config);
                    writer.print("");
                    writer.close();
                }
                createConfigFile(repository, config);

                FileEntity entity = new FileEntity(config);

                String uri = "http://" + JENKINS_IP + "/createItem?name=" + applicationName + "&mode=hudson.model.FreeStyleProject";

                HttpPost post = new HttpPost(uri);

                post.setEntity(entity);

                post.setHeader("Authorization", rootToken);
                post.setHeader("Content-Type", "application/xml");

                httpclient.execute(post);

                config.delete();
                if(!config.exists())
                    logger.info("JenkinsService : createProject " + config.getName() + " is deleted!");
            }
        } catch (IOException e) {
            logger.debug("JenkinsService : Exception createProject " + applicationName);
        }
    }

    /**
     * Delete a job identifying a project in Jenkins
     *
     * @param applicationName
     */
    public void deleteProject(String applicationName) {
        try {
            logger.info("JenkinsService : deleteProject " + applicationName);

            if (rootToken == null) {
                logger.error("Cannot use this feature because no token for Jenkins");
                return;
            }

            if(jenkinsOpen) {
                HttpClient httpclient = HttpClientBuilder.create().build();

                String uri = "http://" + JENKINS_IP + "/job/" + applicationName + "/doDelete";

                HttpPost post = new HttpPost(uri);

                post.setHeader("Authorization", rootToken);

                httpclient.execute(post);
            }
        } catch (IOException e) {
            logger.debug("JenkinsService : Exception deleteProject " + applicationName);
        }
    }

    /**
     * Create a config file of project for initialization in Jenkins
     *
     * @param repository
     * @param config
     */
    private void createConfigFile (String repository, File config) {
        try {
            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

            Document doc = docBuilder.newDocument();
            Element rootElement = doc.createElement("project");
            doc.appendChild(rootElement);

            Element actions = doc.createElement("actions");
            rootElement.appendChild(actions);

            Element description = doc.createElement("description");
            rootElement.appendChild(description);

            Element keepDependencies = doc.createElement("keepDependencies");
            keepDependencies.appendChild(doc.createTextNode("false"));
            rootElement.appendChild(keepDependencies);

            Element properties = doc.createElement("properties");
            rootElement.appendChild(properties);

            Element scm = doc.createElement("scm");
            if(repository.equals("")) {
                scm.setAttribute("class", "hudson.scm.NullSCM");
                rootElement.appendChild(scm);
            }
            else {
                scm.setAttribute("class", "hudson.plugins.git.GitSCM");
                scm.setAttribute("plugin", "git@2.4.4");

                Element configVersion = doc.createElement("configVersion");
                configVersion.appendChild(doc.createTextNode("2"));
                scm.appendChild(configVersion);

                Element userRemoteConfigs = doc.createElement("userRemoteConfigs");
                Element userRemoteConfig = doc.createElement("hudson.plugins.git.UserRemoteConfig");
                userRemoteConfigs.appendChild(userRemoteConfig);
                scm.appendChild(userRemoteConfigs);

                Element branches = doc.createElement("branches");
                Element branchSpec = doc.createElement("hudson.plugins.git.BranchSpec");
                Element name = doc.createElement("name");
                name.appendChild(doc.createTextNode("*/master"));
                branchSpec.appendChild(name);
                branches.appendChild(branchSpec);
                scm.appendChild(branches);

                Element doGenerateSubmoduleConfigurations = doc.createElement("doGenerateSubmoduleConfigurations");
                doGenerateSubmoduleConfigurations.appendChild(doc.createTextNode("false"));
                scm.appendChild(doGenerateSubmoduleConfigurations);

                Element browser = doc.createElement("browser");
                browser.setAttribute("class", "hudson.plugins.git.browser.GitLab");
                Element url = doc.createElement("url");
                url.appendChild(doc.createTextNode(repository));
                browser.appendChild(url);
                Element version = doc.createElement("version");
                version.appendChild(doc.createTextNode("8.7"));
                browser.appendChild(version);
                scm.appendChild(browser);

                Element submoduleCfg = doc.createElement("submoduleCfg");
                submoduleCfg.setAttribute("class", "list");
                scm.appendChild(submoduleCfg);

                Element extensions = doc.createElement("extensions");
                scm.appendChild(extensions);
            }
            rootElement.appendChild(scm);

            Element canRoam = doc.createElement("canRoam");
            canRoam.appendChild(doc.createTextNode("true"));
            rootElement.appendChild(canRoam);

            Element disabled = doc.createElement("disabled");
            disabled.appendChild(doc.createTextNode("false"));
            rootElement.appendChild(disabled);

            Element blockBuildWhenDownstreamBuilding = doc.createElement("blockBuildWhenDownstreamBuilding");
            blockBuildWhenDownstreamBuilding.appendChild(doc.createTextNode("false"));
            rootElement.appendChild(blockBuildWhenDownstreamBuilding);

            Element blockBuildWhenUpstreamBuilding = doc.createElement("blockBuildWhenUpstreamBuilding");
            blockBuildWhenUpstreamBuilding.appendChild(doc.createTextNode("false"));
            rootElement.appendChild(blockBuildWhenUpstreamBuilding);

            Element triggers = doc.createElement("triggers");
            rootElement.appendChild(triggers);

            Element concurrentBuild = doc.createElement("concurrentBuild");
            concurrentBuild.appendChild(doc.createTextNode("false"));
            rootElement.appendChild(concurrentBuild);

            Element builders = doc.createElement("builders");
            rootElement.appendChild(builders);

            Element publishers = doc.createElement("publishers");
            rootElement.appendChild(publishers);

            Element buildWrappers = doc.createElement("buildWrappers");
            rootElement.appendChild(buildWrappers);

            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(config);
            transformer.transform(source, result);

        } catch (Exception e) {
            logger.error("JenkinsService : createConfigFile " + e.getLocalizedMessage());
        }

    }

    /**
     * Create a json for create a user with user's informations
     *
     * @param user
     * @return
     */
    private String createJson(User user) {
        return "{\"username\": \"" + user.getLogin() + "\"," +
                " \"password1\": \"" + user.getPassword() + "\"," +
                " \"password2\": \"" + user.getPassword() + "\"," +
                " \"fullname\": \"" + user.getFirstName() + " " + user.getLastName() + "\"," +
                " \"email\": \"" + user.getEmail() + "\"}";
    }

    /**
     * Test if we can call Jenkins url
     * 
     * @return
     */
    private Boolean testJenkins() {
        try {
            logger.info("JenkinsService : testJenkins");

            HttpClient httpclient = HttpClientBuilder.create().build();

            String uri = "http://" + JENKINS_IP + "/api/";

            HttpGet get = new HttpGet(uri);

            HttpResponse response = httpclient.execute(get);

            logger.info("JenkinsService : testJenkins " + response.getStatusLine());

            if(response.getStatusLine().getStatusCode() == 200)
                return true;
            else
                return false;

        } catch (Exception e) {
            logger.error("JenkinsService : testJenkins " + e.getLocalizedMessage());
            return false;
        }
    }
}
