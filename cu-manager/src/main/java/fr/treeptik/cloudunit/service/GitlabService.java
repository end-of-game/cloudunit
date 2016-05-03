package fr.treeptik.cloudunit.service;

import fr.treeptik.cloudunit.dto.gitlab.GitLabUser;
import org.springframework.http.HttpStatus;

/**
 * Created by angular5 on 29/04/16.
 */
public interface GitlabService {

    public void getToken();

    public HttpStatus createUser(GitLabUser user);

    public HttpStatus deleteUser(String login);
}

