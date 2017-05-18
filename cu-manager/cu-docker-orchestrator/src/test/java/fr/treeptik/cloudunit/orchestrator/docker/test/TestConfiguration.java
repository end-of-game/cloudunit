package fr.treeptik.cloudunit.orchestrator.docker.test;

import com.spotify.docker.client.DockerClient;
import com.spotify.docker.client.exceptions.DockerException;
import fr.treeptik.cloudunit.orchestrator.docker.service.impl.DockerServiceImpl;
import org.junit.Assume;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

@Configuration
public class TestConfiguration {

    @Autowired
    private DockerClient dockerClient;

    @PostConstruct
    public void checkEnv() throws DockerException, InterruptedException {
        Assume.assumeTrue(DockerServiceImpl.DEFAULT_NETWORK + " should exist",
                dockerClient.listNetworks().stream()
                    .anyMatch(network -> network.name().equalsIgnoreCase(DockerServiceImpl.DEFAULT_NETWORK)));
    }
}
