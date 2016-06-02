package fr.treeptik.cloudunit.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import fr.treeptik.cloudunit.model.User;
import fr.treeptik.cloudunit.service.GitlabService;
import org.gitlab.api.AuthMethod;
import org.gitlab.api.GitlabAPI;
import org.gitlab.api.TokenType;
import org.gitlab.api.models.GitlabBranch;
import org.gitlab.api.models.GitlabProject;
import org.gitlab.api.models.GitlabUser;
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

    private String gitlabAPI = "http://192.168.50.4:480";

    /**
     * Create an user on Gitlab
     *
     * @param user
     * @return
     */
    public HttpStatus createUser(User user) {
        logger.info("GitlabService : createUser " + user.getLogin());

        if (gitlabToken == null) {
            logger.error("Cannot use this feature because no token for GitLab");
            return HttpStatus.BAD_REQUEST;
        }

        if (gitlabAPI == null) {
            logger.error("Cannot use this feature because no URL given for GitLab API");
            return HttpStatus.BAD_REQUEST;
        }

        try {
            GitlabAPI api = GitlabAPI.connect(gitlabAPI, gitlabToken, TokenType.PRIVATE_TOKEN, AuthMethod.URL_PARAMETER);
            api.createUser(user.getEmail(), user.getPassword(), user.getLogin(),
                    user.getFirstName() + " " + user.getLastName(),
                    null, null, null, null, null, null, null, null, false, false, false);
            return HttpStatus.OK;
        } catch(IOException e) {
            logger.debug("IOException createUser : " + user.getLogin());
            return HttpStatus.BAD_REQUEST;
        }
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

        try {
            GitlabAPI api = GitlabAPI.connect(gitlabAPI, gitlabToken, TokenType.PRIVATE_TOKEN, AuthMethod.URL_PARAMETER);
            List<GitlabUser> users = api.getUsers();

            for(GitlabUser user : users) {
                if(user.getUsername().equals(username)) {
                    api.deleteUser(user.getId());
                    return HttpStatus.OK;
                }
            }
            return HttpStatus.NOT_FOUND;
        } catch(IOException e) {
            logger.debug("IOException createUser : " + username);
            return HttpStatus.BAD_REQUEST;
        }
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

        try {
            GitlabAPI api = GitlabAPI.connect(gitlabAPI, gitlabToken, TokenType.PRIVATE_TOKEN, AuthMethod.URL_PARAMETER);
            api.createProject(applicationName);
            return HttpStatus.OK;
        } catch (IOException e) {
            logger.debug("IOException createProject : " + applicationName);
            return HttpStatus.BAD_REQUEST;
        }
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

        try {
            GitlabAPI api = GitlabAPI.connect(gitlabAPI, gitlabToken, TokenType.PRIVATE_TOKEN, AuthMethod.URL_PARAMETER);
            List<GitlabProject> projects = api.getAllProjects();

            for (GitlabProject project : projects) {
                if(project.getName().equals(applicationName)) {
                    api.deleteProject(project.getId());
                    return HttpStatus.OK;
                }
            }
            return HttpStatus.NOT_FOUND;
        } catch (IOException e) {
            logger.debug("IOException createProject : " + applicationName);
            return HttpStatus.BAD_REQUEST;
        }
    }

    /**
     * List all branches of a project on Gitlab
     *
     * @param applicationName
     * @return
     */
    public List<GitlabBranch> listBranches(String applicationName) {
        logger.info("GitlabService : listBranches " + applicationName);

        if (gitlabToken == null) {
            logger.error("Cannot use this feature because no token for GitLab");
            return new ArrayList<>();
        }

        if (gitlabAPI == null) {
            logger.error("Cannot use this feature because no URL given for GitLab API");
            return new ArrayList<>();
        }

        try {
            GitlabAPI api = GitlabAPI.connect(gitlabAPI, gitlabToken, TokenType.PRIVATE_TOKEN, AuthMethod.URL_PARAMETER);
            List<GitlabProject> projects = api.getProjects();

            for (GitlabProject project : projects) {
                if(project.getName().equals(applicationName)) {
                    return api.getBranches(project);
                }
            }
            return new ArrayList<>();
        } catch (IOException e) {
            logger.debug("IOException createProject : " + applicationName);
            return new ArrayList<>();
        }
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

        try {
            GitlabAPI api = GitlabAPI.connect(gitlabAPI, gitlabToken, TokenType.PRIVATE_TOKEN, AuthMethod.URL_PARAMETER);
            List<GitlabProject> projects = api.getProjects();

            for (GitlabProject project : projects) {
                if(project.getName().equals(applicationName)) {
                    return project.getSshUrl();
                }
            }
            return HttpStatus.NOT_FOUND.toString();
        } catch (IOException e) {
            logger.debug("IOException createProject : " + applicationName);
            return HttpStatus.BAD_REQUEST.toString();
        }
    }
}
