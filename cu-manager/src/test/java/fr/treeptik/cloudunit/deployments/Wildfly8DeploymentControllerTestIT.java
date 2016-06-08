package fr.treeptik.cloudunit.deployments;

/**
 * Created by guillaume on 07/06/16.
 */
public class Wildfly8DeploymentControllerTestIT extends AbstractJBossDeploymentControllerTestIT {

    public Wildfly8DeploymentControllerTestIT() {
        super.release = "wildfly-8";
    }

}
