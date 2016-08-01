package fr.treeptik.cloudunit.docker.core;

import fr.treeptik.cloudunit.docker.builders.ConfigBuilder;
import fr.treeptik.cloudunit.docker.builders.ContainerBuilder;
import fr.treeptik.cloudunit.docker.builders.HostConfigBuilder;
import fr.treeptik.cloudunit.docker.model.Config;
import fr.treeptik.cloudunit.docker.model.DockerContainer;
import fr.treeptik.cloudunit.docker.model.HostConfig;
import fr.treeptik.cloudunit.exception.DockerJSONException;
import fr.treeptik.cloudunit.utils.ContainerUtils;
import org.junit.*;
import org.junit.runners.MethodSorters;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by guillaume on 21/10/15.
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ContainerCommandTests {

    public static String DOCKER_HOST;
    public static Boolean isTLS;

    private static DockerClient dockerClient;
    private static final String CONTAINER_NAME = "myContainer";
    private static DockerContainer container;

    @BeforeClass
    public static void setupClass() {

        boolean integration = System.getenv("CLOUDUNIT_JENKINS_CI") != null;
        if (integration) {
            DOCKER_HOST = "cloudunit.dev:2376";
            isTLS = true;
        } else {
            DOCKER_HOST = "cloudunit.dev:4243";
            isTLS = false;
        }


        dockerClient = new DockerClient();
        dockerClient.setDriver(new SimpleDockerDriver("../cu-vagrant/certificats", isTLS));
    }

    @Before
    public void setup() throws Exception {
        HostConfig hostConfig = HostConfigBuilder.aHostConfig()
                .withBinds(Arrays.asList("/etc/localtime:/etc/localtime:ro"))
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
        container = ContainerBuilder.aContainer()
                .withName(CONTAINER_NAME)
                .withConfig(config)
                .build();
        dockerClient.createContainer(container, DOCKER_HOST);
    }

    @After
    public void tearDown() throws Exception {
        DockerContainer container = ContainerBuilder.aContainer()
                .withName(CONTAINER_NAME)
                .build();
        dockerClient.removeContainer(container, DOCKER_HOST);
    }

    @Test
    public void test01_lifecycle() throws DockerJSONException {

        Assert.assertNotNull(dockerClient.findContainer(container, DOCKER_HOST).getId());

        dockerClient.findContainer(container, DOCKER_HOST);

        container = ContainerUtils.newStartInstance(container.getName(), null, null, null);

        dockerClient.startContainer(container, DOCKER_HOST);

        Assert.assertTrue(dockerClient.findContainer(container, DOCKER_HOST).getState().getRunning());

        dockerClient.stopContainer(container, DOCKER_HOST);
        Assert.assertFalse(dockerClient.findContainer(container, DOCKER_HOST).getState().getRunning());

        dockerClient.startContainer(container, DOCKER_HOST);
        dockerClient.killContainer(container, DOCKER_HOST);
        dockerClient.startContainer(container, DOCKER_HOST);

    }

    @Test
    public void test20_createContainerWithVolumeFrom() throws DockerJSONException, InterruptedException {
        SimpleDateFormat format = new SimpleDateFormat("yyyy");
        container = ContainerUtils.newStartInstance(container.getName(), null, Arrays.asList("tomcat-8"), null);
        dockerClient.startContainer(container, DOCKER_HOST);
        container = dockerClient.findContainer(container, DOCKER_HOST);
        Assert.assertTrue(dockerClient.execCommand(container, Arrays.asList("bash", "-c", "ls /cloudunit/binaries"), DOCKER_HOST).getBody()
               .contains("bin"));
        Assert.assertTrue(dockerClient.execCommand(container, Arrays.asList("date"), DOCKER_HOST).getBody()
                .contains(format.format(new Date(System.currentTimeMillis()))));
        container = ContainerUtils.newStartInstance(container.getName(), null, null, null);
        dockerClient.killContainer(container, DOCKER_HOST);
    }

    @Test
    public void test30_createContainerWithVolumes() throws DockerJSONException {
        container = ContainerUtils.newStartInstance(container.getName(),
                null, null, null);
        dockerClient.startContainer(container, DOCKER_HOST);
        List mounts = dockerClient.findContainer(container, DOCKER_HOST).getMounts();
        Assert.assertTrue(mounts.toString().contains("localtime"));
    }

    @Test
    public void test40_startContainerWithPorts() throws DockerJSONException {
        container = ContainerUtils.newStartInstance(container.getName(),
                new HashMap() {{
                    put("22/tcp", "22");
                }}, null,
                null);
        dockerClient.startContainer(container, DOCKER_HOST);
        Assert.assertTrue((dockerClient.findContainer(container, DOCKER_HOST)
                .getNetworkSettings().getPorts().toString().contains("22")));

    }
}
