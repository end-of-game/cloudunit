package fr.treeptik.cloudunit.docker.core;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import fr.treeptik.cloudunit.docker.builders.ExecBodyBuilder;
import fr.treeptik.cloudunit.docker.builders.ExecStartBodyBuilder;
import fr.treeptik.cloudunit.docker.model.DockerContainer;
import fr.treeptik.cloudunit.docker.model.ExecBody;
import fr.treeptik.cloudunit.docker.model.ExecStartBody;
import fr.treeptik.cloudunit.docker.model.Image;
import fr.treeptik.cloudunit.dto.DockerResponse;
import fr.treeptik.cloudunit.exception.DockerJSONException;
import fr.treeptik.cloudunit.exception.ErrorDockerJSONException;
import fr.treeptik.cloudunit.exception.FatalDockerJSONException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;

/**
 * Created by guillaume on 21/10/15.
 */
public class DockerClient {

    private Logger logger = LoggerFactory.getLogger(DockerClient.class);

    private DockerDriver driver;

    private String defaultHost;

    private String registryHost;

    private ObjectMapper objectMapper = new ObjectMapper();

    /**
     * @param container
     * @param host
     * @return
     * @throws DockerJSONException
     */
    public DockerContainer findContainer(DockerContainer container, String host) throws DockerJSONException {
        logger.info("The client attempts to find a container...");
        try {
            DockerResponse dockerResponse = driver.find(container, host);
            handleDockerAPIError(dockerResponse);
            container = objectMapper.readValue(dockerResponse.getBody(), DockerContainer.class);
        } catch (FatalDockerJSONException | IOException e) {
            throw new DockerJSONException(e.getMessage(), e);
        }
        return container;
    }

    /**
     * @param container
     * @return
     * @throws DockerJSONException
     */
    public DockerContainer findContainer(DockerContainer container) throws DockerJSONException {
        logger.info("The client attempts to find a container...");
        try {
            DockerResponse dockerResponse = driver.find(container, defaultHost);
            handleDockerAPIError(dockerResponse);
            container = objectMapper.readValue(dockerResponse.getBody(), DockerContainer.class);
        } catch (FatalDockerJSONException | IOException e) {
            throw new DockerJSONException(e.getMessage(), e);
        }
        return container;
    }

    /**
     * @param host
     * @return
     * @throws DockerJSONException
     */
    public List<DockerContainer> findAllContainers(String host) throws DockerJSONException {
        List<DockerContainer> containers = null;
        try {
            logger.info("The client attempts to list all containers...");
            DockerResponse dockerResponse = driver.findAll(host);
            handleDockerAPIError(dockerResponse);
            containers = objectMapper.readValue(dockerResponse.getBody(),
                    new TypeReference<List<DockerContainer>>() {
                    });
        } catch (FatalDockerJSONException | IOException e) {
            throw new DockerJSONException(e.getMessage(), e);
        }
        return containers;
    }

    /**
     * @return
     * @throws DockerJSONException
     */
    public List<DockerContainer> findAllContainers() throws DockerJSONException {
        List<DockerContainer> containers = null;
        try {
            logger.info("The client attempts to list all containers...");
            DockerResponse dockerResponse = driver.findAll(defaultHost);
            handleDockerAPIError(dockerResponse);
            containers = objectMapper.readValue(dockerResponse.getBody(),
                    new TypeReference<List<DockerContainer>>() {
                    });
        } catch (FatalDockerJSONException | IOException e) {
            throw new DockerJSONException(e.getMessage(), e);
        }
        return containers;
    }

    /**
     * @param container
     * @param host
     * @throws DockerJSONException
     */
    public void createContainer(DockerContainer container, String host) throws DockerJSONException {
        try {
            logger.info("The client attempts to create a container...");
            DockerResponse dockerResponse = driver.create(container, host);
            handleDockerAPIError(dockerResponse);
        } catch (FatalDockerJSONException e) {
            throw new DockerJSONException(e.getMessage(), e);
        }
    }

    /**
     * @param container
     * @throws DockerJSONException
     */
    public void createContainer(DockerContainer container) throws DockerJSONException {
        try {
            logger.info("The client attempts to create a container...");
            DockerResponse dockerResponse = driver.create(container, defaultHost);
            handleDockerAPIError(dockerResponse);
        } catch (FatalDockerJSONException e) {
            throw new DockerJSONException(e.getMessage(), e);
        }
    }

    /**
     * @param container
     * @param host
     * @throws DockerJSONException
     */
    public void startContainer(DockerContainer container, String host) throws DockerJSONException {
        try {
            logger.info("The client attempts to start a container...");
            DockerResponse dockerResponse = driver.start(container, host);
            handleDockerAPIError(dockerResponse);
        } catch (FatalDockerJSONException e) {
            throw new DockerJSONException(e.getMessage(), e);
        }
    }


    /**
     * @param container
     * @throws DockerJSONException
     */
    public void startContainer(DockerContainer container) throws DockerJSONException {
        try {
            logger.info("The client attempts to start a container...");
            DockerResponse dockerResponse = driver.start(container, defaultHost);
            handleDockerAPIError(dockerResponse);
        } catch (FatalDockerJSONException e) {
            throw new DockerJSONException(e.getMessage(), e);
        }
    }

    /**
     * @param container
     * @param host
     * @throws DockerJSONException
     */
    public void stopContainer(DockerContainer container, String host) throws DockerJSONException {
        try {
            logger.info("The client attempts to stop a container...");
            DockerResponse dockerResponse = driver.stop(container, host);
            handleDockerAPIError(dockerResponse);
        } catch (FatalDockerJSONException e) {
            throw new DockerJSONException(e.getMessage(), e);
        }
    }

    /**
     * @param container
     * @throws DockerJSONException
     */
    public void stopContainer(DockerContainer container) throws DockerJSONException {
        try {
            logger.info("The client attempts to stop a container...");
            DockerResponse dockerResponse = driver.stop(container, defaultHost);
            handleDockerAPIError(dockerResponse);
        } catch (FatalDockerJSONException e) {
            throw new DockerJSONException(e.getMessage(), e);
        }
    }

    /**
     * @param container
     * @param host
     * @return
     * @throws DockerJSONException
     */
    public DockerResponse killContainer(DockerContainer container, String host) throws DockerJSONException {
        DockerResponse dockerResponse = null;
        try {
            logger.info("The client attempts to kill a container...");
            dockerResponse = driver.kill(container, host);
            handleDockerAPIError(dockerResponse);
        } catch (FatalDockerJSONException e) {
            throw new DockerJSONException(e.getMessage(), e);
        }
        return dockerResponse;
    }

    /**
     * @param container
     * @return
     * @throws DockerJSONException
     */
    public DockerResponse killContainer(DockerContainer container) throws DockerJSONException {
        DockerResponse dockerResponse = null;
        try {
            logger.info("The client attempts to kill a container...");
            dockerResponse = driver.kill(container, defaultHost);
            handleDockerAPIError(dockerResponse);
        } catch (FatalDockerJSONException e) {
            throw new DockerJSONException(e.getMessage(), e);
        }
        return dockerResponse;
    }

    /**
     * @param container
     * @param host
     * @return
     * @throws DockerJSONException
     */
    public DockerResponse removeContainer(DockerContainer container, String host) throws DockerJSONException {
        DockerResponse dockerResponse = null;
        try {
            logger.info("The client attempts to remove a container...");
            dockerResponse = driver.remove(container, host);
            handleDockerAPIError(dockerResponse);
        } catch (FatalDockerJSONException e) {
            throw new DockerJSONException(e.getMessage(), e);
        }
        return dockerResponse;
    }

    /**
     * @param container
     * @return
     * @throws DockerJSONException
     */
    public DockerResponse removeContainer(DockerContainer container) throws DockerJSONException {
        DockerResponse dockerResponse = null;
        try {
            logger.info("The client attempts to remove a container...");
            dockerResponse = driver.remove(container, defaultHost);
            handleDockerAPIError(dockerResponse);
        } catch (FatalDockerJSONException e) {
            throw new DockerJSONException(e.getMessage(), e);
        }
        return dockerResponse;
    }

    /**
     * @param container
     * @param host
     * @param tag
     * @return
     * @throws DockerJSONException
     */
    public DockerResponse commitImage(DockerContainer container, String host, String tag, String repository) throws DockerJSONException {
        DockerResponse dockerResponse = null;
        try {
            logger.info("The client attempts to commit an image...");
            dockerResponse = driver.commit(container, host, tag, repository);
            handleDockerAPIError(dockerResponse);
        } catch (FatalDockerJSONException e) {
            throw new DockerJSONException(e.getMessage(), e);
        }
        return dockerResponse;
    }

    /**
     * @param container
     * @param tag
     * @return
     * @throws DockerJSONException
     */
    public DockerResponse commitImage(DockerContainer container, String tag, String repository) throws DockerJSONException {
        DockerResponse dockerResponse = null;
        try {
            logger.info("The client attempts to commit an image...");
            dockerResponse = driver.commit(container, defaultHost, tag, repository);
            handleDockerAPIError(dockerResponse);
        } catch (FatalDockerJSONException e) {
            throw new DockerJSONException(e.getMessage(), e);
        }
        return dockerResponse;
    }

    /**
     * @param host
     * @param tag
     * @param repository
     * @return
     * @throws DockerJSONException
     */
    public DockerResponse pullImage(String host, String tag, String repository) throws DockerJSONException {
        DockerResponse dockerResponse = null;
        try {
            logger.info("The client attempts to pull an image...");
            dockerResponse = driver.pull(host, tag, repository);
            handleDockerAPIError(dockerResponse);
        } catch (FatalDockerJSONException e) {
            throw new DockerJSONException(e.getMessage(), e);
        }
        return dockerResponse;
    }


    /**
     * @param image
     * @param host
     * @return
     * @throws DockerJSONException
     */
    public Image findAnImage(Image image, String host) throws DockerJSONException {
        DockerResponse dockerResponse = null;
        try {
            logger.info("The client attempts to find an image...");
            dockerResponse = driver.findAnImage(image, host);
            handleDockerAPIError(dockerResponse);
            image = objectMapper.readValue(dockerResponse.getBody(), Image.class);
        } catch (FatalDockerJSONException | IOException e) {
            throw new DockerJSONException(e.getMessage(), e);
        }
        return image;
    }

    public Image findAnImage(Image image) throws DockerJSONException {
        DockerResponse dockerResponse = null;
        try {
            logger.info("The client attempts to find an image...");
            dockerResponse = driver.findAnImage(image, defaultHost);
            handleDockerAPIError(dockerResponse);
            image = objectMapper.readValue(dockerResponse.getBody(), Image.class);
        } catch (FatalDockerJSONException | IOException e) {
            throw new DockerJSONException(e.getMessage(), e);
        }
        return image;
    }


    /**
     * @param image
     * @param host
     * @return
     * @throws DockerJSONException
     */
    public DockerResponse removeImage(Image image, String host) throws DockerJSONException {
        DockerResponse dockerResponse = null;
        try {
            logger.info("The client attempts to remove an image...");
            dockerResponse = driver.removeImage(image, host);
            handleDockerAPIError(dockerResponse);
        } catch (FatalDockerJSONException e) {
            throw new DockerJSONException(e.getMessage(), e);
        }
        return dockerResponse;
    }

    /**
     * @param image
     * @return
     * @throws DockerJSONException
     */
    public DockerResponse removeImage(Image image) throws DockerJSONException {
        DockerResponse dockerResponse = null;
        try {
            logger.info("The client attempts to remove an image...");
            dockerResponse = driver.removeImage(image, defaultHost);
            handleDockerAPIError(dockerResponse);
        } catch (FatalDockerJSONException e) {
            throw new DockerJSONException(e.getMessage(), e);
        }
        return dockerResponse;
    }


    /**
     * @param container
     * @param commands
     * @param host
     * @return
     * @throws DockerJSONException
     */
    public DockerResponse execCommand(DockerContainer container, List<String> commands, String host) throws DockerJSONException {
        DockerResponse dockerResponse = null;
        try {
            logger.info("The client attempts to execute a command into a container...");
            ExecBody execBody = ExecBodyBuilder.anExecBody()
                    .withCmd(commands)
                    .withAttachStdin(Boolean.TRUE)
                    .withAttachStdout(Boolean.TRUE)
                    .withAttachStderr(Boolean.TRUE)
                    .withTty(Boolean.TRUE)
                    .build();
            dockerResponse = driver.execCreate(container, execBody, host);
            handleDockerAPIError(dockerResponse);
            ExecStartBody execStartBody = ExecStartBodyBuilder
                    .anExecStartBody()
                    .withDetach(Boolean.FALSE)
                    .withTty(Boolean.TRUE)
                    .build();
            execBody = objectMapper.readValue(dockerResponse.getBody(), ExecBody.class);
            dockerResponse = driver.execStart(execBody.getId(), execStartBody, host);
            handleDockerAPIError(dockerResponse);
        } catch (FatalDockerJSONException | IOException e) {
            throw new DockerJSONException(e.getMessage(), e);
        }
        return dockerResponse;
    }

    /**
     * @param container
     * @param commands
     * @return
     * @throws DockerJSONException
     */
    public DockerResponse execCommand(DockerContainer container, List<String> commands) throws DockerJSONException {
        DockerResponse dockerResponse = null;
        try {
            logger.info("The client attempts to execute a command into a container...");
            ExecBody execBody = ExecBodyBuilder.anExecBody()
                    .withCmd(commands)
                    .withAttachStdin(Boolean.TRUE)
                    .withAttachStdout(Boolean.TRUE)
                    .withAttachStderr(Boolean.TRUE)
                    .withTty(Boolean.TRUE)
                    .build();
            dockerResponse = driver.execCreate(container, execBody, defaultHost);
            handleDockerAPIError(dockerResponse);
            ExecStartBody execStartBody = ExecStartBodyBuilder
                    .anExecStartBody()
                    .withDetach(Boolean.FALSE)
                    .withTty(Boolean.TRUE)
                    .build();
            execBody = objectMapper.readValue(dockerResponse.getBody(), ExecBody.class);
            dockerResponse = driver.execStart(execBody.getId(), execStartBody, defaultHost);
            handleDockerAPIError(dockerResponse);
        } catch (FatalDockerJSONException | IOException e) {
            throw new DockerJSONException(e.getMessage(), e);
        }
        return dockerResponse;
    }

    /**
     * @param dockerResponse
     * @throws DockerJSONException
     */
    private void handleDockerAPIError(DockerResponse dockerResponse) throws DockerJSONException {
        switch (dockerResponse.getStatus()) {
            case 101:
                logger.info("No error : hints proxy about hijacking");
                break;
            case 200:
                logger.info("Status OK");
                break;
            case 201:
                logger.info("Status OK");
                break;
            case 204:
                logger.info("Status OK");
                break;
            case 304:
                logger.error("Docker API answers with a 304 error code : " + dockerResponse.getBody());
                throw new ErrorDockerJSONException("Docker API answers with a 304 error code : " +
                        dockerResponse.getBody());
            case 400:
                logger.error("Docker API answers with a 400 error code : " + dockerResponse.getBody());
                throw new ErrorDockerJSONException("Docker API answers with a 400 error code : " +
                        dockerResponse.getBody());
            case 404:
                logger.error("Docker API answers with a 404 error code : " + dockerResponse.getBody());
                throw new ErrorDockerJSONException("Docker API answers with a 404 error code : " +
                        dockerResponse.getBody());
            case 406:
                logger.error("Docker API answers with a 406 error code : " + dockerResponse.getBody());
                throw new ErrorDockerJSONException("Docker API answers with a 406 error code : " +
                        dockerResponse.getBody());
            case 409:
                logger.error("Docker API answers with a 409 error code : " + dockerResponse.getBody());
                throw new ErrorDockerJSONException("Docker API answers with a 409 error code : " +
                        dockerResponse.getBody());
            case 500:
                logger.error("Docker API answers with a 500 error code : " + dockerResponse.getBody());
                throw new ErrorDockerJSONException("Docker API answers with a 500 error code : " +
                        dockerResponse.getBody());
        }
    }

    public DockerClient() {
    }

    public DockerClient(String registryHost, String defaultHost, DockerDriver driver) {
        this.registryHost = registryHost;
        this.defaultHost = defaultHost;
        this.driver = driver;
    }


    public String getDefaultHost() {
        return defaultHost;
    }

    public void setDefaultHost(String defaultHost) {
        this.defaultHost = defaultHost;
    }

    public DockerDriver getDriver() {
        return driver;
    }

    public void setDriver(DockerDriver driver) {
        this.driver = driver;
    }

    public String getRegistryHost() {
        return registryHost;
    }

    public void setRegistryHost(String registryHost) {
        this.registryHost = registryHost;
    }
}
