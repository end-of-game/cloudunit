package fr.treeptik.cloudunit.docker.core;

import fr.treeptik.cloudunit.docker.builders.ConfigBuilder;
import fr.treeptik.cloudunit.docker.builders.ContainerBuilder;
import fr.treeptik.cloudunit.docker.builders.HostConfigBuilder;
import fr.treeptik.cloudunit.docker.builders.ImageBuilder;
import fr.treeptik.cloudunit.docker.model.Config;
import fr.treeptik.cloudunit.docker.model.DockerContainer;
import fr.treeptik.cloudunit.docker.model.HostConfig;
import fr.treeptik.cloudunit.docker.model.Image;
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
public class ImageCommandTests {

    static String DOCKER_HOST;
    static Boolean isTLS;

    private static DockerClient dockerClient;
    private static final String CONTAINER_NAME = "myContainer" + System.currentTimeMillis();
    private final String TAG = "mytag";

    @Before
    public void setup() {

        String OS = System.getProperty("os.name").toLowerCase();
        if (OS.indexOf("mac") >= 0) {
            DOCKER_HOST = "cloudunit.dev:4243";
            isTLS = false;
        } else {
            DOCKER_HOST = "cloudunit.dev:2376";
            isTLS = true;
        }

        dockerClient = new DockerClient();
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
                .withImage("cloudunit/tomcat-8")
                .withHostConfig(hostConfig)
                .withExposedPorts(new HashMap<>())
                .withMemory(0L)
                .withMemorySwap(0L)
                .build();
        DockerContainer container = ContainerBuilder.aContainer().withName(CONTAINER_NAME).withConfig(config).build();
        try {
            dockerClient.setDriver(new SimpleDockerDriver("../cu-vagrant/certificats", isTLS));
            dockerClient.createContainer(container, DOCKER_HOST);
        } catch (DockerJSONException e) {
            Assert.fail();
        }
    }

    //@After
    public void tearDown() throws DockerJSONException {
        DockerContainer container = ContainerBuilder.aContainer()
                .withName(CONTAINER_NAME)
                .build();
        dockerClient.removeContainer(container, DOCKER_HOST);
    }

    @Test
    public void test00_commitAnImage() throws DockerJSONException {
        DockerContainer container = ContainerBuilder.aContainer()
                .withName(CONTAINER_NAME)
                .build();
        container = dockerClient.findContainer(container, DOCKER_HOST);
        dockerClient.commitImage(container, DOCKER_HOST, TAG, container.getConfig().getImage());
    }

    @Test
    public void test00_findAnImage() throws DockerJSONException {
        Image image = ImageBuilder.anImage().withName(TAG + ":" + TAG).build();
        dockerClient.findAnImage(image, DOCKER_HOST);
    }

    @Test
    public void test05_removeImage() throws DockerJSONException {
        Image image = ImageBuilder.anImage().withName(TAG + ":" + TAG).build();
        image = dockerClient.findAnImage(image, DOCKER_HOST);
        dockerClient.removeImage(image, DOCKER_HOST);
    }

}
