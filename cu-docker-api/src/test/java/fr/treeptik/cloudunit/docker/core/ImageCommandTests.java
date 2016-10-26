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
import fr.treeptik.cloudunit.exception.ErrorDockerJSONException;
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

    private static DockerCloudUnitClient dockerCloudUnitClient;
    private static final String CONTAINER_NAME = "myContainer" + System.currentTimeMillis();
    private final String TAG = "mytag";

    @Before
    public void setup() {

        boolean integration = System.getenv("CLOUDUNIT_JENKINS_CI") != null;
        if (integration) {
            DOCKER_HOST = "cloudunit.dev:2376";
            isTLS = true;
        } else {
            DOCKER_HOST = "cloudunit.dev:4243";
            isTLS = false;
        }

        dockerCloudUnitClient = new DockerCloudUnitClient();
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
            dockerCloudUnitClient.setDriver(new SimpleDockerDriver(DOCKER_HOST, "../cu-vagrant/certificats", isTLS));
            dockerCloudUnitClient.createContainer(container, DOCKER_HOST);
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

    @Test
    public void test00_commitAnImage() throws DockerJSONException {
        DockerContainer container = ContainerBuilder.aContainer()
                .withName(CONTAINER_NAME)
                .build();
        container = dockerCloudUnitClient.findContainer(container, DOCKER_HOST);
        dockerCloudUnitClient.commitImage(container, TAG, container.getConfig().getImage());
    }

    @Test
    public void test00_PullAndFindAnImage() throws DockerJSONException {
        dockerCloudUnitClient.pullImage("latest", "busybox");
        Image image = ImageBuilder.anImage().withName("busybox:latest").build();
        image = dockerCloudUnitClient.findAnImage(image);
        Assert.assertTrue("Busybox found !", image.getRepoTags().get(0).contains("busybox"));
    }

    @Test(expected=ErrorDockerJSONException.class)
    public void test05_removeImage() throws DockerJSONException {
        dockerCloudUnitClient.pullImage("latest", "busybox");
        Image image = ImageBuilder.anImage().withName("busybox:latest").build();
        image = dockerCloudUnitClient.findAnImage(image);
        Assert.assertTrue("Busybox found !", image.getRepoTags().get(0).contains("busybox"));
        dockerCloudUnitClient.removeImage(image);
        image = dockerCloudUnitClient.findAnImage(image);
    }

}
