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

    private static DockerCloudUnitClient dockerCloudUnitClient;
    private static final String CONTAINER_NAME = "myContainer";

    @Before
    public void setup() {

        DOCKER_HOST = "cloudunit.dev:4243";

        dockerCloudUnitClient = new DockerCloudUnitClient();
        dockerCloudUnitClient.setDriver(new SimpleDockerDriver(false, DOCKER_HOST));

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
                .withImage("cloudunit/tomcat-8:latest")
                .withHostConfig(hostConfig)
                .withExposedPorts(new HashMap<>())
                .withMemory(0L)
                .withMemorySwap(0L)
                .build();
        DockerContainer container = ContainerBuilder.aContainer().withName(CONTAINER_NAME).withConfig(config).build();
        try {
            dockerCloudUnitClient.createContainer(container, DOCKER_HOST);
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
            dockerCloudUnitClient.startContainer(container);
        } catch (DockerJSONException e) {
            Assert.fail();
        }
    }

    @After
    public void tearDown() throws DockerJSONException {
        DockerContainer container = ContainerBuilder.aContainer()
                .withName(CONTAINER_NAME)
                .build();
        dockerCloudUnitClient.removeContainer(container);
    }


}
