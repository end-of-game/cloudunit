package fr.treeptik.cloudunit.docker.core;

import fr.treeptik.cloudunit.docker.builders.ConfigBuilder;
import fr.treeptik.cloudunit.docker.builders.ContainerBuilder;
import fr.treeptik.cloudunit.docker.builders.HostConfigBuilder;
import fr.treeptik.cloudunit.docker.model.Config;
import fr.treeptik.cloudunit.docker.model.DockerContainer;
import fr.treeptik.cloudunit.docker.model.HostConfig;
import fr.treeptik.cloudunit.exception.DockerJSONException;
import org.junit.*;
import org.junit.runners.MethodSorters;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

/**
 * Created by guillaume on 22/10/15.
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class AdvancedCommandTests {

    static String DOCKER_HOST;
    static Boolean isTLS;

    private static DockerClient dockerClient;
    private static final String CONTAINER_NAME = "myContainer";
    private final int RUNNING_CONTAINERS = 7;
    private final String TAG = "mytag";
    private final String REPOSITORY = "localhost:5000/";
    private final String REGISTRY_HOST = "192.168.50.4:5000";

    @BeforeClass
    public static void setup() {

        String OS = System.getProperty("os.name").toLowerCase();
        if (OS.indexOf("mac") >= 0) {
            DOCKER_HOST = "cloudunit.dev:4243";
            isTLS = false;
        } else {
            DOCKER_HOST = "cloudunit.dev:2376";
            isTLS = true;
        }

        dockerClient = new DockerClient();
        dockerClient.setDriver(new SimpleDockerDriver("../cu-vagrant/certificats", isTLS));

        HostConfig hostConfig = HostConfigBuilder.aHostConfig()
                .withVolumesFrom(new ArrayList<>())
                .build();
        Config config = ConfigBuilder.aConfig()
                .withAttachStdin(Boolean.FALSE)
                .withAttachStdout(Boolean.TRUE)
                .withAttachStderr(Boolean.TRUE)
                .withCmd(Arrays.asList("/bin/bash", "/cloudunit/scripts/start-service.sh", "johndoe", "abc2015",
                        "192.168.2.116", "172.17.0.221", "aaaa",
                        "AezohghooNgaegh8ei2jabib2nuj9yoe", "main"))
                .withImage("cloudunit/git:latest")
                .withHostConfig(hostConfig)
                .withExposedPorts(new HashMap<>())
                .withMemory(0L)
                .withMemorySwap(0L)
                .build();
        DockerContainer container = ContainerBuilder.aContainer().withName(CONTAINER_NAME).withConfig(config).build();
        try {
            dockerClient.createContainer(container, DOCKER_HOST);
            hostConfig = HostConfigBuilder.aHostConfig()
                    .withLinks(new ArrayList<>())
                    .withBinds(new ArrayList<>())
                    .withPortBindings(new HashMap<>())
                    .withPrivileged(Boolean.FALSE)
                    .withPublishAllPorts(Boolean.TRUE)
                    .withVolumesFrom(new ArrayList<>()).build();
            config = ConfigBuilder.aConfig()
                    .withHostConfig(hostConfig)
                    .build();
            container = ContainerBuilder.aContainer()
                    .withName(CONTAINER_NAME)
                    .withConfig(config).build();
            dockerClient.startContainer(container, DOCKER_HOST);
        } catch (DockerJSONException e) {
            Assert.fail();
        }
    }

    @AfterClass
    public static void tearDown() throws DockerJSONException {
        DockerContainer container = ContainerBuilder.aContainer()
                .withName(CONTAINER_NAME)
                .build();
        dockerClient.removeContainer(container, DOCKER_HOST);
    }

    @Test
    public void test00_execCommand() throws DockerJSONException {
        DockerContainer container = ContainerBuilder.aContainer()
                .withName("myContainer")
                .build();
        container = dockerClient.findContainer(container, DOCKER_HOST);
        // Change it in 2017 :-)
        Assert.assertTrue(dockerClient.execCommand(container, Arrays.asList("date"), DOCKER_HOST).getBody()
                .contains("2016"));
    }


}
