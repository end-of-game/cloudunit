package fr.treeptik.cloudunit.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import fr.treeptik.cloudunit.model.User;
import fr.treeptik.cloudunit.service.GitlabService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * This class is used for all interactions with Gitlab concerning users
 * Created by angular5 on 29/04/16.
 */

@Service
public class GitlabServiceImpl implements GitlabService {

    private final Logger logger = LoggerFactory.getLogger(GitlabServiceImpl.class);

    @Value("${gitlab.token}")
    private String gitlabToken;

    private String gitlabAPI = "192.168.50.4:480";

    /**
     * Create an user on Gitlab
     *
     * @param user
     * @return
     */
    public HttpStatus createUser(User user) {
        logger.info("GitlabService : createUser " + user.getLogin());

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
            URL urlPost = new URL("http://" + gitlabAPI + "/api/v3/users?private_token=" + gitlabToken);
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
            } catch (Exception ignore) {
            }
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
        logger.info("GitlabService : deleteUser " + username);

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

            URL urlPost = new URL("http://" + gitlabAPI + "/api/v3/users/" + id + "?private_token=" + gitlabToken);
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
     * Create a project on Gitlab
     *
     * @param applicationName
     * @return
     */
    public HttpStatus createProject(String applicationName) {
        logger.info("GitlabService : createProject " + applicationName);

        if (gitlabToken == null) {
            logger.error("Cannot use this feature because no token for GitLab");
            return HttpStatus.NOT_IMPLEMENTED;
        }

        if (gitlabAPI == null) {
            logger.error("Cannot use this feature because no URL given for GitLab API");
            return HttpStatus.NOT_IMPLEMENTED;
        }

        DataOutputStream wr = null;
        HttpURLConnection connPost = null;
        HttpStatus code = HttpStatus.EXPECTATION_FAILED;
        try {
            String uri = "http://" + gitlabAPI + "/api/v3/projects?private_token=" + gitlabToken;

            URL urlPost = new URL(uri);
            connPost = (HttpURLConnection) urlPost.openConnection();
            connPost.setDoOutput(true);
            connPost.setDoInput(true);
            connPost.setRequestProperty("Content-Type", "application/json");
            connPost.setRequestProperty("Accept", "application/json");
            connPost.setRequestMethod("POST");
            connPost.connect();

            ObjectMapper mapper = new ObjectMapper();
            JsonNode rootNode = mapper.createObjectNode();
            ((ObjectNode) rootNode).put("name", applicationName);
            String jsonString = mapper.writeValueAsString(rootNode);

            wr = new DataOutputStream(connPost.getOutputStream());
            wr.writeBytes(jsonString);
            code = HttpStatus.valueOf(connPost.getResponseCode());
        } catch (IOException e) {
            logger.debug("IOException createProject : " + applicationName);
        }
        return code;
    }

    /**
     * Delete a project on Gitlab
     *
     * @param applicationName
     * @return
     */
    public HttpStatus deleteProject(String applicationName) {
        logger.info("GitlabService : deleteProject " + applicationName);

        if (gitlabToken == null) {
            logger.error("Cannot use this feature because no token for GitLab");
            return HttpStatus.NOT_IMPLEMENTED;
        }

        if (gitlabAPI == null) {
            logger.error("Cannot use this feature because no URL given for GitLab API");
            return HttpStatus.NOT_IMPLEMENTED;
        }

        DataOutputStream wr = null;
        HttpURLConnection connPost = null;
        HttpStatus code = HttpStatus.EXPECTATION_FAILED;
        try {
            int id = getIdProject(applicationName);

            URL urlPost = new URL("http://" + gitlabAPI + "/api/v3/projects/" + id + "?private_token=" + gitlabToken);
            connPost = (HttpURLConnection) urlPost.openConnection();
            connPost.setDoOutput(true);
            connPost.setDoInput(true);
            connPost.setRequestProperty("Content-Type", "application/json");
            connPost.setRequestProperty("Accept", "application/json");
            connPost.setRequestMethod("DELETE");
            connPost.connect();

            ObjectMapper mapper = new ObjectMapper();
            JsonNode rootNode = mapper.createObjectNode();
            ((ObjectNode) rootNode).put("name", applicationName);
            String jsonString = mapper.writeValueAsString(rootNode);

            wr = new DataOutputStream(connPost.getOutputStream());
            wr.writeBytes(jsonString);
            code = HttpStatus.valueOf(connPost.getResponseCode());

        } catch (IOException e) {
            logger.debug("IOException createProject : " + applicationName);
        }
        return code;
    }

    /**
     * List all branches of a project on Gitlab
     *
     * @param applicationName
     * @return
     */
    public List<JsonNode> listBranches(String applicationName) {
        logger.info("GitlabService : listBranches " + applicationName);

        if (gitlabToken == null) {
            logger.error("Cannot use this feature because no token for GitLab");
            return new ArrayList<>();
        }

        if (gitlabAPI == null) {
            logger.error("Cannot use this feature because no URL given for GitLab API");
            return new ArrayList<>();
        }

        DataOutputStream wr = null;
        HttpURLConnection connPost = null;
        HttpStatus code = HttpStatus.EXPECTATION_FAILED;
        try {
            int id = getIdProject(applicationName);

            URL urlPost = new URL("http://" + gitlabAPI + "/api/v3/projects/" + id + "/repository/branches?private_token=" + gitlabToken);
            connPost = (HttpURLConnection) urlPost.openConnection();
            connPost.setDoOutput(true);
            connPost.setDoInput(true);
            connPost.setRequestProperty("Content-Type", "application/json");
            connPost.setRequestProperty("Accept", "application/json");
            connPost.setRequestMethod("GET");
            connPost.connect();

            int status = connPost.getResponseCode();
            String jsonS = "";
            if (status == 200) {
                StringBuilder sb = new StringBuilder();
                try {
                    BufferedReader br = new BufferedReader(new InputStreamReader(connPost.getInputStream()));
                    String line;
                    while ((line = br.readLine()) != null) {
                        sb.append(line + "\n");
                    }
                    br.close();
                } catch (IOException e) {
                    logger.debug("GitlabService : IOException listBranches read " + applicationName);
                }

                jsonS = sb.toString();
            } else {
                logger.error("GitlabService : status " + status);
            }

            logger.info("GitlabService : listBranches " + jsonS);


        } catch (IOException e) {
            logger.debug("IOException createProject : " + applicationName);
        }

        return new ArrayList<>();
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
            url = new URL("http://" + gitlabAPI + "/api/v3/users?private_token=" + gitlabToken);
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
            logger.error("GitlabService : status " + status);
            return -2;
        }

        ObjectMapper mapper = new ObjectMapper();
        try{
            Iterator<JsonNode> nodes = mapper.readTree(jsonS).elements();
            while(nodes.hasNext()) {
                JsonNode node = nodes.next();
                if(node.get("username").asText().equals(username)){
                    return node.get("id").asInt();
                }
            }
        } catch (IOException e) {
            logger.error("GitlabService : error getIdProject json : " + e.getLocalizedMessage());
        }

        return -1;
    }

    /**
     * Get Gitlab id of a project with his name
     *
     * @param applicationName
     * @return
     */
    public int getIdProject(String applicationName) {
        HttpURLConnection c = null;
        URL url = null;
        int status = -1;
        try {
            url = new URL("http://" + gitlabAPI + "/api/v3/projects/all?private_token=" + gitlabToken);
            c = (HttpURLConnection) url.openConnection();
            c.setRequestMethod("GET");
            c.setRequestProperty("Content-length", "0");
            c.setUseCaches(false);
            c.setAllowUserInteraction(false);
            c.connect();
            status = c.getResponseCode();
        } catch (IOException e) {
            logger.debug("IOException getIdProject get infos : " + applicationName);
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
                logger.debug("IOException getIdProject read : " + applicationName);
            }


            jsonS = sb.toString();
        } else {
            return -2;
        }

        ObjectMapper mapper = new ObjectMapper();
        try{
            Iterator<JsonNode> nodes = mapper.readTree(jsonS).elements();
            while(nodes.hasNext()) {
                JsonNode node = nodes.next();
                if(node.get("name").asText().equals(applicationName)){
                    return node.get("id").asInt();
                }
            }
        } catch (IOException e) {
            logger.error("GitlabService : error getIdProject json : " + e.getLocalizedMessage());
        }

        return -1;
    }

    /**
     * Get Git repository address of a project for Jenkins initialization
     *
     * @param applicationName
     * @return
     */
    public String getGitRepository(String applicationName) {
        logger.info("GitlabService : getGitRepository " + applicationName);

        if (gitlabToken == null) {
            logger.error("Cannot use this feature because no token for GitLab");
            return "";
        }

        if (gitlabAPI == null) {
            logger.error("Cannot use this feature because no URL given for GitLab API");
            return "";
        }

        HttpURLConnection connPost = null;
        int status = -1;
        try {
            int id = getIdProject(applicationName);

            URL urlPost = new URL("http://" + gitlabAPI + "/api/v3/projects/" + id + "?private_token=" + gitlabToken);
            connPost = (HttpURLConnection) urlPost.openConnection();
            connPost.setDoOutput(true);
            connPost.setDoInput(true);
            connPost.setRequestProperty("Content-Type", "application/json");
            connPost.setRequestProperty("Accept", "application/json");
            connPost.setRequestMethod("GET");
            connPost.connect();
            status = connPost.getResponseCode();
        } catch (IOException e) {
            logger.debug("IOException createProject : " + applicationName);
        }

        String jsonS;
        if (status == 200) {
            StringBuilder sb = new StringBuilder();
            try {
                BufferedReader br = new BufferedReader(new InputStreamReader(connPost.getInputStream()));
                String line;
                while ((line = br.readLine()) != null) {
                    sb.append(line + "\n");
                }
                br.close();
            } catch (IOException e) {
                logger.debug("IOException getIdProject read : " + applicationName);
            }
            jsonS = sb.toString();
        } else {
            logger.error("GitlabService : getGitRepository error " + status);
            return "";
        }

        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode node = mapper.readTree(jsonS);
            if(!node.get("http_url_to_repo").equals(null))
                return node.get("http_url_to_repo").asText();
            else
                logger.error("GitlabService : Repository doesn't exist");

        } catch (IOException e) {
            logger.error("GitlabService : getGitRepository error " + e.getLocalizedMessage());
        }


        return "";
    }
}
