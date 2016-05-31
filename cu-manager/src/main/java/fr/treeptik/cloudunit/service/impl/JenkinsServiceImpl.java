package fr.treeptik.cloudunit.service.impl;

import fr.treeptik.cloudunit.model.User;
import fr.treeptik.cloudunit.service.JenkinsService;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import java.io.IOException;
import java.io.StringWriter;
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

    /**
     * Add a user to the Jenkins server
     *
     * @param user
     */
    public void addUser(User user) {
        try {
            DefaultHttpClient httpclient = new DefaultHttpClient();

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

            post.setHeader("Token", rootToken);
            post.setHeader("Content-Type", "application/x-www-form-urlencoded");

            HttpResponse response = httpclient.execute(post);
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
            DefaultHttpClient httpclient = new DefaultHttpClient();

            ArrayList<NameValuePair> parameters = new ArrayList<NameValuePair>();

            HttpPost post = new HttpPost("http://" + JENKINS_IP + "/securityRealm/user/" + username + "/doDelete");
            parameters.add(new BasicNameValuePair("json", "{}"));
            parameters.add(new BasicNameValuePair("Submit", "Oui"));

            post.setEntity(new UrlEncodedFormEntity(parameters));

            post.setHeader("Token", rootToken);
            post.setHeader("Content-Type", "application/x-www-form-urlencoded");

            HttpResponse response = httpclient.execute(post);
        } catch (IOException e) {
            logger.debug("JenkinsService : Exception deleteUser " + username);
        }
    }

    public void createProject(String name) {
        /*JenkinsServer jenkins = null;
        try {
           jenkins = new JenkinsServer(new URI("http://" + JENKINS_IP), rootName, rootPassword);
           jenkins.createJob(name, createXmlConfigFile());
        } catch (URISyntaxException e) {
            logger.error("JenkinsService : Error connecting to jenkins " + e.getLocalizedMessage());
        } catch (IOException e) {
            logger.error("JenkinsService : Error creating xml config string " + e.getLocalizedMessage());
        }*/
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

    public String createXmlConfigFile() {
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        Document doc = null;
        try {
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            doc = dBuilder.newDocument();
        } catch (Exception e) {
            logger.error("JenkinsService/createXmlConfigFile : Error creating Document : " + e.getLocalizedMessage());
        }

        Element rootElement = doc.createElement("project");
        doc.appendChild(rootElement);

        Element keepDependencies = doc.createElement("keepDependencies");
        keepDependencies.appendChild(doc.createTextNode("false"));
        rootElement.appendChild(keepDependencies);

        Element properties = doc.createElement("properties");
        rootElement.appendChild(properties);

        Element scm = doc.createElement("scm");
        scm.setAttribute("class", "hudson.scm.NullSCM");
        rootElement.appendChild(scm);

        Element canRoam = doc.createElement("canRoam");
        canRoam.appendChild(doc.createTextNode("false"));
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
        disabled.appendChild(doc.createTextNode("false"));
        rootElement.appendChild(concurrentBuild);

        Element builders = doc.createElement("builders");
        rootElement.appendChild(builders);

        Element publishers = doc.createElement("publishers");
        rootElement.appendChild(publishers);

        Element buildWrappers = doc.createElement("buildWrappers");
        rootElement.appendChild(buildWrappers);

        String output = "";
        try {
            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            StringWriter writer = new StringWriter();
            transformer.transform(new DOMSource(doc), new StreamResult(writer));
            output = writer.getBuffer().toString();
        } catch (Exception e) {
            logger.error("JenkinsService/createXmlConfigFile : Error tranform xml string : " + e.getLocalizedMessage());
        }

        return output;
    }
}
