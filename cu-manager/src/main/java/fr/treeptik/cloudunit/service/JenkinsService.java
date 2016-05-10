package fr.treeptik.cloudunit.service;

import fr.treeptik.cloudunit.model.User;

/**
 * Created by angular5 on 03/05/16.
 */
public interface JenkinsService {

    public void addUser(User user);

    public void deleteUser(String username);
}
