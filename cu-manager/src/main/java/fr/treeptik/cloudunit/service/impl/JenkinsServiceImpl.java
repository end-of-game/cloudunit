package fr.treeptik.cloudunit.service.impl;

import fr.treeptik.cloudunit.model.User;
import fr.treeptik.cloudunit.service.JenkinsService;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.FileEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
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
            logger.info("JenkinsService : addUser " + user.getLogin());

            if (rootToken == null) {
                logger.error("Cannot use this feature because no token for Jenkins");
                return;
            }

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

            post.setHeader("Authorization", rootToken);
            post.setHeader("Content-Type", "application/x-www-form-urlencoded");

            httpclient.execute(post);
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

            DefaultHttpClient httpclient = new DefaultHttpClient();

            ArrayList<NameValuePair> parameters = new ArrayList<NameValuePair>();

            HttpPost post = new HttpPost("http://" + JENKINS_IP + "/securityRealm/user/" + username + "/doDelete");
            parameters.add(new BasicNameValuePair("json", "{}"));
            parameters.add(new BasicNameValuePair("Submit", "Oui"));

            post.setEntity(new UrlEncodedFormEntity(parameters));

            post.setHeader("Authorization", rootToken);
            post.setHeader("Content-Type", "application/x-www-form-urlencoded");

            httpclient.execute(post);
        } catch (IOException e) {
            logger.debug("JenkinsService : Exception deleteUser " + username);
        }
    }

    public void createProject(String applicationName) {
        try {
            logger.info("JenkinsService : createProject " + applicationName);

            if (rootToken == null) {
                logger.error("Cannot use this feature because no token for Jenkins");
                return;
            }

            DefaultHttpClient httpclient = new DefaultHttpClient();
            File config = new File("src/main/resources/config.xml");
            FileEntity entity = new FileEntity(config);
            
            String uri = "http://" + JENKINS_IP + "/createItem?name=" + applicationName + "&mode=hudson.model.FreeStyleProject";

            HttpPost post = new HttpPost(uri);

            post.setEntity(entity);

            post.setHeader("Authorization", rootToken);
            post.setHeader("Content-Type", "application/xml");

            httpclient.execute(post);
        } catch (IOException e) {
            logger.debug("JenkinsService : Exception createProject " + applicationName);
        }
    }

    public void deleteProject(String applicationName) {
        try {
            logger.info("JenkinsService : deleteProject " + applicationName);

            if (rootToken == null) {
                logger.error("Cannot use this feature because no token for Jenkins");
                return;
            }

            DefaultHttpClient httpclient = new DefaultHttpClient();
            File config = new File("src/main/resources/config.xml");

            String uri = "http://" + JENKINS_IP + "/job/" + applicationName + "/doDelete";

            HttpPost post = new HttpPost(uri);

            post.setHeader("Authorization", rootToken);

            httpclient.execute(post);
        } catch (IOException e) {
            logger.debug("JenkinsService : Exception deleteProject " + applicationName);
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
}
