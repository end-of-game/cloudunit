package fr.treeptik.cloudunit.orchestrator.docker.test;

import com.spotify.docker.client.DockerClient;
import com.spotify.docker.client.exceptions.DockerException;
import fr.treeptik.cloudunit.orchestrator.docker.service.impl.DockerServiceImpl;
import org.junit.Assume;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.util.Random;

@Configuration
public class TestConfiguration {

    @Autowired
    private DockerClient dockerClient;

    @Bean("testContainerName")
    public String testContainerName() {
        return String.format("container%s", new Random().nextInt(100000))+"c";
    }

    @Bean("testVolumeName")
    public String testVolumeName() {
        return String.format("volume%s", new Random().nextInt(100000))+"v";
    }


    @PostConstruct
    public void checkEnv() throws DockerException, InterruptedException {
        Assume.assumeTrue(DockerServiceImpl.DEFAULT_NETWORK + " should exist",
                dockerClient.listNetworks().stream()
                    .anyMatch(network -> network.name().equalsIgnoreCase(DockerServiceImpl.DEFAULT_NETWORK)));
    }
}
