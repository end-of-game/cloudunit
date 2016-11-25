package fr.treeptik.cloudunit.docker.core;

import fr.treeptik.cloudunit.docker.builders.ConfigBuilder;
import fr.treeptik.cloudunit.docker.builders.ContainerBuilder;
import fr.treeptik.cloudunit.docker.builders.HostConfigBuilder;
import fr.treeptik.cloudunit.docker.model.Config;
import fr.treeptik.cloudunit.docker.model.DockerContainer;
import fr.treeptik.cloudunit.docker.model.HostConfig;
import fr.treeptik.cloudunit.docker.model.Network;
import fr.treeptik.cloudunit.model.Container;
import fr.treeptik.cloudunit.utils.ContainerUtils;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;

public class NetworksCommandTest {

    public static String DOCKER_HOST;

    private static DockerCloudUnitClient dockerCloudUnitClient;
    private static final String CONTAINER_NAME = "myContainer";
    private static DockerContainer container;

    @BeforeClass
    public static void setupClass() {
        DOCKER_HOST = "cloudunit.dev:4243";
        dockerCloudUnitClient = new DockerCloudUnitClient();
        dockerCloudUnitClient.setDriver(new SimpleDockerDriver(false, DOCKER_HOST));
    }

    @Test
    public void createThenDeleteNetworkAsBridge() throws IOException {
        dockerCloudUnitClient.createNetwork("myNetwork", "group1");
        Network network = dockerCloudUnitClient.findNetwork("myNetwork");
        Assert.assertEquals("myNetwork", network.getName());
        Assert.assertTrue(network.getLabels().keySet().contains("cloudunit.type"));
        Assert.assertTrue(network.getLabels().values().contains("group1"));
        container = getContainerConfig();
        dockerCloudUnitClient.createContainer(container);
        container = dockerCloudUnitClient.findContainer(container);

        dockerCloudUnitClient.connectToNetwork(network.getId(), container.getId());

        container = dockerCloudUnitClient.findContainer(container);

        Assert.assertTrue(container.getNetworkSettings().getNetworks().keySet().stream()
                .filter(n-> n.equalsIgnoreCase("myNetwork"))
                .findAny()
                .isPresent());
        container = ContainerBuilder.aContainer().withName(CONTAINER_NAME).build();
        dockerCloudUnitClient.removeContainer(container);

       dockerCloudUnitClient.removeNetwork(network.getId());
    }

    private DockerContainer getContainerConfig() {
        HostConfig hostConfig = HostConfigBuilder.aHostConfig()
                .withBinds(Arrays.asList("/etc/localtime:/etc/localtime:ro")).build();
        Config config = ConfigBuilder.aConfig().withAttachStdin(Boolean.FALSE).withAttachStdout(Boolean.TRUE)
                .withAttachStderr(Boolean.TRUE)
                .withCmd(Arrays.asList("/bin/bash", "/cloudunit/scripts/start-service.sh", "johndoe", "abc2015",
                        "192.168.2.116", "172.17.0.221", "aaaa", "AezohghooNgaegh8ei2jabib2nuj9yoe", "main"))
                .withImage("cloudunit/tomcat-8:latest").withHostConfig(hostConfig).withExposedPorts(new HashMap<>())
                .withMemory(0L).withMemorySwap(0L).build();
        container = ContainerBuilder.aContainer().withName(CONTAINER_NAME).withConfig(config).build();
        return container;
    }


}
