package fr.treeptik.cloudunit.docker.core;

import fr.treeptik.cloudunit.docker.model.DockerContainer;
import fr.treeptik.cloudunit.docker.model.ExecBody;
import fr.treeptik.cloudunit.docker.model.ExecStartBody;
import fr.treeptik.cloudunit.docker.model.Image;
import fr.treeptik.cloudunit.dto.DockerResponse;
import fr.treeptik.cloudunit.exception.FatalDockerJSONException;

/**
 * Created by guillaume on 21/10/15.
 */
public interface DockerDriver {
    /*
    Usual methods to manipulate containers
     */

    DockerResponse find(DockerContainer container, String host) throws FatalDockerJSONException;

    DockerResponse findAll(String host) throws FatalDockerJSONException;

    DockerResponse create(DockerContainer container, String host) throws FatalDockerJSONException;

    DockerResponse start(DockerContainer container, String host) throws FatalDockerJSONException;

    DockerResponse stop(DockerContainer container, String host) throws FatalDockerJSONException;

    DockerResponse kill(DockerContainer container, String host) throws FatalDockerJSONException;

    DockerResponse remove(DockerContainer container, String host) throws FatalDockerJSONException;

    /*
    Usual methods to manipulate images and registry
     */

    DockerResponse findAnImage(Image image, String host) throws FatalDockerJSONException;

    DockerResponse commit(DockerContainer container, String host, String tag, String repository)
            throws FatalDockerJSONException;

    DockerResponse pull(String host, String tag, String repository)
            throws FatalDockerJSONException;

    DockerResponse removeImage(Image image, String host)
            throws FatalDockerJSONException;

    /*
    Advanced methods
     */

    DockerResponse execCreate(DockerContainer container, ExecBody execBody, String host)
            throws FatalDockerJSONException;

    DockerResponse execStart(String execId, ExecStartBody execStartBody, String host)
            throws FatalDockerJSONException;

}
