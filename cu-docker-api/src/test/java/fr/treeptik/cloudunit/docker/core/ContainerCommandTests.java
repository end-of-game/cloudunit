package fr.treeptik.cloudunit.docker.core;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import fr.treeptik.cloudunit.docker.builders.ConfigBuilder;
import fr.treeptik.cloudunit.docker.builders.ContainerBuilder;
import fr.treeptik.cloudunit.docker.builders.HostConfigBuilder;
import fr.treeptik.cloudunit.docker.model.Config;
import fr.treeptik.cloudunit.docker.model.DockerContainer;
import fr.treeptik.cloudunit.docker.model.HostConfig;
import fr.treeptik.cloudunit.docker.model.Mounts;
import fr.treeptik.cloudunit.exception.DockerJSONException;
import fr.treeptik.cloudunit.utils.ContainerUtils;

/**
 * Created by guillaume on 21/10/15.
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ContainerCommandTests {

    public static String DOCKER_HOST;
    public static Boolean isTLS;

    private static DockerCloudUnitClient dockerCloudUnitClient;
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

        dockerCloudUnitClient = new DockerCloudUnitClient();
        dockerCloudUnitClient.setDriver(new SimpleDockerDriver(DOCKER_HOST, "../cu-vagrant/certificats", isTLS));
    }

    @Before
    public void setup() throws Exception {
        HostConfig hostConfig = HostConfigBuilder.aHostConfig()
                .withBinds(Arrays.asList("/etc/localtime:/etc/localtime:ro")).build();
        Config config = ConfigBuilder.aConfig().withAttachStdin(Boolean.FALSE).withAttachStdout(Boolean.TRUE)
                .withAttachStderr(Boolean.TRUE)
                .withCmd(Arrays.asList("/bin/bash", "/cloudunit/scripts/start-service.sh", "johndoe", "abc2015",
                        "192.168.2.116", "172.17.0.221", "aaaa", "AezohghooNgaegh8ei2jabib2nuj9yoe", "main"))
                .withImage("cloudunit/tomcat-8:latest").withHostConfig(hostConfig).withExposedPorts(new HashMap<>())
                .withMemory(0L).withMemorySwap(0L).build();
        container = ContainerBuilder.aContainer().withName(CONTAINER_NAME).withConfig(config).build();
        dockerCloudUnitClient.createContainer(container);
    }

    @After
    public void tearDown() throws Exception {
        DockerContainer container = ContainerBuilder.aContainer().withName(CONTAINER_NAME).build();
        dockerCloudUnitClient.removeContainer(container);
    }

    @Test
    public void test01_lifecycle() throws DockerJSONException {

        Assert.assertNotNull(dockerCloudUnitClient.findContainer(container).getId());

        dockerCloudUnitClient.findContainer(container);

        container = ContainerUtils.newStartInstance(container.getName(), null, null, false);

        dockerCloudUnitClient.startContainer(container);

        Assert.assertTrue(dockerCloudUnitClient.findContainer(container).getState().getRunning());

        dockerCloudUnitClient.stopContainer(container);
        Assert.assertFalse(dockerCloudUnitClient.findContainer(container).getState().getRunning());

        dockerCloudUnitClient.startContainer(container);
        dockerCloudUnitClient.killContainer(container);
        dockerCloudUnitClient.startContainer(container);

    }

    @Test
    public void test30_createContainerWithVolumes() throws DockerJSONException {
        container = ContainerUtils.newStartInstance(container.getName(), null, null, false);
        dockerCloudUnitClient.startContainer(container);
        List<Mounts> mounts = dockerCloudUnitClient.findContainer(container).getMounts();
        Assert.assertTrue(mounts.toString().contains("localtime"));
    }

    @Test
    public void test40_startContainerWithPorts() throws DockerJSONException {
        container = ContainerUtils.newStartInstance(container.getName(), null, null, false);
        dockerCloudUnitClient.startContainer(container);
        Assert.assertTrue((dockerCloudUnitClient.findContainer(container).getNetworkSettings().getPorts().toString()
                .contains("22")));

    }
}
