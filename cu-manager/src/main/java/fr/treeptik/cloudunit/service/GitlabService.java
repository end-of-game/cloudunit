package fr.treeptik.cloudunit.service;

import com.fasterxml.jackson.databind.JsonNode;
import fr.treeptik.cloudunit.model.User;
import org.springframework.http.HttpStatus;

import java.util.List;

/**
 * Created by angular5 on 29/04/16.
 */
public interface GitlabService {

    HttpStatus createUser(User user);

    HttpStatus deleteUser(String login);

    HttpStatus createProject(String applicationName);

    HttpStatus deleteProject(String applicationName);

    List<JsonNode> listBranches(String applicationName);

    String getGitRepository(String applicationName);
}

