/*
 * LICENCE : CloudUnit is available under the GNU Affero General Public License : https://gnu.org/licenses/agpl.html
 * but CloudUnit is licensed too under a standard commercial license.
 * Please contact our sales team if you would like to discuss the specifics of our Enterprise license.
 * If you are not sure whether the AGPL is right for you,
 * you can always test our software under the AGPL and inspect the source code before you contact us
 * about purchasing a commercial license.
 *
 * LEGAL TERMS : "CloudUnit" is a registered trademark of Treeptik and can't be used to endorse
 * or promote products derived from this project without prior written permission from Treeptik.
 * Products or services derived from this software may not be called "CloudUnit"
 * nor may "Treeptik" or similar confusing terms appear in their names without prior written permission.
 * For any questions, contact us : contact@treeptik.fr
 */

package fr.treeptik.cloudunit.service;

import fr.treeptik.cloudunit.exception.CheckException;
import fr.treeptik.cloudunit.exception.DockerJSONException;
import fr.treeptik.cloudunit.exception.FatalDockerJSONException;
import fr.treeptik.cloudunit.exception.ServiceException;
import fr.treeptik.cloudunit.model.Server;
import fr.treeptik.cloudunit.model.User;

import java.util.List;

/**
 * Created by Nicolas MULLER on 03/05/16.
 */
public interface DockerService {

    void createServer(String name, Server server, String imagePath, User user) throws DockerJSONException;

    Server startServer(String containerName, Server server) throws DockerJSONException;

    void stopServer(String containerName) throws DockerJSONException;

    void killServer(String containerName) throws DockerJSONException;

    void removeServer(String containerName) throws DockerJSONException;

    String execCommand(String containerName, String command) throws FatalDockerJSONException;

    String execCommand(String containerName, String[] command) throws FatalDockerJSONException;

    String getContainerId(String containerName) throws FatalDockerJSONException;

    Boolean isRunning(String containerName) throws FatalDockerJSONException;

    List<String> listContainers() throws FatalDockerJSONException;

    String getContainerNameFromId(String id) throws FatalDockerJSONException;
}

