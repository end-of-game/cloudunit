package fr.treeptik.cloudunit.service;

import fr.treeptik.cloudunit.model.User;
import org.springframework.http.HttpStatus;

/**
 * Created by angular5 on 29/04/16.
 */
public interface GitlabService {

    public HttpStatus createUser(User user);

    public HttpStatus deleteUser(String login);
}

