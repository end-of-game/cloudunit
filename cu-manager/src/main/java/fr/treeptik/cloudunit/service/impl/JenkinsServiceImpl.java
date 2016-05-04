package fr.treeptik.cloudunit.service.impl;

import fr.treeptik.cloudunit.dto.jenkins.JenkinsUser;
import fr.treeptik.cloudunit.service.JenkinsService;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

/**
 * This class is used for all interactions with Jenkins concerning users
 * Created by angular5 on 03/05/16.
 */
@Service
public class JenkinsServiceImpl implements JenkinsService {

    private static String JENKINS_IP = "192.168.50.4:9080";
    private static String rootName;
    private static String rootPassword;


    /**
     * Get the root's private token of Gitlab for the differents methods
     */
    @Autowired
    public void getToken () {
        try {
            File file = new File("src/main/resources/application.properties");
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;
            this.rootName = "";
            this.rootPassword="";
            while ((line = br.readLine()) != null) {
                if(line.contains("jenkins.rootName") && line.split("=").length > 1)
                    this.rootName = line.split("=")[1];
                if(line.contains("jenkins.rootPassword") && line.split("=").length > 1)
                    this.rootPassword = line.split("=")[1];
            }
            br.close();
        } catch (IOException e) {
            System.out.println("Exception read");
        }
    }


    /**
     * Add a user to the Jenkins server
     * @param user
     */
    public void addUser(JenkinsUser user) {

        try {
            DefaultHttpClient httpclient = new DefaultHttpClient();

            ArrayList<NameValuePair> parameters = new ArrayList<NameValuePair>();

            HttpPost post = new HttpPost("http://" + JENKINS_IP + "/securityRealm/createAccountByAdmin");
            parameters.add(new BasicNameValuePair("username", user.getUsername()));
            parameters.add(new BasicNameValuePair("password1", user.getPassword()));
            parameters.add(new BasicNameValuePair("password2", user.getPassword()));
            parameters.add(new BasicNameValuePair("fullname", user.getFullname()));
            parameters.add(new BasicNameValuePair("email", user.getEmail()));
            parameters.add(new BasicNameValuePair("json", createJson(user)));
            parameters.add(new BasicNameValuePair("Submit", "Créer un utilisateur"));

            post.setEntity(new UrlEncodedFormEntity(parameters));

            post.setHeader("username", this.rootName);
            post.setHeader("password", this.rootPassword);
            post.setHeader("Authorization", "Basic S2FuMjM6amVzYWlzcGFz");
            post.setHeader("Content-Type", "application/x-www-form-urlencoded");

            HttpResponse response = httpclient.execute(post);
        } catch (IOException e) {}
    }

    /**
     * Delete a user from a Jenkins server
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

            post.setHeader("username", this.rootName);
            post.setHeader("password", this.rootPassword);
            post.setHeader("Authorization", "Basic S2FuMjM6amVzYWlzcGFz");
            post.setHeader("Content-Type", "application/x-www-form-urlencoded");

            HttpResponse response = httpclient.execute(post);
        } catch (IOException e) {}
    }

    /**
     * Create a json for create a user with user's informations
     *
     * @param user
     * @return
     */
    private String createJson(JenkinsUser user) {
        return "{\"username\": \""+ user.getUsername() + "\"," +
                " \"password1\": \"" + user.getPassword() + "\"," +
                " \"password2\": \"" + user.getPassword() + "\"," +
                " \"fullname\": \"" + user.getFullname() + "\"," +
                " \"email\": \"" + user.getEmail() + "\"}";
    }
}
