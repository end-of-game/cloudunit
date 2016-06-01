package fr.treeptik.cloudunit.service;

import fr.treeptik.cloudunit.model.User;

/**
 * Created by angular5 on 03/05/16.
 */
public interface JenkinsService {

    void addUser(User user);

    void deleteUser(String username);

    void createProject(String applicationName);

    void deleteProject(String applicationName);
}
