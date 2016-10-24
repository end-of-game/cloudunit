package fr.treeptik.cloudunit.docker.core;

import fr.treeptik.cloudunit.docker.model.DockerContainer;
import fr.treeptik.cloudunit.docker.model.Network;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;

public class NetworksCommandTest {

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

    @Test
    public void createThenDeleteNetworkAsBridge() throws IOException {
        dockerCloudUnitClient.createNetwork("myNetwork", "group1");
        Network network = dockerCloudUnitClient.findNetwork("myNetwork");
        Assert.assertEquals("myNetwork", network.getName());
        Assert.assertTrue(network.getLabels().keySet().contains("cloudunit.type"));
        Assert.assertTrue(network.getLabels().values().contains("group1"));
        dockerCloudUnitClient.removeNetwork(network.getId());
    }
	

}
