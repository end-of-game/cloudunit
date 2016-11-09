package fr.treeptik.cloudunit.docker.core;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
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

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

/**
 * Created by guillaume on 22/10/15.
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ImageCommandTests {

    static String DOCKER_HOST;
    static Boolean isUnixSocketConnection;
    static String socketPathAsString;

    private static DockerCloudUnitClient dockerCloudUnitClient;
    private static final String CONTAINER_NAME = "myContainer" + System.currentTimeMillis();
    private final String TAG = "mytag";

    @Before
    public void setup() {
        DOCKER_HOST = "cloudunit.dev:4243";
        isUnixSocketConnection = true;
        socketPathAsString = "/var/run/docker.sock";

        dockerCloudUnitClient = new DockerCloudUnitClient();
        HostConfig hostConfig = HostConfigBuilder.aHostConfig()
                .withVolumesFrom(new ArrayList<>())
                .build();
        Config config = ConfigBuilder.aConfig()
                .withAttachStdin(Boolean.FALSE)
                .withAttachStdout(Boolean.TRUE)
                .withAttachStderr(Boolean.TRUE)
                .withCmd(Arrays.asList("/bin/bash"))
                .withImage("alpine")
                .withHostConfig(hostConfig)
                .withExposedPorts(new HashMap<>())
                .withMemory(0L)
                .withMemorySwap(0L)
                .build();
        DockerContainer container = ContainerBuilder.aContainer().withName(CONTAINER_NAME).withConfig(config).build();
        try {
            if(isUnixSocketConnection){
                dockerCloudUnitClient.setDriver(new SimpleDockerDriver(true, socketPathAsString));
            } else {
               dockerCloudUnitClient.setDriver(new SimpleDockerDriver(false, DOCKER_HOST));
            }
            dockerCloudUnitClient.pullImage("latest", "alpine");
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
        Image image = ImageBuilder.anImage().withName("alpine:latest").build();
        image = dockerCloudUnitClient.findAnImage(image);
        dockerCloudUnitClient.removeImage(image);

    }

    @Test
    public void test01_commitAnImage() throws DockerJSONException {
        DockerContainer container = ContainerBuilder.aContainer()
                .withName(CONTAINER_NAME)
                .build();
        container = dockerCloudUnitClient.findContainer(container, DOCKER_HOST);
        dockerCloudUnitClient.commitImage(container, TAG, container.getConfig().getImage());
        Image image = ImageBuilder.anImage().withName("alpine:"+TAG).build();
        image = dockerCloudUnitClient.findAnImage(image);
        Assert.assertTrue("Alpine found !", image.getId() != null);
        dockerCloudUnitClient.removeImage(image);

    }

    @Test
    public void test02_PullAndFindAnImage() throws DockerJSONException, JsonProcessingException {
        dockerCloudUnitClient.pullImage("latest", "busybox");
        Image image = ImageBuilder.anImage().withName("busybox:latest").build();
        image = dockerCloudUnitClient.findAnImage(image);
        Assert.assertTrue("Busybox found !", image.getId() != null);
    }

    @Test(expected=ErrorDockerJSONException.class)
    public void test05_removeImage() throws DockerJSONException {
        dockerCloudUnitClient.pullImage("latest", "busybox");
        Image image = ImageBuilder.anImage().withName("busybox:latest").build();
        image = dockerCloudUnitClient.findAnImage(image);
        Assert.assertTrue("Busybox found !", image.getId() != null);
        dockerCloudUnitClient.removeImage(image);
        dockerCloudUnitClient.findAnImage(image);
    }

}
