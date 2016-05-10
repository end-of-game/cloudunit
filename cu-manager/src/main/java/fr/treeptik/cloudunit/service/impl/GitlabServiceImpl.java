package fr.treeptik.cloudunit.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import fr.treeptik.cloudunit.model.User;
import fr.treeptik.cloudunit.service.GitlabService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private final Logger logger = LoggerFactory.getLogger(GitlabServiceImpl.class);

    @Value("${gitlab.token}")
    private String gitlabToken;

    @Value("${gitlab.api}")
    private String gitlabAPI;

    /**
     * Create an user on Gitlab
     *
     * @param user
     * @return
     */
    public HttpStatus createUser(User user) {
        DataOutputStream wr = null;
        HttpURLConnection connPost = null;
        HttpStatus code = HttpStatus.EXPECTATION_FAILED;

        if (gitlabToken == null) {
            logger.error("Cannot use this feature because no token for GitLab");
            return HttpStatus.NOT_IMPLEMENTED;
        }

        if (gitlabAPI == null) {
            logger.error("Cannot use this feature because no URL given for GitLab API");
            return HttpStatus.NOT_IMPLEMENTED;
        }

        try {
            URL urlPost = new URL(gitlabAPI + "/api/v3/users?private_token=" + gitlabToken);
            connPost = (HttpURLConnection) urlPost.openConnection();
            connPost.setDoOutput(true);
            connPost.setDoInput(true);
            connPost.setRequestProperty("Content-Type", "application/json");
            connPost.setRequestMethod("POST");
            connPost.connect();

            ObjectMapper mapper = new ObjectMapper();
            JsonNode rootNode = mapper.createObjectNode();
            ((ObjectNode) rootNode).put("username", user.getLogin());
            ((ObjectNode) rootNode).put("name", user.getFirstName() + " " + user.getLastName());
            ((ObjectNode) rootNode).put("email", user.getEmail());
            ((ObjectNode) rootNode).put("password", user.getPassword());
            String jsonString = mapper.writeValueAsString(rootNode);

            wr = new DataOutputStream(connPost.getOutputStream());
            wr.writeBytes(jsonString);
            code = HttpStatus.valueOf(connPost.getResponseCode());

        } catch (IOException e) {
            logger.debug("IOException createUser : " + user.getLogin());
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

        if (gitlabToken == null) {
            logger.error("Cannot use this feature because no token for GitLab");
            return HttpStatus.NOT_IMPLEMENTED;
        }

        if (gitlabAPI == null) {
            logger.error("Cannot use this feature because no URL given for GitLab API");
            return HttpStatus.NOT_IMPLEMENTED;
        }


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
            logger.debug("IOException deleteUser : " + username);
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
        int status = -1;
        try {
            url = new URL("http://" + this.GITLAB_IP + "/api/v3/users?private_token=" + this.privateToken);
            c = (HttpURLConnection) url.openConnection();
            c.setRequestMethod("GET");
            c.setRequestProperty("Content-length", "0");
            c.setUseCaches(false);
            c.setAllowUserInteraction(false);
            c.connect();
            status = c.getResponseCode();
        } catch (IOException e) {
            logger.debug("IOException getIdUser get infos : " + username);
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
                logger.debug("IOException getIdUser read : " + username);
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
                    logger.debug("IOException getIdUser JSON : " + username);
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
