package fr.treeptik.cloudunit.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import fr.treeptik.cloudunit.dto.gitlab.GitLabUser;
import fr.treeptik.cloudunit.service.GitlabService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * This class is used for all interactions with Gitlab concerning users
 * Created by angular5 on 29/04/16.
 */

@Service
public class GitlabServiceImpl implements GitlabService {

    private static String GITLAB_IP = "192.168.50.4:480";
    private static String privateToken;
    private static String CU_BACK_LINK = "192.168.2.106:8080";

    /**
     * Get the root's private token of Gitlab for the differents methods
     */
    @Autowired
    public void getToken () {
        try {
            File file = new File("src/main/resources/application.properties");
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;
            this.privateToken = "";
            while ((line = br.readLine()) != null) {
                if(line.contains("gitlab.token"))
                    this.privateToken = line.split("=")[1];
            }
            br.close();
        } catch (IOException e) {
            System.out.println("Exception read");
        }
    }

    /**
     * Create an user on Gitlab
     *
     * @param user
     * @return
     */
    public HttpStatus createUser(GitLabUser user) {
        System.out.println("privateToken = " + this.privateToken);

        DataOutputStream wr = null;
        HttpURLConnection connPost = null;
        HttpStatus code = HttpStatus.EXPECTATION_FAILED;
        try {
            URL urlPost = new URL("http://" + this.GITLAB_IP + "/api/v3/users?private_token=" + this.privateToken);
            connPost = (HttpURLConnection) urlPost.openConnection();
            connPost.setDoOutput(true);
            connPost.setDoInput(true);
            connPost.setRequestProperty("Content-Type", "application/json");
            connPost.setRequestMethod("POST");
            connPost.connect();

            ObjectMapper mapper = new ObjectMapper();
            JsonNode rootNode = mapper.createObjectNode();
            ((ObjectNode) rootNode).put("username", user.getUsername());
            ((ObjectNode) rootNode).put("name", user.getName());
            ((ObjectNode) rootNode).put("email", user.getEmail());
            ((ObjectNode) rootNode).put("password", user.getPassword());
            String jsonString = mapper.writeValueAsString(rootNode);

            wr = new DataOutputStream(connPost.getOutputStream());
            wr.writeBytes(jsonString);
            code = HttpStatus.valueOf(connPost.getResponseCode());

        } catch (IOException e) {
        } finally {
            try {
                if (wr != null) wr.flush();
                if (wr != null) wr.close();
            } catch (Exception ignore) {}
        }
        return code;
    }

    /**
     * Delete an user on Gitlab
     *
     * @param username
     * @return
     */
    public HttpStatus deleteUser(String username) {
        HttpURLConnection connPost = null;
        HttpStatus code = HttpStatus.EXPECTATION_FAILED;
        try {

            int id = getIdUser(username);

            URL urlPost = new URL("http://" + this.GITLAB_IP + "/api/v3/users/" + id + "?private_token=" + this.privateToken);
            connPost = (HttpURLConnection) urlPost.openConnection();
            connPost.setDoOutput(true);
            connPost.setDoInput(true);
            connPost.setRequestProperty("Content-Type", "application/json");
            connPost.setRequestProperty("Accept", "application/json");
            connPost.setRequestMethod("DELETE");
            connPost.connect();

            code = HttpStatus.valueOf(connPost.getResponseCode());


        } catch (IOException e) {
            System.out.println("IOException");
        }
        return code;
    }

    /**
     * Get Gitlab id of an user with his username
     *
     * @param username
     * @return
     */
    private int getIdUser(String username) {
        HttpURLConnection c = null;
        URL url = null;
        try {
            url = new URL("http://" + this.GITLAB_IP + "/api/v3/users?private_token=" + this.privateToken);
            c = (HttpURLConnection) url.openConnection();
            c.setRequestMethod("GET");
        } catch (IOException e) {
            System.out.println("Exception Connexion 1");
        }

        c.setRequestProperty("Content-length", "0");
        c.setUseCaches(false);
        c.setAllowUserInteraction(false);

        int status = -1;

        try {
            c = (HttpURLConnection) url.openConnection();
            c.connect();
            status = c.getResponseCode();
        } catch (IOException e) {
            System.out.println("Exception Connexion 2");
        }

        String jsonS;
        if (status == 200) {
            StringBuilder sb = new StringBuilder();

            try {
                BufferedReader br = new BufferedReader(new InputStreamReader(c.getInputStream()));
                String line;
                while ((line = br.readLine()) != null) {
                    sb.append(line + "\n");
                }
                br.close();
            } catch (IOException e) {
                System.out.println("Exception read");
            }


            jsonS = sb.toString();
        } else {
            return -2;
        }

        String[] jsonA = jsonS.split("(?<=\\})");
        ObjectMapper mapper = new ObjectMapper();

        for (int i = 0; i < jsonA.length-1; i++) {
            String s = jsonA[i];
            if(s != null) {
                s = s.substring(1);

                JsonNode node = null;
                try {
                    node = mapper.readTree(s);
                } catch (IOException e) {
                    System.out.println("Exception JSON");
                }

                String localUsername = node.get("username").toString();
                String localId = node.get("id").toString();

                localUsername = localUsername.replace("\"", "");
                localId = localId.replace("\"", "");

                if (localUsername.equals(username))
                    return Integer.parseInt(localId);
            }
        }

        return -1;
    }
}
