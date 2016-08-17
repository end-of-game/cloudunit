package fr.treeptik.cloudunit.cli.integration.deployment;

/**
 * Created by guillaume on 19/10/15.
 */
public class Tomcat6DeploymentCommandsIT extends AbstractDeploymentCommandsIT {

    public Tomcat6DeploymentCommandsIT() {
        super.serverType = "tomcat-6";
    }
}
