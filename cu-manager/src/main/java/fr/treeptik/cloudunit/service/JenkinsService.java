package fr.treeptik.cloudunit.service;

import fr.treeptik.cloudunit.dto.jenkins.JenkinsUser;

/**
 * Created by angular5 on 03/05/16.
 */
public interface JenkinsService {

    public void addUser(JenkinsUser user);

    public void deleteUser(String username);
}
