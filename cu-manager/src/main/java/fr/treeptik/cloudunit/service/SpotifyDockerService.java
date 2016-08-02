package fr.treeptik.cloudunit.service;

import fr.treeptik.cloudunit.exception.CheckException;
import fr.treeptik.cloudunit.exception.ServiceException;

/**
 * Created by guillaume on 01/08/16.
 */
public interface SpotifyDockerService {

     String exec(String containerName, String command) throws CheckException, ServiceException;

     Boolean isRunning(String containerName) throws CheckException, ServiceException;

     String getContainerId(String containerName) throws CheckException, ServiceException;
}
